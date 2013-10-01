/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Gateway pap export
***
***     ======================================================================================================================
**/

CREATE OR REPLACE VIEW _dba_.gateway_pap_export AS 
(
SELECT  b.property_code AS "Property Code",
        a.info_unit_number AS "Unit",
        l.lease_id AS "LeaseID",
        lp.participant_id AS "Tenant ID",
        ppd.name_on AS "Name",
        ppd.bank_id AS "Bank ID",
        ppd.branch_transit_number AS "Transit Number",
        ppd.account_no_number AS "Account Number",
        bi.id AS "Charge ID",
        y.charge_code AS "Charge Code",
        bi.agreed_price AS "Lease Charge",
        ppc.amount AS "Amount"
FROM    gateway.building b 
JOIN    gateway.apt_unit a ON (b.id = a.building)
JOIN    gateway.lease l ON (a.id = l.unit)
JOIN    gateway.lease_participant lp ON (l.id = lp.lease)
JOIN    gateway.preauthorized_payment pp ON (pp.tenant = lp.id)
JOIN    gateway.preauthorized_payment_covered_item ppc ON (ppc.pap = pp.id)
JOIN    gateway.billable_item bi ON (ppc.billable_item = bi.id)
JOIN    gateway.yardi_lease_charge_data y ON (y.id = bi.extra_data)
JOIN    gateway.payment_method pm ON (pp.payment_method = pm.id)
JOIN    gateway.payment_payment_details ppd ON (pm.details = ppd.id)
WHERE   b.property_code IN ('albe0383','albe0457','belm0545','belm0547',
        'belm0565','conf0104','erb0285','oldc0100','oldc0120','oldc0170',
        'park0400','shak0200','univ0137','west0093','west0109')
AND NOT pp.is_deleted
ORDER BY pp.id,ppc.id
);

COPY (SELECT * FROM _dba_.gateway_pap_export) TO '/home/akinareevski/gateway_pap_export.csv' CSV HEADER;
