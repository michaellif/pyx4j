/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 9, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.experimental.categories.Category;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.biz.system.eft.CreditCardPaymentProcessorFacade;
import com.propertyvista.biz.system.eft.EFTTransportFacade;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.eft.mock.cards.CreditCardPaymentProcessorFacadeMock;
import com.propertyvista.eft.mock.efttransport.EFTTransportFacadeMock;
import com.propertyvista.operations.domain.eft.cards.to.CreditCardPaymentInstrument;
import com.propertyvista.operations.domain.eft.cards.to.Merchant;
import com.propertyvista.operations.domain.eft.cards.to.PaymentRequest;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.models.TenantSureMerchantAccountDataModel;
import com.propertyvista.test.mock.models.VistaEquifaxMerchantAccountDataModel;
import com.propertyvista.test.mock.schedule.OperationsTriggerFacadeMock;
import com.propertyvista.test.mock.schedule.SchedulerMock;

@Category({ FunctionalTests.class })
public class CardsReconciliationTest extends LeaseFinancialTestBase {

    private CustomerDataModel customerDataModel;

    private Customer customer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        registerFacadeMock(CreditCardPaymentProcessorFacade.class, CreditCardPaymentProcessorFacadeMock.class);
        CreditCardPaymentProcessorFacadeMock.init();

        registerFacadeMock(EFTTransportFacade.class, EFTTransportFacadeMock.class);
        registerFacadeMock(OperationsTriggerFacade.class, OperationsTriggerFacadeMock.class);
        EFTTransportFacadeMock.init();

        customerDataModel = getDataModel(CustomerDataModel.class);
        customer = customerDataModel.addCustomer();
        createLease("01-Feb-2011", "01-Sep-2012", new BigDecimal(100), BigDecimal.ZERO, customer);
        activateLease();

    }

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = super.getMockModelTypes();
        models.add(TenantSureMerchantAccountDataModel.class);
        models.add(VistaEquifaxMerchantAccountDataModel.class);
        return models;
    }

    public void testSingleCardPayment() throws Exception {
        setSysDate("2011-04-01");

        LeasePaymentMethod paymentMethodVisa = customerDataModel.addPaymentMethodCard(customer, getBuilding(), CreditCardType.Visa);
        Persistence.service().commit();
        PaymentRecord paymentRecordVista;
        // Make a payment
        {
            paymentRecordVista = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethodVisa, "100");
            Persistence.service().commit();

            ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecordVista, null);
            Persistence.service().commit();

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Received);
        }

        setSysDate("2011-04-02");
        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveCardsReconciliation, (Date) null);

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Cleared);

        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveCardsReconciliation, (Date) null);

        Persistence.service().retrieve(paymentMethodVisa);

        {
            EntityQueryCriteria<CardsAggregatedTransfer> criteria = EntityQueryCriteria.create(CardsAggregatedTransfer.class);
            CardsAggregatedTransfer at = Persistence.service().retrieve(criteria);
            Assert.assertNotNull("AggregatedTransfer created", at);
            assertEquals("AggregatedTransfer amounts", paymentRecordVista.amount().getValue(), at.grossPaymentAmount().getValue());

            assertEquals("Visa amounts", paymentRecordVista.amount().getValue(), at.visaDeposit().getValue());

        }

    }

    public void testMultipleCardPayment() throws Exception {
        setSysDate("2011-05-01");

        LeasePaymentMethod paymentMethodVisa = customerDataModel.addPaymentMethodCard(customer, getBuilding(), CreditCardType.Visa);
        LeasePaymentMethod paymentMethodMC = customerDataModel.addPaymentMethodCard(customer, getBuilding(), CreditCardType.MasterCard);
        Persistence.service().commit();
        PaymentRecord paymentRecordVista;
        PaymentRecord paymentRecordMC;
        // Make a payment
        {
            paymentRecordVista = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethodVisa, "100");
            Persistence.service().commit();

            ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecordVista, null);
            Persistence.service().commit();

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Received);
        }

        {
            paymentRecordMC = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethodMC, "248.10");
            Persistence.service().commit();

            ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecordMC, null);
            Persistence.service().commit();

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Received);
        }

        setSysDate("2011-05-02");
        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveCardsReconciliation, (Date) null);

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Cleared);

        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveCardsReconciliation, (Date) null);

        Persistence.service().retrieve(paymentMethodVisa);
        Persistence.service().retrieve(paymentRecordMC);

        {
            EntityQueryCriteria<CardsAggregatedTransfer> criteria = EntityQueryCriteria.create(CardsAggregatedTransfer.class);
            CardsAggregatedTransfer at = Persistence.service().retrieve(criteria);
            Assert.assertNotNull("AggregatedTransfer created", at);
            assertEquals("AggregatedTransfer amounts", //
                    paymentRecordVista.amount().getValue().add(paymentRecordMC.amount().getValue()), at.grossPaymentAmount().getValue());

            assertEquals("Visa amounts", paymentRecordVista.amount().getValue(), at.visaDeposit().getValue());
            assertEquals("MasterCard amounts", paymentRecordMC.amount().getValue(), at.mastercardDeposit().getValue());

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Cleared);
        }

    }

    //TODO  Test invalid transactions handling

    public void testNonVistaTransactions() {
        setSysDate("2011-04-01");

        // Make NonVistaTransaction
        {
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            criteria.eq(criteria.proto().invalid(), Boolean.FALSE);
            criteria.eq(criteria.proto().status(), MerchantAccountActivationStatus.Active);
            criteria.eq(criteria.proto()._buildings(), getBuilding());
            MerchantAccount merchantAccount = Persistence.service().retrieve(criteria);

            Merchant merchant = EntityFactory.create(Merchant.class);
            merchant.terminalID().setValue(merchantAccount.merchantTerminalId().getValue());

            PaymentRequest request = EntityFactory.create(PaymentRequest.class);
            request.referenceNumber().setValue("1234");
            request.amount().setValue(BigDecimal.TEN);

            CreditCardPaymentInstrument ccInfo = EntityFactory.create(CreditCardPaymentInstrument.class);
//            ccInfo.creditCardNumber().setValue(cc.card().newNumber().getValue());
            ccInfo.creditCardExpiryDate().setValue(new LogicalDate());
//            ccInfo.securityCode().setValue(cc.securityCode().getValue());
            ccInfo.cardType().setValue(CreditCardType.MasterCard);

            request.paymentInstrument().set(ccInfo);

            ServerSideFactory.create(CreditCardPaymentProcessorFacade.class).realTimeSale(merchant, request);
        }

        setSysDate("2011-04-02");
        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveCardsReconciliation, (Date) null);
        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveCardsReconciliation, (Date) null);

        {
            EntityQueryCriteria<CardsAggregatedTransfer> criteria = EntityQueryCriteria.create(CardsAggregatedTransfer.class);
            CardsAggregatedTransfer at = Persistence.service().retrieve(criteria);
            Assert.assertNotNull("AggregatedTransfer created", at);
            assertEquals("AggregatedTransfer amounts", BigDecimal.TEN, at.netAmount().getValue());
            assertEquals("AggregatedTransfer amounts", BigDecimal.ZERO, at.grossPaymentAmount().getValue());

            assertEquals("Visa amounts", BigDecimal.ZERO, at.visaDeposit().getValue());
            assertEquals("MasterCard amounts", BigDecimal.TEN, at.mastercardDeposit().getValue());

            assertEquals("Has Non Vista Transactions", 1, at.nonVistaTransactions().size());
            assertEquals("Non Vista Transaction", BigDecimal.TEN, at.nonVistaTransactions().get(0).amount().getValue());

        }
    }

}
