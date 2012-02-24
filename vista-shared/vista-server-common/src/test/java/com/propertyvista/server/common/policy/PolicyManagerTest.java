/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.policy;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaCrmBehavior;

@Ignore
public class PolicyManagerTest {

    @Before
    public void setUp() {
        VistaTestDBSetup.init();
        TestLifecycle.testSession(new UserVisit(new Key(-101), "Mad Max"), VistaCrmBehavior.PropertyManagement, VistaBasicBehavior.CRM);
        TestLifecycle.beginRequest();

//        Persistence.service().delete(new EntityQueryCriteria<OrganizationPoliciesNode>(OrganizationPoliciesNode.class));
//        Persistence.service().delete(new EntityQueryCriteria<Country>(Country.class));
//        Persistence.service().delete(new EntityQueryCriteria<Province>(Province.class));
//        Persistence.service().delete(new EntityQueryCriteria<Building>(Building.class));

        Persistence.service().delete(new EntityQueryCriteria<FooA>(FooA.class));
        Persistence.service().delete(new EntityQueryCriteria<FooB>(FooB.class));

        FooA fooA = EntityFactory.create(FooA.class);
        fooA.abstractFooValue().setValue(1);
        Persistence.service().merge(fooA);

        FooB FooB = EntityFactory.create(FooB.class);
        FooB.abstractFooValue().setValue(2);
        Persistence.service().merge(FooB);

        // TODO remove data from tables
    }

    @Test
    public void experiment() {

        EntityQueryCriteria<AbstractFoo> criteria = new EntityQueryCriteria<AbstractFoo>(AbstractFoo.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().abstractFooValue(), 1));

        List<AbstractFoo> aLotOfFoo = Persistence.service().query(criteria);
    }
}
