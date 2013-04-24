CREATE TABLE _admin_.test_yardi_eft
(
        batch_id                VARCHAR(50),
        client_id               VARCHAR(50),
        amount                  NUMERIC(18,2),
        bank_id                 VARCHAR(3),
        branch_transit_number   VARCHAR(5),
        account_number          VARCHAR(12)
);

ALTER TABLE _admin_.test_yardi_eft OWNER TO vista;

CREATE TABLE _dba_.tmp_yardi_eft
(
        amount                  NUMERIC(18,2),
        bank_id                 VARCHAR(3),
        transit_no              VARCHAR(5),
        transit_code            VARCHAR(3),
        account_no              VARCHAR(20),
        tenant_name             VARCHAR(120),
        tenant_id               VARCHAR(50),
        unit_code               VARCHAR(50),
        report_id               VARCHAR(120)
);

SET client_encoding TO 'latin1';

COPY _dba_.tmp_yardi_eft FROM '/home/akinareevski/greenwin_apr23.txt' DELIMITERS E'\t' CSV HEADER;

SET client_encoding TO 'utf8';

INSERT INTO _admin_.test_yardi_eft (batch_id,client_id,amount,bank_id,branch_transit_number,account_number)
(SELECT 'PVBERK23',tenant_id,amount,bank_id,transit_no,account_no
FROM   _dba_.tmp_yardi_eft);

CREATE VIEW _admin_.berkd2v1_transactions AS
(SELECT l.lease_id AS client_id,
        pr.amount,pmd.bank_id,pmd.branch_transit_number,
        pmd.account_no_number AS account_number
FROM    berkd2v1.payment_record pr
JOIN    berkd2v1.payment_method pm ON (pm.id = pr.payment_method)
JOIN    berkd2v1.payment_payment_details pmd ON (pm.details = pmd.id)
JOIN    berkd2v1.lease_term_participant ltp ON (ltp.id = pr.lease_term_participant)
JOIN    berkd2v1.lease_participant lp ON (lp.id = ltp.lease_participant)
JOIN    berkd2v1.lease l ON (lp.lease = l.id)
);  

SELECT y.*,'YARDI' 
FROM (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE batch_id = 'PVBERK23'
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.berkd2v1_transactions) AS y
UNION
SELECT v.*,'VISTA' 
FROM (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.berkd2v1_transactions
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE batch_id = 'PVBERK23') AS v 
ORDER BY 1,5,6,2;
;   

SELECT  CASE WHEN a.client_id IS NULL THEN b.client_id ELSE a.client_id END AS client_id,
        a.amount AS yardi_amount,
        b.amount AS vista_amount,
        CASE WHEN a.bank_id IS NULL THEN b.bank_id ELSE a.bank_id END AS bank_id,
        CASE WHEN a.branch_transit_number IS NULL THEN b.branch_transit_number ELSE a.branch_transit_number END AS branch_transit_number,
        CASE WHEN a.account_number IS NULL THEN b.account_number ELSE a.account_number END AS account_number             
FROM (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE batch_id = 'PVBERK23'
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.berkd2v1_transactions) AS a
FULL OUTER JOIN (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.berkd2v1_transactions
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE batch_id = 'PVBERK23') b 
ON   (a.client_id = b.client_id AND a.bank_id = b.bank_id AND a.branch_transit_number = b.branch_transit_number AND a.account_number = b.account_number) 
ORDER BY 1,5,6,2; 


/**
 GREENWIN
**/

INSERT INTO _admin_.test_yardi_eft (batch_id,client_id,amount,bank_id,branch_transit_number,account_number)
(SELECT 'PVGRN23',tenant_id,amount,bank_id,transit_no,account_no
FROM   _dba_.tmp_yardi_eft
WHERE report_id ~ 'pvgrn');

CREATE VIEW _admin_.greenwin_transactions AS
(SELECT l.lease_id AS client_id,
        pr.amount,pmd.bank_id,pmd.branch_transit_number,
        pmd.account_no_number AS account_number
FROM    greenwin.payment_record pr
JOIN    greenwin.payment_method pm ON (pm.id = pr.payment_method)
JOIN    greenwin.payment_payment_details pmd ON (pm.details = pmd.id)
JOIN    greenwin.lease_term_participant ltp ON (ltp.id = pr.lease_term_participant)
JOIN    greenwin.lease_participant lp ON (lp.id = ltp.lease_participant)
JOIN    greenwin.lease l ON (lp.lease = l.id)
);  

