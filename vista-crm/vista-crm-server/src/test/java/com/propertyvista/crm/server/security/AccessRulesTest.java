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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.CrmUserBuildings;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;

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

        Tenant t1 = EntityFactory.create(Tenant.class);
        t1.person().name().firstName().setValue(setId);
        Persistence.service().persist(t1);

        Tenant t2 = EntityFactory.create(Tenant.class);
        t2.person().name().firstName().setValue(setId);
        Persistence.service().persist(t2);
        t2._tenantInLease().add(t2._tenantInLease().$());

        EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().person().name().firstName(), setId));
        new TenantDatasetAccessRule().applyRule(criteria);

        List<Tenant> r = Persistence.service().query(criteria);
        Assert.assertEquals("result set size", 2, r.size());
    }

    public void testTenantDatasetExistingEntityAccess() {
        String setId = uniqueString();

        Key userPk = new Key(uniqueLong());
        TestLifecycle.testSession(new UserVisit(userPk, "bob"), VistaBasicBehavior.CRM);
        TestLifecycle.beginRequest();

        Tenant t1 = EntityFactory.create(Tenant.class);
        t1.person().name().firstName().setValue(setId);
        Persistence.service().persist(t1);

        TenantInLease tl1 = EntityFactory.create(TenantInLease.class);
        Building building = tl1.lease().unit().belongsTo();
        Persistence.service().persist(tl1.lease().unit().belongsTo());
        Persistence.service().persist(tl1.lease().unit());
        Persistence.service().persist(tl1.lease());

        tl1.tenant().set(t1);
        Persistence.service().persist(tl1);

        EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().person().name().firstName(), setId));
        new TenantDatasetAccessRule().applyRule(criteria);

        List<Tenant> r = Persistence.service().query(criteria);
        Assert.assertEquals("should not find building", 0, r.size());

        // Create access rule record
        CrmUserBuildings arr = EntityFactory.create(CrmUserBuildings.class);
        arr.user().setPrimaryKey(userPk);
        arr.building().set(building);
        Persistence.service().persist(arr);

        r = Persistence.service().query(criteria);
        Assert.assertEquals("should find building", 1, r.size());
    }
}
