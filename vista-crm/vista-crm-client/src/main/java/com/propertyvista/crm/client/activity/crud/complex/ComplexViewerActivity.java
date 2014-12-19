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
 */
package com.propertyvista.crm.client.activity.crud.complex;

import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.complex.ComplexViewerView;
import com.propertyvista.crm.client.visor.dashboard.DashboardVisorController;
import com.propertyvista.crm.client.visor.dashboard.IDashboardVisorController;
import com.propertyvista.crm.rpc.services.building.ComplexCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.ComplexDTO;

public class ComplexViewerActivity extends CrmViewerActivity<ComplexDTO> implements ComplexViewerView.Presenter {

    public ComplexViewerActivity(CrudAppPlace place) {
        super(ComplexDTO.class, place, CrmSite.getViewFactory().getView(ComplexViewerView.class), GWT
                .<AbstractCrudService<ComplexDTO>> create(ComplexCrudService.class));
    }

    @Override
    protected void onPopulateSuccess(ComplexDTO result) {
        super.onPopulateSuccess(result);
    }

    @Override
    public IDashboardVisorController getDashboardController(DashboardMetadata dashboardMetadata, List<Building> buildings) {
        return new DashboardVisorController(getView(), dashboardMetadata, buildings);
    }
}
