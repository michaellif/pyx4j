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

import org.junit.Assert;
import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.biz.system.eft.CreditCardPaymentProcessorFacade;
import com.propertyvista.biz.system.eft.EFTTransportFacade;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.eft.mock.cards.CreditCardPaymentProcessorFacadeMock;
import com.propertyvista.eft.mock.efttransport.EFTTransportFacadeMock;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
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

    public void testSuccessfulCardPayment() throws Exception {

    }

    public void TODO_testSuccessfulCardPayment() throws Exception {
        setSysDate("2011-04-01");

        LeasePaymentMethod paymentMethod = customerDataModel.addPaymentMethod(customer, getBuilding(), PaymentType.CreditCard);
        Persistence.service().commit();
        PaymentRecord paymentRecord;
        // Make a payment
        {
            paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "100");
            Persistence.service().commit();

            ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
            Persistence.service().commit();

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Cleared);
        }

        setSysDate("2011-04-02");
        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveCardsReconciliation, (Date) null);

        {
            EntityQueryCriteria<CardsAggregatedTransfer> criteria = EntityQueryCriteria.create(CardsAggregatedTransfer.class);
            CardsAggregatedTransfer at = Persistence.service().retrieve(criteria);
            Assert.assertNotNull("AggregatedTransfer created", at);
            Assert.assertEquals("AggregatedTransfer amounts", paymentRecord.amount().getValue(), at.grossPaymentAmount().getValue());
        }

    }

}
