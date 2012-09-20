/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.notices;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.IObject;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.components.LeasesDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.UnitDetailsFactory;
import com.propertyvista.crm.rpc.dto.gadgets.NoticesGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.NoticesGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.NoticesGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class NoticesGadgetFactory extends AbstractGadget<NoticesGadgetMetadata> {

    public static class NoticesGadget extends CounterGadgetInstanceBase<NoticesGadgetDataDTO, Vector<Building>, NoticesGadgetMetadata> {

        public NoticesGadget(GadgetMetadata metadata) {
            super(//@formatter:off
                    NoticesGadgetDataDTO.class,
                    GWT.<NoticesGadgetService>create(NoticesGadgetService.class),
                    new NoticesSummaryForm(),
                    metadata,
                    NoticesGadgetMetadata.class
            );//@formatter:on            
        }

        @Override
        protected Vector<Building> prepareSummaryQuery() {
            return new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs());
        }

        @Override
        protected void bindDetailsFactories() {
            bindDetailsFactory(proto().unitVacancyLabel(), unitDetailsFactory(proto().vacantUnits()));

            bindLeaseDetailsFactory(proto().noticesLeavingThisMonth());
            bindLeaseDetailsFactory(proto().noticesLeavingNextMonth());
            bindLeaseDetailsFactory(proto().noticesLeaving60to90Days());
            bindLeaseDetailsFactory(proto().noticesLeavingOver90Days());

        }

        private void bindLeaseDetailsFactory(IObject<?> category) {
            bindDetailsFactory(category, new LeasesDetailsFactory(GWT.<LeaseExpirationGadgetService> create(NoticesGadgetService.class), this, category));
        }

        private CounterDetailsFactory unitDetailsFactory(IObject<?> category) {
            return new UnitDetailsFactory(GWT.<LeaseExpirationGadgetService> create(NoticesGadgetService.class), this, category);
        }

    }

    public NoticesGadgetFactory() {
        super(NoticesGadgetMetadata.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Leases.toString());
    }

    @Override
    protected GadgetInstanceBase<NoticesGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new NoticesGadget(gadgetMetadata);
    }

}
