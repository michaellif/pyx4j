/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _expiring_ schema  - new
***
***     =====================================================================================================================
**/

SET client_min_messages = 'error';

BEGIN TRANSACTION;

CREATE SCHEMA _expiring_ AUTHORIZATION vista;
ALTER SCHEMA _expiring_ OWNER TO vista;

SET search_path = '_expiring_';

CREATE TABLE customer_credit_check_report
(
        id                      BIGINT                          NOT NULL,
        pmc                     BIGINT                          NOT NULL,
        created                 TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
        customer                BIGINT                          NOT NULL,
        data                    BYTEA,
                CONSTRAINT customer_credit_check_report_pk PRIMARY KEY(id)
);   

ALTER TABLE customer_credit_check_report OWNER TO vista;

CREATE TABLE customer_credit_check_report_no_backup
(
        id                      BIGINT                          NOT NULL,
        pmc                     BIGINT                          NOT NULL,
        created                 TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
        customer                BIGINT                          NOT NULL,
        data                    BYTEA,
                CONSTRAINT customer_credit_check_report_no_backup_pk PRIMARY KEY(id)
);   

ALTER TABLE customer_credit_check_report_no_backup OWNER TO vista;



COMMIT;

SET client_min_messages = 'notice';
