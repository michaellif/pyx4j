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

import java.util.List;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.customer.lead.LeadViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.crm.rpc.services.customer.lead.LeadCrudService;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadViewerActivity extends CrmViewerActivity<Lead> implements LeadViewerView.Presenter {

    private final IListerView.Presenter<Appointment> appointmentsLister;

    @SuppressWarnings("unchecked")
    public LeadViewerActivity(Place place) {
        super(place, MarketingViewFactory.instance(LeadViewerView.class), (AbstractCrudService<Lead>) GWT.create(LeadCrudService.class));

        appointmentsLister = new ListerActivityBase<Appointment>(place, ((LeadViewerView) view).getAppointmentsListerView(),
                (AbstractCrudService<Appointment>) GWT.create(AppointmentCrudService.class), Appointment.class);

    }

    @Override
    public void getInterestedUnits(final AsyncCallback<List<AptUnit>> callback) {
        ((LeadCrudService) service).getInterestedUnits(new DefaultAsyncCallback<Vector<AptUnit>>() {
            @Override
            public void onSuccess(Vector<AptUnit> result) {
                callback.onSuccess(result);
            }
        }, entityId);
    }

    @Override
    public void convertToLease(Key unitId) {
        ((LeadCrudService) service).convertToLease(new AsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                onLeaseConvertionSuccess();
            }

            @Override
            public void onFailure(Throwable caught) {
                onConvertionFail(caught);
            }
        }, entityId, unitId);
    }

    public void onLeaseConvertionSuccess() {
        ((LeadViewerView) view).onLeaseConvertionSuccess();
    }

    protected void onConvertionFail(Throwable caught) {
        if (!((LeadViewerView) view).onConvertionFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    @Override
    public Presenter<Appointment> getAppointmentsPresenter() {
        return appointmentsLister;
    }

    @Override
    protected void onPopulateSuccess(Lead result) {
        super.onPopulateSuccess(result);

        appointmentsLister.setParent(result.getPrimaryKey());
        appointmentsLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) appointmentsLister).onStop();
        super.onStop();
    }

    @Override
    public void close() {
        ((LeadCrudService) service).close(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId);
    }
}
