/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.landlord;

import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.landlord.LandlordViewerView;
import com.propertyvista.crm.client.visor.dashboard.DashboardVisorController;
import com.propertyvista.crm.client.visor.dashboard.IDashboardVisorController;
import com.propertyvista.crm.rpc.services.building.LandlordCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.LandlordDTO;

public class LandlordViewerActivity extends CrmViewerActivity<LandlordDTO> implements LandlordViewerView.Presenter {

    @SuppressWarnings("unchecked")
    public LandlordViewerActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(LandlordViewerView.class), (AbstractCrudService<LandlordDTO>) GWT.create(LandlordCrudService.class));

    }

    @Override
    protected void onPopulateSuccess(LandlordDTO result) {
        super.onPopulateSuccess(result);
    }

    @Override
    public boolean canEdit() {
        return SecurityController.checkBehavior(VistaCrmBehavior.PropertyManagement);
    }

    @Override
    public IDashboardVisorController getDashboardController(DashboardMetadata dashboardMetadata, List<Building> buildings) {
        return new DashboardVisorController(getView(), dashboardMetadata, buildings);
    }
}
