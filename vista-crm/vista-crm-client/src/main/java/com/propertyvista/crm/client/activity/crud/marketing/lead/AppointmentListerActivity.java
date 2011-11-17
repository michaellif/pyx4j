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
package com.propertyvista.crm.client.activity.crud.marketing.lead;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;

import com.propertyvista.crm.client.ui.crud.marketing.lead.AppointmentListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.AppointmentCrudService;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentListerActivity extends ListerActivityBase<Appointment> {

    @SuppressWarnings("unchecked")
    public AppointmentListerActivity(Place place) {
        super(place, MarketingViewFactory.instance(AppointmentListerView.class), (AbstractCrudService<Appointment>) GWT.create(AppointmentCrudService.class),
                Appointment.class);
    }
}
