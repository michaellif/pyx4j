/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Per-building statistics views
***
***     ======================================================================================================================
**/

CREATE OR REPLACE VIEW _dba_.building_stats_view AS
(
        SELECT  t0.pmc AS "PMC",t0.property_code AS "Property Code",
                t0.total_units AS "Total Units",
                t0.active_leases AS "Active Leases",
                t0.reg_units AS "Units with Registered Tenants",
                ROUND(t0.reg_units::numeric(4,1)*100/t0.total_units,1) AS "% Units with Registered Tenants",
                t0.units_epay AS "Units with Electronic Payments",
                t0.reg_units_epay AS "Units with Registered Tenants Using Electronic Payments",
                ROUND(t0.reg_units_epay::numeric(4,1)*100/t0.units_epay,1) AS "% Reg. Units with Epay to Total Units with Epay",
                t0.total_tenants AS "Total Tenants",
                ROUND(t0.total_tenants/t0.total_units::numeric(4,1),1) AS "Average Tenant per Unit",
                t0.reg_tenants AS "Registered Tenants",
                t0.reg_tenants - t1.reg_tenants AS "Registered Tennats: Change over Last Week",
                ROUND(t0.reg_tenants::numeric(4,1)*100/t0.total_tenants,1) AS "% Registered Tenants",
                t0.tenant_login_week AS "Tenant Logins This Week",
                t0.tenant_login_month AS "Tenant Logins This Month",
                t0.tenants_epay AS "Tenants Using Electronic Payments",
                t0.tenants_epay - t1.tenants_epay AS "Tenants Using Electronic Payments : Change over Last Week",
                t0.reg_tenants_epay AS "Registered Tenants Using Electronic Payments",
                t0.reg_tenants_epay - t1.reg_tenants_epay  "Registered Tenants Using Electronic Payments : Change over Last Week",
                ROUND(t0.reg_tenants_epay::numeric(4,1)*100/t0.tenants_epay,1) AS "% Registered Tenants with Epay to Total Tenants with Epay",
                ROUND(t0.reg_tenants_epay::numeric(4,1)*100/t0.reg_tenants,1) AS "% Registered Tenants with Epay to All Registered Tenants",
                t0.count_trans_recur AS "Transactions Processed This Month, Recurring",
                t0.amount_trans_recur AS "Amount Processed This Month, Recurring",
                t0.count_trans_recur_reg AS "Registered Tenants: Transactions Processed This Month,Recurring",
                t0.amount_trans_recur_reg AS "Registered Tenants: Amount Processed This Month, Recurring",
                t0.count_trans_onetime AS "Transactions Processed This Month, One Time",
                t0.amount_trans_onetime AS "Amount Processed This Month, One Time",
                t0.count_trans_onetime_reg AS "Registered Tenants: Transactions Processed This Month, One Time",
                t0.amount_trans_onetime_reg AS "Registered Tenants: Amount Processed This Month, One Time",
                t0.count_eft_recur AS "EFT Transactions, Recurring",
                t0.amount_eft_recur AS "EFT Amount, Recurring",
                t0.count_eft_recur_reg AS "Registered Tenants: EFT Transactions, Recurring",
                t0.amount_eft_recur_reg AS "Registered Tenants: EFT Amount, Recurring",
                t0.count_eft_onetime AS "EFT Transactions, One Time",
                t0.amount_eft_onetime AS "EFT Amount, One Time",
                t0.count_eft_onetime_reg AS "Registered Tenants: EFT Transactions, One Time",
                t0.amount_eft_onetime_reg AS "Registered Tenants: EFT Amount, One Time",
                t0.count_direct_debit AS "Direct Banking Transactions",
                t0.amount_direct_debit AS "Direct Banking Amount",
                t0.count_direct_debit_reg AS "Registered Tenants: Direct Banking Transactions",
                t0.amount_direct_debit_reg AS "Registered Tenants: Direct Banking Amount",
                t0.count_interac AS "Interac Transactions",
                t0.amount_interac AS "Interac Amount",
                t0.count_interac_reg AS "Registered Tenants: Interac Transactions",
                t0.amount_interac_reg AS "Registered Tenants: Interac Amount",
                t0.count_visa_recur AS "Visa Transactions, Recurring",
                t0.amount_visa_recur AS "Visa Amount, Recurring",
                t0.count_visa_recur_reg AS "Registered Tenants: Visa Transactions, Recurring",
                t0.amount_visa_recur_reg AS "Registered Tenants: Visa Amount, Recurring",
                t0.count_visa_onetime AS "Visa Transactions, One Time",
                t0.amount_visa_onetime AS "Visa Amount, One Time",
                t0.count_visa_onetime_reg AS "Registered Tenants: Visa Transactions, One Time",
                t0.amount_visa_onetime_reg AS "Registered Tenants: Visa Amount, One Time",
                t0.count_mc_recur  AS "MasterCard Transactions, Recurring",
                t0.amount_mc_recur AS "MasterCard Amount, Recurring",
                t0.count_mc_recur_reg  AS "Registered Tenants: MasterCard Transactions, Recurring",
                t0.amount_mc_recur_reg AS "Registered Tenants: MasterCard Amount, Recurring",
                t0.count_mc_onetime AS "MasterCard Transactions, One Time",
                t0.amount_mc_onetime AS "MasterCard Amount, One Time",
                t0.count_mc_onetime_reg AS "Registered Tenants: MasterCard Transactions, One Time",
                t0.amount_mc_onetime_reg AS "Registered Tenants: MasterCard Amount, One Time",
                t0.count_visadebit_recur AS "Visa Debit Transactions, Recurring",
                t0.amount_visadebit_recur AS "Visa Debit Amount, Recurring",
                t0.count_visadebit_recur_reg AS "Registered Tenants: Visa Debit Transactions, Recurring",
                t0.amount_visadebit_recur_reg AS "Registered Tenants: Visa Debit Amount, Recurring",
                t0.count_visadebit_onetime AS "Visa Debit Transactions, One Time",
                t0.amount_visadebit_onetime AS "Visa Debit Amount, One Time",
                t0.count_visadebit_onetime_reg AS "Registered Tenants: Visa Debit Transactions, One Time",
                t0.amount_visadebit_onetime_reg AS "Registered Tenants: Visa Debit Amount, One Time",
                t0.total_maint_requests AS "Total Maintenance Requests",
                t0.maint_requests_month AS "Maintenance Requests This Month",
                t0.tenant_maint_requests AS "Total Tenant Maintenance Requests",
                t0.tenant_maint_requests_month AS "Tenant Maintenance Requests This Month"          
        FROM    (SELECT  * 
                FROM    _dba_.building_stats
                WHERE   reg_tenants > 0 
                AND     stats_week = (SELECT MAX(stats_week) FROM _dba_.building_stats)) AS t0
        LEFT JOIN       (SELECT  * 
                        FROM    _dba_.building_stats
                        WHERE   reg_tenants > 0 
                        AND     stats_week = (SELECT (MAX(stats_week) -7) FROM _dba_.building_stats)) AS t1
        ON      (t0.property_code = t1.property_code  AND t0.pmc = t1.pmc)
 );
