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
package com.propertyvista.crm.client.activity.crud.tenant.lead;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.tenant.lead.LeadViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.LeadCrudService;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeadViewerActivity extends ViewerActivityBase<Lead> implements LeadViewerView.Presenter {

    @SuppressWarnings("unchecked")
    public LeadViewerActivity(Place place) {
        super((LeadViewerView) TenantViewFactory.instance(LeadViewerView.class), (AbstractCrudService<Lead>) GWT.create(LeadCrudService.class));
        withPlace(place);
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
}
