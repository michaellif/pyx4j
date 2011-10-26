mysql -u vista_star --password=vista_star -D vista_star

## verification SQL

SELECT COUNT(*), Floorplan.name, Building.propertyCode
  FROM Floorplan, Building WHERE Floorplan.building = Building.id
 GROUP BY Floorplan.name, Building.propertyCode HAVING COUNT(*) > 1;

#this one is broken
SELECT A.info_unitNumber, F.name, B.propertyCode
   FROM AptUnit A, Floorplan F, Building B
 WHERE A.belongsTo = B.id
   AND A.floorplan = F.id
   AND F.building = B.id
   AND F.name IN (SELECT Floorplan.name from Floorplan GROUP BY Floorplan.name, Floorplan.building HAVING COUNT(*) > 1)
   AND F.building IN (SELECT Floorplan.building from Floorplan GROUP BY Floorplan.name, Floorplan.building HAVING COUNT(*) > 1);


SELECT MAX(Floorplan.id), Floorplan.name, Floorplan.building
  FROM Floorplan
 GROUP BY Floorplan.name, Floorplan.building
HAVING COUNT(*) > 1;

##----------- REMOVAL SQL  ---------------

CREATE TABLE mig_floorplan (id bigint(20), building bigint(20) DEFAULT NULL);

INSERT INTO mig_floorplan (id, building )
SELECT MAX(Floorplan.id), Floorplan.building
  FROM Floorplan
 GROUP BY Floorplan.name, Floorplan.building
HAVING COUNT(*) > 1;

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
CREATE TABLE mig_floorplan_remain (id bigint(20), building bigint(20) DEFAULT NULL);

INSERT INTO mig_floorplan_remain (id, building )
SELECT MIN(Floorplan.id), Floorplan.building
  FROM Floorplan
 GROUP BY Floorplan.name, Floorplan.building
HAVING COUNT(*) > 1;

COMMIT;

SELECT Floorplan.name, Building.propertyCode, Floorplan.bedrooms, Floorplan.dens, Floorplan.bathrooms
  FROM Floorplan, Building
 WHERE Floorplan.building = Building.id
   AND Floorplan.id IN  (SELECT id from mig_floorplan_remain)
 ORDER BY Building.propertyCode, Floorplan.name;

SELECT AptUnit.info_unitNumber, AptUnit.availableForRent, Floorplan.name, Building.propertyCode
  FROM Floorplan, Building, AptUnit
 WHERE Floorplan.building = Building.id
   AND AptUnit.floorplan = Floorplan.id
   AND Floorplan.id IN  (SELECT id FROM mig_floorplan_remain)
 ORDER BY Building.propertyCode, Floorplan.name;


DELETE FROM AptUnit WHERE floorplan IN (SELECT id from mig_floorplan);

DELETE FROM FloorplanAmenity WHERE belongsTo IN (SELECT id from mig_floorplan);

CREATE TABLE mig_rm_media(id bigint(20), blobKey  bigint(20));

INSERT INTO mig_rm_media (id) (SELECT value FROM Floorplan$media WHERE owner IN (SELECT id FROM mig_floorplan));

COMMIT;

UPDATE mig_rm_media SET blobKey = (SELECT mediaFile_blobKey FROM Media WHERE id = mig_rm_media.id);

COMMIT;

DELETE FROM FileBlob WHERE id IN (SELECT blobKey FROM mig_rm_media WHERE blobKey IS NOT NULL);

DELETE FROM ThumbnailBlob WHERE id IN  (SELECT blobKey FROM mig_rm_media WHERE blobKey IS NOT NULL);

DELETE FROM Media WHERE id IN (SELECT id FROM mig_rm_media);

DELETE FROM Floorplan$media  WHERE owner IN (SELECT id FROM mig_floorplan);

DELETE FROM Floorplan WHERE id IN (SELECT id FROM mig_floorplan);

COMMIT;

DROP TABLE mig_rm_media;
DROP TABLE mig_floorplan;
DROP TABLE mig_floorplan_remain;

#-------- units
SELECT AptUnit.info_unitNumber, AptUnit.availableForRent, Floorplan.name, Building.propertyCode
  FROM AptUnit, Floorplan, Building
 WHERE Floorplan.building = Building.id
   AND AptUnit.belongsTo = Building.id
   AND AptUnit.floorplan = Floorplan.id
 GROUP BY AptUnit.info_unitNumber, AptUnit.belongsTo
HAVING COUNT(*) > 1;

CREATE TABLE mig_AptUnit (id bigint(20));
CREATE TABLE mig_AptUnit_remain (id bigint(20));

INSERT INTO mig_AptUnit (id )
SELECT MAX(AptUnit.id)
  FROM AptUnit
 GROUP BY AptUnit.info_unitNumber, AptUnit.belongsTo
HAVING COUNT(*) > 1;

INSERT INTO mig_AptUnit_remain (id )
SELECT MIN(AptUnit.id)
  FROM AptUnit
 GROUP BY AptUnit.info_unitNumber, AptUnit.belongsTo
HAVING COUNT(*) > 1;

SELECT AptUnit.info_unitNumber, AptUnit.availableForRent, Floorplan.name, Building.propertyCode
  FROM AptUnit, Floorplan, Building
 WHERE AptUnit.belongsTo = Building.id
   AND AptUnit.floorplan = Floorplan.id
   AND AptUnit.id IN (SELECT id FROM mig_AptUnit)
 ORDER BY Building.propertyCode, Floorplan.name, AptUnit.info_unitNumber;

SELECT AptUnit.info_unitNumber, AptUnit.availableForRent, Floorplan.name, Building.propertyCode
  FROM AptUnit, Floorplan, Building
 WHERE AptUnit.belongsTo = Building.id
   AND AptUnit.floorplan = Floorplan.id
   AND AptUnit.id IN (SELECT id FROM mig_AptUnit_remain)
 ORDER BY Building.propertyCode, Floorplan.name, AptUnit.info_unitNumber;

DELETE FROM AptUnit WHERE id IN (SELECT id from mig_AptUnit);
COMMIT;