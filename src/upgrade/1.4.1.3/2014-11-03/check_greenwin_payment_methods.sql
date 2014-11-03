/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Check greenwin payment_methods
***
***     ===========================================================================================================
**/  

\i tmp_tenant_data.sql

SELECT  c.id AS customer, 
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        c.person_email AS email,
        pm.creation_date, pm.created_by, pm.is_deleted,
        pm.id AS payment_method
FROM    greenwin.customer c
JOIN    _dba_.tmp_tenant_data t ON (c.id = t.customer_id)
JOIN    greenwin.payment_method pm ON (c.id = pm.customer)
WHERE   created_by = 209 
AND     DATE_TRUNC('day', creation_date) = '29-OCT-2014';



DROP TABLE _dba_.tmp_tenant_data;                                                   
