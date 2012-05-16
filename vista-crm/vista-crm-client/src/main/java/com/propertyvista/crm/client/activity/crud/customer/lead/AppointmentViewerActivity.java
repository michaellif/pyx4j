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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.crm.rpc.services.customer.lead.ShowingCrudService;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Showing;

public class AppointmentViewerActivity extends CrmViewerActivity<Appointment> implements AppointmentViewerView.Presenter {

    private final IListerView.Presenter<Showing> showingsLister;

    @SuppressWarnings("unchecked")
    public AppointmentViewerActivity(CrudAppPlace place) {
        super(place, MarketingViewFactory.instance(AppointmentViewerView.class), (AbstractCrudService<Appointment>) GWT.create(AppointmentCrudService.class));

        showingsLister = new ListerActivityBase<Showing>(place, ((AppointmentViewerView) getView()).getShowingsListerView(),
                (AbstractCrudService<Showing>) GWT.create(ShowingCrudService.class), Showing.class);

    }

    @Override
    protected void onPopulateSuccess(Appointment result) {
        super.onPopulateSuccess(result);

        showingsLister.setParent(result.getPrimaryKey());
        showingsLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) showingsLister).onStop();
        super.onStop();
    }
}
