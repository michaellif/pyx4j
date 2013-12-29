/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.impl;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.components.LeasesDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.UnitDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.forms.NoticesSummaryForm;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.dto.gadgets.NoticesGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.NoticesGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.NoticesGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public class NoticesGadget extends CounterGadgetInstanceBase<NoticesGadgetDataDTO, Vector<Building>, NoticesGadgetMetadata> {

    public NoticesGadget(NoticesGadgetMetadata metadata) {
        super(//@formatter:off
                NoticesGadgetDataDTO.class,
                GWT.<NoticesGadgetService>create(NoticesGadgetService.class),
                new NoticesSummaryForm(),
                metadata,
                NoticesGadgetMetadata.class
        );//@formatter:on            
    }

    @Override
    protected Vector<Building> makeSummaryQuery() {
        return new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs());
    }

    @Override
    protected void bindDetailsFactories() {
        bindUnitDetailsFactory(proto().vacantUnits());

        bindLeaseDetailsFactory(proto().noticesLeavingThisMonth());
        bindLeaseDetailsFactory(proto().noticesLeavingNextMonth());
        bindLeaseDetailsFactory(proto().noticesLeaving60to90Days());
        bindLeaseDetailsFactory(proto().noticesLeavingOver90Days());

    }

    private void bindLeaseDetailsFactory(IObject<?> category) {
        ICriteriaProvider<LeaseDTO, CounterGadgetFilter> criteriaProvider = new ICriteriaProvider<LeaseDTO, CounterGadgetFilter>() {

            @Override
            public void makeCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, CounterGadgetFilter filterData) {
                GWT.<NoticesGadgetService> create(NoticesGadgetService.class).makeLeaseFilterCriteria(callback, filterData.getBuildings(),
                        filterData.getCounterMember());
            }
        };
        bindDetailsFactory(category, new LeasesDetailsFactory(this, criteriaProvider, new Proxy<ListerUserSettings>() {

            @Override
            public ListerUserSettings get() {
                return getMetadata().leaseLeasterDetails();
            }

            @Override
            public void save() {
                saveMetadata();
            }

            @Override
            public boolean isModifiable() {
                return ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getMetadata().ownerUser().getPrimaryKey());
            }
        }));
    }

    private void bindUnitDetailsFactory(IObject<?> category) {
        ICriteriaProvider<AptUnitDTO, CounterGadgetFilter> criteriaProvider = new ICriteriaProvider<AptUnitDTO, CounterGadgetFilter>() {
            @Override
            public void makeCriteria(AsyncCallback<EntityListCriteria<AptUnitDTO>> callback, CounterGadgetFilter filterData) {
                GWT.<NoticesGadgetService> create(NoticesGadgetService.class).makeUnitFilterCriteria(callback, filterData.getBuildings(),
                        filterData.getCounterMember());
            }
        };
        bindDetailsFactory(category, new UnitDetailsFactory(this, criteriaProvider, new Proxy<ListerUserSettings>() {

            @Override
            public ListerUserSettings get() {
                return getMetadata().unitsListerSettings();
            }

            @Override
            public void save() {
                saveMetadata();
            }

            @Override
            public boolean isModifiable() {
                return ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getMetadata().ownerUser().getPrimaryKey());
            }

        }));
    }
}