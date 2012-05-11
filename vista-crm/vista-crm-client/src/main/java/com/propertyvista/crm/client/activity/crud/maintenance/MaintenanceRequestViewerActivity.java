/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.maintenance;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MaintenanceViewFactory;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestViewerActivity extends CrmViewerActivity<MaintenanceRequestDTO> {

    @SuppressWarnings("unchecked")
    public MaintenanceRequestViewerActivity(CrudAppPlace place) {
        super(place, MaintenanceViewFactory.instance(MaintenanceRequestViewerView.class), (AbstractCrudService<MaintenanceRequestDTO>) GWT
                .create(MaintenanceCrudService.class));
    }

    @Override
    public boolean canEdit() {
        return super.canEdit() & SecurityController.checkBehavior(VistaCrmBehavior.Maintenance);
    }
}
