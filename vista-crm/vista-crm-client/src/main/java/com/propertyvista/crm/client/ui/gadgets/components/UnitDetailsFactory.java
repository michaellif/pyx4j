/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitCriteriaProvider;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;

public class UnitDetailsFactory implements CounterGadgetInstanceBase.CounterDetailsFactory {

    private final UnitsDetailsLister lister;

    private final String unitsFilter;

    private final UnitCriteriaProvider provider;

    private final IBuildingFilterContainer buildingsFilterContainer;

    public UnitDetailsFactory(UnitCriteriaProvider unitsCriteriaProvider, IBuildingFilterContainer buildingsFilterProvider, IObject<?> unitsFilter) {
        this.lister = new UnitsDetailsLister();
        this.buildingsFilterContainer = buildingsFilterProvider;
        this.buildingsFilterContainer.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {

            @Override
            public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                createDetailsWidget();
            }
        });
        this.provider = unitsCriteriaProvider;
        this.unitsFilter = unitsFilter.getPath().toString();
    }

    @Override
    public Widget createDetailsWidget() {

        provider.makeUnitFilterCriteria(new DefaultAsyncCallback<EntityListCriteria<AptUnitDTO>>() {

            @Override
            public void onSuccess(EntityListCriteria<AptUnitDTO> result) {
                ListerDataSource<AptUnitDTO> dataSource = new ListerDataSource<AptUnitDTO>(AptUnitDTO.class, GWT
                        .<UnitCrudService> create(UnitCrudService.class));
                dataSource.setPreDefinedFilters(result.getFilters());
                lister.setDataSource(dataSource);
                lister.obtain(0);
            }

        }, new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs()), unitsFilter);

        return lister;
    }

}
