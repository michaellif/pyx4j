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
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.components.LeasesDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.UnitDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public class LeaseExpirationGadgetFactory extends AbstractGadget<LeaseExpirationGadgetMetadata> {

    public class LeaseExpirationGadget extends CounterGadgetInstanceBase<LeaseExpirationGadgetDataDTO, Vector<Building>, LeaseExpirationGadgetMetadata> implements
            IBuildingFilterContainer {

        public LeaseExpirationGadget(GadgetMetadata metadata) {
            super(LeaseExpirationGadgetDataDTO.class, GWT.<LeaseExpirationGadgetService> create(LeaseExpirationGadgetService.class),
                    new LeaseExpirationSummaryForm(), metadata, LeaseExpirationGadgetMetadata.class);
        }

        @Override
        protected Vector<Building> prepareSummaryQuery() {
            return new Vector<Building>(getSelectedBuildingsStubs());
        }

        @Override
        protected void bindDetailsFactories() {
            ICriteriaProvider<AptUnitDTO, CounterGadgetFilter> unitCriteriaProvider = new ICriteriaProvider<AptUnitDTO, CounterGadgetFilter>() {
                @Override
                public void makeCriteria(AsyncCallback<EntityListCriteria<AptUnitDTO>> callback, CounterGadgetFilter filterData) {
                    GWT.<LeaseExpirationGadgetService> create(LeaseExpirationGadgetService.class).makeUnitFilterCriteria(callback, filterData.getBuildings(),
                            filterData.getCounterMember().toString());
                }
            };
            bindDetailsFactory(proto().unitOccupancyLabel(), new UnitDetailsFactory(this, unitCriteriaProvider));

            bindLeaseDetailsFactory(proto().numOfLeasesEndingThisMonth());
            bindLeaseDetailsFactory(proto().numOfLeasesEndingNextMonth());
            bindLeaseDetailsFactory(proto().numOfLeasesEnding60to90Days());
            bindLeaseDetailsFactory(proto().numOfLeasesEndingOver90Days());

            bindLeaseDetailsFactory(proto().numOfLeasesOnMonthToMonth());
        }

        private void bindLeaseDetailsFactory(IObject<?> filter) {
            ICriteriaProvider<LeaseDTO, CounterGadgetFilter> criteriaProvider = new ICriteriaProvider<LeaseDTO, CounterGadgetFilter>() {

                @Override
                public void makeCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, CounterGadgetFilter filterData) {
                    GWT.<LeaseExpirationGadgetService> create(LeaseExpirationGadgetService.class).makeLeaseFilterCriteria(callback, filterData.getBuildings(),
                            filterData.getCounterMember());
                }
            };
            bindDetailsFactory(filter, new LeasesDetailsFactory(this, criteriaProvider));
        }
    }

    public LeaseExpirationGadgetFactory() {
        super(LeaseExpirationGadgetMetadata.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Leases.toString());
    }

    @Override
    protected GadgetInstanceBase<LeaseExpirationGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new LeaseExpirationGadget(gadgetMetadata);
    }

}
