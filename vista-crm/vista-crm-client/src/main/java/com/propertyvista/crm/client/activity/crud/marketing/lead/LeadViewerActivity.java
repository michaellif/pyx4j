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
package com.propertyvista.crm.client.activity.crud.marketing.lead;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.ui.crud.marketing.lead.LeadViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.AppointmentCrudService;
import com.propertyvista.crm.rpc.services.LeadCrudService;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeadViewerActivity extends ViewerActivityBase<Lead> implements LeadViewerView.Presenter {

    private final IListerView.Presenter appointmentsLister;

    @SuppressWarnings("unchecked")
    public LeadViewerActivity(Place place) {
        super((LeadViewerView) MarketingViewFactory.instance(LeadViewerView.class), (AbstractCrudService<Lead>) GWT.create(LeadCrudService.class));

        appointmentsLister = new ListerActivityBase<Appointment>(((LeadViewerView) view).getAppointmentsListerView(),
                (AbstractCrudService<Appointment>) GWT.create(AppointmentCrudService.class), Appointment.class);

        setPlace(place);
        appointmentsLister.setPlace(place);
    }

    @Override
    public void convertToLease() {
        ((LeadCrudService) service).convertToLease(new AsyncCallback<Lease>() {

            @Override
            public void onSuccess(Lease result) {
                onLeaseConvertionSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                onConvertionFail(caught);
            }
        }, entityId);
    }

    public void onLeaseConvertionSuccess(Lease result) {
        ((LeadViewerView) view).onLeaseConvertionSuccess(result);
    }

    protected void onConvertionFail(Throwable caught) {
        if (!((LeadViewerView) view).onConvertionFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    @Override
    public Presenter getAppointmentsPresenter() {
        return appointmentsLister;
    }

    @Override
    protected void onPopulateSuccess(Lead result) {
        super.onPopulateSuccess(result);

        appointmentsLister.setParentFiltering(result.getPrimaryKey());
        appointmentsLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) appointmentsLister).onStop();
        super.onStop();
    }
}
