-- #$Id$

UPDATE AptUnit SET financial_marketRent = financial_unitRent;

UPDATE Media SET visibility = 'global';

UPDATE PropertyPhone SET visibility = 'global';

COMMIT;

