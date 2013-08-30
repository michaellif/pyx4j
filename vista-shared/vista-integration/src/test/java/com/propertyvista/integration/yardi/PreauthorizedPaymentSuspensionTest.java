/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 * the License.
 *
 * Created on 2013-08-29
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.rmi.RemoteException;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.test.integration.PaymentAgreementTester;
import com.propertyvista.test.integration.PreauthorizedPaymentBuilder;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.AutoPayPolicyDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.yardi.mock.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.RtCustomerUpdater;

public class PreauthorizedPaymentSuspensionTest extends PaymentYardiTestBase {

    private Lease lease;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createYardiLease("prop123", "t000111");

        setSysDate("2011-01-01");
        yardiImportAll(getYardiCredential("prop123"));

        lease = loadLeaseToModel("t000111");
        Tenant tenant = lease.leaseParticipants().iterator().next().cast();
        getDataModel(CustomerDataModel.class).addPaymentMethod(tenant.customer(), lease.unit().building(), PaymentType.Echeck);
    }

    public void testLeaseEndSuspension1() throws Exception {
        getDataModel(AutoPayPolicyDataModel.class).setExcludeLastBillingPeriodCharge(false);
        Persistence.service().commit();

        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YLEASE.ExpectedMoveOutDate, DateUtils.detectDateformat("2012-03-31"));
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        leaseEndSuspentionTestSequence();
    }

    public void testLeaseEndSuspension2() throws Exception {
        getDataModel(AutoPayPolicyDataModel.class).setExcludeLastBillingPeriodCharge(false);
        Persistence.service().commit();

        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YLEASE.ExpectedMoveOutDate, DateUtils.detectDateformat("2012-03-01"));
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        leaseEndSuspentionTestSequence();
    }

    public void testLeaseEndSuspension3() throws Exception {
        getDataModel(AutoPayPolicyDataModel.class).setExcludeLastBillingPeriodCharge(false);
        Persistence.service().commit();

        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YLEASE.ExpectedMoveOutDate, DateUtils.detectDateformat("2012-03-21"));
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        leaseEndSuspentionTestSequence();
    }

    private void leaseEndSuspentionTestSequence() throws RemoteException, YardiServiceException {
        createSomePap();

        setSysDate("2012-01-11");
        yardiImportAll(getYardiCredential("prop123"));
        new PaymentAgreementTester(lease.billingAccount()).activeCount(1); // PAP is NOT suspended

        setSysDate("2012-02-11");
        yardiImportAll(getYardiCredential("prop123"));
        new PaymentAgreementTester(lease.billingAccount()).activeCount(1); // PAP is NOT suspended

        setSysDate("2012-02-29");
        yardiImportAll(getYardiCredential("prop123"));
        new PaymentAgreementTester(lease.billingAccount()).activeCount(0); // PAP is suspended!

        // new round:
        createSomePap();
        setSysDate("2012-03-01");
        yardiImportAll(getYardiCredential("prop123"));
        new PaymentAgreementTester(lease.billingAccount()).activeCount(0); // PAP is suspended!

        // new round:
        createSomePap();
        setSysDate("2012-03-11");
        yardiImportAll(getYardiCredential("prop123"));
        new PaymentAgreementTester(lease.billingAccount()).activeCount(0); // PAP is suspended!

        // new round:
        createSomePap();
        setSysDate("2012-03-31");
        yardiImportAll(getYardiCredential("prop123"));
        new PaymentAgreementTester(lease.billingAccount()).activeCount(0); // PAP is suspended!

        // new round:
        createSomePap();
        setSysDate("2012-04-11");
        yardiImportAll(getYardiCredential("prop123"));
        new PaymentAgreementTester(lease.billingAccount()).activeCount(0); // PAP is suspended!        
    }

    public void testLeaseEndByPolicySuspension1() throws Exception {
        getDataModel(AutoPayPolicyDataModel.class).setExcludeLastBillingPeriodCharge(true);
        Persistence.service().commit();

        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YLEASE.ExpectedMoveOutDate, DateUtils.detectDateformat("2012-03-31"));
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        leaseEndSuspentionByPolicyTestSequence();
    }

    public void testLeaseEndByPolicySuspension2() throws Exception {
        getDataModel(AutoPayPolicyDataModel.class).setExcludeLastBillingPeriodCharge(true);
        Persistence.service().commit();

        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YLEASE.ExpectedMoveOutDate, DateUtils.detectDateformat("2012-03-01"));
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        leaseEndSuspentionByPolicyTestSequence();
    }

    public void testLeaseEndByPolicySuspension3() throws Exception {
        getDataModel(AutoPayPolicyDataModel.class).setExcludeLastBillingPeriodCharge(true);
        Persistence.service().commit();

        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YLEASE.ExpectedMoveOutDate, DateUtils.detectDateformat("2012-03-21"));
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        leaseEndSuspentionByPolicyTestSequence();
    }

    private void leaseEndSuspentionByPolicyTestSequence() throws RemoteException, YardiServiceException {
        createSomePap();

        setSysDate("2012-01-11");
        yardiImportAll(getYardiCredential("prop123"));
        new PaymentAgreementTester(lease.billingAccount()).activeCount(1); // PAP is NOT suspended

        setSysDate("2012-02-11");
        yardiImportAll(getYardiCredential("prop123"));
        new PaymentAgreementTester(lease.billingAccount()).activeCount(0); // PAP is suspended!

        // new round:
        createSomePap();
        setSysDate("2012-02-29");
        yardiImportAll(getYardiCredential("prop123"));
        new PaymentAgreementTester(lease.billingAccount()).activeCount(0); // PAP is suspended!

        // new round:
        createSomePap();
        setSysDate("2012-03-01");
        yardiImportAll(getYardiCredential("prop123"));
        new PaymentAgreementTester(lease.billingAccount()).activeCount(0); // PAP is suspended!
    }

    private void createSomePap() {
        getDataModel(LeaseDataModel.class).createPreauthorizedPayment(lease, new PreauthorizedPaymentBuilder(). //
                add(lease.currentTerm().version().leaseProducts().serviceItem(), "500.00"). //
                add(lease.currentTerm().version().leaseProducts().featureItems().get(0), "80.00"). //
                build());
        Persistence.service().commit();
    }
}
