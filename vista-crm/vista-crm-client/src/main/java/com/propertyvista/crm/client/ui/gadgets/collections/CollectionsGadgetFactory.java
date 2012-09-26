/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.collections;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.components.PaymentDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.TenantsDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.rpc.dto.gadgets.CollectionsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.CollectionsGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.CollectionsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.TenantDTO;

public class CollectionsGadgetFactory extends AbstractGadgetFactory<CollectionsGadgetMetadata> {

    public static class CollectionsGaget extends CounterGadgetInstanceBase<CollectionsGadgetDataDTO, Vector<Building>, CollectionsGadgetMetadata> {

        public CollectionsGaget(GadgetMetadata metadata) {
            super(//@formatter:off
                    CollectionsGadgetDataDTO.class,
                    GWT.<CollectionsGadgetService> create(CollectionsGadgetService.class),
                    new CollectionsSummaryForm(),
                    metadata,
                    CollectionsGadgetMetadata.class
            );//@formatter:on
        }

        @Override
        protected void bindDetailsFactories() {
            bindTenantsDetailsFactory(proto().tenantsPaidThisMonth());
            bindPaymentDetailsFactory(proto().fundsCollectedThisMonthLabel(), proto().fundsCollectedThisMonth());
            bindPaymentDetailsFactory(proto().fundsInProcessingLabel(), proto().fundsInProcessing());
        }

        @Override
        protected Vector<Building> prepareSummaryQuery() {
            return new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs());
        }

        private void bindTenantsDetailsFactory(IObject<?> member) {

            ICriteriaProvider<TenantDTO, CounterGadgetFilter> criteriaProvider = new ICriteriaProvider<TenantDTO, CounterGadgetFilter>() {
                @Override
                public void makeCriteria(AsyncCallback<EntityListCriteria<TenantDTO>> callback, CounterGadgetFilter filterData) {
                    GWT.<CollectionsGadgetService> create(CollectionsGadgetService.class).makeTenantCriteria(callback, filterData.getBuildings(),
                            filterData.getCounterMember().toString());
                }
            };
            bindDetailsFactory(member, new TenantsDetailsFactory(this, criteriaProvider));
        }

        private void bindPaymentDetailsFactory(IObject<?> member, IObject<?> bindingFilter) {
            ICriteriaProvider<PaymentRecordDTO, CounterGadgetFilter> criteriaProvider = new ICriteriaProvider<PaymentRecordDTO, CounterGadgetFilter>() {
                @Override
                public void makeCriteria(AsyncCallback<EntityListCriteria<PaymentRecordDTO>> callback, CounterGadgetFilter filterData) {
                    GWT.<CollectionsGadgetService> create(CollectionsGadgetService.class).makePaymentCriteria(callback, filterData.getBuildings(),
                            filterData.getCounterMember().toString());
                }
            };
            bindDetailsFactory(member, new PaymentDetailsFactory(this, criteriaProvider));
        }
    }

    public CollectionsGadgetFactory() {
        super(CollectionsGadgetMetadata.class);
    }

    @Override
    protected GadgetInstanceBase<CollectionsGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new CollectionsGaget(gadgetMetadata);
    }

}
