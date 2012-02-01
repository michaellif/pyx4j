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

import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.PersonScreeningHolder;
import com.propertyvista.domain.tenant.Tenant;

public class TenantRetriever {

    public Class<? extends PersonScreeningHolder> tenantClass;

    private PersonScreeningHolder tenant;

    public PersonScreening tenantScreening;

    public List<PersonScreening> tenantScreenings;

    private final boolean financial;

    // Construction:
    public TenantRetriever(Class<? extends PersonScreeningHolder> tenantClass) {
        this(tenantClass, false);
    }

    public TenantRetriever(Class<? extends PersonScreeningHolder> tenantClass, boolean financial) {
        this.tenantClass = tenantClass;
        this.financial = financial;
    }

    public TenantRetriever(Class<? extends PersonScreeningHolder> tenantClass, Key tenantId) {
        this(tenantClass, tenantId, false);
    }

    public TenantRetriever(Class<? extends PersonScreeningHolder> tenantClass, Key tenantId, boolean financial) {
        this(tenantClass, financial);
        retrieve(tenantId);
    }

    // Manipulation:
    public void retrieve(Key tenantId) {
        tenant = Persistence.service().retrieve(tenantClass, tenantId);
        if (tenant != null) {
            EntityQueryCriteria<PersonScreening> criteria = EntityQueryCriteria.create(PersonScreening.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().screene(), tenant));
            tenantScreenings = Persistence.service().query(criteria);
            if (tenantScreenings != null && !tenantScreenings.isEmpty()) {
                tenantScreening = tenantScreenings.get(tenantScreenings.size() - 1); // use last screenings
            } else {
                tenantScreening = EntityFactory.create(PersonScreening.class);
            }

            if (!tenantScreening.isEmpty()) {
                Persistence.service().retrieve(tenantScreening.documents());
                if (financial) {
                    Persistence.service().retrieve(tenantScreening.incomes());
                    Persistence.service().retrieve(tenantScreening.assets());
                    Persistence.service().retrieve(tenantScreening.guarantors());
                    Persistence.service().retrieve(tenantScreening.equifaxApproval());
                }
            } else {
                // newly created - set belonging to tenant:
                tenantScreening.screene().set(tenant);
            }

            if (tenant instanceof Tenant) {
                Persistence.service().retrieve(((Tenant) tenant).emergencyContacts());
            }
        }
    }

    public Person getPerson() {
        return tenant.person();
    }

    public Tenant getTenant() {
        if (tenant instanceof Tenant) {
            return (Tenant) tenant;
        } else {
            throw new Error(tenantClass.getName() + " object stored!");
        }
    }

    public Guarantor getGuarantor() {
        if (tenant instanceof Guarantor) {
            return (Guarantor) tenant;
        } else {
            throw new Error(tenantClass.getName() + " object stored!");
        }
    }

    public void saveTenant() {
        Persistence.service().merge(tenant);
    }

    public void saveScreening() {
        Persistence.service().merge(tenantScreening);

        // save detached entities:
        if (!financial) {
            int no = 0;
            for (ApplicationDocument applicationDocument : tenantScreening.documents()) {
                applicationDocument.orderInOwner().setValue(no++);
                //TODO assert Documents Id
                Persistence.service().merge(applicationDocument);
            }
        }

        if (financial) {
            Persistence.service().merge(tenantScreening.incomes());
            Persistence.service().merge(tenantScreening.assets());
            Persistence.service().merge(tenantScreening.guarantors());
        }
    }
}
