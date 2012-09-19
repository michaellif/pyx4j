/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components.details;

import com.pyx4j.entity.shared.Path;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory.IFilterDataChangedHandler;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory.IFilterDataProvider;

public class CounterGadgetFilterProvider implements IFilterDataProvider<CounterGadgetFilter> {

    private final IBuildingFilterContainer buildingFilterContainer;

    private final Path member;

    public CounterGadgetFilterProvider(IBuildingFilterContainer buildingFilterContainer, Path member) {
        this.buildingFilterContainer = buildingFilterContainer;
        this.member = member;
    }

    @Override
    public CounterGadgetFilter getFilterData() {
        return new CounterGadgetFilter(buildingFilterContainer.getSelectedBuildingsStubs(), member);
    }

    @Override
    public void addFilterDataChangedHandler(final IFilterDataChangedHandler<CounterGadgetFilter> handler) {
        buildingFilterContainer.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
            @Override
            public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                handler.handleFilterDataChange(new CounterGadgetFilter(event.getBuildings(), member));
            }
        });
    }

}