SELECT y.*,'YARDI' 
FROM (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE   batch_id = 'PVGRN23'
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.greenwin_transactions) AS y
UNION
SELECT v.*,'VISTA' 
FROM (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.greenwin_transactions
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE   batch_id = 'PVGRN23') AS v 
ORDER BY 1,5,6,2;
;   

SELECT  CASE WHEN a.client_id IS NULL THEN b.client_id ELSE a.client_id END AS client_id,
        a.amount AS yardi_amount,
        b.amount AS vista_amount,
        CASE WHEN a.bank_id IS NULL THEN b.bank_id ELSE a.bank_id END AS bank_id,
        CASE WHEN a.branch_transit_number IS NULL THEN b.branch_transit_number ELSE a.branch_transit_number END AS branch_transit_number,
        CASE WHEN a.account_number IS NULL THEN b.account_number ELSE a.account_number END AS account_number             
FROM (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE   batch_id = 'PVGRN23'
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.greenwin_transactions) AS a
FULL OUTER JOIN (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.greenwin_transactions
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE   batch_id = 'PVGRN23') b 
ON   (a.client_id = b.client_id AND a.bank_id = b.bank_id AND a.branch_transit_number = b.branch_transit_number AND a.account_number = b.account_number) 
ORDER BY 1,5,6,2; 


/**
*** PANGROUP
**/

TRUNCATE TABLE _dba_.tmp_yardi_eft;

SET client_encoding TO 'latin1';

COPY _dba_.tmp_yardi_eft FROM '/home/akinareevski/pan_trans_report.txt' DELIMITERS E'\t' CSV HEADER;

SET client_encoding TO 'utf8';

INSERT INTO _admin_.test_yardi_eft (batch_id,client_id,amount,bank_id,branch_transit_number,account_number)
(SELECT 'PAN24',tenant_id,amount,bank_id,transit_no,account_no
FROM   _dba_.tmp_yardi_eft
WHERE report_id ~ 'ppan');

CREATE VIEW _admin_.pangrv1_transactions AS
(SELECT l.lease_id AS client_id,
        pr.amount,pmd.bank_id,pmd.branch_transit_number,
        pmd.account_no_number AS account_number
FROM    pangrv1.payment_record pr
JOIN    pangrv1.payment_method pm ON (pm.id = pr.payment_method)
JOIN    pangrv1.payment_payment_details pmd ON (pm.details = pmd.id)
JOIN    pangrv1.lease_term_participant ltp ON (ltp.id = pr.lease_term_participant)
JOIN    pangrv1.lease_participant lp ON (lp.id = ltp.lease_participant)
JOIN    pangrv1.lease l ON (lp.lease = l.id)
);  

SELECT y.*,'YARDI' 
FROM (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE   batch_id = 'PAN24'
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.pangrv1_transactions) AS y
UNION
SELECT v.*,'VISTA' 
FROM (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.pangrv1_transactions
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE   batch_id = 'PAN24') AS v 
ORDER BY 1,5,6,2;
;   

SELECT  CASE WHEN a.client_id IS NULL THEN b.client_id ELSE a.client_id END AS client_id,
        a.amount AS yardi_amount,
        b.amount AS vista_amount,
        CASE WHEN a.bank_id IS NULL THEN b.bank_id ELSE a.bank_id END AS bank_id,
        CASE WHEN a.branch_transit_number IS NULL THEN b.branch_transit_number ELSE a.branch_transit_number END AS branch_transit_number,
        CASE WHEN a.account_number IS NULL THEN b.account_number ELSE a.account_number END AS account_number             
FROM (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE   batch_id = 'PAN24'
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.pangrv1_transactions) AS a
FULL OUTER JOIN (SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.pangrv1_transactions
        EXCEPT
        SELECT  client_id,amount,bank_id,branch_transit_number,account_number
        FROM    _admin_.test_yardi_eft
        WHERE   batch_id = 'PAN24') b 
ON   (a.client_id = b.client_id AND a.bank_id = b.bank_id AND a.branch_transit_number = b.branch_transit_number AND a.account_number = b.account_number) 
ORDER BY 1,5,6,2; 

  
