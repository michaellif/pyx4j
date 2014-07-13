/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.onlineapplication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.onlineapplication.LeaseApplicationPolicyListerView;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseApplicationPolicyCrudService;
import com.propertyvista.domain.policy.dto.LeaseApplicationPolicyDTO;

public class LeaseApplicationPolicyListerActivity extends AbstractListerActivity<LeaseApplicationPolicyDTO> {
    public LeaseApplicationPolicyListerActivity(Place place) {
        super(LeaseApplicationPolicyDTO.class, place, CrmSite.getViewFactory().getView(LeaseApplicationPolicyListerView.class), GWT
                        .<LeaseApplicationPolicyCrudService> create(LeaseApplicationPolicyCrudService.class));
    }

}
