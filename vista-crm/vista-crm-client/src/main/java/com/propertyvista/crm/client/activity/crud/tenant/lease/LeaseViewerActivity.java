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
package com.propertyvista.crm.client.activity.crud.tenant.lease;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;

import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.LeaseCrudService;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerActivity extends ViewerActivityBase<LeaseDTO> implements LeaseViewerView.Presenter {

    @SuppressWarnings("unchecked")
    public LeaseViewerActivity(Place place) {
        super((LeaseViewerView) TenantViewFactory.instance(LeaseViewerView.class), (AbstractCrudService<LeaseDTO>) GWT.create(LeaseCrudService.class));
        setPlace(place);
    }

    @Override
    public void createMasterApplication() {
        ((LeaseCrudService) service).createMasterApplication(new AsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, entityId);
    }

}
