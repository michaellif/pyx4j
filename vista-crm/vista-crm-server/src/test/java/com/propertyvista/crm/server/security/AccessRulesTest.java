/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 4, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import java.util.List;

import junit.framework.Assert;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserBuildings;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class AccessRulesTest extends VistaDBTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestLifecycle.tearDown();
    }

    public void testTenantDatasetNewEntityAccess() {
        String setId = uniqueString();

        TestLifecycle.testSession(new UserVisit(new Key(-101), "bob"), VistaBasicBehavior.CRM);
        TestLifecycle.beginRequest();

        Customer t1 = EntityFactory.create(Customer.class);
        t1.person().name().firstName().setValue(setId);
        Persistence.service().persist(t1);

        // Tenant with Lease but no Unit
        {
            Customer t2 = EntityFactory.create(Customer.class);
            t2.person().name().firstName().setValue(setId);
            Persistence.service().persist(t2);

            Lease lease = EntityFactory.create(Lease.class);
            lease.paymentFrequency().setValue(PaymentFrequency.Monthly);
            lease.saveAction().setValue(SaveAction.saveAsFinal);
            Persistence.service().persist(lease);

            Tenant tl2 = EntityFactory.create(Tenant.class);
            tl2.leaseV().set(lease.version());
            tl2.customer().set(t2);
            Persistence.service().persist(tl2);
        }

        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().person().name().firstName(), setId));
        new TenantDatasetAccessRule().applyRule(criteria);

        List<Customer> r = Persistence.service().query(criteria);
        Assert.assertEquals("result set size", 1, r.size());
    }

    public void testTenantDatasetExistingEntityAccess() {
        String setId = uniqueString();

        TestLifecycle.setNamespace();
        CrmUser user = EntityFactory.create(CrmUser.class);
        user.name().setValue(uniqueString());
        Persistence.service().persist(user);

        TestLifecycle.testSession(new UserVisit(user.getPrimaryKey(), "bob"), VistaBasicBehavior.CRM);
        TestLifecycle.beginRequest();

        Customer t1 = EntityFactory.create(Customer.class);
        t1.person().name().firstName().setValue(setId);
        Persistence.service().persist(t1);

        Lease lease = EntityFactory.create(Lease.class);
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        Building building = lease.unit().building();
        Persistence.service().persist(building);
        Persistence.service().persist(lease.unit());

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(lease);

        Tenant tl1 = EntityFactory.create(Tenant.class);

        tl1.leaseV().set(lease.version());
        tl1.customer().set(t1);
        Persistence.service().persist(tl1);

        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().person().name().firstName(), setId));
        new TenantDatasetAccessRule().applyRule(criteria);

        List<Customer> r = Persistence.service().query(criteria);
        Assert.assertEquals("should not find building", 0, r.size());

        // Create access rule record
        CrmUserBuildings arr = EntityFactory.create(CrmUserBuildings.class);
        arr.user().setPrimaryKey(user.getPrimaryKey());
        arr.building().set(building);
        Persistence.service().persist(arr);

        r = Persistence.service().query(criteria);
        Assert.assertEquals("should find building", 1, r.size());
    }
}
