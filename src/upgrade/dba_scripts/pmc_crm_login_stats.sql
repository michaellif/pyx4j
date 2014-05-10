/**
***     ===========================================================================================================
***
***     @version $Revision$ ($Author$) $Date$
***
***     Query to get to know our non yardi-enabled customers better
***
***     ===========================================================================================================
**/

SELECT  ROUND(AVG(s.buildings),2) AS avg_buildings,
        ROUND(AVG(s.units),2) AS avg_units,
        ROUND(AVG(t.total_logins),2) AS avg_crm_logins,
        AVG(t.interested_for) AS avg_activity
FROM    _dba_.pmc_stats s
JOIN    _admin_.admin_pmc p ON (s.namespace = p.namespace)
JOIN    _admin_.admin_pmc_vista_features f ON (f.id = p.features)
JOIN    (SELECT namespace,MIN(created) AS first_crm_login,
                MAX(created) as last_crm_login,
                COUNT(created) AS total_logins,
                MAX(created) - MIN(created) AS interested_for
        FROM    _admin_.audit_record
        WHERE   event = 'Login'
        AND     user_type = 'crm'
        GROUP BY namespace) AS t ON (s.namespace = t.namespace)
WHERE NOT f.yardi_integration;

select * from  _admin_.Global_Crm_User_Index

-- CRM
--! support@propertyvista.com

-- 208.97.73.123 propertyvista-office
-- 173.206.185.62 propertyvista-office
-- 173.206.186.94 starlight-office

--! support

SELECT DATE_PART('hour', created) hourOfDay, COUNT(created) AS total_logins
	FROM _admin_.audit_record
 WHERE event = 'Login'
   AND namespace != '_admin_'
   AND user_type = 'crm'
   AND usr NOT IN (SELECT id FROM greenwin.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM berkley.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM realstar.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM larlyn.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM cogir.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM dms.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM pangroup.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM propertyvistatest.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM gateway.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM metcap.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM sterling.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM ofm.crm_user WHERE email = 'support@propertyvista.com' )
   AND remote_addr NOT IN (SELECT DISTINCT remote_addr FROM _admin_.audit_record WHERE namespace = '_admin_' and app ='operations' and event = 'Login')
   AND DATE_PART('day', created) BETWEEN 10 and 18
 GROUP BY DATE_PART('hour', created)
 ORDER BY DATE_PART('hour', created);

SELECT DATE_PART('hour', created) hourOfDay, COUNT(created) AS total_logins
	FROM _admin_.audit_record
 WHERE event = 'Login'
   AND namespace != '_admin_'
   AND user_type = 'crm'
   AND usr IN (SELECT crm_user FROM _admin_.Global_Crm_User_Index)
   AND DATE_PART('day', created) BETWEEN 10 and 18
 GROUP BY DATE_PART('hour', created)
 ORDER BY DATE_PART('hour', created);


SELECT DATE_PART('day', created) dayOfMonth, COUNT(created) AS total_logins
	FROM _admin_.audit_record
 WHERE event = 'Login'
   AND namespace != '_admin_'
   AND user_type = 'crm'
   AND usr IN (SELECT crm_user FROM _admin_.Global_Crm_User_Index)
 GROUP BY DATE_PART('day', created)
 ORDER BY DATE_PART('day', created);

-- only support

SELECT DATE_PART('hour', created) hourOfDay, COUNT(created) AS total_logins
	FROM _admin_.audit_record
 WHERE event = 'Login'
   AND namespace != '_admin_'
   AND user_type = 'crm'
   AND (usr IN (SELECT id FROM greenwin.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM berkley.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM realstar.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM larlyn.crm_user WHERE email = 'support@propertyvista.com' UNION
                    SELECT id FROM ofm.crm_user WHERE email = 'support@propertyvista.com' )
   OR remote_addr IN (SELECT DISTINCT remote_addr FROM _admin_.audit_record WHERE namespace = '_admin_' and app ='operations' and event = 'Login'))
 GROUP BY DATE_PART('hour', created)
 ORDER BY DATE_PART('hour', created);

-- Tenants

SELECT DATE_PART('hour', created) hourOfDay, COUNT(created) AS total_logins
	FROM _admin_.audit_record
 WHERE event = 'Login'
   AND namespace != '_admin_'
   AND user_type = 'customer'
 GROUP BY DATE_PART('hour', created)
 ORDER BY DATE_PART('hour', created);

SELECT DATE_PART('day', created) dayOfMonth, COUNT(created) AS total_logins
	FROM _admin_.audit_record
 WHERE event = 'Login'
   AND namespace != '_admin_'
   AND user_type = 'customer'
 GROUP BY DATE_PART('day', created)
 ORDER BY DATE_PART('day', created);

-- Mid Month

SELECT DATE_PART('hour', created) hourOfDay, COUNT(created) AS total_logins
	FROM _admin_.audit_record
 WHERE event = 'Login'
   AND namespace != '_admin_'
   AND user_type = 'customer'
   AND DATE_PART('day', created) BETWEEN 10 and 18
 GROUP BY DATE_PART('hour', created)
 ORDER BY DATE_PART('hour', created);

------ Setup Payments

SELECT DATE_PART('hour', created) hourOfDay, COUNT(created) AS total_logins
	FROM _admin_.audit_record
 WHERE entity_class IN ('AutopayAgreement', 'LeasePaymentMethod')
   AND namespace != '_admin_'
   AND user_type = 'customer'
 GROUP BY DATE_PART('hour', created)
 ORDER BY DATE_PART('hour', created);

SELECT DATE_PART('day', created) dayOfMonth, COUNT(created) AS total_logins
	FROM _admin_.audit_record
 WHERE entity_class IN ('AutopayAgreement', 'LeasePaymentMethod')
   AND namespace != '_admin_'
   AND user_type = 'customer'
 GROUP BY DATE_PART('day', created)
 ORDER BY DATE_PART('day', created);


--  Setup Payments Mid Month
SELECT DATE_PART('hour', created) hourOfDay, COUNT(created) AS total_logins
	FROM _admin_.audit_record
 WHERE entity_class IN ('AutopayAgreement', 'LeasePaymentMethod')
   AND namespace != '_admin_'
   AND user_type = 'customer'
   AND DATE_PART('day', created) BETWEEN 10 and 18
 GROUP BY DATE_PART('hour', created)
 ORDER BY DATE_PART('hour', created);

--  Setup Payments Hot Days
SELECT DATE_PART('hour', created) hourOfDay, COUNT(created) AS total_logins
	FROM _admin_.audit_record
 WHERE entity_class IN ('AutopayAgreement', 'LeasePaymentMethod')
   AND namespace != '_admin_'
   AND user_type = 'customer'
   AND (DATE_PART('day', created) BETWEEN 1 and 9) or  (DATE_PART('day', created) BETWEEN 25 and 31)
 GROUP BY DATE_PART('hour', created)
 ORDER BY DATE_PART('hour', created);