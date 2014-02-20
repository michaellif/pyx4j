/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.processors;

import java.util.Date;

import com.yardi.entity.leaseapp30.AccountingData;
import com.yardi.entity.leaseapp30.Charge;
import com.yardi.entity.leaseapp30.ChargeSet;
import com.yardi.entity.leaseapp30.ChargeType;
import com.yardi.entity.leaseapp30.Frequency;
import com.yardi.entity.leaseapp30.LALease;
import com.yardi.entity.leaseapp30.LeaseApplication;
import com.yardi.entity.leaseapp30.Name;
import com.yardi.entity.leaseapp30.PropertyType;
import com.yardi.entity.leaseapp30.ResidentType;
import com.yardi.entity.leaseapp30.Tenant;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease;

public class YardiApplicationProcessor {

    public LeaseApplication createApplication(Lease lease) {
        LeaseApplication leaseApp = new LeaseApplication();
        // create main applicant
        Tenant tenant = getMainApplicant(lease);

        // add deposit charges
        AccountingData charges = new AccountingData();
        tenant.setAccountingData(charges);
        ChargeSet chargeSet = new ChargeSet();
        chargeSet.setFrequency(Frequency.ONE_TIME);
        chargeSet.setStart(new Date(0));
        chargeSet.setEnd(new Date(0));
        charges.getChargeSet().add(chargeSet);
        // add service deposits
        for (Deposit deposit : lease.currentTerm().version().leaseProducts().serviceItem().deposits()) {
            chargeSet.getCharge().add(getDepositCharge(deposit));
        }
        // add feature deposits
        for (BillableItem feature : lease.currentTerm().version().leaseProducts().featureItems()) {
            for (Deposit deposit : feature.deposits()) {
                chargeSet.getCharge().add(getDepositCharge(deposit));
            }
        }

        // add lease info
        LALease laLease = new LALease();
        PropertyType property = new PropertyType();
        com.yardi.entity.leaseapp30.Identification propId = new com.yardi.entity.leaseapp30.Identification();
        propId.setIDValue(lease.unit().building().propertyCode().getValue());
        property.getIdentification().add(propId);
        property.setMarketingName(lease.unit().building().marketing().name().getValue(""));
        laLease.setProperty(property);
        laLease.getIdentification().add(tenant.getIdentification().get(0));

        leaseApp.getTenant().add(tenant);
        leaseApp.getLALease().add(laLease);

        return leaseApp;
    }

    private Tenant getMainApplicant(Lease lease) {
        Tenant tenant = new Tenant();
        tenant.setResidentType(ResidentType.INDIVIDUAL);

        Persistence.ensureRetrieve(lease._applicant(), AttachLevel.Attached);
        Name name = new Name();
        name.setFirstName(lease._applicant().customer().person().name().firstName().getValue());
        name.setLastName(lease._applicant().customer().person().name().lastName().getValue());
        tenant.setName(name);

        com.yardi.entity.leaseapp30.Identification tId = new com.yardi.entity.leaseapp30.Identification();
//        tId.setIDType("prospect");
        tId.setIDType("thirdparty");
        tId.setIDValue(lease.getPrimaryKey().toString());
        tenant.getIdentification().add(tId);

        return tenant;
    }

    public Charge getDepositCharge(Deposit deposit) {
        Charge charge = new Charge();
        com.yardi.entity.leaseapp30.Identification chargeId = new com.yardi.entity.leaseapp30.Identification();
        charge.setChargeType(ChargeType.APPLICATION_FEE);
        charge.setLabel(deposit.chargeCode().yardiChargeCodes().get(0).yardiChargeCode().getValue());
        chargeId.setIDValue("");
        chargeId.setOrganizationName(deposit.description().getValue(""));
        charge.getIdentification().add(chargeId);
        charge.setAmount(deposit.amount().getValue().toPlainString());
        return charge;
    }
}
