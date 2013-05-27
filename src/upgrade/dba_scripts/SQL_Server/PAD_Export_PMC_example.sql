-- Change accordingly
USE sl_0422;

SELECT  property_id "Property Code",
        SUNITCODE "Unit",
        lease_id "Lease Id",
        tenant_id "Tenant Id",
        bank_account_holder "Name",
        bank_id "Bank Id",
        transit_number "Transit Number",
        SACCT "Account Number",
        charge_id "Charge Id",
        charge_code "Charge Code",
        pap_applicable "PAP Applicable",
        recurring_eft   "Recurring EFT",
        tenant_eft "Tenant EFT",
        estimated_charge "Estimated Charge",
        percentage "Percentage"
FROM tenant_EFT_charges
WHERE property_list like 'green%'		-- CHANGE HERE !!!!
order by property_id,lease_id,achDefault,achId,tenant_id,charge_id;




