-- #$Id$

UPDATE AptUnit SET financial_marketRent = financial_unitRent;

UPDATE Media SET visibility = 'global';

UPDATE Media SET mediaFile_updated_timestamp = 1316459569 WHERE mediaFile_blobKey IS NOT NULL;

UPDATE PropertyPhone SET visibility = 'global';

DELETE FROM FloorplanCounters;

INSERT INTO FloorplanCounters (ns, id, _unitCount) SELECT ns, floorplan, COUNT(*) FROM AptUnit WHERE floorplan IS NOT NULL GROUP BY ns, floorplan;

COMMIT;

UPDATE FloorplanCounters SET _marketingUnitCount = (SELECT COUNT(*) FROM AptUnit, Floorplan Fa, Floorplan Fm
     WHERE AptUnit.floorplan = Fa.id AND AptUnit.belongsTo = Fm.building AND Fa.marketingName = Fm.marketingName AND Fa.building = Fm.building AND FloorplanCounters.id = Fm.id);

UPDATE Floorplan SET counters = Floorplan.id;

COMMIT;

