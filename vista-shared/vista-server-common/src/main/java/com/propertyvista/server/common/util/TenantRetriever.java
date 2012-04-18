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
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;

public class TenantRetriever {

    public Class<? extends Customer> customerClass;

    private Customer screeningHolder;

    public PersonScreening personScreening;

    public List<PersonScreening> personScreenings;

    private final boolean financial;

    // Construction:
    public TenantRetriever(Class<? extends Customer> tenantClass) {
        this(tenantClass, false);
    }

    public TenantRetriever(Class<? extends Customer> tenantClass, boolean financial) {
        this.customerClass = tenantClass;
        this.financial = financial;
    }

    public TenantRetriever(Class<? extends Customer> tenantClass, Key tenantId) {
        this(tenantClass, tenantId, false);
    }

    public TenantRetriever(Class<? extends Customer> tenantClass, Key tenantId, boolean financial) {
        this(tenantClass, financial);
        retrieve(tenantId);
    }

    // Manipulation:
    public void retrieve(Key customerId) {
        screeningHolder = Persistence.service().retrieve(customerClass, customerId);
        if (screeningHolder != null) {
            EntityQueryCriteria<PersonScreening> criteria = EntityQueryCriteria.create(PersonScreening.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().screene(), screeningHolder));
            personScreenings = Persistence.service().query(criteria);
            if (personScreenings != null && !personScreenings.isEmpty()) {
                personScreening = personScreenings.get(personScreenings.size() - 1); // use last screenings
            } else {
                personScreening = EntityFactory.create(PersonScreening.class);
            }

            if (!personScreening.isEmpty()) {
                Persistence.service().retrieve(personScreening.documents());
                if (financial) {
                    Persistence.service().retrieve(personScreening.incomes());
                    Persistence.service().retrieve(personScreening.assets());
                    Persistence.service().retrieve(personScreening.equifaxApproval());
                }
            } else {
                // newly created - set belonging to tenant:
                personScreening.screene().set(screeningHolder);
            }

            if (screeningHolder instanceof Customer) {
                Persistence.service().retrieve(screeningHolder.emergencyContacts());
            }
        }
    }

    public Person getPerson() {
        return screeningHolder.person();
    }

    public Customer getCustomer() {
        return screeningHolder;
    }

    public Customer getTenant() {
        if (screeningHolder instanceof Customer) {
            return screeningHolder;
        } else {
            throw new Error(customerClass.getName() + " object stored!");
        }
    }

    public Guarantor getGuarantor() {
        if (screeningHolder instanceof Guarantor) {
            return (Guarantor) screeningHolder;
        } else {
            throw new Error(customerClass.getName() + " object stored!");
        }
    }

    public void saveTenant() {
        Persistence.service().merge(screeningHolder);
    }

    public void saveScreening() {
        Persistence.service().merge(personScreening);

        // save detached entities:
        if (!financial) {
            Persistence.service().merge(personScreening.documents());
        }

        if (financial) {
            Persistence.service().merge(personScreening.incomes());
            Persistence.service().merge(personScreening.assets());
        }
    }
}
