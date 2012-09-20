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
package com.propertyvista.crm.client.ui.gadgets.arrears;

import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.IObject;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsGadget extends CounterGadgetInstanceBase<ArrearsGadgetDataDTO, Vector<Building>, ArrearsGadgetMetadata> {

    public ArrearsGadget(GadgetMetadata metadata) {
        super(//@formatter:off
                ArrearsGadgetDataDTO.class,
                GWT.<ArrearsGadgetService> create(ArrearsGadgetService.class),
                new ArrearsGadgetSummaryForm(),
                metadata,
                ArrearsGadgetMetadata.class
       );//@formatter:on
    }

    @Override
    protected Vector<Building> prepareSummaryQuery() {
        return new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs());
    }

    @Override
    protected void bindDetailsFactories() {
        bind(proto().delinquentTenants());
        bind(proto().outstandingThisMonth());
        bind(proto().outstanding1to30Days());
        bind(proto().outstanding31to60Days());
        bind(proto().outstanding61to90Days());
        bind(proto().outstanding91andMoreDays());
        bind(proto().outstandingTotal());
    }

    private void bind(IObject<?> member) {
        bindDetailsFactory(member, new DelinquentTenantsDetailsFactory(GWT.<ArrearsGadgetService> create(ArrearsGadgetService.class), this, member));
    }

}
