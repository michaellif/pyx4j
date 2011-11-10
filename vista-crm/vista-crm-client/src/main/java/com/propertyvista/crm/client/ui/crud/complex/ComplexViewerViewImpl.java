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
package com.propertyvista.crm.client.ui.crud.complex;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.building.BuildingLister;
import com.propertyvista.crm.client.ui.crud.building.dashboard.BuildingDashboardView;
import com.propertyvista.crm.client.ui.crud.building.dashboard.BuildingDashboardViewImpl;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ComplexDTO;

public class ComplexViewerViewImpl extends CrmViewerViewImplBase<ComplexDTO> implements ComplexViewerView {

    private final BuildingDashboardView dashboardView = new BuildingDashboardViewImpl();

    private final IListerView<BuildingDTO> buildingLister;

    public ComplexViewerViewImpl() {
        super(CrmSiteMap.Properties.Complex.class);

        buildingLister = new ListerInternalViewImplBase<BuildingDTO>(new BuildingLister());

        // set main form here: 
        setForm(new ComplexEditorForm(new CrmViewersComponentFactory()));
    }

    @Override
    public void populate(ComplexDTO value) {
        super.populate(value);

        // calculate and set current dashboard filtering here:
        dashboardView.setFiltering(calculateFiltering(value));
    }

    @Override
    public BuildingDashboardView getDashboardView() {
        return dashboardView;
    }

    @Override
    public IListerView<BuildingDTO> getBuildingListerView() {
        return buildingLister;
    }

    // Internals:

    private IBuildingGadget.FilterData calculateFiltering(ComplexDTO value) {
        IBuildingGadget.FilterData filterData = new IBuildingGadget.FilterData();

        if (value != null && !value.buildings().isEmpty()) {
            for (Building building : value.buildings()) {
                filterData.buildings.add(building.getPrimaryKey());
            }
        } else {
            filterData.buildings.add(new Key(-1l));
        }

        return filterData;
    }
}
