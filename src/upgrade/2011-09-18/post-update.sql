-- #$Id$

UPDATE AptUnit SET financial_marketRent = financial_unitRent;

UPDATE Media SET visibility = 'global';

UPDATE Media SET mediaFile_updated_timestamp = 1316459569 WHERE mediaFile_blobKey IS NOT NULL;

UPDATE PropertyPhone SET visibility = 'global';

DELETE FROM FloorplanCounters;

INSERT INTO FloorplanCounters (ns, id ) SELECT ns, id FROM Floorplan;

UPDATE Floorplan SET counters = Floorplan.id;

COMMIT;

UPDATE FloorplanCounters SET _unitCount = (SELECT COUNT(*) FROM AptUnit, Floorplan
     WHERE AptUnit.floorplan = Floorplan.id AND AptUnit.belongsTo = Floorplan.building AND FloorplanCounters.id = Floorplan.id);

COMMIT;

UPDATE FloorplanCounters SET _marketingUnitCount = (SELECT COUNT(*) FROM AptUnit, Floorplan Fa, Floorplan Fm
     WHERE AptUnit.floorplan = Fa.id AND AptUnit.belongsTo = Fm.building AND Fa.marketingName = Fm.marketingName AND Fa.building = Fm.building AND FloorplanCounters.id = Fm.id);


COMMIT;


INSERT INTO PhoneProvider(id,ns,name) VALUES (1,'star','Roges');
INSERT INTO PhoneProvider(id,ns,name) VALUES (2,'star','Bell');
INSERT INTO PhoneProvider(id,ns,name) VALUES (3,'star','Telus');
INSERT INTO PhoneProvider(id,ns,name) VALUES (4,'star','Fido');
INSERT INTO PhoneProvider(id,ns,name) VALUES (5,'star','Mobilicity');
INSERT INTO PhoneProvider(id,ns,name) VALUES (6,'star','Primus');
INSERT INTO PhoneProvider(id,ns,name) VALUES (7,'star','Télébec');
INSERT INTO PhoneProvider(id,ns,name) VALUES (8,'star','Virgin Mobile');
INSERT INTO PhoneProvider(id,ns,name) VALUES (9,'star','Wind Mobile');

COMMIT;