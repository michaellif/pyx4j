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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestLister;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractDetailsLister;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilterProvider;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.MaintenanceRequestCriteriaProvider;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestsDetailsFactory extends AbstractListerDetailsFactory<MaintenanceRequestDTO, CounterGadgetFilter> {

    public static class MaintenanceRequestsDetailsLister extends AbstractDetailsLister<MaintenanceRequestDTO> {

        public MaintenanceRequestsDetailsLister() {
            super(MaintenanceRequestDTO.class);
            setColumnDescriptors(MaintenanceRequestLister.createColumnDescriptors());
        }

    }

    public MaintenanceRequestsDetailsFactory(final MaintenanceRequestCriteriaProvider maintenanceRequestCriteriaProvider,
            IBuildingFilterContainer buildingsFilterContainer, IObject<?> filterPreset) {
        super(//@formatter:off
                MaintenanceRequestDTO.class,
                new MaintenanceRequestsDetailsLister(),
                GWT.<MaintenanceCrudService>create(MaintenanceCrudService.class),
                new CounterGadgetFilterProvider(buildingsFilterContainer, filterPreset.getPath()),
                new ICriteriaProvider<MaintenanceRequestDTO, CounterGadgetFilter>() {
                    @Override
                    public void makeCriteria(AsyncCallback<EntityListCriteria<MaintenanceRequestDTO>> callback, CounterGadgetFilter filterData) {
                        maintenanceRequestCriteriaProvider.makeMaintenaceRequestCriteria(callback, filterData.getBuildings(), filterData.getCounterMember().toString());
                    }
                }
        );//@formatter:on
    }
}
