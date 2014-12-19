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
 */
package com.propertyvista.crm.client.activity.policies.maintenancerequest;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.policies.maintenancerequest.MaintenanceRequestPolicyViewerView;
import com.propertyvista.crm.rpc.services.policies.policy.MaintenanceRequestPolicyCrudService;
import com.propertyvista.domain.policy.dto.MaintenanceRequestPolicyDTO;

public class MaintenanceRequestPolicyViewerActivity extends CrmViewerActivity<MaintenanceRequestPolicyDTO> {

    public MaintenanceRequestPolicyViewerActivity(CrudAppPlace place) {
        super(MaintenanceRequestPolicyDTO.class, place, CrmSite.getViewFactory().getView(MaintenanceRequestPolicyViewerView.class), GWT
                .<MaintenanceRequestPolicyCrudService> create(MaintenanceRequestPolicyCrudService.class));
    }

}
