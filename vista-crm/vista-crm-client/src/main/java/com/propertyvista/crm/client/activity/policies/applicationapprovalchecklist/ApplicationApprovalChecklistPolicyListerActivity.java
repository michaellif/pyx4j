/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
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
package com.propertyvista.crm.client.activity.policies.applicationapprovalchecklist;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.applicationapprovalchecklist.ApplicationApprovalChecklistPolicyListerView;
import com.propertyvista.domain.policy.dto.ApplicationApprovalChecklistPolicyDTO;

public class ApplicationApprovalChecklistPolicyListerActivity extends AbstractPrimeListerActivity<ApplicationApprovalChecklistPolicyDTO> {

    public ApplicationApprovalChecklistPolicyListerActivity(AppPlace place) {
        super(ApplicationApprovalChecklistPolicyDTO.class, place, CrmSite.getViewFactory().getView(ApplicationApprovalChecklistPolicyListerView.class));
    }

}
