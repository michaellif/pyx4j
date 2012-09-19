/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.leadsandrentals;

import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.components.AppointmentsDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.LeadsDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.LeasesFromLeadsDetailsFactory;
import com.propertyvista.crm.rpc.dto.gadgets.LeadsAndRentalsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeadsAndRentalsGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.LeadsAndRentalsGadgetMeta;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class LeadsAndRentalsGadget extends CounterGadgetInstanceBase<LeadsAndRentalsGadgetDataDTO, Vector<Building>, LeadsAndRentalsGadgetMeta> {

    public LeadsAndRentalsGadget(GadgetMetadata metadata) {
        super(//@formatter:off
                LeadsAndRentalsGadgetDataDTO.class,
                GWT.<LeadsAndRentalsGadgetService> create(LeadsAndRentalsGadgetService.class),
                new LeadsAndRentalsSummaryForm(),
                metadata,
                LeadsAndRentalsGadgetMeta.class
        );//@formatter:on
    }

    @Override
    protected Vector<Building> prepareSummaryQuery() {
        return new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs());
    }

    @Override
    protected void bindDetailsFactories() {
        bindDetailsFactory(proto().leads(), new LeadsDetailsFactory(GWT.<LeadsAndRentalsGadgetService> create(LeadsAndRentalsGadgetService.class), this,
                proto().leads()));

        bindDetailsFactory(proto().appointmentsLabel(),
                new AppointmentsDetailsFactory(GWT.<LeadsAndRentalsGadgetService> create(LeadsAndRentalsGadgetService.class), this, proto().appointments()));

        bindDetailsFactory(proto().rentalsLabel(),
                new LeasesFromLeadsDetailsFactory(GWT.<LeadsAndRentalsGadgetService> create(LeadsAndRentalsGadgetService.class), this, proto().rentals()));
    }

}
