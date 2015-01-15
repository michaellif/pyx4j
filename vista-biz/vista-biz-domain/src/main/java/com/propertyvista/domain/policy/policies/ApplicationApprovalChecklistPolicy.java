/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2015
 * @author VladL
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.core.IList;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.policies.domain.ApplicationApprovalChecklistPolicyItem;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("ApplicationApprovalChecklistPolicy")
@LowestApplicableNode(value = Building.class)
public interface ApplicationApprovalChecklistPolicy extends Policy {

    @Owned
    IList<ApplicationApprovalChecklistPolicyItem> itemsToCheck();
}
