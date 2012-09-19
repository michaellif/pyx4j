/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractDetailsLister;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilterProvider;
import com.propertyvista.crm.rpc.services.customer.lead.LeadCrudService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.LeadCriteriaProvider;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadsDetailsFactory extends AbstractListerDetailsFactory<Lead, CounterGadgetFilter> {

    private static class LeadsDetailsLister extends AbstractDetailsLister<Lead> {

        private static final I18n i18n = I18n.get(LeadsDetailsLister.class);

        public LeadsDetailsLister() {
            super(Lead.class);

            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().leadId(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().guests(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().guests().$().person().name().lastName()).columnTitle(i18n.tr("Guest Last Name")).searchableOnly().build(),
                    new MemberColumnDescriptor.Builder(proto().moveInDate(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().leaseTerm(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().floorplan(), true).searchable(false).build(),
                    new MemberColumnDescriptor.Builder(proto().createDate(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().status(), true).build()
            );//@formatter:on
        }

    }

    public LeadsDetailsFactory(final LeadCriteriaProvider leadCriteriaProvider, IBuildingFilterContainer builindgsFilterContainer, IObject<?> filterMember) {
        super(//@formatter:off
                Lead.class,
                new LeadsDetailsLister(),
                GWT.<LeadCrudService>create(LeadCrudService.class),
                new CounterGadgetFilterProvider(builindgsFilterContainer, filterMember.getPath()), 
                new ICriteriaProvider<Lead, CounterGadgetFilter>() {

                    @Override
                    public void makeCriteria(final AsyncCallback<EntityListCriteria<Lead>> callback, CounterGadgetFilter filterData) {
                        leadCriteriaProvider.makeLeadFilterCriteria(callback, filterData.getBuildings(), filterData.getCounterMember().toString());                        
                    }
                }
        );//@formatter:on
    }

}
