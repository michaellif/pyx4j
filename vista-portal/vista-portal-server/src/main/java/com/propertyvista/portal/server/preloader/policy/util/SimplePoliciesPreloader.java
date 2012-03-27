/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.util;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.PolicyNode;

public abstract class SimplePoliciesPreloader extends AbstractPoliciesPreloader {

    protected SimplePoliciesPreloader() {
        this(createOrganizationPoliciesNode());
    }

    protected SimplePoliciesPreloader(PolicyNode topNode) {
        assert topNode != null;
        assert !topNode.id().isNull();
        setTopNode(topNode);
    }

    private static PolicyNode createOrganizationPoliciesNode() {
        OrganizationPoliciesNode topNode = EntityFactory.create(OrganizationPoliciesNode.class);
        Persistence.service().merge(topNode);
        return topNode;
    }

}
