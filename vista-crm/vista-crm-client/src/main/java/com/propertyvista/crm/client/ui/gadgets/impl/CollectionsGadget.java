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
import com.propertyvista.crm.client.ui.gadgets.components.PaymentDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.forms.CollectionsSummaryForm;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.dto.gadgets.CollectionsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.CollectionsGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.CollectionsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public class CollectionsGadget extends CounterGadgetInstanceBase<CollectionsGadgetDataDTO, Vector<Building>, CollectionsGadgetMetadata> {

    private final CollectionsGadgetService collectionsGadgetService;

    public CollectionsGadget(CollectionsGadgetMetadata metadata) {
        super(//@formatter:off
                CollectionsGadgetDataDTO.class,
                GWT.<CollectionsGadgetService> create(CollectionsGadgetService.class),
                new CollectionsSummaryForm(),
                metadata,
                CollectionsGadgetMetadata.class
        );//@formatter:on
        collectionsGadgetService = GWT.<CollectionsGadgetService> create(CollectionsGadgetService.class);
    }

    @Override
    protected void bindDetailsFactories() {
        bindLeaseDetailsFacotry(proto().leasesPaidThisMonth());
        bindPaymentDetailsFactory(proto().fundsCollectedThisMonth());
        bindPaymentDetailsFactory(proto().fundsInProcessing());
    }

    @Override
    protected Vector<Building> makeSummaryQuery() {
        return new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs());
    }

    private void bindLeaseDetailsFacotry(IObject<?> member) {
        ICriteriaProvider<LeaseDTO, CounterGadgetFilter> criteriaProvider = new ICriteriaProvider<LeaseDTO, CounterGadgetFilter>() {
            @Override
            public void makeCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, CounterGadgetFilter filterData) {
                collectionsGadgetService.makeLeaseFilterCriteria(callback, filterData.getBuildings(), filterData.getCounterMember().toString());
            }
        };
        bindDetailsFactory(member, new LeasesDetailsFactory(this, criteriaProvider, new Proxy<ListerUserSettings>() {

            @Override
            public ListerUserSettings get() {
                return getMetadata().leasesListerDetails();
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

    private void bindPaymentDetailsFactory(IObject<?> member) {
        ICriteriaProvider<PaymentRecordDTO, CounterGadgetFilter> criteriaProvider = new ICriteriaProvider<PaymentRecordDTO, CounterGadgetFilter>() {
            @Override
            public void makeCriteria(AsyncCallback<EntityListCriteria<PaymentRecordDTO>> callback, CounterGadgetFilter filterData) {
                GWT.<CollectionsGadgetService> create(CollectionsGadgetService.class).makePaymentCriteria(callback, filterData.getBuildings(),
                        filterData.getCounterMember().toString());
            }
        };
        bindDetailsFactory(member, new PaymentDetailsFactory(this, criteriaProvider, new Proxy<ListerUserSettings>() {

            @Override
            public ListerUserSettings get() {
                return getMetadata().paymentListerDetails();
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