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

import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentsDetailsFactory extends AbstractListerDetailsFactory<Appointment, CounterGadgetFilter> {

    private static final List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;
    static {
        Appointment proto = EntityFactory.getEntityPrototype(Appointment.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto.date()).build(),
                new MemberColumnDescriptor.Builder(proto.time()).build(),
                new MemberColumnDescriptor.Builder(proto.agent()).build(),
                new MemberColumnDescriptor.Builder(proto.phone()).build(),
                new MemberColumnDescriptor.Builder(proto.email()).build(),
                new MemberColumnDescriptor.Builder(proto.status()).build()
        );//@formatter:on

    }

    public AppointmentsDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider,
            ICriteriaProvider<Appointment, CounterGadgetFilter> criteriaProvider, Proxy<ListerUserSettings> appointmentsListerSettingsProxy) {
        super(//@formatter:off
                Appointment.class,
                DEFAULT_COLUMN_DESCRIPTORS,
                GWT.<AppointmentCrudService>create(AppointmentCrudService.class),
                filterDataProvider,
                criteriaProvider,
                appointmentsListerSettingsProxy
        );//@formatter:on
    }
}
