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
package com.propertyvista.test.preloader;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.person.Person.Sex;
import com.propertyvista.domain.tenant.Customer;

public class CustomerDataModel {

    private static Object[][] customersMeta = new Object[][] {

    { "John", "Doe", Sex.Male, "647-555-1111", "647-333-1111", "17-Mar-1955" },

    { "Jane", "Doe", Sex.Female, "647-666-2222", "647-333-2222", "1-Feb-1960" },

    { "Peter", "Smith", Sex.Male, "647-555-3333", "647-333-3333", "17-Mar-1955" },

    { "Laura", "Smith", Sex.Female, "647-666-4444", "647-333-4444", "1-Feb-1960" },

    { "Richard", "Roe", Sex.Male, "647-555-5555", "647-333-5555", "17-Mar-1955" },

    { "Molly", "Doe", Sex.Female, "647-666-6666", "647-333-6666", "1-Feb-1960" } };

    private final List<Customer> customers;

    public CustomerDataModel(PreloadConfig config) {
        customers = new ArrayList<Customer>();
    }

    public void generate() {
    }

    public Customer getCustomer(int index) {
        return customers.get(index);
    }

    public Customer addCustomer() {
        Object[] customerMeta = customersMeta[customers.size()];
        Customer customer = EntityFactory.create(Customer.class);
        customer.person().name().firstName().setValue((String) customerMeta[0]);
        customer.person().name().lastName().setValue((String) customerMeta[1]);
        customer.person().sex().setValue((Sex) customerMeta[2]);
        customer.person().mobilePhone().setValue((String) customerMeta[3]);
        customer.person().homePhone().setValue((String) customerMeta[4]);
        customer.person().birthDate().setValue(new LogicalDate(DateUtils.detectDateformat((String) customerMeta[5])));
        customers.add(customer);
        Persistence.service().persist(customer);
        return customer;
    }
}
