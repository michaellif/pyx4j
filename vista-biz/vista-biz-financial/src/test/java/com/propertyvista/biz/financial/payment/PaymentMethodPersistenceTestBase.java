/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.List;

import junit.framework.Assert;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;

public class PaymentMethodPersistenceTestBase extends LeaseFinancialTestBase {

    private CustomerDataModel customerDataModel;

    private Customer customer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        setSysDate("01-Feb-2012");
        customerDataModel = getDataModel(CustomerDataModel.class);
        customer = customerDataModel.addCustomer();
        createLease("01-Feb-2012", "01-Sep-2012", new BigDecimal(100), BigDecimal.ZERO, customer);
        activateLease();
    }

    protected void testPersistPaymentMethod(PaymentType type) throws PaymentException {

        customerDataModel.deleteAllPaymentMethods(customer);

        int existingPaymentMethodsCount = customerDataModel.retrieveAllPaymentMethods(customer).size();

        Customer customer = getDataModel(CustomerDataModel.class).getItem(0);

        LeasePaymentMethod paymentMethod = customerDataModel.addPaymentMethod(customer, getBuilding(), type);

        List<LeasePaymentMethod> profileMethods = customerDataModel.retrieveSerializableProfilePaymentMethods(customer);
        assertRpcTransientMemebers(profileMethods);

        Assert.assertEquals("PaymentMethod Added to profile", 1, profileMethods.size());

        // Make a payment
        PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), profileMethods.get(0), "100");

        Assert.assertEquals("Just one PaymentMethod remains", existingPaymentMethodsCount + 1, customerDataModel.retrieveAllPaymentMethods(customer).size());

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);

        ServerSideFactory.create(PaymentMethodFacade.class).deleteLeasePaymentMethod(paymentMethod);

        profileMethods = customerDataModel.retrieveSerializableProfilePaymentMethods(customer);
        Assert.assertEquals(0, profileMethods.size());

        Assert.assertEquals("PaymentMethod remains in DB", existingPaymentMethodsCount + 1, customerDataModel.retrieveAllPaymentMethods(customer).size());
    }

    protected void testUpdatePaymentMethod(PaymentType type) throws PaymentException {

        customerDataModel.deleteAllPaymentMethods(customer);

        int existingPaymentMethodsCount = customerDataModel.retrieveAllPaymentMethods(customer).size();

        customerDataModel.addPaymentMethod(customer, getBuilding(), type);

        List<LeasePaymentMethod> profileMethods = customerDataModel.retrieveSerializableProfilePaymentMethods(customer);
        assertRpcTransientMemebers(profileMethods);

        Assert.assertEquals("PaymentMethod Added to profile", 1, profileMethods.size());

        // Make a payment
        PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), profileMethods.get(0), "100");

        Persistence.service().commit();

        Assert.assertEquals("Just one PaymentMethod remains", existingPaymentMethodsCount + 1, customerDataModel.retrieveAllPaymentMethods(customer).size());

        {
            profileMethods = customerDataModel.retrieveSerializableProfilePaymentMethods(customer);
            LeasePaymentMethod paymentMethodUpdate = profileMethods.get(0);
            {
                switch (paymentMethodUpdate.type().getValue()) {
                case Echeck:
                    EcheckInfo ec = paymentMethodUpdate.details().cast();
                    ec.accountNo().obfuscatedNumber().setValue("garbage");
                    break;
                case CreditCard:
                    CreditCardInfo cc = paymentMethodUpdate.details().cast();
                    cc.card().obfuscatedNumber().setValue("garbage");
                    break;
                default:
                    throw new IllegalArgumentException();
                }
                try {
                    ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate, getBuilding());
                    Assert.fail("Obfuscated Account numbers should be validated during save");
                } catch (IllegalArgumentException ok) {
                    Persistence.service().rollback();
                }
            }

            if (paymentMethodUpdate.type().getValue() == PaymentType.CreditCard) {
                paymentMethodUpdate = customerDataModel.retrieveSerializableProfilePaymentMethods(customer).get(0);
                CreditCardInfo cc = paymentMethodUpdate.details().cast();
                cc.token().setValue("garbage");
                try {
                    ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate, getBuilding());
                    Assert.fail("token changes should be validated during save");
                } catch (Error ok) {
                    Persistence.service().rollback();
                }
            }
        }

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
        Persistence.service().commit();

        {
            profileMethods = customerDataModel.retrieveSerializableProfilePaymentMethods(customer);
            LeasePaymentMethod paymentMethodUpdate = profileMethods.get(0);
            // Nothing changed, save will not change anything
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate, getBuilding());
        }

        {
            profileMethods = customerDataModel.retrieveSerializableProfilePaymentMethods(customer);
            LeasePaymentMethod paymentMethodUpdate = profileMethods.get(0);
            switch (paymentMethodUpdate.type().getValue()) {
            case Echeck:
                EcheckInfo ec = paymentMethodUpdate.details().cast();
                ec.accountNo().obfuscatedNumber().setValue("garbage");
                break;
            case CreditCard:
                CreditCardInfo cc = paymentMethodUpdate.details().cast();
                cc.card().obfuscatedNumber().setValue("garbage");
                break;
            default:
                throw new IllegalArgumentException();
            }
            try {
                ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate, getBuilding());
                Assert.fail("Obfuscated Account numbers should be validated during save");
            } catch (IllegalArgumentException ok) {
                Persistence.service().rollback();
            }

            if (paymentMethodUpdate.type().getValue() == PaymentType.CreditCard) {
                paymentMethodUpdate = customerDataModel.retrieveSerializableProfilePaymentMethods(customer).get(0);
                CreditCardInfo cc = paymentMethodUpdate.details().cast();
                cc.token().setValue("garbage");
                try {
                    ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate, getBuilding());
                    Assert.fail("token changes should be validated during save");
                } catch (Error ok) {
                    Persistence.service().rollback();
                }
            }
        }

        {
            profileMethods = customerDataModel.retrieveSerializableProfilePaymentMethods(customer);
            LeasePaymentMethod paymentMethodUpdate = profileMethods.get(0);
            customerDataModel.updatePaymentMethod(paymentMethodUpdate);
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate, getBuilding());
        }

        profileMethods = customerDataModel.retrieveSerializableProfilePaymentMethods(customer);
        Assert.assertEquals(1, profileMethods.size());

        Assert.assertEquals("PaymentMethod in DB", existingPaymentMethodsCount + 2, customerDataModel.retrieveAllPaymentMethods(customer).size());
    }

    protected void assertRpcTransientMemebers(List<LeasePaymentMethod> methods) {
        for (LeasePaymentMethod method : methods) {
            assertRpcTransientMemebers(method);
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected void assertRpcTransientMemebers(LeasePaymentMethod paymentMethod) {
        switch (paymentMethod.type().getValue()) {
        case Echeck:
            EcheckInfo ec = paymentMethod.details().cast();
            Assert.assertNull(ec.accountNo().number().getValue());
            break;
        case CreditCard:
            CreditCardInfo cc = paymentMethod.details().cast();
            Assert.assertNull(cc.card().number().getValue());
            Assert.assertNull(cc.securityCode().getValue());
            break;
        }
    }
}
