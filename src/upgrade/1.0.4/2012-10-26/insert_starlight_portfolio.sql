/**
***	==========================================================================
***	
***		Starlight portfolio import
***
***	==========================================================================
**/

-- Import of portfolios for Starlight

CREATE TABLE _dba_.tmp_portfolio
( 	property_code  			VARCHAR(25),
	analyst 			VARCHAR(50)
);

\COPY _dba_.tmp_portfolio FROM import/starlight_building_codes.csv CSV HEADER;

UPDATE _dba_.tmp_portfolio
SET	property_code = LOWER(TRIM(property_code)),
	analyst = TRIM(analyst);

UPDATE _dba_.tmp_portfolio
SET	property_code = regexp_replace(property_code,' ','','g');

CREATE OR REPLACE FUNCTION _dba_.insert_starlight_portfolio() RETURNS VOID AS
$$
DECLARE 
	v_portfolio_id			BIGINT;
	v_analyst			VARCHAR(50);
	-- v_building			BIGINT;
BEGIN
	FOR v_analyst IN 
	SELECT DISTINCT analyst FROM _dba_.tmp_portfolio
	LOOP
		SELECT INTO v_portfolio_id NEXTVAL('public.portfolio_seq');
		INSERT INTO starlight.portfolio (id,name) VALUES (v_portfolio_id,'Portfolio for '||v_analyst);

		INSERT INTO starlight.portfolio$buildings (id,owner,value,seq)
		(SELECT NEXTVAL('public.portfolio$buildings_seq'),v_portfolio_id,a.id,0
		FROM starlight.building a
		JOIN _dba_.tmp_portfolio b ON (LOWER(a.property_code) = b.property_code)
		WHERE b.analyst = v_analyst);
	END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;		

BEGIN TRANSACTION;

	SELECT _dba_.insert_starlight_portfolio();

COMMIT;

DROP TABLE  _dba_.tmp_portfolio;
DROP FUNCTION _dba_.insert_starlight_portfolio();


