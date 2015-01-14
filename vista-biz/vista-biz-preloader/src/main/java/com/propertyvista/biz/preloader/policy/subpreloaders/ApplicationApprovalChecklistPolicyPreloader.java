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
package com.propertyvista.biz.preloader.policy.subpreloaders;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.preloader.policy.AbstractPolicyPreloader;
import com.propertyvista.domain.policy.policies.ApplicationApprovalChecklistPolicy;
import com.propertyvista.domain.policy.policies.domain.ApplicationApprovalChecklistPolicyItem;

public class ApplicationApprovalChecklistPolicyPreloader extends AbstractPolicyPreloader<ApplicationApprovalChecklistPolicy> {

    public ApplicationApprovalChecklistPolicyPreloader() {
        super(ApplicationApprovalChecklistPolicy.class);
    }

    @Override
    protected ApplicationApprovalChecklistPolicy createPolicy(StringBuilder log) {
        ApplicationApprovalChecklistPolicy policy = EntityFactory.create(ApplicationApprovalChecklistPolicy.class);

        ApplicationApprovalChecklistPolicyItem item = policy.itemsToCheck().$();
        item.itemToCheck().setValue("Information Completeness");
        policy.itemsToCheck().add(item);

        item = policy.itemsToCheck().$();
        item.itemToCheck().setValue("Credit Check");
        policy.itemsToCheck().add(item);

        item = policy.itemsToCheck().$();
        item.itemToCheck().setValue("Employment Confirmation");
        policy.itemsToCheck().add(item);

        item = policy.itemsToCheck().$();
        item.itemToCheck().setValue("landlord Confirmation");
        policy.itemsToCheck().add(item);

        return policy;
    }
}
