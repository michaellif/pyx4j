/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.policy;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public class PolicyFacadeImpl implements PolicyFacade {

    @Override
    public <POLICY extends Policy> POLICY obtainEffectivePolicy(PolicyNode node, final Class<POLICY> policyClass) {
        return PolicyManager.obtainEffectivePolicy(node, policyClass);
    }

    @Override
    public <POLICY extends Policy> POLICY obtainHierarchicalEffectivePolicy(IEntity entity, Class<POLICY> policyClass) {
        // Find Object hierarchy, Like in BreadcrumbsHelper
        PolicyNode node = null;
        // Special case for no business owned
        if (entity instanceof PersonScreening) {
            // Find  LeaseTerm
            LeaseTerm leaseTerm = null;
            EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().screening(), entity));
            Tenant tenant = Persistence.service().retrieve(criteria);
            if (tenant != null) {
                Persistence.service().retrieve(tenant.leaseTermV());
                leaseTerm = tenant.leaseTermV().holder();
            } else {
                EntityQueryCriteria<Guarantor> criteria2 = EntityQueryCriteria.create(Guarantor.class);
                criteria2.add(PropertyCriterion.eq(criteria2.proto().screening(), entity));
                Guarantor guarantor = Persistence.service().retrieve(criteria2);
                if (guarantor != null) {
                    Persistence.service().retrieve(guarantor.leaseTermV());
                    leaseTerm = guarantor.leaseTermV().holder();

                }
            }
            if (leaseTerm == null) {
                throw new Error("Lease not found");
            }
            Persistence.service().retrieve(leaseTerm.lease());
            Persistence.service().retrieve(leaseTerm.lease().unit());
            node = leaseTerm.lease().unit().building();
        } else {
            // TODO use the same code as in BreadcrumbsHelper
            throw new IllegalArgumentException("TODO take a code from BreadcrumbsHelper and find fist PolicyNode in onbject hierarchy");
        }

        return PolicyManager.obtainEffectivePolicy(node, policyClass);
    }

}
