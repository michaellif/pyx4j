/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 8, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.financial.BillingAccount.PaymentAccepted;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.integration.PreauthorizedPaymentBuilder;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.yardi.mock.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.PropertyUpdater;
import com.propertyvista.yardi.mock.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.RtCustomerUpdater;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

public class PreauthorizedPaymentProcessYardiTest extends PaymentYardiTestBase {

    private final List<Lease> leasesAll = new ArrayList<Lease>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setPaymentBatchProcess();
        setCaledonPadPaymentBatchProcess();

        createPpoperty("prop1", 10);
        createPpoperty("prop2", 10);
        createPpoperty("prop3", 1);

        setSysDate("2011-01-01");
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop1,prop2,prop3"), new ExecutionMonitor());

        loadBuildingToModel("prop1");
        loadBuildingToModel("prop2");
        loadBuildingToModel("prop3");

        for (Lease lease : Persistence.service().query(EntityQueryCriteria.create(Lease.class))) {
            leasesAll.add(loadLeaseAndCreatePAP(lease.leaseId().getValue()));
        }
    }

    private void createPpoperty(String propertyId, int leaseCount) {
        {
            // @formatter:off
            PropertyUpdater updater = new PropertyUpdater(propertyId).
            set(PropertyUpdater.ADDRESS.Address1, "11 " + propertyId  + " str").
            set(PropertyUpdater.ADDRESS.Country, "Canada");        
            // @formatter:on
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        for (int i = 1; i <= leaseCount; i++) {
            createYardiLease(propertyId, "t" + propertyId + String.valueOf(i));
        }
    }

    protected Lease loadLeaseAndCreatePAP(String leaseId) {
        Lease lease = loadLeaseToModel(leaseId);
        Tenant tenant = lease.leaseParticipants().iterator().next().cast();
        getDataModel(CustomerDataModel.class).addPaymentMethod(tenant.customer(), lease.unit().building(), PaymentType.Echeck);

        getDataModel(LeaseDataModel.class).createPreauthorizedPayment(lease, new PreauthorizedPaymentBuilder(). //
                add(lease.currentTerm().version().leaseProducts().serviceItem(), "500.00"). //
                add(lease.currentTerm().version().leaseProducts().featureItems().get(0), "80.00"). //
                build());

        Persistence.service().commit();
        return lease;
    }

    protected LogicalDate getNextAutopayDate(Lease lease) {
        return ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(lease);
    }

    public void testPadSuccessful() throws Exception {
        assertEquals("PAD next Generation date", "2011-02-01", getNextAutopayDate(leasesAll.get(0)));

        // PAD creation triggered at the end of the month
        // Post and process all payments
        advanceSysDate("2011-02-01");

        // Expect PAD executed, verify amount
        for (Lease lease : leasesAll) {
            new PaymentRecordTester(lease.billingAccount())//
                    .count(1) //
                    .lastRecordStatus(PaymentStatus.Cleared)//
                    .lastRecordAmount("580.00");
        }
    }

    public void testBatchPartialCompleation() throws Exception {
        assertEquals("PAD next Generation date", "2011-02-01", getNextAutopayDate(leasesAll.get(0)));

        // Make some lease fail to post
        List<Lease> leaseToFail = new ArrayList<Lease>();
        leaseToFail.add(leasesAll.get(3));
        leaseToFail.add(leasesAll.get(11));
        for (Lease lease : leaseToFail) {
            RtCustomerUpdater updater = new RtCustomerUpdater(lease.unit().building().propertyCode().getValue(), lease.leaseId().getValue());
            updater.set(RtCustomerUpdater.RTCUSTOMER.PaymentAccepted, String.valueOf(PaymentAccepted.DoNotAccept.paymentCode()));
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        // PAD creation triggered at the end of the month
        // Post and process all payments
        advanceSysDate("2011-02-01");

        // Expect PAD executed, verify amount
        for (Lease lease : leaseToFail) {
            new PaymentRecordTester(lease.billingAccount())//
                    .count(1) //
                    .lastRecordStatus(PaymentStatus.Scheduled)//
                    .lastRecordAmount("580.00");
        }

        for (Lease lease : leasesAll) {
            if (leaseToFail.contains(lease)) {
                continue;
            }
            new PaymentRecordTester(lease.billingAccount())//
                    .count(1) //
                    .lastRecordStatus(PaymentStatus.Cleared)//
                    .lastRecordAmount("580.00");
        }

        // Recover the tenants
        for (Lease lease : leaseToFail) {
            RtCustomerUpdater updater = new RtCustomerUpdater(lease.unit().building().propertyCode().getValue(), lease.leaseId().getValue());
            updater.set(RtCustomerUpdater.RTCUSTOMER.PaymentAccepted, String.valueOf(PaymentAccepted.Any.paymentCode()));
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        advanceSysDate("2011-02-02");
        for (Lease lease : leaseToFail) {
            new PaymentRecordTester(lease.billingAccount())//
                    .count(1)//
                    .lastRecordStatus(PaymentStatus.Cleared);
        }
    }
}
