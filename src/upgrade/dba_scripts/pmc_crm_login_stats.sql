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


