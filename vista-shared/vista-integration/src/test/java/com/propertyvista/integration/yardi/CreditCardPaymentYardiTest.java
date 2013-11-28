/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 28, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.financial.payment.PaymentException;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.payment.CreditCardPaymentProcessorFacade;
import com.propertyvista.payment.cards.CreditCardMockFacade;
import com.propertyvista.payment.cards.CreditCardPaymentProcessorFacadeMock;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.yardi.mock.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.PropertyUpdater;

@Category(FunctionalTests.class)
public class CreditCardPaymentYardiTest extends PaymentYardiTestBase {

    private Lease lease;

    private LeasePaymentMethod paymentMethod;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ServerSideFactory.register(CreditCardPaymentProcessorFacade.class, CreditCardPaymentProcessorFacadeMock.class);
        CreditCardPaymentProcessorFacadeMock.init();

        createYardiBuilding("prop1");
        createYardiLease("prop1", "t000111");
        setSysDate("2011-01-01");
        yardiImportAll(getYardiCredential("prop1"));
        loadBuildingToModel("prop1");
        lease = loadLeaseToModel("t000111");
        Tenant tenant = lease.leaseParticipants().iterator().next().cast();
        paymentMethod = getDataModel(CustomerDataModel.class).addPaymentMethod(tenant.customer(), lease.unit().building(), PaymentType.CreditCard);
    }

    protected Lease getLease() {
        return lease;
    }

    public void testSuccessfulCardPayment() throws Exception {

        // Make a payment
        {
            PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "100");
            Persistence.service().commit();

            ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
            Persistence.service().commit();

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Cleared);
        }

        assertEquals(ServerSideFactory.create(CreditCardMockFacade.class).getAccountBalance(paymentMethod), new BigDecimal("-100.00"));
    }

    public void TODO_testFailedPostingVoidTransaction() throws Exception {
        {
            PropertyUpdater updater = new PropertyUpdater("prop1")//
                    .set(PropertyUpdater.MockFeatures.BlockTransactionPostLeases, getLease().leaseId().getValue());
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        // Make a payment
        {
            final PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "100");
            Persistence.service().commit();
            try {
                new UnitOfWork().execute(new Executable<Void, PaymentException>() {

                    @Override
                    public Void execute() throws PaymentException {
                        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
                        return null;
                    }
                });
                Assert.fail("Payment should fail");
            } catch (PaymentException expected) {
            }

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Void);
        }

        assertEquals(ServerSideFactory.create(CreditCardMockFacade.class).getAccountBalance(paymentMethod), new BigDecimal("-100.00"));
    }
}
