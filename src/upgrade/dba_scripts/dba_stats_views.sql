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
        SELECT  pmc AS "PMC",property_code AS "Property Code",
                total_units AS "Total Units",
                active_leases AS "Active Leases",
                reg_units AS "Units with Registered Tenants",
                ROUND(reg_units::numeric(4,1)*100/total_units,1) AS "% Units with Registered Tenants",
                units_epay AS "Units with Electronic Payments",
                reg_units_epay AS "Units with Registered Tenants Using Electronic Payments",
                ROUND(reg_units_epay::numeric(4,1)*100/units_epay,1) AS "% Reg. Units with Epay to Total Units with Epay",
                total_tenants AS "Total Tenants",
                ROUND(total_tenants/total_units::numeric(4,1),1) AS "Average Tenant per Unit",
                reg_tenants AS "Registered Tenants",
                ROUND(reg_tenants::numeric(4,1)*100/total_tenants,1) AS "% Registered Tenants",
                tenant_login_week AS "Tenant Logins This Week",
                tenant_login_month AS "Tenant Logins This Month",
                tenants_epay AS "Tenants Using Electronic Payments",
                reg_tenants_epay AS "Registered Tenants Using Electronic Payments",
                ROUND(reg_tenants_epay::numeric(4,1)*100/tenants_epay,1) AS "% Registered Tenants with Epay to Total Tenants with Epay",
                ROUND(reg_tenants_epay::numeric(4,1)*100/reg_tenants,1) AS "% Registered Tenants with Epay to All Registered Tenants",
                count_trans_recur AS "Transactions Processed This Month, Recurring",
                amount_trans_recur AS "Amount Processed This Month, Recurring",
                count_trans_recur_reg AS "Registered Tenants: Transactions Processed This Month,Recurring",
                amount_trans_recur_reg AS "Registered Tenants: Amount Processed This Month, Recurring",
                count_trans_onetime AS "Transactions Processed This Month, One Time",
                amount_trans_onetime AS "Amount Processed This Month, One Time",
                count_trans_onetime_reg AS "Registered Tenants: Transactions Processed This Month, One Time",
                amount_trans_onetime_reg AS "Registered Tenants: Amount Processed This Month, One Time",
                count_eft_recur AS "EFT Transactions, Recurring",
                amount_eft_recur AS "EFT Amount, Recurring",
                count_eft_recur_reg AS "Registered Tenants: EFT Transactions, Recurring",
                amount_eft_recur_reg AS "Registered Tenants: EFT Amount, Recurring",
                count_eft_onetime AS "EFT Transactions, One Time",
                amount_eft_onetime AS "EFT Amount, One Time",
                count_eft_onetime_reg AS "Registered Tenants: EFT Transactions, One Time",
                amount_eft_onetime_reg AS "Registered Tenants: EFT Amount, One Time",
                count_direct_debit AS "Direct Banking Transactions",
                amount_direct_debit AS "Direct Banking Amount",
                count_direct_debit_reg AS "Registered Tenants: Direct Banking Transactions",
                amount_direct_debit_reg AS "Registered Tenants: Direct Banking Amount",
                count_interac AS "Interac Transactions",
                amount_interac AS "Interac Amount",
                count_interac_reg AS "Registered Tenants: Interac Transactions",
                amount_interac_reg AS "Registered Tenants: Interac Amount",
                count_visa_recur AS "Visa Transactions, Recurring",
                amount_visa_recur AS "Visa Amount, Recurring",
                count_visa_recur_reg AS "Registered Tenants: Visa Transactions, Recurring",
                amount_visa_recur_reg AS "Registered Tenants: Visa Amount, Recurring",
                count_visa_onetime AS "Visa Transactions, One Time",
                amount_visa_onetime AS "Visa Amount, One Time",
                count_visa_onetime_reg AS "Registered Tenants: Visa Transactions, One Time",
                amount_visa_onetime_reg AS "Registered Tenants: Visa Amount, One Time",
                count_mc_recur  AS "MasterCard Transactions, Recurring",
                amount_mc_recur AS "MasterCard Amount, Recurring",
                count_mc_recur_reg  AS "Registered Tenants: MasterCard Transactions, Recurring",
                amount_mc_recur_reg AS "Registered Tenants: MasterCard Amount, Recurring",
                count_mc_onetime AS "MasterCard Transactions, One Time",
                amount_mc_onetime AS "MasterCard Amount, One Time",
                count_mc_onetime_reg AS "Registered Tenants: MasterCard Transactions, One Time",
                amount_mc_onetime_reg AS "Registered Tenants: MasterCard Amount, One Time",
                count_visadebit_recur AS "Visa Debit Transactions, Recurring",
                amount_visadebit_recur AS "Visa Debit Amount, Recurring",
                count_visadebit_recur_reg AS "Registered Tenants: Visa Debit Transactions, Recurring",
                amount_visadebit_recur_reg AS "Registered Tenants: Visa Debit Amount, Recurring",
                count_visadebit_onetime AS "Visa Debit Transactions, One Time",
                amount_visadebit_onetime AS "Visa Debit Amount, One Time",
                count_visadebit_onetime_reg AS "Registered Tenants: Visa Debit Transactions, One Time",
                amount_visadebit_onetime_reg AS "Registered Tenants: Visa Debit Amount, One Time",
                total_maint_requests AS "Total Maintenance Requests",
                maint_requests_month AS "Maintenance Requests This Month",
                tenant_maint_requests AS "Total Tenant Maintenance Requests",
                tenant_maint_requests_month AS "Tenant Maintenance Requests This Month"          
        FROM    _dba_.building_stats
        WHERE   reg_tenants > 0 
        AND     stats_week = (SELECT MAX(stats_week) FROM _dba_.building_stats)  
 );
