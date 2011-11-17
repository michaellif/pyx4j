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

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.ui.crud.marketing.lead.AppointmentViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.AppointmentCrudService;
import com.propertyvista.crm.rpc.services.ShowingCrudService;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Showing;

public class AppointmentViewerActivity extends ViewerActivityBase<Appointment> implements AppointmentViewerView.Presenter {

    private final IListerView.Presenter showingsLister;

    @SuppressWarnings("unchecked")
    public AppointmentViewerActivity(Place place) {
        super(place, MarketingViewFactory.instance(AppointmentViewerView.class), (AbstractCrudService<Appointment>) GWT.create(AppointmentCrudService.class));

        showingsLister = new ListerActivityBase<Showing>(place, ((AppointmentViewerView) view).getShowingsListerView(),
                (AbstractCrudService<Showing>) GWT.create(ShowingCrudService.class), Showing.class);

    }

    @Override
    public Presenter getShowingsPresenter() {
        return showingsLister;
    }

    @Override
    protected void onPopulateSuccess(Appointment result) {
        super.onPopulateSuccess(result);

        showingsLister.setParentFiltering(result.getPrimaryKey());
        showingsLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) showingsLister).onStop();
        super.onStop();
    }
}
