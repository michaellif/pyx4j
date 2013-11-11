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
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestListerView;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestListerActivity extends AbstractListerActivity<MaintenanceRequestDTO> {

    @SuppressWarnings("unchecked")
    public MaintenanceRequestListerActivity(Place place) {
        super(place,  CrmSite.getViewFactory().getView(MaintenanceRequestListerView.class), (AbstractCrudService<MaintenanceRequestDTO>) GWT
                .create(MaintenanceCrudService.class), MaintenanceRequestDTO.class);
    }

    @Override
    public boolean canCreateNewItem() {
        return SecurityController.checkBehavior(VistaCrmBehavior.Maintenance);
    }
}
