/*      DELETE FROM starlight.floorplan_amenity WHERE floorplan NOT IN (SELECT DISTINCT floorplan FROM starlight.apt_unit);     */

CREATE OR REPLACE FUNCTION _dba_.fix_starlight_floorplans() RETURNS VOID AS
$$
DECLARE
        v_building      BIGINT;
        v_floorplan_name VARCHAR(80);
        v_floorplan_id  BIGINT;
BEGIN
        /* Make all floorplan names standard */
        
        UPDATE  starlight.floorplan 
                SET name = CASE WHEN name ~* '^1 Bedroom' THEN '1bdrm'
                WHEN name ~* '^2 Bedroom' THEN '2bdrm'
                WHEN name ~* '^3 Bedroom' THEN '3bdrm'
                WHEN name ~* '^4 Bedroom' THEN  '4bdrm' 
                WHEN name ~* '^5 Bedroom' THEN  '5bdrm'
                WHEN name ~* '^7 Bedroom' THEN  '7bdrm'
                WHEN name ~* 'Bachelor' THEN 'bach'
                WHEN name ~* '^Junior (1 Bedroom|1bdrm)' THEN  'j1bdrm'
                WHEN name ~* '^Junior 2bdrm' THEN 'j2bdrm'
                WHEN name ~* '^Junior 3bdrm' THEN 'j3bdrm'
                ELSE name  END;
       -- WHERE   id IN (SELECT DISTINCT floorplan FROM starlight.apt_unit);
        
        
        FOR v_building,v_floorplan_name IN
        SELECT  DISTINCT building,name 
        FROM    starlight.floorplan
        WHERE   id IN (SELECT DISTINCT COALESCE(floorplan,0) FROM starlight.apt_unit)
        LOOP
                WITH t AS (SELECT a.id, COUNT(b.id) AS units
                        FROM    starlight.floorplan A
                        JOIN    starlight.apt_unit b ON (a.id = b.floorplan)
                        WHERE   a.building = v_building
                        AND     a.name = v_floorplan_name
                        GROUP BY a.id)
                SELECT  id
                INTO    v_floorplan_id
                FROM    t 
                WHERE   units = (SELECT MAX(units) FROM t)
                LIMIT 1;
                
                /*
                IF NOT EXISTS ( SELECT 'x' FROM starlight.floorplan_amenity
                                WHERE floorplan = v_floorplan_id) 
                THEN
                        INSERT INTO starlight.floorplan_amenity (id,description,name,floorplan_type,floorplan,order_in_parent)
                        (SELECT nextval('public.floorplan_amenity_seq') AS id,description,name,floorplan_type,v_floorplan_id,order_in_parent
                        FROM starlight.floorplan_amenity
                        WHERE floorplan = (SELECT MIN(id) FROM starlight.floorplan WHERE building = v_building AND name = v_floorplan_name));
                END IF;
                
                */         
                
                UPDATE  starlight.apt_unit
                SET     floorplan = v_floorplan_id
                WHERE   floorplan IN    (SELECT id FROM starlight.floorplan 
                                        WHERE   building = v_building
                                        AND     name = v_floorplan_name);
                                        
                UPDATE  starlight.floorplan_counters AS a
                SET     _marketing_unit_count = b.units,
                        _unit_count = b.units
                FROM    (SELECT COUNT(a.id) AS units,b.counters 
                        FROM    starlight.apt_unit a 
                        JOIN    starlight.floorplan b ON (a.floorplan = b.id)
                        WHERE   a.floorplan = v_floorplan_id
                        GROUP BY b.counters) AS b
                WHERE   id = b.counters;  
                
        END LOOP;       
END;
$$
LANGUAGE plpgsql VOLATILE;


BEGIN TRANSACTION;

SELECT * FROM _dba_.fix_starlight_floorplans();

DELETE FROM starlight.floorplan_amenity WHERE floorplan NOT IN (SELECT DISTINCT COALESCE(floorplan,0) FROM starlight.apt_unit);
DELETE FROM starlight.floorplan$media WHERE owner NOT IN (SELECT DISTINCT COALESCE(floorplan,0) FROM starlight.apt_unit);
DELETE FROM starlight.media WHERE id NOT IN (SELECT DISTINCT COALESCE(value,0) FROM starlight.floorplan$media UNION SELECT DISTINCT COALESCE(value,0) FROM starlight.building$media);
DELETE FROM starlight.file_blob WHERE id NOT IN (SELECT DISTINCT COALESCE(media_file_blob_key,0) FROM starlight.media);
DELETE FROM starlight.file_image_thumbnail_blob WHERE blob_key NOT IN (SELECT DISTINCT id FROM starlight.file_blob);
DELETE FROM starlight.floorplan WHERE id NOT IN (SELECT DISTINCT COALESCE(floorplan,0) FROM starlight.apt_unit);
DELETE FROM starlight.floorplan_counters WHERE id NOT IN (SELECT DISTINCT COALESCE(counters,0) FROM starlight.floorplan);

COMMIT;
        
