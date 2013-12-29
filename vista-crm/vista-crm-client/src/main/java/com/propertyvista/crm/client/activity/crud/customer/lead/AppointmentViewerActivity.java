/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.customer.lead;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentViewerView;
import com.propertyvista.crm.rpc.dto.tenant.ShowingDTO;
import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentViewerActivity extends CrmViewerActivity<Appointment> implements AppointmentViewerView.Presenter {

    private final ILister.Presenter<ShowingDTO> showingsLister;

    public AppointmentViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(AppointmentViewerView.class), GWT.<AppointmentCrudService> create(AppointmentCrudService.class));

        showingsLister = new ShowingListerController(((AppointmentViewerView) getView()).getShowingsListerView());
    }

    @Override
    protected void onPopulateSuccess(Appointment result) {
        super.onPopulateSuccess(result);

        showingsLister.setParent(result.getPrimaryKey());
        showingsLister.populate();
    }

    @Override
    public void close(String reason) {
        ((AppointmentCrudService) getService()).close(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, reason, EntityFactory.createIdentityStub(Appointment.class, getEntityId()));
    }
}
