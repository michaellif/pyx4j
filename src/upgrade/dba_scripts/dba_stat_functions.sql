/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Per-building statistics functions
***
***     ======================================================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.get_building_units(    v_schema_name   TEXT, 
                                                        v_property_code TEXT, 
                                                        OUT total_units INT,
                                                        OUT active_leases INT,
                                                        OUT avg_tpu NUMERIC(4,1),
                                                        OUT reg_units      INT,
                                                        OUT units_epay     INT,
                                                        OUT reg_units_epay INT,
                                                        OUT total_tenants  INT,
                                                        OUT reg_tenants    INT,
                                                        OUT tenant_logins_this_week INT,
                                                        OUT tenant_logins_this_month INT)
AS
$$      
BEGIN
      
        EXECUTE   'SELECT COUNT(a.id)::int '
                ||'FROM '||v_schema_name||'.apt_unit a '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                INTO total_units;
                
        EXECUTE 'SELECT COUNT(l.id) '
                ||'FROM '||v_schema_name||'.lease l '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (l.unit = a.id) '
                ||'JOIN '||v_schema_name||'.building b ON (a.building = b.id) '
                ||'WHERE l.status = ''Active'' '
                ||'AND b.property_code = '''||v_property_code||''' '
                INTO active_leases;
        
        EXECUTE 'SELECT ROUND(AVG(tpu),1) AS avg_tpu '
                ||'FROM         (SELECT l.unit,COUNT(p.id) AS tpu '
                ||'             FROM '||v_schema_name||'.lease l '
                ||'             JOIN '||v_schema_name||'.lease_participant p ON (p.lease = l.id) '
                ||'             JOIN '||v_schema_name||'.apt_unit a ON (l.unit = a.id) '
                ||'             JOIN '||v_schema_name||'.building b ON (a.building = b.id) '
                ||'             WHERE l.status = ''Active'' '
                ||'             AND  b.property_code = '''||v_property_code||''' '
                ||'             GROUP BY l.unit) AS t '
                INTO avg_tpu;
                
                
        EXECUTE   'SELECT COUNT(a.id)::int '
                ||'FROM '||v_schema_name||'.apt_unit a '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'JOIN         (SELECT DISTINCT unit '
                ||              'FROM '||v_schema_name||'.lease l '
                ||              'JOIN '||v_schema_name||'.lease_participant p ON (p.lease = l.id) '
                ||              'JOIN '||v_schema_name||'.customer c ON (p.customer = c.id) '
                ||              'WHERE  l.status = ''Active'' '
                ||              'AND    c.registered_in_portal ) AS c ON (c.unit = a.id) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                INTO reg_units;
          
              
         EXECUTE   'SELECT COUNT(a.id)::int '
                ||'FROM '||v_schema_name||'.apt_unit a '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'JOIN         (SELECT DISTINCT unit '
                ||              'FROM '||v_schema_name||'.lease l '
                ||              'JOIN '||v_schema_name||'.lease_participant lp ON (lp.lease = l.id) '
                ||              'JOIN '||v_schema_name||'.lease_term_participant ltp ON (lp.id = ltp.lease_participant) '
                ||              'JOIN '||v_schema_name||'.payment_record p ON (ltp.id = p.lease_term_participant) '
                ||              'JOIN '||v_schema_name||'.payment_method pm ON (pm.id = p.payment_method) '
                ||              'WHERE  l.status = ''Active'' '
                ||              'AND    pm.id_discriminator = ''LeasePaymentMethod'' '
                ||              'AND    pm.payment_type IN (''DirectBanking'',''CreditCard'',''Echeck'',''Interac'') '
                ||              'AND    DATE_TRUNC(''month'',p.last_status_change_date) = DATE_TRUNC(''month'',current_date) ) AS c ON (c.unit = a.id) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                INTO units_epay;
          
        EXECUTE   'SELECT COUNT(a.id)::int '
                ||'FROM '||v_schema_name||'.apt_unit a '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'JOIN         (SELECT DISTINCT unit '
                ||              'FROM '||v_schema_name||'.lease l '
                ||              'JOIN '||v_schema_name||'.lease_participant lp ON (lp.lease = l.id) '
                ||              'JOIN '||v_schema_name||'.customer c ON (c.id = lp.customer) '
                ||              'JOIN '||v_schema_name||'.lease_term_participant ltp ON (lp.id = ltp.lease_participant) '
                ||              'JOIN '||v_schema_name||'.payment_record p ON (ltp.id = p.lease_term_participant) '
                ||              'JOIN '||v_schema_name||'.payment_method pm ON (pm.id = p.payment_method) '
                ||              'WHERE  l.status = ''Active'' '
                ||              'AND    pm.id_discriminator = ''LeasePaymentMethod'' '
                ||              'AND    pm.payment_type IN (''DirectBanking'',''CreditCard'',''Echeck'',''Interac'') '
                ||              'AND    DATE_TRUNC(''month'',p.last_status_change_date) = DATE_TRUNC(''month'',current_date) '
                ||              'AND    c.registered_in_portal) AS c ON (c.unit = a.id) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                INTO reg_units_epay;
 
        EXECUTE 'SELECT COUNT(p.id) '
                ||'FROM '||v_schema_name||'.lease_participant p '
                ||'JOIN '||v_schema_name||'.lease l ON (l.id = p.lease) '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                ||'JOIN '||v_schema_name||'.building b ON (a.building = b.id) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                ||'AND  l.status = ''Active'' '
                INTO total_tenants;
                
        EXECUTE 'SELECT COUNT(p.id) '
                ||'FROM '||v_schema_name||'.lease_participant p '
                ||'JOIN '||v_schema_name||'.lease l ON (l.id = p.lease) '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                ||'JOIN '||v_schema_name||'.building b ON (a.building = b.id) '
                ||'JOIN '||v_schema_name||'.customer c ON (p.customer = c.id) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                ||'AND  l.status = ''Active'' '
                ||'AND  c.registered_in_portal '
                INTO reg_tenants;
                
        IF (reg_tenants != 0)
        THEN
                EXECUTE 'SELECT  COUNT(a.id) '
                        ||'FROM _admin_.audit_record a '
                        ||'JOIN '||v_schema_name||'.customer_user cu ON (a.usr = cu.id) '
                        ||'JOIN '||v_schema_name||'.customer c ON (c.user_id = cu.id) '
                        ||'JOIN '||v_schema_name||'.lease_participant lp ON (lp.customer = c.id) '
                        ||'JOIN '||v_schema_name||'.lease l ON (lp.lease = l.id) '
                        ||'JOIN '||v_schema_name||'.apt_unit au ON (l.unit = au.id) '
                        ||'JOIN '||v_schema_name||'.building b ON (au.building = b.id) '
                        ||'WHERE   a.event = ''Login'' '
                        ||'AND     DATE_TRUNC(''week'',a.created) = DATE_TRUNC(''week'',current_date) '
                        ||'AND     l.status = ''Active'' '
                        INTO  tenant_logins_this_week;
                        
                EXECUTE 'SELECT  COUNT(a.id) '
                        ||'FROM _admin_.audit_record a '
                        ||'JOIN '||v_schema_name||'.customer_user cu ON (a.usr = cu.id) '
                        ||'JOIN '||v_schema_name||'.customer c ON (c.user_id = cu.id) '
                        ||'JOIN '||v_schema_name||'.lease_participant lp ON (lp.customer = c.id) '
                        ||'JOIN '||v_schema_name||'.lease l ON (lp.lease = l.id) '
                        ||'JOIN '||v_schema_name||'.apt_unit au ON (l.unit = au.id) '
                        ||'JOIN '||v_schema_name||'.building b ON (au.building = b.id) '
                        ||'WHERE   a.event = ''Login'' '
                        ||'AND     DATE_TRUNC(''month'',a.created) = DATE_TRUNC(''month'',current_date) '
                        ||'AND     l.status = ''Active'' '
                        INTO  tenant_logins_this_month;
                        
        ELSE
                tenant_logins_this_week := 0;
                tenant_logins_this_month := 0;
                
        END IF;
                
                
        -- pct_reg_units := ROUND(reg_units::numeric(4,1)*100/total_units,1);
        -- pct_reg_tenants := ROUND(reg_tenants::numeric(4,1)*100/total_tenants,1);
END;
$$
LANGUAGE plpgsql VOLATILE;


CREATE OR REPLACE FUNCTION _dba_.get_building_transactions(     v_schema_name   TEXT,
                                                                v_property_code TEXT,
                                                                OUT     count_tenants_epay      INT,
                                                                OUT     count_trans_recur       INT,
                                                                OUT     amount_trans_recur      NUMERIC(18,2),
                                                                OUT     count_trans_onetime     INT,
                                                                OUT     amount_trans_onetime    NUMERIC(18,2),
                                                                OUT     count_eft_recur         INT,
                                                                OUT     amount_eft_recur        NUMERIC(18,2),
                                                                OUT     count_eft_onetime       INT,
                                                                OUT     amount_eft_onetime      NUMERIC(18,2),
                                                                OUT     count_direct_debit      INT,
                                                                OUT     amount_direct_debit     NUMERIC(18,2),
                                                                OUT     count_interac           INT,
                                                                OUT     amount_interac          NUMERIC(18,2),
                                                                OUT     count_visa_recur        INT,
                                                                OUT     amount_visa_recur       NUMERIC(18,2),
                                                                OUT     count_visa_onetime      INT,
                                                                OUT     amount_visa_onetime     NUMERIC(18,2),
                                                                OUT     count_mc_recur          INT,
                                                                OUT     amount_mc_recur         NUMERIC(18,2),
                                                                OUT     count_mc_onetime        INT,
                                                                OUT     amount_mc_onetime       NUMERIC(18,2),
                                                                OUT     count_visadebit_recur   INT,
                                                                OUT     amount_visadebit_recur  NUMERIC(18,2),
                                                                OUT     count_visadebit_onetime        INT,
                                                                OUT     amount_visadebit_onetime        NUMERIC(18,2))
AS
$$
BEGIN
        
        /**     ============================================================================================================
        ***
        ***             Tenants that TRY to use e-pay, no matter how successfull they are in it this.
        ***             Last status change date is used to determine month
        ***     
        ***     ============================================================================================================
        **/      
        
        
        EXECUTE 'SELECT COUNT(lp.id) '
                ||'FROM '||v_schema_name||'.payment_record p '
                ||'JOIN '||v_schema_name||'.payment_method pm ON (pm.id = p.payment_method) '
                ||'JOIN '||v_schema_name||'.lease_term_participant ltp ON (ltp.id = p.lease_term_participant) '
                ||'JOIN '||v_schema_name||'.lease_participant lp ON (lp.id = ltp.lease_participant) '
                ||'JOIN '||v_schema_name||'.lease l ON (l.id = lp.lease) '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'WHERE   DATE_TRUNC(''month'',p.last_status_change_date) = DATE_TRUNC(''month'',current_date) '
                ||'AND     pm.id_discriminator = ''LeasePaymentMethod'' '
                ||'AND     pm.payment_type IN (''DirectBanking'',''CreditCard'',''Echeck'',''Interac'') '
                ||'AND     b.property_code = '''||v_property_code||'''  '
                INTO count_tenants_epay;
        
        
        IF (count_tenants_epay != 0) 
        THEN
        
                /**     ============================================================================================================
                ***
                ***             For payment information only cleared records considered
                ***     
                ***     ============================================================================================================
                **/      
        
        
                EXECUTE 'WITH t AS (    SELECT  '
                        ||'             b.property_code,'
                        ||'             p.preauthorized_payment,'
                        ||'             p.id,p.amount,'
                        ||'             CASE    WHEN ppd.id_discriminator = ''CreditCard'' THEN ppd.card_type '
                        ||'                     WHEN ppd.id_discriminator = ''EcheckInfo'' THEN ''EFT'' '
                        ||'                     WHEN ppd.id_discriminator = ''InteracInfo'' THEN ''Interac'' '
                        ||'                     ELSE ppd.id_discriminator END as payment_type '
                        ||'             FROM '||v_schema_name||'.payment_record p '
                        ||'             JOIN '||v_schema_name||'.payment_method pm ON (pm.id = p.payment_method) '
                        ||'             JOIN '||v_schema_name||'.payment_payment_details ppd ON (ppd.id = pm.details) '
                        ||'             JOIN '||v_schema_name||'.lease_term_participant ltp ON (p.lease_term_participant = ltp.id) '
                        ||'             JOIN '||v_schema_name||'.lease_participant lp ON (lp.id = ltp.lease_participant) '
                        ||'             JOIN '||v_schema_name||'.lease l ON (l.id = lp.lease) '
                        ||'             JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                        ||'             JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                        ||'             WHERE   DATE_TRUNC(''month'',p.finalize_date) = DATE_TRUNC(''month'',current_date) '
                        ||'             AND     p.payment_status = ''Cleared'' '
                        ||'             AND     pm.id_discriminator = ''LeasePaymentMethod'' '
                        ||'             AND     pm.payment_type IN (''DirectBanking'',''CreditCard'',''Echeck'',''Interac'') '
                        ||'             AND     b.property_code = '''||v_property_code||''' )'
                        ||'SELECT       COALESCE(a.count_trans_recur,0) AS count_trans_recur,COALESCE(a.amount_trans_recur,0) AS amount_trans_recur, '
                        ||'             COALESCE(b.count_trans_onetime,0) AS count_trans_onetime, COALESCE(b.amount_trans_onetime,0) AS amount_trans_onetime, '
                        ||'             COALESCE(c.count_eft_recur,0) AS count_eft_recur,COALESCE(c.amount_eft_recur,0) AS amount_eft_recur,'
                        ||'             COALESCE(d.count_direct_debit,0) AS count_direct_debit,COALESCE(d.amount_direct_debit,0) AS amount_direct_debit,'
                        ||'             COALESCE(e.count_interac,0) AS count_interac,COALESCE(e.amount_interac,0) AS amount_interac,'
                        ||'             COALESCE(f.count_visa_recur,0) AS count_visa_recur,COALESCE(f.amount_visa_recur,0) AS amount_visa_recur,'
                        ||'             COALESCE(g.count_visa_onetime,0) AS count_visa_onetime,COALESCE(g.amount_visa_onetime,0) AS amount_visa_onetime,'
                        ||'             COALESCE(h.count_mc_recur,0) AS count_mc_recur,COALESCE(h.amount_mc_recur,0) AS amount_mc_recur,'
                        ||'             COALESCE(i.count_mc_onetime,0) AS count_mc_onetime,COALESCE(i.amount_mc_onetime,0) AS amount_mc_onetime,'
                        ||'             COALESCE(j.count_visadebit_recur,0) AS count_visadebit_recur,COALESCE(j.amount_visadebit_recur,0) AS amount_visadebit_recur,'
                        ||'             COALESCE(k.count_visadebit_onetime,0) AS count_visadebit_onetime,COALESCE(k.amount_visadebit_onetime,0) AS amount_visadebit_onetime, '
                        ||'             COALESCE(l.count_eft_onetime,0) AS count_eft_onetime,COALESCE(l.amount_eft_onetime,0) AS amount_eft_onetime '
                        ||'FROM         (SELECT COUNT(id) AS count_trans_recur, '
                        ||'                     SUM(amount) AS amount_trans_recur, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NOT NULL '
                        ||'             GROUP BY property_code) AS a '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_trans_onetime, '
                        ||'                     SUM(amount) AS amount_trans_onetime, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NULL '
                        ||'             GROUP BY property_code ) AS b ON (a.property_code = b.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_eft_recur, '
                        ||'                     SUM(amount) AS amount_eft_recur, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NOT NULL '
                        ||'             AND     payment_type = ''EFT'' '
                        ||'             GROUP BY property_code ) AS c ON (a.property_code = c.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_direct_debit, '
                        ||'                     SUM(amount) AS amount_direct_debit, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   payment_type = ''DirectDebit'' '
                        ||'             GROUP BY property_code ) AS d ON (a.property_code = d.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_interac, '
                        ||'                     SUM(amount) AS amount_interac, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   payment_type = ''InteracInfo'' '
                        ||'             GROUP BY property_code ) AS e ON (a.property_code = e.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_visa_recur, '
                        ||'                     SUM(amount) AS amount_visa_recur, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NOT NULL '
                        ||'             AND     payment_type = ''Visa'' '
                        ||'             GROUP BY property_code ) AS f ON (a.property_code = f.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_visa_onetime, '
                        ||'                     SUM(amount) AS amount_visa_onetime, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NULL '
                        ||'             AND     payment_type = ''Visa'' '
                        ||'             GROUP BY property_code ) AS g ON (a.property_code = g.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_mc_recur, '
                        ||'                     SUM(amount) AS amount_mc_recur, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NOT NULL '
                        ||'             AND     payment_type = ''MasterCard'' '
                        ||'             GROUP BY property_code ) AS h ON (a.property_code = h.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_mc_onetime, '
                        ||'                     SUM(amount) AS amount_mc_onetime, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NULL '
                        ||'             AND     payment_type = ''MasterCard'' '
                        ||'             GROUP BY property_code ) AS i ON (a.property_code = i.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_visadebit_recur, '
                        ||'                     SUM(amount) AS amount_visadebit_recur, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NOT NULL '
                        ||'             AND     payment_type = ''VisaDebit'' '
                        ||'             GROUP BY property_code ) AS j ON (a.property_code = j.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_visadebit_onetime, '
                        ||'                     SUM(amount) AS amount_visadebit_onetime, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NULL '
                        ||'             AND     payment_type = ''VisaDebit'' '
                        ||'             GROUP BY property_code ) AS k ON (a.property_code = k.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_eft_onetime, '
                        ||'                     SUM(amount) AS amount_eft_onetime, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NULL '
                        ||'             AND     payment_type = ''EFT'' '
                        ||'             GROUP BY property_code ) AS l ON (a.property_code = l.property_code) '
                        INTO    count_trans_recur,amount_trans_recur,count_trans_onetime,amount_trans_onetime,
                                count_eft_recur,amount_eft_recur,count_direct_debit,amount_direct_debit,
                                count_interac,amount_interac,count_visa_recur,amount_visa_recur,
                                count_visa_onetime,amount_visa_onetime,count_mc_recur,amount_mc_recur,
                                count_mc_onetime,amount_mc_onetime,count_visadebit_recur,amount_visadebit_recur,
                                count_visadebit_onetime,amount_visadebit_onetime,count_eft_onetime,amount_eft_onetime;
        ELSE
               count_trans_recur := 0;
               amount_trans_recur := 0;
               count_trans_onetime := 0;
               amount_trans_onetime := 0;
               count_eft_recur := 0;
               amount_eft_recur := 0;
               count_direct_debit := 0;
               amount_direct_debit := 0;
               count_interac := 0;
               amount_interac := 0;
               count_visa_recur := 0;
               amount_visa_recur := 0;
               count_visa_onetime := 0;
               amount_visa_onetime := 0;
               count_mc_recur := 0 ;
               amount_mc_recur := 0;
               count_mc_onetime := 0;
               amount_mc_onetime := 0;
               count_visadebit_recur := 0;
               amount_visadebit_recur := 0;
               count_visadebit_onetime := 0;
               amount_visadebit_onetime := 0;
               count_eft_onetime := 0;
               amount_eft_onetime := 0;
        END IF; 
                
END;
$$
LANGUAGE plpgsql VOLATILE;
                                                                                      

CREATE OR REPLACE FUNCTION _dba_.get_building_transactions_reg(         v_schema_name   TEXT,
                                                                        v_property_code TEXT,
                                                                OUT     count_tenants_epay_reg  INT,
                                                                OUT     count_trans_recur_reg   INT,
                                                                OUT     amount_trans_recur_reg  NUMERIC(18,2),
                                                                OUT     count_trans_onetime_reg INT,
                                                                OUT     amount_trans_onetime_reg        NUMERIC(18,2),
                                                                OUT     count_eft_recur_reg     INT,
                                                                OUT     amount_eft_recur_reg    NUMERIC(18,2),
                                                                OUT     count_eft_onetime_reg   INT,
                                                                OUT     amount_eft_onetime_reg  NUMERIC(18,2),
                                                                OUT     count_direct_debit_reg  INT,
                                                                OUT     amount_direct_debit_reg NUMERIC(18,2),
                                                                OUT     count_interac_reg       INT,
                                                                OUT     amount_interac_reg      NUMERIC(18,2),
                                                                OUT     count_visa_recur_reg    INT,
                                                                OUT     amount_visa_recur_reg   NUMERIC(18,2),
                                                                OUT     count_visa_onetime_reg  INT,
                                                                OUT     amount_visa_onetime_reg NUMERIC(18,2),
                                                                OUT     count_mc_recur_reg      INT,
                                                                OUT     amount_mc_recur_reg     NUMERIC(18,2),
                                                                OUT     count_mc_onetime_reg    INT,
                                                                OUT     amount_mc_onetime_reg   NUMERIC(18,2),
                                                                OUT     count_visadebit_recur_reg       INT,
                                                                OUT     amount_visadebit_recur_reg      NUMERIC(18,2),
                                                                OUT     count_visadebit_onetime_reg     INT,
                                                                OUT     amount_visadebit_onetime_reg    NUMERIC(18,2))
AS
$$
BEGIN
        
        -- Tenants using epay
        
        EXECUTE 'SELECT COUNT(lp.id) '
                ||'FROM '||v_schema_name||'.payment_record p '
                ||'JOIN '||v_schema_name||'.payment_method pm ON (pm.id = p.payment_method) '
                ||'JOIN '||v_schema_name||'.lease_term_participant ltp ON (ltp.id = p.lease_term_participant) '
                ||'JOIN '||v_schema_name||'.lease_participant lp ON (lp.id = ltp.lease_participant) '
                ||'JOIN '||v_schema_name||'.lease l ON (l.id = lp.lease) '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'JOIN '||v_schema_name||'.customer c ON (lp.customer = c.id) '
                ||'WHERE   DATE_TRUNC(''month'',p.last_status_change_date) = DATE_TRUNC(''month'',current_date) '
                ||'AND     pm.id_discriminator = ''LeasePaymentMethod'' '
                ||'AND     pm.payment_type IN (''DirectBanking'',''CreditCard'',''Echeck'',''Interac'') '
                ||'AND     b.property_code = '''||v_property_code||'''  '
                ||'AND     c.registered_in_portal '
                INTO count_tenants_epay_reg;
        
        
        IF (count_tenants_epay_reg != 0) 
        THEN
        
                EXECUTE 'WITH t AS (    SELECT  '
                        ||'             b.property_code,'
                        ||'             p.preauthorized_payment,'
                        ||'             p.id,p.amount,'
                        ||'             CASE    WHEN ppd.id_discriminator = ''CreditCard'' THEN ppd.card_type '
                        ||'                     WHEN ppd.id_discriminator = ''EcheckInfo'' THEN ''EFT'' '
                        ||'                     WHEN ppd.id_discriminator = ''InteracInfo'' THEN ''Interac'' '
                        ||'                     ELSE ppd.id_discriminator END as payment_type '
                        ||'             FROM '||v_schema_name||'.payment_record p '
                        ||'             JOIN '||v_schema_name||'.payment_method pm ON (pm.id = p.payment_method) '
                        ||'             JOIN '||v_schema_name||'.payment_payment_details ppd ON (ppd.id = pm.details) '
                        ||'             JOIN '||v_schema_name||'.lease_term_participant ltp ON (p.lease_term_participant = ltp.id) '
                        ||'             JOIN '||v_schema_name||'.lease_participant lp ON (lp.id = ltp.lease_participant) '
                        ||'             JOIN '||v_schema_name||'.lease l ON (l.id = lp.lease) '
                        ||'             JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                        ||'             JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                        ||'             JOIN '||v_schema_name||'.customer c ON (c.id = lp.customer) '
                        ||'             WHERE   DATE_TRUNC(''month'',p.finalize_date) = DATE_TRUNC(''month'',current_date) '
                        ||'             AND     p.payment_status = ''Cleared'' '
                        ||'             AND     pm.id_discriminator = ''LeasePaymentMethod'' '
                        ||'             AND     pm.payment_type IN (''DirectBanking'',''CreditCard'',''Echeck'',''Interac'') '
                        ||'             AND     b.property_code = '''||v_property_code||''' '
                        ||'             AND     c.registered_in_portal )'
                        ||'SELECT       COALESCE(a.count_trans_recur,0) AS count_trans_recur,COALESCE(a.amount_trans_recur,0) AS amount_trans_recur, '
                        ||'             COALESCE(b.count_trans_onetime,0) AS count_trans_onetime, COALESCE(b.amount_trans_onetime,0) AS amount_trans_onetime, '
                        ||'             COALESCE(c.count_eft_recur,0) AS count_eft_recur,COALESCE(c.amount_eft_recur,0) AS amount_eft_recur,'
                        ||'             COALESCE(d.count_direct_debit,0) AS count_direct_debit,COALESCE(d.amount_direct_debit,0) AS amount_direct_debit,'
                        ||'             COALESCE(e.count_interac,0) AS count_interac,COALESCE(e.amount_interac,0) AS amount_interac,'
                        ||'             COALESCE(f.count_visa_recur,0) AS count_visa_recur,COALESCE(f.amount_visa_recur,0) AS amount_visa_recur,'
                        ||'             COALESCE(g.count_visa_onetime,0) AS count_visa_onetime,COALESCE(g.amount_visa_onetime,0) AS amount_visa_onetime,'
                        ||'             COALESCE(h.count_mc_recur,0) AS count_mc_recur,COALESCE(h.amount_mc_recur,0) AS amount_mc_recur,'
                        ||'             COALESCE(i.count_mc_onetime,0) AS count_mc_onetime,COALESCE(i.amount_mc_onetime,0) AS amount_mc_onetime,'
                        ||'             COALESCE(j.count_visadebit_recur,0) AS count_visadebit_recur,COALESCE(j.amount_visadebit_recur,0) AS amount_visadebit_recur,'
                        ||'             COALESCE(k.count_visadebit_onetime,0) AS count_visadebit_onetime,COALESCE(k.amount_visadebit_onetime,0) AS amount_visadebit_onetime, '
                        ||'             COALESCE(l.count_eft_onetime,0) AS count_eft_onetime,COALESCE(l.amount_eft_onetime,0) AS amount_eft_onetime '
                        ||'FROM         (SELECT COUNT(id) AS count_trans_recur, '
                        ||'                     SUM(amount) AS amount_trans_recur, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NOT NULL '
                        ||'             GROUP BY property_code) AS a '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_trans_onetime, '
                        ||'                     SUM(amount) AS amount_trans_onetime, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NULL '
                        ||'             GROUP BY property_code ) AS b ON (a.property_code = b.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_eft_recur, '
                        ||'                     SUM(amount) AS amount_eft_recur, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NOT NULL '
                        ||'             AND     payment_type = ''EFT'' '
                        ||'             GROUP BY property_code ) AS c ON (a.property_code = c.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_direct_debit, '
                        ||'                     SUM(amount) AS amount_direct_debit, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   payment_type = ''DirectDebit'' '
                        ||'             GROUP BY property_code ) AS d ON (a.property_code = d.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_interac, '
                        ||'                     SUM(amount) AS amount_interac, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   payment_type = ''InteracInfo'' '
                        ||'             GROUP BY property_code ) AS e ON (a.property_code = e.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_visa_recur, '
                        ||'                     SUM(amount) AS amount_visa_recur, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NOT NULL '
                        ||'             AND     payment_type = ''Visa'' '
                        ||'             GROUP BY property_code ) AS f ON (a.property_code = f.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_visa_onetime, '
                        ||'                     SUM(amount) AS amount_visa_onetime, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NULL '
                        ||'             AND     payment_type = ''Visa'' '
                        ||'             GROUP BY property_code ) AS g ON (a.property_code = g.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_mc_recur, '
                        ||'                     SUM(amount) AS amount_mc_recur, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NOT NULL '
                        ||'             AND     payment_type = ''MasterCard'' '
                        ||'             GROUP BY property_code ) AS h ON (a.property_code = h.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_mc_onetime, '
                        ||'                     SUM(amount) AS amount_mc_onetime, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NULL '
                        ||'             AND     payment_type = ''MasterCard'' '
                        ||'             GROUP BY property_code ) AS i ON (a.property_code = i.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_visadebit_recur, '
                        ||'                     SUM(amount) AS amount_visadebit_recur, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NOT NULL '
                        ||'             AND     payment_type = ''VisaDebit'' '
                        ||'             GROUP BY property_code ) AS j ON (a.property_code = j.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_visadebit_onetime, '
                        ||'                     SUM(amount) AS amount_visadebit_onetime, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NULL '
                        ||'             AND     payment_type = ''VisaDebit'' '
                        ||'             GROUP BY property_code ) AS k ON (a.property_code = k.property_code) '
                        ||'LEFT JOIN         (SELECT COUNT(id) AS count_eft_onetime, '
                        ||'                     SUM(amount) AS amount_eft_onetime, '
                        ||'                     property_code '
                        ||'             FROM    t '
                        ||'             WHERE   preauthorized_payment IS NULL '
                        ||'             AND     payment_type = ''EFT'' '
                        ||'             GROUP BY property_code ) AS l ON (a.property_code = l.property_code) '
                        INTO    count_trans_recur_reg,amount_trans_recur_reg,count_trans_onetime_reg,amount_trans_onetime_reg,
                                count_eft_recur_reg,amount_eft_recur_reg,count_direct_debit_reg,amount_direct_debit_reg,
                                count_interac_reg,amount_interac_reg,count_visa_recur_reg,amount_visa_recur_reg,
                                count_visa_onetime_reg,amount_visa_onetime_reg,count_mc_recur_reg,amount_mc_recur_reg,
                                count_mc_onetime_reg,amount_mc_onetime_reg,count_visadebit_recur_reg,amount_visadebit_recur_reg,
                                count_visadebit_onetime_reg,amount_visadebit_onetime_reg,count_eft_onetime_reg,amount_eft_onetime_reg;
        ELSE
               count_trans_recur_reg := 0;
               amount_trans_recur_reg := 0;
               count_trans_onetime_reg := 0;
               amount_trans_onetime_reg := 0;
               count_eft_recur_reg := 0;
               amount_eft_recur_reg := 0;
               count_direct_debit_reg := 0;
               amount_direct_debit_reg := 0;
               count_interac_reg := 0;
               amount_interac_reg := 0;
               count_visa_recur_reg := 0;
               amount_visa_recur_reg := 0;
               count_visa_onetime_reg := 0;
               amount_visa_onetime_reg := 0;
               count_mc_recur_reg := 0 ;
               amount_mc_recur_reg := 0;
               count_mc_onetime_reg := 0;
               amount_mc_onetime_reg := 0;
               count_visadebit_recur_reg := 0;
               amount_visadebit_recur_reg := 0;
               count_visadebit_onetime_reg := 0;
               amount_visadebit_onetime_reg := 0;
               count_eft_onetime_reg := 0;
               amount_eft_onetime_reg := 0;
        END IF; 
                
END;
$$
LANGUAGE plpgsql VOLATILE;             

CREATE OR REPLACE FUNCTION _dba_.get_building_maintenance(      v_schema_name   TEXT,
                                                                v_property_code TEXT,
                                                                OUT total_maint_requests        INT,
                                                                OUT maint_requests_this_month   INT,
                                                                OUT tenant_maint_requests       INT,
                                                                OUT tenant_maint_requests_this_month    INT)
AS
$$
BEGIN

        EXECUTE 'SELECT COUNT(m.id) '
                ||'FROM '||v_schema_name||'.maintenance_request m '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = m.building) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                INTO total_maint_requests;
                
        IF (total_maint_requests != 0)
        THEN
               
               EXECUTE 'SELECT COUNT(m.id) '
                ||'FROM '||v_schema_name||'.maintenance_request m '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = m.building) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                ||'AND DATE_TRUNC(''month'',m.submitted) = DATE_TRUNC(''month'',current_date) '
                INTO maint_requests_this_month; 
                
                
                EXECUTE 'SELECT COUNT(m.id) '
                ||'FROM '||v_schema_name||'.maintenance_request m '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = m.building) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                ||'AND m.reporter_discriminator = ''Tenant'' '
                INTO tenant_maint_requests;  
                
                EXECUTE 'SELECT COUNT(m.id) '
                ||'FROM '||v_schema_name||'.maintenance_request m '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = m.building) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                ||'AND m.reporter_discriminator = ''Tenant'' '
                ||'AND DATE_TRUNC(''month'',m.submitted) = DATE_TRUNC(''month'',current_date) '
                INTO tenant_maint_requests_this_month;  
        ELSE
              
                maint_requests_this_month := 0;
                tenant_maint_requests := 0;
                tenant_maint_requests_this_month := 0;
                
        END IF;
END;
$$
LANGUAGE plpgsql VOLATILE;

        


CREATE OR REPLACE FUNCTION _dba_.populate_building_stats(v_schema_name text) RETURNS VOID AS
$$
BEGIN
        
        EXECUTE 'INSERT INTO _dba_.building_stats (pmc,property_code,stats_week,total_units,active_leases,'
                ||'avg_tpu,reg_units,units_epay,reg_units_epay,total_tenants,reg_tenants,tenant_logins_this_week,tenant_logins_this_month,'
                ||'count_tenants_epay,count_trans_recur,amount_trans_recur,count_trans_onetime,amount_trans_onetime,'
                ||'count_eft_recur,amount_eft_recur,count_eft_onetime,amount_eft_onetime,count_direct_debit,'
                ||'amount_direct_debit,count_interac,amount_interac,count_visa_recur,amount_visa_recur,count_visa_onetime,'
                ||'amount_visa_onetime,count_mc_recur,amount_mc_recur,count_mc_onetime,amount_mc_onetime,'
                ||'count_visadebit_recur,amount_visadebit_recur,count_visadebit_onetime,amount_visadebit_onetime,'
                ||'count_tenants_epay_reg,count_trans_recur_reg,amount_trans_recur_reg,count_trans_onetime_reg,'
                ||'amount_trans_onetime_reg,count_eft_recur_reg,amount_eft_recur_reg,count_eft_onetime_reg,'
                ||'amount_eft_onetime_reg,count_direct_debit_reg,amount_direct_debit_reg,count_interac_reg,'
                ||'amount_interac_reg,count_visa_recur_reg,amount_visa_recur_reg,count_visa_onetime_reg,'
                ||'amount_visa_onetime_reg,count_mc_recur_reg,amount_mc_recur_reg,count_mc_onetime_reg,amount_mc_onetime_reg,'
                ||'count_visadebit_recur_reg,amount_visadebit_recur_reg,count_visadebit_onetime_reg,amount_visadebit_onetime_reg,'
                ||'total_maint_requests,maint_requests_this_month,tenant_maint_requests,tenant_maint_requests_this_month) '
                ||'(SELECT '''||v_schema_name||''',property_code,DATE_TRUNC(''week'',current_date),'    
                ||'     (_dba_.get_building_units('''||v_schema_name||''',property_code)).*, '
                ||'     (_dba_.get_building_transactions('''||v_schema_name||''',property_code)).*,'
                ||'     (_dba_.get_building_transactions_reg('''||v_schema_name||''',property_code)).*,'
                ||'     (_dba_.get_building_maintenance('''||v_schema_name||''',property_code)).* '
                ||'FROM '||v_schema_name||'.building b '
                ||'WHERE NOT b.suspended) ';

END;
$$
LANGUAGE plpgsql VOLATILE;

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
                        ||'LEFT JOIN    '||v_schema_name||'.preauthorized_payment pap ON (pap.id = p.preauthorized_payment) '
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
                SET     reg_units = t.reg_units
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
                
                DROP TABLE _dba_.tmp_stats;
        END IF;
END;
$$
LANGUAGE plpgsql VOLATILE;


        
