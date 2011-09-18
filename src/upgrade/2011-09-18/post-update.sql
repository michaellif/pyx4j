-- #$Id$

UPDATE AptUnit SET financial_marketRent = financial_unitRent;

UPDATE Media SET visibility = 'global';

UPDATE PropertyPhone SET visibility = 'global';

DELETE FROM FloorplanCounters;

INSERT INTO FloorplanCounters (ns, id, _unitCount) SELECT ns, floorplan, COUNT(*) FROM AptUnit WHERE floorplan IS NOT NULL GROUP BY ns, floorplan;

COMMIT;

