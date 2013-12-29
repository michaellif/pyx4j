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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.DelinquentLeasesDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.forms.ArrearsGadgetSummaryForm;
import com.propertyvista.crm.client.ui.gadgets.forms.ArrearsGadgetSummaryMetadataForm;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetQueryDataDTO;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentLeaseDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;

public class ArrearsSummaryGadget extends CounterGadgetInstanceBase<ArrearsGadgetDataDTO, ArrearsGadgetQueryDataDTO, ArrearsSummaryGadgetMetadata> {

    private final static I18n i18n = I18n.get(ArrearsSummaryGadget.class);

    ICriteriaProvider<DelinquentLeaseDTO, CounterGadgetFilter> criteriaProvider;

    ArrearsGadgetService service = GWT.create(ArrearsGadgetService.class);

    public ArrearsSummaryGadget(ArrearsSummaryGadgetMetadata metadata) {
        super(//@formatter:off
                ArrearsGadgetDataDTO.class,
                GWT.<ArrearsGadgetService> create(ArrearsGadgetService.class),
                new ArrearsGadgetSummaryForm(),
                metadata,
                ArrearsSummaryGadgetMetadata.class,
                new ArrearsGadgetSummaryMetadataForm()
       );//@formatter:on
    }

    @Override
    protected ArrearsGadgetQueryDataDTO makeSummaryQuery() {
        ArrearsGadgetQueryDataDTO query = EntityFactory.create(ArrearsGadgetQueryDataDTO.class);
        query.buildingsFilter().addAll(buildingsFilterContainer.getSelectedBuildingsStubs());
        query.asOf().setValue(getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate());
        query.category().setValue(getMetadata().customizeCategory().isBooleanTrue() ? getMetadata().category().getValue() : null);
        return query;
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

    @Override
    protected Widget renderTitle() {
        ArrearsGadgetQueryDataDTO query = makeSummaryQuery();
        String category = query.category().isNull() ? i18n.tr("Total") : query.category().getValue().toString();
        Label title = new Label(i18n.tr("{0} arrears as of {1,date,short}", category, query.asOf().getValue()));
        return title;
    }

    private void bind(final IObject<?> member) {
        bindDetailsFactory(member, new DelinquentLeasesDetailsFactory(this, new ICriteriaProvider<DelinquentLeaseDTO, CounterGadgetFilter>() {
            @Override
            public void makeCriteria(AsyncCallback<EntityListCriteria<DelinquentLeaseDTO>> callback, CounterGadgetFilter filterData) {
                service.makeDelinquentLeaseCriteria(callback, makeSummaryQuery(), filterData.getCounterMember());
            }
        }, new Proxy<ListerUserSettings>() {

            @Override
            public ListerUserSettings get() {
                return getMetadata().delinquentLeasesListerSettings();
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
