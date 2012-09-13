/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase.CounterDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.MaintenanceRequestCriteriaProvider;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestsDetailsFactory implements CounterDetailsFactory {

    private final MaintenanceRequestsDetailsLister lister;

    private final MaintenanceRequestCriteriaProvider maintenanceRequestCriteriaProvider;

    private final String filter;

    private final IBuildingFilterContainer buildingsFilterProvider;

    public MaintenanceRequestsDetailsFactory(MaintenanceRequestCriteriaProvider maintenanceRequestCriteriaProvider,
            IBuildingFilterContainer buildingsFilterContainer, IObject<?> filterPreset) {
        this.buildingsFilterProvider = buildingsFilterContainer;
        this.maintenanceRequestCriteriaProvider = maintenanceRequestCriteriaProvider;
        this.lister = new MaintenanceRequestsDetailsLister();
        this.filter = filterPreset.getPath().toString();
    }

    @Override
    public Widget createDetailsWidget() {
        maintenanceRequestCriteriaProvider.makeMaintenaceRequestCriteria(new DefaultAsyncCallback<EntityListCriteria<MaintenanceRequestDTO>>() {

            @Override
            public void onSuccess(EntityListCriteria<MaintenanceRequestDTO> result) {
                ListerDataSource<MaintenanceRequestDTO> dataSource = new ListerDataSource<MaintenanceRequestDTO>(MaintenanceRequestDTO.class, GWT
                        .<AbstractListService<MaintenanceRequestDTO>> create(MaintenanceCrudService.class));
                List<Criterion> filters = result.getFilters();
                if (filters != null) {
                    dataSource.setPreDefinedFilters(result.getFilters());
                }
                lister.setDataSource(dataSource);
                lister.obtain(0);
            }

        }, new Vector<Building>(buildingsFilterProvider.getSelectedBuildingsStubs()), filter);
        return lister;
    }
}
