/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Owned;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.policies.domain.LegalTermsPolicyItem;

@DiscriminatorValue("LegalTermsPolicy")
@LowestApplicableNode(value = OrganizationPoliciesNode.class)
public interface LegalTermsPolicy extends Policy, TenantsAccessiblePolicy {

    @Owned
    LegalTermsPolicyItem rentalCriteriaGuidelines();

}
