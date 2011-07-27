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
package com.propertyvista.crm.client.activity.crud.tenant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.tenant.TenantEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.TenantView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.TenantCrudService;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorActivity extends EditorActivityBase<TenantDTO> implements TenantEditorView.Presenter {

    private final TenantActivityDelegate delegate;

    private Tenant.Type tenantType;

    @SuppressWarnings("unchecked")
    public TenantEditorActivity(Place place) {
        super((TenantEditorView) TenantViewFactory.instance(TenantEditorView.class), (AbstractCrudService<TenantDTO>) GWT.create(TenantCrudService.class),
                TenantDTO.class);
        delegate = new TenantActivityDelegate((TenantView) view);
        withPlace(place);
    }

    @Override
    public Presenter getScreeningPresenter() {
        return delegate.getScreeningPresenter();
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        if (isNewItem()) {
            ((TenantEditorView) view).showSelectTypePopUp(new AsyncCallback<Tenant.Type>() {
                @Override
                public void onSuccess(Tenant.Type result) {
                    tenantType = result;
                    TenantEditorActivity.super.start(panel, eventBus);
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }
            });
        } else {
            super.start(panel, eventBus);
        }
    }

    @Override
    protected void initNewItem(TenantDTO entity) {
        if (isNewItem()) {
            entity.type().setValue(tenantType);
        }
    }
}
