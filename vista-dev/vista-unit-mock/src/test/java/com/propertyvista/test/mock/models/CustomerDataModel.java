/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.io.Serializable;
import java.util.List;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.RpcEntityServiceFilter;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.person.Person.Sex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.test.mock.MockDataModel;

public class CustomerDataModel extends MockDataModel<Customer> {

    private static int counter = 0;

    private static Object[][] customersMeta = new Object[][] {

    { "John", "Doe", Sex.Male, "647-555-1111", "647-333-1111", "17-Mar-1955" },

    { "Jane", "Doe", Sex.Female, "647-666-2222", "647-333-2222", "1-Feb-1960" },

    { "Peter", "Smith", Sex.Male, "647-555-3333", "647-333-3333", "17-Mar-1955" },

    { "Laura", "Smith", Sex.Female, "647-666-4444", "647-333-4444", "1-Feb-1960" },

    { "Richard", "Roe", Sex.Male, "647-555-5555", "647-333-5555", "17-Mar-1955" },

    { "Molly", "Doe", Sex.Female, "647-666-6666", "647-333-6666", "1-Feb-1960" } };

    public CustomerDataModel() {

    }

    @Override
    protected void generate() {
    }

    public Customer addCustomer() {
        Object[] customerMeta = customersMeta[counter++ % customersMeta.length];
        Customer customer = EntityFactory.create(Customer.class);
        customer.person().name().firstName().setValue((String) customerMeta[0]);
        customer.person().name().lastName().setValue((String) customerMeta[1]);
        customer.person().sex().setValue((Sex) customerMeta[2]);
        customer.person().mobilePhone().setValue((String) customerMeta[3]);
        customer.person().homePhone().setValue((String) customerMeta[4]);
        customer.person().birthDate().setValue(new LogicalDate(DateUtils.detectDateformat((String) customerMeta[5])));
        Persistence.service().persist(customer);

        addItem(customer);
        return customer;
    }

    public LeasePaymentMethod addPaymentMethod(Customer customer, Building building, PaymentType type) {
        return createPaymentMethod(customer, building, type);
    }

    private LeasePaymentMethod createPaymentMethod(Customer customer, Building building, PaymentType type) {
        LeasePaymentMethod paymentMethod = EntityFactory.create(LeasePaymentMethod.class);
        paymentMethod.customer().set(customer);
        paymentMethod.type().setValue(type);
        paymentMethod.isProfiledMethod().setValue(Boolean.TRUE);

        switch (type) {
        case Echeck: {
            EcheckInfo details = EntityFactory.create(EcheckInfo.class);
            setEcheckInfoDetails(details);
            paymentMethod.details().set(details);
        }
            break;
        case CreditCard: {
            CreditCardInfo details = EntityFactory.create(CreditCardInfo.class);
            setCreditCardDetails(details);
            paymentMethod.details().set(details);
        }
            break;
        default:
            throw new IllegalArgumentException();
        }

        ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethod, building);

        return paymentMethod;
    }

    public void updatePaymentMethod(LeasePaymentMethod paymentMethod) {
        switch (paymentMethod.type().getValue()) {
        case Echeck:
            setEcheckInfoDetails((EcheckInfo) paymentMethod.details().cast());
            break;
        case CreditCard:
            setCreditCardDetails((CreditCardInfo) paymentMethod.details().cast());
            break;
        default:
            throw new IllegalArgumentException();
        }

    }

    public void deletePaymentMethod(LeasePaymentMethod paymentMethod) {
        ServerSideFactory.create(PaymentMethodFacade.class).deleteLeasePaymentMethod(paymentMethod);
    }

    public void deleteAllPaymentMethods(Customer customer) {
        for (LeasePaymentMethod paymentMethod : retrieveAllPaymentMethods(customer)) {
            deletePaymentMethod(paymentMethod);
        }
    }

    private void setEcheckInfoDetails(EcheckInfo details) {
        details.bankId().setValue(CommonsStringUtils.paddZerro(DataGenerator.randomInt(999), 3));
        details.branchTransitNumber().setValue(CommonsStringUtils.paddZerro(DataGenerator.randomInt(99999), 5));
        details.accountNo().newNumber().setValue(Integer.toString(DataGenerator.randomInt(99999)) + Integer.toString(DataGenerator.randomInt(999999)));
    }

    private void setCreditCardDetails(CreditCardInfo details) {
        details.cardType().setValue(CreditCardType.Visa);
        details.card().newNumber().setValue(CreditCardNumberGenerator.generateCardNumber(details.cardType().getValue()));
        details.expiryDate().setValue(new LogicalDate(2015 - 1900, 1, 1));
    }

    public List<LeasePaymentMethod> retrieveAllPaymentMethods(Customer customer) {
        EntityQueryCriteria<LeasePaymentMethod> criteria = new EntityQueryCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().customer(), customer));
        return Persistence.service().query(criteria);
    }

    public List<LeasePaymentMethod> retrieveSerializableProfilePaymentMethods(Customer customer) {
        EntityQueryCriteria<LeasePaymentMethod> criteria = new EntityQueryCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
        criteria.eq(criteria.proto().customer(), customer);
        criteria.eq(criteria.proto().isProfiledMethod(), Boolean.TRUE);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        List<LeasePaymentMethod> profileMethods = Persistence.service().query(criteria);
        RpcEntityServiceFilter.filterRpcTransient((Serializable) profileMethods);
        return profileMethods;
    }

}
