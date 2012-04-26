/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonScreening;

public class CustomerRetriever {

    private Customer customer;

    private PersonScreening screening;

    private final boolean retrieveFinancialData;

    public CustomerRetriever() {
        this(false);
    }

    public CustomerRetriever(boolean retrieveFinancialData) {
        this.retrieveFinancialData = retrieveFinancialData;
    }

    public CustomerRetriever(Key tenantId) {
        this(tenantId, false);
    }

    public CustomerRetriever(Key tenantId, boolean retrieveFinancialData) {
        this(retrieveFinancialData);
        retrieve(tenantId);
    }

    public void retrieve(Key customerId) {
        retrieve(Persistence.service().retrieve(Customer.class, customerId));
    }

    protected void retrieve(Customer in) {
        customer = in;
        if (customer != null) {
            EntityQueryCriteria<PersonScreening> criteria = EntityQueryCriteria.create(PersonScreening.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().screene(), customer));
            List<PersonScreening> allScreenings = Persistence.service().query(criteria);
            if (allScreenings != null && !allScreenings.isEmpty()) {
                screening = allScreenings.get(allScreenings.size() - 1); // use last screening
            } else {
                screening = EntityFactory.create(PersonScreening.class);
            }

            if (!screening.isEmpty()) {
                Persistence.service().retrieve(screening.documents());
                if (retrieveFinancialData) {
                    Persistence.service().retrieve(screening.incomes());
                    Persistence.service().retrieve(screening.assets());
                    Persistence.service().retrieve(screening.equifaxApproval());
                }
            } else {
                // newly created - set owner:
                screening.screene().set(customer);
            }

            Persistence.service().retrieve(customer.emergencyContacts());
        }
    }

    public Person getPerson() {
        return customer.person();
    }

    public Customer getCustomer() {
        return customer;
    }

    public void saveCustomer() {
        Persistence.service().merge(customer);
    }

    public PersonScreening getScreening() {
        return screening;
    }

    public void saveScreening() {
        Persistence.service().merge(screening);

        // save detached entities:
        Persistence.service().merge(screening.documents());
        if (retrieveFinancialData) {
            Persistence.service().merge(screening.incomes());
            Persistence.service().merge(screening.assets());
        }
    }
}
