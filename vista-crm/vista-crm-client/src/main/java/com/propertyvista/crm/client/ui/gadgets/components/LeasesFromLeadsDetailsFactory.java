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
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeasesFromLeadListService;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeasesFromLeadsDetailsFactory extends AbstractListerDetailsFactory<Lead, CounterGadgetFilter> {

    private static List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;
    static {
        Lead proto = EntityFactory.getEntityPrototype(Lead.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                    new ColumnDescriptor.Builder(proto.lease().leaseId()).build(),
                    new ColumnDescriptor.Builder(proto.lease().type()).build(),

                    new ColumnDescriptor.Builder(proto.lease().unit().building().propertyCode()).build(),
                    new ColumnDescriptor.Builder(proto.lease().unit()).searchable(false).build(),
                    new ColumnDescriptor.Builder(proto.lease().unit().info().number()).columnTitle(proto.lease().unit().getMeta().getCaption()).searchableOnly().build(),

                    new ColumnDescriptor.Builder(proto.lease().status()).build(),
                    new ColumnDescriptor.Builder(proto.lease().completion()).build(),
                    new ColumnDescriptor.Builder(proto.lease().billingAccount().accountNumber()).build(),

                    new ColumnDescriptor.Builder(proto.lease().leaseFrom()).build(),
                    new ColumnDescriptor.Builder(proto.lease().leaseTo()).build(),

                    new ColumnDescriptor.Builder(proto.lease().expectedMoveIn()).build(),
                    new ColumnDescriptor.Builder(proto.lease().expectedMoveOut()).visible(false).build(),
                    new ColumnDescriptor.Builder(proto.lease().actualMoveIn()).visible(false).build(),
                    new ColumnDescriptor.Builder(proto.lease().actualMoveOut()).visible(false).build(),
                    new ColumnDescriptor.Builder(proto.lease().moveOutSubmissionDate()).visible(false).build(),

                    new ColumnDescriptor.Builder(proto.lease().approvalDate()).visible(false).build(),
                    new ColumnDescriptor.Builder(proto.lease().creationDate()).visible(false).build()
                );//@formatter:on

    }

    public LeasesFromLeadsDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider,
            ICriteriaProvider<Lead, CounterGadgetFilter> criteriaProvider, Proxy<ListerUserSettings> leasesFromListListerSettingsProxy) {
        super(//@formatter:off
                Lead.class,
                DEFAULT_COLUMN_DESCRIPTORS,
                GWT.<LeasesFromLeadListService>create(LeasesFromLeadListService.class),
                filterDataProvider,
                criteriaProvider,
                leasesFromListListerSettingsProxy
        );//@formatter:on
    }

    @Override
    protected void onItemSelected(Lead item) {
        AppSite.getPlaceController()
                .goTo(AppPlaceEntityMapper.resolvePlace(item.lease().getInstanceValueClass()).formViewerPlace(item.lease().getPrimaryKey()));
    }
}
