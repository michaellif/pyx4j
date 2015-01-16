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

        ApplicationApprovalChecklistPolicyItem item = addItem(policy, "Credit Check");
        addStatus(item, "Completed - OK");
        addStatus(item, "Complete - No Hit");
        addStatus(item, "Complete - Declined");
        addStatus(item, "Missing - Information");
        addStatus(item, "Missing - Guarantor");

        item = addItem(policy, "Reference");
        addStatus(item, "Called - Confirmed");
        addStatus(item, "Called - Not Confirmed");
        addStatus(item, "Called - Left Message");
        addStatus(item, "Called - Bad Reference");

        item = addItem(policy, "Employment Confirmation");
        addStatus(item, "Called - Confirmed");
        addStatus(item, "Called - Left Message");
        addStatus(item, "Called - Not Confirmed");
        addStatus(item, "Called - Bad Reference");
        addStatus(item, "Called - Not Working there");

        item = addItem(policy, "Documents");
        addStatus(item, "Checked - All OK");
        addStatus(item, "Checked - Missing");
        addStatus(item, "Checked - Not Legible");
        addStatus(item, "Checked - Insufficient");

        return policy;
    }

    private ApplicationApprovalChecklistPolicyItem addItem(ApplicationApprovalChecklistPolicy policy, String item) {
        ApplicationApprovalChecklistPolicyItem policyItem = policy.itemsToCheck().$();
        policyItem.itemToCheck().setValue(item);
        policy.itemsToCheck().add(policyItem);
        return policyItem;
    }

    private void addStatus(ApplicationApprovalChecklistPolicyItem item, String status) {
        ApplicationApprovalChecklistPolicyItem.StatusSelectionPolicyItem statusItem = item.statusesToSelect().$();
        statusItem.statusSelection().setValue(status);
        item.statusesToSelect().add(statusItem);
    }
}
