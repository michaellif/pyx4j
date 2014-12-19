/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-31
 * @author ArtyomB
 */
package com.propertyvista.crm.client.activity.policies.leasetermination;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.policies.leasetermination.LeaseTerminationPolicyViewerView;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseTerminationPolicyCrudService;
import com.propertyvista.domain.policy.dto.LeaseTerminationPolicyDTO;

public class LeaseTerminationPolicyViewerActivity extends CrmViewerActivity<LeaseTerminationPolicyDTO> {

    public LeaseTerminationPolicyViewerActivity(CrudAppPlace place) {
        super(LeaseTerminationPolicyDTO.class, place, CrmSite.getViewFactory().getView(LeaseTerminationPolicyViewerView.class), GWT
                .<LeaseTerminationPolicyCrudService> create(LeaseTerminationPolicyCrudService.class));
    }

}
