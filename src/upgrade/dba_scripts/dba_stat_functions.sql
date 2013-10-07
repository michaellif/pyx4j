/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Per-building statistics functions and views
***
***     ======================================================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.get_building_units(    v_schema_name   TEXT, 
                                                        v_property_code TEXT, 
                                                        OUT total_units INT,
                                                        OUT active_leases INT,
                                                        OUT avg_tpu NUMERIC(4,1),
                                                        OUT reg_units      INT,
                                                        OUT pct_reg_units NUMERIC(4,1),
                                                        OUT total_tenants  INT,
                                                        OUT reg_tenants    INT,
                                                        OUT pct_reg_tenants NUMERIC(4,1))
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
                
        pct_reg_units := ROUND(reg_units*100/total_units,1);
        pct_reg_tenants := ROUND(reg_tenants*100/total_tenants,1);
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
        
        -- Tenants using epay
        
        EXECUTE 'SELECT COUNT(lp.id) '
                ||'FROM '||v_schema_name||'.payment_record p '
                ||'JOIN '||v_schema_name||'.payment_method pm ON (pm.id = p.payment_method) '
                ||'JOIN '||v_schema_name||'.lease_term_participant ltp ON (ltp.id = p.lease_term_participant) '
                ||'JOIN '||v_schema_name||'.lease_participant lp ON (lp.id = ltp.lease_participant) '
                ||'JOIN '||v_schema_name||'.lease l ON (l.id = lp.lease) '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'WHERE   DATE_TRUNC(''month'',p.finalize_date) = DATE_TRUNC(''month'',current_date) '
                ||'AND     p.payment_status = ''Cleared'' '
                ||'AND     pm.id_discriminator = ''LeasePaymentMethod'' '
                ||'AND     b.property_code = '''||v_property_code||'''  '
                INTO count_tenants_epay;
        
        
        IF (count_tenants_epay != 0) 
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
                        ||'             WHERE   DATE_TRUNC(''month'',p.finalize_date) = DATE_TRUNC(''month'',current_date) '
                        ||'             AND     p.payment_status = ''Cleared'' '
                        ||'             AND     pm.id_discriminator = ''LeasePaymentMethod'' '
                        ||'             AND     b.property_code = '''||v_property_code||''' ) '
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
                ||'WHERE   DATE_TRUNC(''month'',p.finalize_date) = DATE_TRUNC(''month'',current_date) '
                ||'AND     p.payment_status = ''Cleared'' '
                ||'AND     pm.id_discriminator = ''LeasePaymentMethod'' '
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
                        ||'             AND     b.property_code = '''||v_property_code||''' '
                        ||'             AND     c.registered_in_portal ) '
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
                  
        
