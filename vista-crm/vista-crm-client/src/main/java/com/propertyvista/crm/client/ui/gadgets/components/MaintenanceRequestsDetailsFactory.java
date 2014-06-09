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

import java.util.Arrays;

import com.google.gwt.core.client.GWT;

import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestLister;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.services.maintenance.MaintenanceCrudService;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestsDetailsFactory extends AbstractListerDetailsFactory<MaintenanceRequestDTO, CounterGadgetFilter> {

    public MaintenanceRequestsDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider,
            ICriteriaProvider<MaintenanceRequestDTO, CounterGadgetFilter> criteriaProvider, Proxy<ListerUserSettings> listerUserSettingsProxy) {
        super(//@formatter:off
                MaintenanceRequestDTO.class,
                Arrays.asList(MaintenanceRequestLister.createColumnDescriptors()),
                GWT.<MaintenanceCrudService>create(MaintenanceCrudService.class),
                filterDataProvider,
                criteriaProvider,
                listerUserSettingsProxy
        );//@formatter:on
    }
}
