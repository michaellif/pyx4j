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
package com.propertyvista.crm.client.ui.gadgets.maintenance;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.components.MaintenanceRequestsDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.rpc.dto.gadgets.MaintenanceGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.MaintenanceGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.MaintenanceRequestCriteriaProvider;
import com.propertyvista.domain.dashboard.gadgets.type.MaintenanceGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceGadgetFactory extends AbstractGadgetFactory<MaintenanceGadgetMetadata> {

    public static class MaintenanceGadget extends CounterGadgetInstanceBase<MaintenanceGadgetDataDTO, Vector<Building>, MaintenanceGadgetMetadata> {

        public MaintenanceGadget(GadgetMetadata metadata) {
            super(MaintenanceGadgetDataDTO.class, GWT.<MaintenanceGadgetService> create(MaintenanceGadgetService.class), new MaintenanceGadgetSummaryForm(),
                    metadata, MaintenanceGadgetMetadata.class);
        }

        @Override
        protected Vector<Building> prepareSummaryQuery() {
            return new Vector<Building>();
        }

        @Override
        protected void bindDetailsFactories() {
            bindDetails(proto().openWorkOrders());
            bindDetails(proto().urgentWorkOrders());
            bindDetails(proto().outstandingWorkOrders1to2days());
            bindDetails(proto().outstandingWorkOrders2to3days());
            bindDetails(proto().outstandingWorkOrdersMoreThan3days());
        }

        private void bindDetails(IObject<?> member) {
            ICriteriaProvider<MaintenanceRequestDTO, CounterGadgetFilter> criteriaProvider = new ICriteriaProvider<MaintenanceRequestDTO, CounterGadgetFilter>() {
                @Override
                public void makeCriteria(AsyncCallback<EntityListCriteria<MaintenanceRequestDTO>> callback, CounterGadgetFilter filterData) {
                    GWT.<MaintenanceRequestCriteriaProvider> create(MaintenanceGadgetService.class).makeMaintenaceRequestCriteria(callback,
                            filterData.getBuildings(), filterData.getCounterMember().toString());
                }
            };
            bindDetailsFactory(member, new MaintenanceRequestsDetailsFactory(this, criteriaProvider));
        }
    }

    public MaintenanceGadgetFactory() {
        super(MaintenanceGadgetMetadata.class);
    }

    @Override
    protected GadgetInstanceBase<MaintenanceGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new MaintenanceGadget(gadgetMetadata);
    }

}
