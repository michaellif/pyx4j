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
 */
package com.propertyvista.integration.yardi;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.experimental.categories.Category;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.system.eft.CreditCardPaymentProcessorFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.payment.ConvenienceFeeCalculationResponseTO;
import com.propertyvista.eft.mock.cards.CreditCardMockFacade;
import com.propertyvista.eft.mock.cards.CreditCardPaymentProcessorFacadeMock;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.yardi.mock.updater.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.updater.PropertyUpdater;

@Category(FunctionalTests.class)
public class PaymentPostingCreditCardYardiTest extends PaymentYardiTestBase {

    private Lease lease;

    private LeasePaymentMethod paymentMethod;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        registerFacadeMock(CreditCardPaymentProcessorFacade.class, CreditCardPaymentProcessorFacadeMock.class);
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

            ServerSideFactory.create(PaymentFacade.class).processPaymentUnitOfWork(paymentRecord, true);

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Received);
        }

        assertEquals("Card Balance", ServerSideFactory.create(CreditCardMockFacade.class).getAccountBalance(paymentMethod), new BigDecimal("-100.00"));
    }

    public void testFailedPostingCancelTransaction() throws Exception {
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
                ServerSideFactory.create(PaymentFacade.class).processPaymentUnitOfWork(paymentRecord, true);
                Assert.fail("Payment should fail");
            } catch (UserRuntimeException expected) {
            }

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Canceled);
        }

        assertEquals("Card Balance", ServerSideFactory.create(CreditCardMockFacade.class).getAccountBalance(paymentMethod), new BigDecimal("0.00"));
    }

    public void testConvenienceFeeFailedPostingCancelTransaction() throws Exception {
        {
            PropertyUpdater updater = new PropertyUpdater("prop1")//
                    .set(PropertyUpdater.MockFeatures.BlockTransactionPostLeases, getLease().leaseId().getValue());
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        // Make a payment
        {
            final PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "100");
            // Add fee
            {
                CreditCardType ccType = paymentRecord.paymentMethod().details().<CreditCardInfo> cast().cardType().getValue();
                ConvenienceFeeCalculationResponseTO fees = ServerSideFactory.create(PaymentFacade.class).getConvenienceFee(getLease().billingAccount(), ccType,
                        paymentRecord.amount().getValue());

                paymentRecord.convenienceFee().setValue(fees.feeAmount().getValue());
                paymentRecord.convenienceFeeReferenceNumber().setValue(fees.transactionNumber().getValue());
                ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
            }
            Persistence.service().commit();
            try {
                ServerSideFactory.create(PaymentFacade.class).processPaymentUnitOfWork(paymentRecord, true);
                Assert.fail("Payment should fail");
            } catch (UserRuntimeException expected) {
            }

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Canceled);
        }
        assertEquals("Card Balance", ServerSideFactory.create(CreditCardMockFacade.class).getConvenienceFeeBalance(), new BigDecimal("0.00"));
        assertEquals("Card Balance", ServerSideFactory.create(CreditCardMockFacade.class).getAccountBalance(paymentMethod), new BigDecimal("0.00"));
    }
}
