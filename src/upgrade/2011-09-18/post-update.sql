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

