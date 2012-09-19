/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.leasexpiration;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.IObject;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.components.LeasesDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.UnitDetailsFactory;
import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMeta;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class LeaseExpirationGadgetFactory extends AbstractGadget<LeaseExpirationGadgetMeta> {

    public class LeaseExpirationGadget extends CounterGadgetInstanceBase<LeaseExpirationGadgetDataDTO, Vector<Building>, LeaseExpirationGadgetMeta> implements
            IBuildingFilterContainer {

        public LeaseExpirationGadget(GadgetMetadata metadata) {
            super(LeaseExpirationGadgetDataDTO.class, GWT.<LeaseExpirationGadgetService> create(LeaseExpirationGadgetService.class),
                    new LeaseExpirationSummaryForm(), metadata, LeaseExpirationGadgetMeta.class);
        }

        @Override
        protected Vector<Building> prepareSummaryQuery() {
            return new Vector<Building>(getSelectedBuildingsStubs());
        }

        @Override
        protected void bindDetailsFactories() {
            UnitDetailsFactory unitDetailsFactory = unitDetailsFactory(proto().unitOccupancy());
            bindDetailsFactory(proto().unitOccupancy(), unitDetailsFactory);

            bindDetailsFactory(proto().numOfLeasesEndingThisMonth(), leaseDetailsFactory(proto().numOfLeasesEndingThisMonth()));
            bindDetailsFactory(proto().numOfLeasesEndingNextMonth(), leaseDetailsFactory(proto().numOfLeasesEndingNextMonth()));
            bindDetailsFactory(proto().numOfLeasesEndingOver90Days(), leaseDetailsFactory(proto().numOfLeasesEndingOver90Days()));
            bindDetailsFactory(proto().numOfLeasesOnMonthToMonth(), leaseDetailsFactory(proto().numOfLeasesOnMonthToMonth()));
        }

        private LeasesDetailsFactory leaseDetailsFactory(IObject<?> category) {
            return new LeasesDetailsFactory(GWT.<LeaseExpirationGadgetService> create(LeaseExpirationGadgetService.class), this, category);
        }

        private UnitDetailsFactory unitDetailsFactory(IObject<?> filter) {
            return new UnitDetailsFactory(GWT.<LeaseExpirationGadgetService> create(LeaseExpirationGadgetService.class), this, filter);
        }

    }

    public LeaseExpirationGadgetFactory() {
        super(LeaseExpirationGadgetMeta.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Leases.toString());
    }

    @Override
    protected GadgetInstanceBase<LeaseExpirationGadgetMeta> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new LeaseExpirationGadget(gadgetMetadata);
    }

}
