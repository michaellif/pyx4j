mysql -u vista_star --password=vista_star -D vista_star


##----------- REMOVAL SQL  ---------------

CREATE TABLE mig_floorplan (id bigint(20), building bigint(20) DEFAULT NULL);

INSERT INTO mig_floorplan (id, building )
SELECT Floorplan.id, Floorplan.building
  FROM Floorplan, Building
 WHERE Floorplan.building = Building.id
   AND Building.propertyCode IN ("0836talw", "0068hill", "1211good", "4141bath");

SELECT Floorplan.name, Building.propertyCode, Floorplan.bedrooms, Floorplan.dens, Floorplan.bathrooms
  FROM Floorplan, Building
 WHERE Floorplan.building = Building.id
   AND Floorplan.id IN  (SELECT id from mig_floorplan)
 ORDER BY Building.propertyCode, Floorplan.name;

SELECT AptUnit.info_unitNumber, AptUnit.availableForRent, Floorplan.name, Building.propertyCode
  FROM Floorplan, Building, AptUnit
 WHERE Floorplan.building = Building.id
   AND AptUnit.floorplan = Floorplan.id
   AND Floorplan.id IN  (SELECT id from mig_floorplan)
 ORDER BY Building.propertyCode, Floorplan.name;

##--------- virification --------------

DELETE FROM AptUnit WHERE floorplan IN (SELECT id from mig_floorplan);

DELETE FROM FloorplanAmenity WHERE belongsTo IN (SELECT id from mig_floorplan);

DELETE FROM Floorplan WHERE id IN (SELECT id FROM mig_floorplan);

COMMIT;

DROP TABLE mig_floorplan;

