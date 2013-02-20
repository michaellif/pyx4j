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
                ELSE name  END
        WHERE   id IN (SELECT DISTINCT floorplan FROM starlight.apt_unit);
        
        
        FOR v_building,v_floorplan_name IN
        SELECT  DISTINCT building,name 
        FROM    starlight.floorplan
        WHERE   id IN (SELECT DISTINCT floorplan FROM starlight.apt_unit)
        LOOP
                SELECT  MIN(id)
                INTO    v_floorplan_id
                FROM    starlight.floorplan
                WHERE   building = v_building
                AND     name = v_floorplan_name;
                
                UPDATE  starlight.apt_unit
                SET     floorplan = v_floorplan_id
                WHERE   floorplan IN    (SELECT id FROM starlight.floorplan 
                                        WHERE   building = v_building
                                        AND     name = v_floorplan_name);
        END LOOP;       
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

ALTER TABLE starlight.apt_unit ADD COLUMN orig_floorplan BIGINT;

UPDATE  starlight.apt_unit
SET     orig_floorplan = floorplan;

SELECT * FROM _dba_.fix_starlight_floorplans();


-- ROLLBACK;
        
