/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Per-building statistics functions
***
***     ======================================================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.gather_building_stats(v_schema_name TEXT) RETURNS VOID
AS
$$
DECLARE 
        v_this_week     DATE := DATE_TRUNC('week',current_date);
BEGIN

        -- Create temporary unlogged table - much easier in the long run

        IF NOT EXISTS ( SELECT 'x' FROM pg_class c 
                        JOIN pg_namespace n ON  (n.oid = c.relnamespace)
                        WHERE   n.nspname = '_dba_'
                        AND     c.relname = 'tmp_stats'
                        AND     c.relkind = 'r')
        THEN
                
                EXECUTE 'CREATE UNLOGGED TABLE _dba_.tmp_stats AS ( '
                        ||'SELECT       b.property_code, a.id AS unit_id,'
                        ||'             l.lease_id, l.status AS lease_status,'
                        ||'             lp.id AS lease_participant,'
                        ||'             c.registered_in_portal,cu.id AS customer_user,'
                        ||'             p.id AS payment_id, p.payment_status,p.amount,'
                        ||'             p.last_status_change_date, p.finalize_date,'
                        ||'             pm.payment_type,pm.id_discriminator AS pm_discriminator,'
                        ||'             pm.is_deleted AS pm_deleted,'
                        ||'             ppd.card_type, '
                        ||'             pap.id AS pap_id, pap.is_deleted AS pap_deleted '
                        ||'FROM '||v_schema_name||'.building b '
                        ||'JOIN '||v_schema_name||'.apt_unit a ON (b.id = a.building) '
                        ||'LEFT JOIN    '||v_schema_name||'.lease l ON (a.id = l.unit) '
                        ||'LEFT JOIN    '||v_schema_name||'.lease_participant lp ON (l.id = lp.lease) '
                        ||'LEFT JOIN    '||v_schema_name||'.lease_term_participant ltp ON (lp.id = ltp.lease_participant) '
                        ||'LEFT JOIN    '||v_schema_name||'.customer c ON (c.id = lp.customer) '
                        ||'LEFT JOIN    '||v_schema_name||'.customer_user cu ON (cu.id = c.user_id) '
                        ||'LEFT JOIN    '||v_schema_name||'.payment_record p ON (ltp.id = p.lease_term_participant) '
                        ||'LEFT JOIN    '||v_schema_name||'.payment_method pm ON (pm.id = p.payment_method) '
                        ||'LEFT JOIN    '||v_schema_name||'.payment_payment_details ppd ON (pm.details = ppd.id) '
                        ||'LEFT JOIN    '||v_schema_name||'.autopay_agreement pap ON (pap.id = p.preauthorized_payment) '
                        ||'WHERE NOT    b.suspended )'; 
                       
                -- Insert building info and total units
                
                INSERT INTO _dba_.building_stats (pmc, stats_week, property_code, total_units)
                (SELECT         v_schema_name AS pmc, v_this_week AS stats_week,
                                t.property_code, t.total_units
                FROM    (SELECT a.property_code, COUNT(a.unit_id) AS total_units
                        FROM    (SELECT DISTINCT property_code,unit_id
                                FROM    _dba_.tmp_stats) AS a
                        GROUP BY a.property_code) AS t);
        
        
                -- Update active leases
                
                UPDATE  _dba_.building_stats AS s
                SET     active_leases = t.active_leases
                FROM    (SELECT a.property_code, COUNT(a.lease_id) AS active_leases
                        FROM    (SELECT DISTINCT property_code,lease_id 
                                FROM    _dba_.tmp_stats 
                                WHERE   lease_status = 'Active') AS a
                        GROUP BY a.property_code) AS t
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;       
            
                
                -- Registered units
                
                UPDATE  _dba_.building_stats AS s
                SET     reg_units = COALESCE(t.reg_units,0)
                FROM    (SELECT a.property_code, COUNT(a.unit_id) AS reg_units
                        FROM    (SELECT DISTINCT property_code, unit_id
                                FROM    _dba_.tmp_stats 
                                WHERE   lease_status = 'Active'
                                AND     registered_in_portal) AS a
                        GROUP BY a.property_code) AS t
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;
                
           
                -- Units with epay
                
                UPDATE  _dba_.building_stats AS s
                SET     units_epay = t.units_epay
                FROM    (SELECT a.property_code, COUNT(a.unit_id) AS units_epay
                        FROM    (SELECT DISTINCT property_code, unit_id
                                FROM    _dba_.tmp_stats 
                                WHERE   pm_discriminator = 'LeasePaymentMethod'
                                AND     payment_type IN ('DirectBanking','CreditCard','Echeck','Interac')
                                AND NOT pm_deleted
                                AND     lease_status = 'Active') AS a
                        GROUP BY a.property_code) AS t
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;
                
                
                -- Registered Units with epay
                
                UPDATE  _dba_.building_stats AS s
                SET     reg_units_epay = t.reg_units_epay
                FROM    (SELECT a.property_code, COUNT(a.unit_id) AS reg_units_epay
                        FROM    (SELECT DISTINCT property_code, unit_id
                                FROM    _dba_.tmp_stats 
                                WHERE   pm_discriminator = 'LeasePaymentMethod'
                                AND     payment_type IN ('DirectBanking','CreditCard','Echeck','Interac')
                                AND NOT pm_deleted
                                AND     registered_in_portal
                                AND     lease_status = 'Active') AS a
                        GROUP BY a.property_code) AS t
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;
                
   
                 -- Total tenants
                
                UPDATE  _dba_.building_stats AS s
                SET     total_tenants = t.total_tenants
                FROM    (SELECT a.property_code, COUNT(a.lease_participant) AS total_tenants
                        FROM    (SELECT DISTINCT property_code, lease_participant
                                FROM    _dba_.tmp_stats 
                                WHERE   lease_status = 'Active') AS a
                        GROUP BY a.property_code) AS t
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;
                
  
                 -- Registered tenants
                
                UPDATE  _dba_.building_stats AS s
                SET     reg_tenants = t.reg_tenants
                FROM    (SELECT a.property_code, COUNT(a.lease_participant) AS reg_tenants
                        FROM    (SELECT DISTINCT property_code, lease_participant
                                FROM    _dba_.tmp_stats 
                                WHERE   lease_status = 'Active'
                                AND     registered_in_portal) AS a
                        GROUP BY a.property_code) AS t
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;
                
     
                
                -- Tenants with epay
                
                UPDATE  _dba_.building_stats AS s
                SET     tenants_epay = t.tenants_epay
                FROM    (SELECT a.property_code, COUNT(a.lease_participant) AS tenants_epay
                        FROM    (SELECT DISTINCT property_code, lease_participant
                                FROM    _dba_.tmp_stats 
                                WHERE   pm_discriminator = 'LeasePaymentMethod'
                                AND     payment_type IN ('DirectBanking','CreditCard','Echeck','Interac')
                                AND NOT pm_deleted
                                AND     lease_status = 'Active') AS a
                        GROUP BY a.property_code) AS t
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;
                
      
                -- Registered Tenants with epay
                
                UPDATE  _dba_.building_stats AS s
                SET     reg_tenants_epay = t.reg_tenants_epay
                FROM    (SELECT a.property_code, COUNT(a.lease_participant) AS reg_tenants_epay
                        FROM    (SELECT DISTINCT property_code, lease_participant
                                FROM    _dba_.tmp_stats 
                                WHERE   pm_discriminator = 'LeasePaymentMethod'
                                AND     payment_type IN ('DirectBanking','CreditCard','Echeck','Interac')
                                AND NOT pm_deleted
                                AND     registered_in_portal
                                AND     lease_status = 'Active' ) AS a
                        GROUP BY a.property_code) AS t
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;
                
     
                
                -- Tenants logins this week
                
                UPDATE  _dba_.building_stats AS s
                SET     tenant_login_week = t.tenant_login_week
                FROM    (SELECT t.property_code, COUNT(a.id) AS tenant_login_week
                        FROM    (SELECT DISTINCT property_code,customer_user FROM _dba_.tmp_stats) t
                        JOIN    _admin_.audit_record a ON (a.usr = t.customer_user)
                        WHERE   a.namespace = v_schema_name
                        AND     a.event = 'Login'
                        AND     DATE_TRUNC('week',a.created) = v_this_week 
                        AND     a.user_type = 'customer'
                        GROUP BY t.property_code) AS t
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;
                
     
                
                -- Tenants logins this month
                
                UPDATE  _dba_.building_stats AS s
                SET     tenant_login_month = t.tenant_login_month
                FROM    (SELECT t.property_code, COUNT(a.id) AS tenant_login_month
                        FROM    (SELECT DISTINCT property_code,customer_user FROM _dba_.tmp_stats) t
                        JOIN    _admin_.audit_record a ON (a.usr = t.customer_user)
                        WHERE   a.namespace = v_schema_name
                        AND     a.event = 'Login'
                        AND     DATE_TRUNC('month',a.created) = DATE_TRUNC('month',v_this_week) 
                        AND     a.user_type = 'customer'
                        GROUP BY t.property_code) AS t
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;
                
             
                -- Total transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_trans_all = t.count_trans_all,
                        amount_trans_all = t.amount_trans_all
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_trans_all, 
                                SUM(amount) AS amount_trans_all
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type IN ('DirectBanking','CreditCard','Echeck','Interac')
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
    
                -- Recurring transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_trans_recur = t.count_trans_recur,
                        amount_trans_recur = t.amount_trans_recur
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_trans_recur, 
                                SUM(amount) AS amount_trans_recur
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type IN ('DirectBanking','CreditCard','Echeck','Interac')
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
     
                -- Recurring transactions - count and amount : registered tenants
                
                UPDATE  _dba_.building_stats AS s
                SET     count_trans_recur_reg = t.count_trans_recur_reg,
                        amount_trans_recur_reg = t.amount_trans_recur_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_trans_recur_reg, 
                                SUM(amount) AS amount_trans_recur_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type IN ('DirectBanking','CreditCard','Echeck','Interac')
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        AND     registered_in_portal
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
      
                
                -- One Time transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_trans_onetime = t.count_trans_onetime,
                        amount_trans_onetime = t.amount_trans_onetime
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_trans_onetime, 
                                SUM(amount) AS amount_trans_onetime
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type IN ('DirectBanking','CreditCard','Echeck','Interac')
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NULL
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
     
                
                -- One Time transactions - count and amount :registered tenants
                
                UPDATE  _dba_.building_stats AS s
                SET     count_trans_onetime_reg = t.count_trans_onetime_reg,
                        amount_trans_onetime_reg = t.amount_trans_onetime_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_trans_onetime_reg, 
                                SUM(amount) AS amount_trans_onetime_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type IN ('DirectBanking','CreditCard','Echeck','Interac')
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NULL
                        AND     registered_in_portal
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
     
                
                -- EFT Recurring transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_eft_recur = t.count_eft_recur,
                        amount_eft_recur = t.amount_eft_recur
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_eft_recur, 
                                SUM(amount) AS amount_eft_recur
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'Echeck'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
      
                -- EFT Recurring transactions - count and amount : registered tenants
                
                UPDATE  _dba_.building_stats AS s
                SET     count_eft_recur_reg = t.count_eft_recur_reg,
                        amount_eft_recur_reg = t.amount_eft_recur_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_eft_recur_reg, 
                                SUM(amount) AS amount_eft_recur_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'Echeck'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        AND     registered_in_portal
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
           
                -- EFT One time transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_eft_onetime = t.count_eft_onetime,
                        amount_eft_onetime = t.amount_eft_onetime
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_eft_onetime, 
                                SUM(amount) AS amount_eft_onetime
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'Echeck'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NULL
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
            
                
                -- EFT One time transactions - count and amount : reg
                
                UPDATE  _dba_.building_stats AS s
                SET     count_eft_onetime_reg = t.count_eft_onetime_reg,
                        amount_eft_onetime_reg = t.amount_eft_onetime_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_eft_onetime_reg, 
                                SUM(amount) AS amount_eft_onetime_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'Echeck'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NULL
                        AND     registered_in_portal
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;  
              
                
                -- Direct Banking transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_direct_debit = t.count_direct_debit,
                        amount_direct_debit = t.amount_direct_debit
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_direct_debit, 
                                SUM(amount) AS amount_direct_debit
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'DirectBanking'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                 
               
                
                -- Direct Banking recurring transactions - count and amount : registered tenants
                
                UPDATE  _dba_.building_stats AS s
                 SET     count_direct_debit_reg = t.count_direct_debit_reg,
                        amount_direct_debit_reg = t.amount_direct_debit_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_direct_debit_reg, 
                                SUM(amount) AS amount_direct_debit_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'DirectBanking'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     registered_in_portal
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
               
                -- Interac transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_interac = t.count_interac,
                        amount_interac = t.amount_interac
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_interac, 
                                SUM(amount) AS amount_interac
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'Interac'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;            
                
               
                
                -- Interac recurring transactions - count and amount :reg
                
                UPDATE  _dba_.building_stats AS s
                SET     count_interac_reg = t.count_interac_reg,
                        amount_interac_reg = t.amount_interac_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_interac_reg, 
                                SUM(amount) AS amount_interac_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'Interac'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     registered_in_portal
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;            
                                
                
                -- Visa recurring transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_visa_recur = t.count_visa_recur,
                        amount_visa_recur = t.amount_visa_recur
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_visa_recur, 
                                SUM(amount) AS amount_visa_recur
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'Visa'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
              
                
                -- Visa recurring transactions - count and amount :reg  
                
                UPDATE  _dba_.building_stats AS s
                SET     count_visa_recur_reg = t.count_visa_recur_reg,
                        amount_visa_recur_reg = t.amount_visa_recur_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_visa_recur_reg, 
                                SUM(amount) AS amount_visa_recur_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'Visa'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        AND     registered_in_portal
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;       
               
                
                -- Visa one time transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_visa_onetime = t.count_visa_onetime,
                        amount_visa_onetime = t.amount_visa_onetime
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_visa_onetime, 
                                SUM(amount) AS amount_visa_onetime
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'Visa'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NULL
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
                
                
                -- Visa one time transactions - count and amount :reg 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_visa_onetime_reg = t.count_visa_onetime_reg,
                        amount_visa_onetime_reg = t.amount_visa_onetime_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_visa_onetime_reg, 
                                SUM(amount) AS amount_visa_onetime_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'Visa'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NULL
                        AND     registered_in_portal
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
                
                 -- MC recurring transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_mc_recur = t.count_mc_recur,
                        amount_mc_recur = t.amount_mc_recur
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_mc_recur, 
                                SUM(amount) AS amount_mc_recur
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'MasterCard'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
               
                
                -- MC recurring transactions - count and amount :reg 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_mc_recur_reg = t.count_mc_recur_reg,
                        amount_mc_recur_reg = t.amount_mc_recur_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_mc_recur_reg, 
                                SUM(amount) AS amount_mc_recur_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'MasterCard'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        AND     registered_in_portal
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
                
                -- MC onetime transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_mc_onetime = t.count_mc_onetime,
                        amount_mc_onetime = t.amount_mc_onetime
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_mc_onetime, 
                                SUM(amount) AS amount_mc_onetime
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'MasterCard'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NULL
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
                
                -- MC onetime transactions - count and amount :reg 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_mc_onetime_reg = t.count_mc_onetime_reg,
                        amount_mc_onetime_reg = t.amount_mc_onetime_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_mc_onetime_reg, 
                                SUM(amount) AS amount_mc_onetime_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'MasterCard'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NULL
                        AND     registered_in_portal 
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
                
                 -- VisaDebit recurring transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_visadebit_recur = t.count_visadebit_recur,
                        amount_visadebit_recur = t.amount_visadebit_recur
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_visadebit_recur, 
                                SUM(amount) AS amount_visadebit_recur
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'VisaDebit'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
                -- VisaDebit recurring transactions - count and amount :reg 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_visadebit_recur_reg = t.count_visadebit_recur_reg,
                        amount_visadebit_recur_reg = t.amount_visadebit_recur_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_visadebit_recur_reg, 
                                SUM(amount) AS amount_visadebit_recur_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'VisaDebit'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        AND     registered_in_portal
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
                -- VisaDebit onetime transactions - count and amount 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_visadebit_onetime = t.count_visadebit_onetime,
                        amount_visadebit_onetime = t.amount_visadebit_onetime
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_visadebit_onetime, 
                                SUM(amount) AS amount_visadebit_onetime
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'VisaDebit'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
                -- VisaDebit onetime transactions - count and amount :reg 
                
                UPDATE  _dba_.building_stats AS s
                SET     count_visadebit_onetime_reg = t.count_visadebit_onetime_reg,
                        amount_visadebit_onetime_reg = t.amount_visadebit_onetime_reg
                FROM    (SELECT property_code, 
                                COUNT(payment_id) AS count_visadebit_onetime_reg, 
                                SUM(amount) AS amount_visadebit_onetime_reg
                        FROM    _dba_.tmp_stats 
                        WHERE   lease_status = 'Active' 
                        AND     payment_id IS NOT NULL 
                        AND     payment_status = 'Cleared'
                        AND     pm_discriminator = 'LeasePaymentMethod'
                        AND     payment_type = 'CreditCard'
                        AND     card_type = 'VisaDebit'
                        AND     DATE_TRUNC('month',last_status_change_date) = DATE_TRUNC('month',v_this_week)
                        AND     pap_id IS NOT NULL
                        AND     registered_in_portal
                        GROUP BY property_code) AS t 
                WHERE   s.property_code = t.property_code
                AND     s.pmc = v_schema_name 
                AND     s.stats_week = v_this_week;        
                
                
                -- Total Maintenance requests 
                
                EXECUTE 'UPDATE _dba_.building_stats AS s '
                        ||'SET  total_maint_requests = t.total_maint_requests '
                        ||'FROM (SELECT property_code, COUNT(id) AS total_maint_requests '
                        ||'     FROM    (SELECT DISTINCT t.property_code,m.id '
                        ||'             FROM    _dba_.tmp_stats t '
                        ||'             JOIN    '||v_schema_name||'.building b ON (t.property_code = b.property_code) ' 
                        ||'             JOIN    '||v_schema_name||'.maintenance_request m ON (b.id = m.building)) AS t '
                        ||'     GROUP BY property_code ) AS t '
                        ||'WHERE   s.property_code = t.property_code '
                        ||'AND     s.pmc = '''||v_schema_name||''' ' 
                        ||'AND     s.stats_week = '''||v_this_week||''' ';  
                
                
                -- Total Maintenance requests this month
                
                EXECUTE 'UPDATE _dba_.building_stats AS s '
                        ||'SET  maint_requests_month = t.maint_requests_month '
                        ||'FROM (SELECT property_code, COUNT(id) AS maint_requests_month '
                        ||'     FROM    (SELECT DISTINCT t.property_code,m.id '
                        ||'             FROM    _dba_.tmp_stats t '
                        ||'             JOIN    '||v_schema_name||'.building b ON (t.property_code = b.property_code) ' 
                        ||'             JOIN    '||v_schema_name||'.maintenance_request m ON (b.id = m.building) '
                        ||'             WHERE   DATE_TRUNC(''month'',m.submitted) = DATE_TRUNC(''month'','''||v_this_week||'''::date)) AS t '
                        ||'     GROUP BY property_code ) AS t '
                        ||'WHERE   s.property_code = t.property_code '
                        ||'AND     s.pmc = '''||v_schema_name||''' ' 
                        ||'AND     s.stats_week = '''||v_this_week||''' ';  
                
                
                
                -- Total Tenant Maintenance requests 
                
                EXECUTE 'UPDATE _dba_.building_stats AS s '
                        ||'SET  tenant_maint_requests = t.tenant_maint_requests '
                        ||'FROM (SELECT property_code, COUNT(id) AS tenant_maint_requests '
                        ||'     FROM    (SELECT DISTINCT t.property_code,m.id '
                        ||'             FROM    _dba_.tmp_stats t '
                        ||'             JOIN    '||v_schema_name||'.building b ON (t.property_code = b.property_code) ' 
                        ||'             JOIN    '||v_schema_name||'.maintenance_request m ON (b.id = m.building) '
                        ||'             WHERE   t.registered_in_portal ) AS t '
                        ||'     GROUP BY property_code ) AS t '
                        ||'WHERE   s.property_code = t.property_code '
                        ||'AND     s.pmc = '''||v_schema_name||''' ' 
                        ||'AND     s.stats_week = '''||v_this_week||''' ';
                        
                        
                        
                -- Tenant Maintenance requests this month
                
                EXECUTE 'UPDATE _dba_.building_stats AS s '
                        ||'SET  tenant_maint_requests_month = t.tenant_maint_requests_month '
                        ||'FROM (SELECT property_code, COUNT(id) AS tenant_maint_requests_month '
                        ||'     FROM    (SELECT DISTINCT t.property_code,m.id '
                        ||'             FROM    _dba_.tmp_stats t '
                        ||'             JOIN    '||v_schema_name||'.building b ON (t.property_code = b.property_code) ' 
                        ||'             JOIN    '||v_schema_name||'.maintenance_request m ON (b.id = m.building) '
                        ||'             WHERE   t.registered_in_portal '
                        ||'             AND     DATE_TRUNC(''month'',m.submitted) = DATE_TRUNC(''month'','''||v_this_week||'''::date)) AS t '
                        ||'     GROUP BY property_code ) AS t '
                        ||'WHERE   s.property_code = t.property_code '
                        ||'AND     s.pmc = '''||v_schema_name||''' ' 
                        ||'AND     s.stats_week = '''||v_this_week||''' ';
                
                DROP TABLE _dba_.tmp_stats;
        END IF;
END;
$$
LANGUAGE plpgsql VOLATILE;


        
