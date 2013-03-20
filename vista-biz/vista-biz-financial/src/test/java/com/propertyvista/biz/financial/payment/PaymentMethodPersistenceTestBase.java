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

    private Lease lease;

    private Customer customer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        setSysDate("01-Feb-2012");
        customer = getMockManager().getDataModel(CustomerDataModel.class).addCustomer();
        lease = getMockManager().getDataModel(LeaseDataModel.class).addLease("01-Feb-2012", "01-Sep-2012", new BigDecimal(100), null, customer);
    }

    protected void testPersistPaymentMethod(PaymentType type) throws PaymentException {
        Assert.assertEquals("Preload should have no PaymentMethods", 0, retrieveAllPaymentMethods(customer).size());

        LeasePaymentMethod paymentMethod = createPaymentMethod(type, customer);
        paymentMethod.customer().set(customer);
        paymentMethod.isOneTimePayment().setValue(Boolean.FALSE);
        ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethod,
                getMockManager().getDataModel(BuildingDataModel.class).getBuilding());

        List<LeasePaymentMethod> profileMethods = retrieveProfilePaymentMethodsSerializable(customer);
        assertRpcTransientMemebers(profileMethods);

        Assert.assertEquals("PaymentMethod Added to profile", 1, profileMethods.size());

        // Make a payment
        PaymentRecord paymentRecord = createPaymentRecord(lease, profileMethods.get(0), "100");
        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);

        Assert.assertEquals("Just one PaymentMethod remains", 1, retrieveAllPaymentMethods(customer).size());

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);

        ServerSideFactory.create(PaymentMethodFacade.class).deleteLeasePaymentMethod(paymentMethod);

        profileMethods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(
                getMockManager().getDataModel(CustomerDataModel.class).getCustomer(0));
        Assert.assertEquals(0, profileMethods.size());

        Assert.assertEquals("PaymentMethod remains in DB", 1, retrieveAllPaymentMethods(customer).size());
    }

    protected void testUpdatePaymentMethod(PaymentType type) throws PaymentException {
        Assert.assertEquals("Preload should have no PaymentMethods", 0, retrieveAllPaymentMethods(customer).size());

        {
            LeasePaymentMethod paymentMethod = createPaymentMethod(type, customer);
            paymentMethod.customer().set(getMockManager().getDataModel(CustomerDataModel.class).addCustomer());
            paymentMethod.isOneTimePayment().setValue(Boolean.FALSE);
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethod,
                    getMockManager().getDataModel(BuildingDataModel.class).getBuilding());
        }

        List<LeasePaymentMethod> profileMethods = retrieveProfilePaymentMethodsSerializable(customer);
        assertRpcTransientMemebers(profileMethods);

        Assert.assertEquals("PaymentMethod Added to profile", 1, profileMethods.size());

        // Make a payment
        PaymentRecord paymentRecord = createPaymentRecord(lease, profileMethods.get(0), "100");
        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
        Persistence.service().commit();

        Assert.assertEquals("Just one PaymentMethod remains", 1, retrieveAllPaymentMethods(customer).size());

        {
            profileMethods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(
                    getMockManager().getDataModel(CustomerDataModel.class).getCustomer(0));
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
                            getMockManager().getDataModel(BuildingDataModel.class).getBuilding());
                    Assert.fail("Obfuscated Account numbers should be validated during save");
                } catch (IllegalArgumentException ok) {
                    Persistence.service().rollback();
                }
            }

            if (paymentMethodUpdate.type().getValue() == PaymentType.CreditCard) {
                paymentMethodUpdate = retrieveProfilePaymentMethodsSerializable(customer).get(0);
                CreditCardInfo cc = paymentMethodUpdate.details().cast();
                cc.token().setValue("garbage");
                try {
                    ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate,
                            getMockManager().getDataModel(BuildingDataModel.class).getBuilding());
                    Assert.fail("token changes should be validated during save");
                } catch (Error ok) {
                    Persistence.service().rollback();
                }
            }
        }

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);
        Persistence.service().commit();

        {
            profileMethods = retrieveProfilePaymentMethodsSerializable(customer);
            LeasePaymentMethod paymentMethodUpdate = profileMethods.get(0);
            // Nothing changed, save will not change anything
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate,
                    getMockManager().getDataModel(BuildingDataModel.class).getBuilding());
        }

        {
            profileMethods = retrieveProfilePaymentMethodsSerializable(customer);
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
                        getMockManager().getDataModel(BuildingDataModel.class).getBuilding());
                Assert.fail("Obfuscated Account numbers should be validated during save");
            } catch (IllegalArgumentException ok) {
                Persistence.service().rollback();
            }

            if (paymentMethodUpdate.type().getValue() == PaymentType.CreditCard) {
                paymentMethodUpdate = retrieveProfilePaymentMethodsSerializable(customer).get(0);
                CreditCardInfo cc = paymentMethodUpdate.details().cast();
                cc.token().setValue("garbage");
                try {
                    ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate,
                            getMockManager().getDataModel(BuildingDataModel.class).getBuilding());
                    Assert.fail("token changes should be validated during save");
                } catch (Error ok) {
                    Persistence.service().rollback();
                }
            }
        }

        {
            profileMethods = retrieveProfilePaymentMethodsSerializable(customer);
            LeasePaymentMethod paymentMethodUpdate = profileMethods.get(0);
            setNewPaymentMethodDetails(paymentMethodUpdate);
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethodUpdate,
                    getMockManager().getDataModel(BuildingDataModel.class).getBuilding());
        }

        profileMethods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(
                getMockManager().getDataModel(CustomerDataModel.class).getCustomer(0));
        Assert.assertEquals(1, profileMethods.size());

        Assert.assertEquals("PaymentMethod in DB", 2, retrieveAllPaymentMethods(customer).size());
    }
}
