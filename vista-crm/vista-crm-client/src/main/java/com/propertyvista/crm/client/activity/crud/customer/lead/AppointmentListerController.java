/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.customer.lead;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.ListerController;

import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentListerView;
import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;

public class AppointmentListerController extends ListerController<Appointment> {

    public AppointmentListerController(Place place, AppointmentListerView view) {
        super(Appointment.class, view, GWT.<AppointmentCrudService> create(AppointmentCrudService.class));
    }

    @Override
    public void populate() {
        super.populate();

        ((AppointmentCrudService) getService()).getActiveState(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                ((AppointmentListerView) getView()).setAddNewVisible(result);
            }
        }, EntityFactory.createIdentityStub(Lead.class, getParent()));
    }
}
