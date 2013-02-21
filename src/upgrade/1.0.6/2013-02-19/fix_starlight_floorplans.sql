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
        WHERE   id IN (SELECT DISTINCT floorplan FROM starlight.apt_unit)
        LOOP
                SELECT  MAX(id)
                INTO    v_floorplan_id
                FROM    starlight.floorplan
                WHERE   building = v_building
                AND     name = v_floorplan_name;
                
                IF NOT EXISTS ( SELECT 'x' FROM starlight.floorplan_amenity
                                WHERE floorplan = v_floorplan_id) 
                THEN
                        INSERT INTO starlight.floorplan_amenity (id,description,name,floorplan_type,floorplan,order_in_parent)
                        (SELECT nextval('public.floorplan_amenity_seq') AS id,description,name,floorplan_type,v_floorplan_id,order_in_parent
                        FROM starlight.floorplan_amenity
                        WHERE floorplan = (SELECT MIN(id) FROM starlight.floorplan WHERE building = v_building AND name = v_floorplan_name));
                END IF;
                           
                
                UPDATE  starlight.apt_unit
                SET     floorplan = v_floorplan_id
                WHERE   floorplan IN    (SELECT id FROM starlight.floorplan 
                                        WHERE   building = v_building
                                        AND     name = v_floorplan_name);
        END LOOP;       
END;
$$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.remove_extra_starlight_media() RETURNS VOID AS
$$
DECLARE 
        v_min_id        BIGINT;
        v_md5           VARCHAR(32);
BEGIN
        FOR v_min_id,v_md5 IN
        SELECT  MIN(id),md5(content)
        FROM    starlight.file_blob
        GROUP BY md5(content)
        HAVING COUNT(id) > 1
        LOOP
                UPDATE  starlight.media
                SET     media_file_blob_key = v_min_id
                WHERE   media_file_blob_key IN 
                (SELECT id FROM starlight.file_blob WHERE md5(content) = v_md5);
        END LOOP; 
       
        DELETE FROM starlight.file_blob
        WHERE id NOT IN (SELECT DISTINCT media_file_blob_key FROM starlight.media);
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

SELECT * FROM _dba_.fix_starlight_floorplans();

DELETE FROM starlight.floorplan_amenity WHERE floorplan NOT IN (SELECT DISTINCT floorplan FROM starlight.apt_unit);


DELETE FROM starlight.floorplan WHERE id NOT IN (SELECT DISTINCT floorplan FROM starlight.apt_unit);

-- ROLLBACK;
        
