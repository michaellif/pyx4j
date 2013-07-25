/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.leaseadjustment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyListerView;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseAdjustmentPolicyCrudService;
import com.propertyvista.domain.policy.dto.LeaseAdjustmentPolicyDTO;

public class LeaseAdjustmentPolicyListerActivity extends AbstractListerActivity<LeaseAdjustmentPolicyDTO> {

    public LeaseAdjustmentPolicyListerActivity(Place place) {
        super(place,  CrmSite.getViewFactory().instantiate(LeaseAdjustmentPolicyListerView.class), GWT
                .<LeaseAdjustmentPolicyCrudService> create(LeaseAdjustmentPolicyCrudService.class), LeaseAdjustmentPolicyDTO.class);
    }

}
