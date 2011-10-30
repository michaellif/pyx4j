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

public class EmbeddedDashboardPanel extends DashboardPanel implements SlaveDashboardView {

    private IBuildingGadget.FilterData filterData;

    private final CEntityComboBox<DashboardMetadata> dashboardSelect;

    public EmbeddedDashboardPanel() {

        dashboardSelect = new CEntityComboBox<DashboardMetadata>(DashboardMetadata.class);
        dashboardSelect.setWidth("25em");
        dashboardSelect.addCriterion(PropertyCriterion.eq(dashboardSelect.proto().type(), DashboardType.building));
        dashboardSelect.addValueChangeHandler(new ValueChangeHandler<DashboardMetadata>() {
            @Override
            public void onValueChange(ValueChangeEvent<DashboardMetadata> event) {
                onDashboardSelected(event.getValue());
            }
        });
        this.addAction(dashboardSelect.asWidget());
        this.setSize("100%", "100%");
    }

    @Override
    public void applyFiltering(IBuildingGadget.FilterData filterData) {
        this.filterData = filterData;
        applyFiltering();
    }

    private void applyFiltering() {
        if (filterData != null & this.getBoard() != null) {
            IGadgetIterator it = this.getBoard().getGadgetIterator();
            while (it.hasNext()) {
                IGadget gadget = it.next();
                if (gadget instanceof IBuildingGadget) {
                    ((IBuildingGadget) gadget).setFiltering(filterData);
                }
            }
        }
    }

    protected void onDashboardSelected(DashboardMetadata boardMetadata) {
        if (boardMetadata != null) {
            super.fill(boardMetadata);
            applyFiltering();
        }
    }

    @Override
    public void fill(DashboardMetadata metadata) {
        dashboardSelect.setValue(metadata);
    }
}