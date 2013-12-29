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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.services.customer.lead.LeadCrudService;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadsDetailsFactory extends AbstractListerDetailsFactory<Lead, CounterGadgetFilter> {

    private static final I18n i18n = I18n.get(LeadsDetailsFactory.class);

    private static final List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;
    static {
        Lead proto = EntityFactory.getEntityPrototype(Lead.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.leadId(), true).build(),
                    new MemberColumnDescriptor.Builder(proto.guests(), true).build(),
                    new MemberColumnDescriptor.Builder(proto.guests().$().person().name().lastName()).columnTitle(i18n.tr("Guest Last Name")).searchableOnly().build(),
                    new MemberColumnDescriptor.Builder(proto.moveInDate(), true).build(),
                    new MemberColumnDescriptor.Builder(proto.leaseTerm(), true).build(),
                    new MemberColumnDescriptor.Builder(proto.floorplan(), true).searchable(false).build(),
                    new MemberColumnDescriptor.Builder(proto.createDate(), true).build(),
                    new MemberColumnDescriptor.Builder(proto.status(), true).build()
            );//@formatter:on

    }

    public LeadsDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider, ICriteriaProvider<Lead, CounterGadgetFilter> criteriaProvider,
            Proxy<ListerUserSettings> listerSettingsProxy) {
        super(//@formatter:off
                Lead.class,
                DEFAULT_COLUMN_DESCRIPTORS,
                GWT.<LeadCrudService>create(LeadCrudService.class),
                filterDataProvider,
                criteriaProvider,
                listerSettingsProxy
        );//@formatter:on
    }

}
