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
package com.propertyvista.crm.client.ui.gadgets.impl;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.DelinquentTenantsDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.forms.ArrearsGadgetSummaryForm;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentTenantDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsGadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsGadget extends CounterGadgetInstanceBase<ArrearsGadgetDataDTO, Vector<Building>, ArrearsGadgetMetadata> {

    public ArrearsGadget(ArrearsGadgetMetadata metadata) {
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
        ICriteriaProvider<DelinquentTenantDTO, CounterGadgetFilter> criteriaProvider = new ICriteriaProvider<DelinquentTenantDTO, CounterGadgetFilter>() {
            @Override
            public void makeCriteria(AsyncCallback<EntityListCriteria<DelinquentTenantDTO>> callback, CounterGadgetFilter filterData) {
                GWT.<ArrearsGadgetService> create(ArrearsGadgetService.class).makeTenantCriteria(callback, filterData.getBuildings(),
                        filterData.getCounterMember().toString());
            }
        };
        bindDetailsFactory(member, new DelinquentTenantsDetailsFactory(this, criteriaProvider));
    }
}
