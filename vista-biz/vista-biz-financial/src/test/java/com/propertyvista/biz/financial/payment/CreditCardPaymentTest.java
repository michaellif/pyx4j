/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.system.eft.CreditCardPaymentProcessorFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.dto.payment.ConvenienceFeeCalculationResponseTO;
import com.propertyvista.eft.mock.cards.CreditCardMockFacade;
import com.propertyvista.eft.mock.cards.CreditCardPaymentProcessorFacadeMock;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;

@Category({ FunctionalTests.class })
public class CreditCardPaymentTest extends LeaseFinancialTestBase {

    private CustomerDataModel customerDataModel;

    private Customer customer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        registerFacadeMock(CreditCardPaymentProcessorFacade.class, CreditCardPaymentProcessorFacadeMock.class);
        CreditCardPaymentProcessorFacadeMock.init();

        customerDataModel = getDataModel(CustomerDataModel.class);
        customer = customerDataModel.addCustomer();
        createLease("01-Feb-2011", "01-Sep-2012", new BigDecimal(100), null, customer);

    }

    public void testSuccessfulCardPayment() throws Exception {
        setSysDate("2011-04-01");

        LeasePaymentMethod paymentMethod = customerDataModel.addPaymentMethod(customer, getBuilding(), PaymentType.CreditCard);
        Persistence.service().commit();
        assertEquals(ServerSideFactory.create(CreditCardMockFacade.class).getAccountBalance(paymentMethod), new BigDecimal("0"));

        // Make a payment
        {
            PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "100");
            Persistence.service().commit();

            ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
            Persistence.service().commit();

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Cleared);
        }

        // Make a payment over limit
        {
            PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "15000");
            Persistence.service().commit();

            ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
            Persistence.service().commit();

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Rejected);
        }

        assertEquals(ServerSideFactory.create(CreditCardMockFacade.class).getAccountBalance(paymentMethod), new BigDecimal("-100.00"));
    }

    public void testConvenienceFeeCardPayment() throws Exception {
        setSysDate("2011-04-01");

        LeasePaymentMethod paymentMethod = customerDataModel.addPaymentMethod(customer, getBuilding(), PaymentType.CreditCard);
        Persistence.service().commit();

        // Make a payment
        {
            PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "500");
            Persistence.service().commit();

            CreditCardType ccType = paymentRecord.paymentMethod().details().<CreditCardInfo> cast().cardType().getValue();
            ConvenienceFeeCalculationResponseTO fees = ServerSideFactory.create(PaymentFacade.class).getConvenienceFee(getLease().billingAccount(), ccType,
                    paymentRecord.amount().getValue());

            paymentRecord.convenienceFee().setValue(fees.feeAmount().getValue());
            paymentRecord.convenienceFeeReferenceNumber().setValue(fees.transactionNumber().getValue());
            ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);

            ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
            Persistence.service().commit();

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Cleared);

            assertEquals(ServerSideFactory.create(CreditCardMockFacade.class).getConvenienceFeeBalance(), paymentRecord.convenienceFee().getValue());
            assertEquals(ServerSideFactory.create(CreditCardMockFacade.class).getAccountBalance(paymentMethod), new BigDecimal("-550.00"));
        }

    }
}
