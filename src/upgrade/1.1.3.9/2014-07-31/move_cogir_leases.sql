/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Move cogir leases to a new building 
***
***     =====================================================================================================================
**/

DROP TABLE IF EXISTS _dba_.cogir_move;

CREATE TABLE _dba_.cogir_move
(
    property_code               VARCHAR(24),
    lease_id                    VARCHAR(24),
    unit_num                    VARCHAR(24)
);

SET client_encoding TO 'latin1';

COPY _dba_.cogir_move FROM '/home/akinareevski/import/cogir_move1.csv' DELIMITERS ',' CSV HEADER;
COPY _dba_.cogir_move FROM '/home/akinareevski/import/cogir_move2.csv' DELIMITERS ',' CSV HEADER;

SET client_encoding TO 'utf8';

UPDATE  _dba_.cogir_move
SET     unit_num = '0'||unit_num
WHERE   unit_num !~ '^0';


BEGIN TRANSACTION;

    WITH t AS   (SELECT DISTINCT    b.property_code,a.id AS unit, 
                                    a.info_unit_number, t.lease_id,
                                    t.unit_num
                 FROM       cogir.building b
                 JOIN       cogir.apt_unit a ON (b.id = a.building)
                 JOIN       _dba_.cogir_move t ON (t.property_code = b.property_code 
                                                    AND t.unit_num = a.info_unit_number))
    UPDATE  cogir.lease AS l
    SET     unit = t.unit
    FROM    t
    WHERE   l.lease_id = t.lease_id;
    
COMMIT;

DROP TABLE IF EXISTS _dba_.cogir_move;
    

    
