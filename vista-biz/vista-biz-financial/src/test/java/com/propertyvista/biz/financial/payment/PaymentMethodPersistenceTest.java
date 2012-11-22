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

import java.util.List;

import junit.framework.Assert;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;

@Category({ FunctionalTests.class })
public class PaymentMethodPersistenceTest extends PaymentTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        SysDateManager.setSysDate("01-Feb-2012");
        createLease("01-Feb-2012", "31-Dec-2012");
    }

    private void testPersistPaymentMethod(PaymentType type) {
        Assert.assertEquals("Preload should have no PaymentMethods", 0, retrieveAllPaymentMethods().size());

        LeasePaymentMethod paymentMethod = createPaymentMethod(type);
        paymentMethod.customer().set(tenantDataModel.getTenantCustomer());
        paymentMethod.isOneTimePayment().setValue(Boolean.FALSE);
        ServerSideFactory.create(PaymentFacade.class).persistPaymentMethod(buildingDataModel.getBuilding(), paymentMethod);

        List<LeasePaymentMethod> profileMethods = ServerSideFactory.create(PaymentFacade.class).retrievePaymentMethods(tenantDataModel.getTenantCustomer());

        Assert.assertEquals("PaymentMethod Added to profile", 1, profileMethods.size());

        // Make a payment
        PaymentRecord paymentRecord = createPaymentRecord(profileMethods.get(0), "100");
        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);

        Assert.assertEquals("Just one PaymentMethod remains", 1, retrieveAllPaymentMethods().size());

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);

        ServerSideFactory.create(PaymentFacade.class).deletePaymentMethod(paymentMethod);

        profileMethods = ServerSideFactory.create(PaymentFacade.class).retrievePaymentMethods(tenantDataModel.getTenantCustomer());
        Assert.assertEquals(0, profileMethods.size());

        Assert.assertEquals("PaymentMethod remains in DB", 1, retrieveAllPaymentMethods().size());
    }

    public void testPersistPaymentMethodEcheck() {
        testPersistPaymentMethod(PaymentType.Echeck);
    }

    public void testPersistPaymentMethodCreditCard() {
        testPersistPaymentMethod(PaymentType.CreditCard);
    }

    public void testUpdatePaymentMethod(PaymentType type) {
        Assert.assertEquals("Preload should have no PaymentMethods", 0, retrieveAllPaymentMethods().size());

        LeasePaymentMethod paymentMethod = createPaymentMethod(type);
        paymentMethod.customer().set(tenantDataModel.getTenantCustomer());
        paymentMethod.isOneTimePayment().setValue(Boolean.FALSE);
        ServerSideFactory.create(PaymentFacade.class).persistPaymentMethod(buildingDataModel.getBuilding(), paymentMethod);

        List<LeasePaymentMethod> profileMethods = ServerSideFactory.create(PaymentFacade.class).retrievePaymentMethods(tenantDataModel.getTenantCustomer());

        Assert.assertEquals("PaymentMethod Added to profile", 1, profileMethods.size());

        // Make a payment
        PaymentRecord paymentRecord = createPaymentRecord(profileMethods.get(0), "100");
        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);

        Assert.assertEquals("Just one PaymentMethod remains", 1, retrieveAllPaymentMethods().size());

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);
        Persistence.service().commit();

        {
            profileMethods = ServerSideFactory.create(PaymentFacade.class).retrievePaymentMethods(tenantDataModel.getTenantCustomer());
            LeasePaymentMethod paymentMethodUpdate = profileMethods.get(0);
            try {
                ServerSideFactory.create(PaymentFacade.class).persistPaymentMethod(buildingDataModel.getBuilding(), paymentMethodUpdate);
                Assert.fail("Account numbers should be validated during save");
            } catch (IllegalArgumentException ok) {
                Persistence.service().rollback();
            }
        }

        {
            profileMethods = ServerSideFactory.create(PaymentFacade.class).retrievePaymentMethods(tenantDataModel.getTenantCustomer());
            LeasePaymentMethod paymentMethodUpdate = profileMethods.get(0);
            setNewPaymentMethodDetails(paymentMethodUpdate);
            ServerSideFactory.create(PaymentFacade.class).persistPaymentMethod(buildingDataModel.getBuilding(), paymentMethodUpdate);
        }

        profileMethods = ServerSideFactory.create(PaymentFacade.class).retrievePaymentMethods(tenantDataModel.getTenantCustomer());
        Assert.assertEquals(1, profileMethods.size());

        Assert.assertEquals("PaymentMethod in DB", 2, retrieveAllPaymentMethods().size());
    }

    public void testUpdatePaymentMethodEcheck() {
        testUpdatePaymentMethod(PaymentType.Echeck);
    }

    public void testUpdatePaymentMethodCreditCard() {
        testUpdatePaymentMethod(PaymentType.CreditCard);
    }
}
