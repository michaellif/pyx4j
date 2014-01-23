/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-23
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.maintenancerequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.maintenancerequest.MaintenanceRequestPolicyListerView;
import com.propertyvista.crm.rpc.services.policies.policy.MaintenanceRequestPolicyCrudService;
import com.propertyvista.domain.policy.dto.MaintenanceRequestPolicyDTO;

public class MaintenanceRequestPolicyListerActivity extends AbstractListerActivity<MaintenanceRequestPolicyDTO> {

    public MaintenanceRequestPolicyListerActivity(Place place) {
        super(place, CrmSite.getViewFactory().getView(MaintenanceRequestPolicyListerView.class), GWT
                .<MaintenanceRequestPolicyCrudService> create(MaintenanceRequestPolicyCrudService.class), MaintenanceRequestPolicyDTO.class);
    }

}
