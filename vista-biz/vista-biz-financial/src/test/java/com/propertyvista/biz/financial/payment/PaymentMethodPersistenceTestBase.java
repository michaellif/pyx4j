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

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.mock.models.BuildingDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;

public class PaymentMethodPersistenceTestBase extends PaymentTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        setSysDate("01-Feb-2012");
        Customer customer = getDataModel(CustomerDataModel.class).addCustomer();
        getDataModel(CustomerDataModel.class).setCurrentItem(customer);
        Lease lease = getDataModel(LeaseDataModel.class).addLease("01-Feb-2012", "01-Sep-2012", new BigDecimal(100), null, customer);
        getDataModel(LeaseDataModel.class).setCurrentItem(lease);
    }

    protected void testPersistPaymentMethod(PaymentType type) throws PaymentException {

        getDataModel(CustomerDataModel.class).deleteAllPaymentMethods();

        Assert.assertEquals("No PaymentMethods should be present now", 0, getDataModel(CustomerDataModel.class).retrieveAllPaymentMethods().size());

        LeasePaymentMethod paymentMethod = getDataModel(CustomerDataModel.class).createPaymentMethod(type);
        paymentMethod.isProfiledMethod().setValue(Boolean.TRUE);
        ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethod, getDataModel(BuildingDataModel.class).getCurrentItem());

        List<LeasePaymentMethod> profileMethods = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods();
        assertRpcTransientMemebers(profileMethods);

        Assert.assertEquals("PaymentMethod Added to profile", 1, profileMethods.size());

        // Make a payment
        PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(profileMethods.get(0), "100");
        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);

        Assert.assertEquals("Just one PaymentMethod remains", 1, getDataModel(CustomerDataModel.class).retrieveAllPaymentMethods().size());

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);

        ServerSideFactory.create(PaymentMethodFacade.class).deleteLeasePaymentMethod(paymentMethod);

        profileMethods = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods();
        Assert.assertEquals(0, profileMethods.size());

        Assert.assertEquals("PaymentMethod remains in DB", 1, getDataModel(CustomerDataModel.class).retrieveAllPaymentMethods().size());
    }

    protected void testUpdatePaymentMethod(PaymentType type) throws PaymentException {

        getDataModel(CustomerDataModel.class).deleteAllPaymentMethods();

        Assert.assertEquals("No PaymentMethods should be present now", 0, getDataModel(CustomerDataModel.class).retrieveAllPaymentMethods().size());

        {
            LeasePaymentMethod paymentMethod = getDataModel(CustomerDataModel.class).createPaymentMethod(type);
            paymentMethod.isProfiledMethod().setValue(Boolean.TRUE);
            ServerSideFactory.create(PaymentMethodFacade.class)
                    .persistLeasePaymentMethod(paymentMethod, getDataModel(BuildingDataModel.class).getCurrentItem());
        }

        List<LeasePaymentMethod> profileMethods = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods();
        assertRpcTransientMemebers(profileMethods);

        Assert.assertEquals("PaymentMethod Added to profile", 1, profileMethods.size());

        // Make a payment
        PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(profileMethods.get(0), "100");
        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
        Persistence.service().commit();

        Assert.assertEquals("Just one PaymentMethod remains", 1, getDataModel(CustomerDataModel.class).retrieveAllPaymentMethods().size());

        {
            profileMethods = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods();
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
                    ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate,
                            getDataModel(BuildingDataModel.class).getCurrentItem());
                    Assert.fail("Obfuscated Account numbers should be validated during save");
                } catch (IllegalArgumentException ok) {
                    Persistence.service().rollback();
                }
            }

            if (paymentMethodUpdate.type().getValue() == PaymentType.CreditCard) {
                paymentMethodUpdate = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods().get(0);
                CreditCardInfo cc = paymentMethodUpdate.details().cast();
                cc.token().setValue("garbage");
                try {
                    ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate,
                            getDataModel(BuildingDataModel.class).getCurrentItem());
                    Assert.fail("token changes should be validated during save");
                } catch (Error ok) {
                    Persistence.service().rollback();
                }
            }
        }

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);
        Persistence.service().commit();

        {
            profileMethods = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods();
            LeasePaymentMethod paymentMethodUpdate = profileMethods.get(0);
            // Nothing changed, save will not change anything
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate,
                    getDataModel(BuildingDataModel.class).getCurrentItem());
        }

        {
            profileMethods = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods();
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
                ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate,
                        getDataModel(BuildingDataModel.class).getCurrentItem());
                Assert.fail("Obfuscated Account numbers should be validated during save");
            } catch (IllegalArgumentException ok) {
                Persistence.service().rollback();
            }

            if (paymentMethodUpdate.type().getValue() == PaymentType.CreditCard) {
                paymentMethodUpdate = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods().get(0);
                CreditCardInfo cc = paymentMethodUpdate.details().cast();
                cc.token().setValue("garbage");
                try {
                    ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate,
                            getDataModel(BuildingDataModel.class).getCurrentItem());
                    Assert.fail("token changes should be validated during save");
                } catch (Error ok) {
                    Persistence.service().rollback();
                }
            }
        }

        {
            profileMethods = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods();
            LeasePaymentMethod paymentMethodUpdate = profileMethods.get(0);
            getDataModel(CustomerDataModel.class).updatePaymentMethod(paymentMethodUpdate);
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate,
                    getDataModel(BuildingDataModel.class).getCurrentItem());
        }

        profileMethods = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods();
        Assert.assertEquals(1, profileMethods.size());

        Assert.assertEquals("PaymentMethod in DB", 2, getDataModel(CustomerDataModel.class).retrieveAllPaymentMethods().size());
    }
}
