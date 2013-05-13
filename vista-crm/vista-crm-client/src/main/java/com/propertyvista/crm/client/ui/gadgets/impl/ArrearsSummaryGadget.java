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
import com.propertyvista.crm.client.ui.gadgets.components.details.DelinquentLeasesDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.forms.ArrearsGadgetSummaryForm;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentLeaseDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsSummaryGadget extends CounterGadgetInstanceBase<ArrearsGadgetDataDTO, Vector<Building>, ArrearsSummaryGadgetMetadata> {

    ICriteriaProvider<DelinquentLeaseDTO, CounterGadgetFilter> criteriaProvider;

    ArrearsGadgetService service = GWT.create(ArrearsGadgetService.class);

    public ArrearsSummaryGadget(ArrearsSummaryGadgetMetadata metadata) {
        super(//@formatter:off
                ArrearsGadgetDataDTO.class,
                GWT.<ArrearsGadgetService> create(ArrearsGadgetService.class),
                new ArrearsGadgetSummaryForm(),
                metadata,
                ArrearsSummaryGadgetMetadata.class
       );//@formatter:on
    }

    @Override
    protected Vector<Building> makeSummaryQuery() {
        return new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs());
    }

    @Override
    protected void bindDetailsFactories() {
        bind(proto().buckets().bucketThisMonth());
        bind(proto().outstandingThisMonthCount());
        bind(proto().buckets().bucket30());
        bind(proto().outstanding1to30DaysCount());
        bind(proto().buckets().bucket60());
        bind(proto().outstanding31to60DaysCount());
        bind(proto().buckets().bucket90());
        bind(proto().outstanding61to90DaysCount());
        bind(proto().buckets().bucketOver90());
        bind(proto().outstanding91andMoreDaysCount());
        bind(proto().delinquentLeases());
        bind(proto().buckets().totalBalance());
    }

    private void bind(final IObject<?> member) {
        bindDetailsFactory(member, new DelinquentLeasesDetailsFactory(this, new ICriteriaProvider<DelinquentLeaseDTO, CounterGadgetFilter>() {
            @Override
            public void makeCriteria(AsyncCallback<EntityListCriteria<DelinquentLeaseDTO>> callback, CounterGadgetFilter filterData) {
                service.makeDelinquentLeaseCriteria(callback, filterData.getBuildings(), member.getPath().toString());
            }
        }));
    }
}
