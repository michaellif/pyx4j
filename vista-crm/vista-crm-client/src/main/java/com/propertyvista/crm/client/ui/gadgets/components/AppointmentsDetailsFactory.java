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

import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractDetailsLister;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilterProvider;
import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.AppointmentsCriteriaProvider;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentsDetailsFactory extends AbstractListerDetailsFactory<Appointment, CounterGadgetFilter> {

    private static class AppointmentsDetailsLister extends AbstractDetailsLister<Appointment> {

        public AppointmentsDetailsLister() {
            super(Appointment.class);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().date()).build(),
                    new MemberColumnDescriptor.Builder(proto().time()).build(),
                    new MemberColumnDescriptor.Builder(proto().agent()).build(),
                    new MemberColumnDescriptor.Builder(proto().phone()).build(),
                    new MemberColumnDescriptor.Builder(proto().email()).build(),
                    new MemberColumnDescriptor.Builder(proto().status()).build()
            );//@formatter:on

        }
    }

    public AppointmentsDetailsFactory(final AppointmentsCriteriaProvider appointmentsCriteriaProvider, IBuildingFilterContainer builingFilterContainer,
            IObject<?> filterPreset) {
        super(//@formatter:off
                Appointment.class,
                new AppointmentsDetailsLister(),
                GWT.<AppointmentCrudService>create(AppointmentCrudService.class),
                new CounterGadgetFilterProvider(builingFilterContainer, filterPreset.getPath()),
                new ICriteriaProvider<Appointment, CounterGadgetFilter>() {
                    @Override
                    public void makeCriteria(AsyncCallback<EntityListCriteria<Appointment>> callback, CounterGadgetFilter filterData) {
                        appointmentsCriteriaProvider.makeAppointmentsCriteria(callback, filterData.getBuildings(), filterData.getCounterMember().toString());
                    }
                }
        );//@formatter:on
    }
}
