-- #$Id$

CREATE INDEX Floorplan_buildingIdx ON Floorplan (building);

CREATE INDEX AptUnit_belongsTo$floorplanIdx ON AptUnit (belongsTo, floorplan);

CREATE INDEX AptUnit_floorplanIdx ON AptUnit (floorplan);

CREATE INDEX Parking_belongsToIdx ON Parking (belongsTo);

CREATE INDEX BuildingAmenity_belongsToIdx ON BuildingAmenity (belongsTo);

CREATE INDEX FloorplanAmenity_belongsToIdx ON FloorplanAmenity (belongsTo);

CREATE INDEX Building$media_ownerIdx ON Building$media (owner);

CREATE INDEX Floorplan$media_ownerIdx ON Floorplan$media (owner);

CREATE INDEX AptUnit$marketing_adBlurbs_ownerIdx ON AptUnit$marketing_adBlurbs (owner);

COMMIT;

