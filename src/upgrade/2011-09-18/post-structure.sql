-- #$Id$

CREATE INDEX Floorplan_buildingIdx ON Floorplan (building);

CREATE INDEX AptUnit_belongsTo$floorplanIdx ON AptUnit (belongsTo, floorplan);

CREATE INDEX Parking_belongsToIdx ON Parking (belongsTo);

CREATE INDEX BuildingAmenity_belongsToIdx ON BuildingAmenity (belongsTo);

CREATE INDEX FloorplanAmenity_belongsToIdx ON FloorplanAmenity (belongsTo);

COMMIT;

