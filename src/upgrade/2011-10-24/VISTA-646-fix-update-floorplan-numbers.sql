-- #$Id$

UPDATE Floorplan SET dens = 0 WHERE dens IS NULL;
UPDATE Floorplan SET bathrooms = 0 WHERE bathrooms IS NULL;
UPDATE Floorplan SET bedrooms = 0 WHERE bedrooms IS NULL;

COMMIT;
