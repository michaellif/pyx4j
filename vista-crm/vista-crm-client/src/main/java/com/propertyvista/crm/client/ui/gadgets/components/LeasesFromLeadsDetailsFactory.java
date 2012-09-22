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
package com.propertyvista.crm.client.ui.gadgets.components;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractDetailsLister;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeasesFromLeadListService;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeasesFromLeadsDetailsFactory extends AbstractListerDetailsFactory<Lead, CounterGadgetFilter> {

    private static class LeasesFromLeadsLister extends AbstractDetailsLister<Lead> {

        public LeasesFromLeadsLister() {
            super(Lead.class);
            setColumnDescriptors(//@formatter:off
                    new Builder(proto().lease().leaseId()).build(),
                    new Builder(proto().lease().type()).build(),
                    
                    new Builder(proto().lease().unit().building().propertyCode()).build(),
                    new Builder(proto().lease().unit()).searchable(false).build(),
                    new Builder(proto().lease().unit().info().number()).columnTitle(proto().lease().unit().getMeta().getCaption()).searchableOnly().build(),
                    
                    new Builder(proto().lease().status()).build(),
                    new Builder(proto().lease().completion()).build(),
                    new Builder(proto().lease().billingAccount().accountNumber()).build(),
                    
                    new Builder(proto().lease().leaseFrom()).build(),
                    new Builder(proto().lease().leaseTo()).build(),
                    
                    new Builder(proto().lease().expectedMoveIn()).build(),
                    new Builder(proto().lease().expectedMoveOut(), false).build(),
                    new Builder(proto().lease().actualMoveIn(), false).build(),
                    new Builder(proto().lease().actualMoveOut(), false).build(),
                    new Builder(proto().lease().moveOutNotice(), false).build(),
                    
                    new Builder(proto().lease().approvalDate(), false).build(),
                    new Builder(proto().lease().creationDate(), false).build()
                );//@formatter:on

        }

        @Override
        protected void onItemSelect(Lead item) {
            AppSite.getPlaceController().goTo(
                    AppPlaceEntityMapper.resolvePlace(item.lease().getInstanceValueClass()).formViewerPlace(item.lease().getPrimaryKey()));
        }

    }

    public LeasesFromLeadsDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider,
            ICriteriaProvider<Lead, CounterGadgetFilter> criteriaProvider) {
        super(//@formatter:off
                Lead.class,
                new LeasesFromLeadsLister(),
                GWT.<LeasesFromLeadListService>create(LeasesFromLeadListService.class),
                filterDataProvider,
                criteriaProvider
        );//@formatter:on
    }
}
