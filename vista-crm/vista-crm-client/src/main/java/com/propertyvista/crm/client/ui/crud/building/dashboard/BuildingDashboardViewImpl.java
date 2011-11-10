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
package com.propertyvista.crm.client.ui.crud.building.dashboard;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;

import com.propertyvista.crm.client.ui.dashboard.DashboardPanel;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;

public class BuildingDashboardViewImpl extends DashboardPanel implements BuildingDashboardView {

    private final CEntityComboBox<DashboardMetadata> dashboardSelect = new CEntityComboBox<DashboardMetadata>(DashboardMetadata.class);

    private IBuildingGadget.FilterData filterData;

    public BuildingDashboardViewImpl() {

        dashboardSelect.setWidth("25em");
        dashboardSelect.addCriterion(PropertyCriterion.eq(dashboardSelect.proto().type(), DashboardType.building));
        dashboardSelect.addValueChangeHandler(new ValueChangeHandler<DashboardMetadata>() {
            @Override
            public void onValueChange(ValueChangeEvent<DashboardMetadata> event) {
                BuildingDashboardViewImpl.super.populate(event.getValue());
                applyFiltering();
            }
        });

        addAction(dashboardSelect.asWidget());
        setSize("100%", "100%");
    }

    @Override
    public void populate(DashboardMetadata metadata) {
        super.populate(metadata);
        dashboardSelect.populate(metadata);
    }

    @Override
    public void setFiltering(IBuildingGadget.FilterData filterData) {
        this.filterData = filterData;
        applyFiltering();
    }

    // Internals:

    private void applyFiltering() {
        if (filterData != null && this.getBoard() != null) {
            IGadgetIterator it = this.getBoard().getGadgetIterator();
            while (it.hasNext()) {
                IGadget gadget = it.next();
                if (gadget instanceof IBuildingGadget) {
                    ((IBuildingGadget) gadget).setFiltering(filterData);
                }
            }
        }
    }
}