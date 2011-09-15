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
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.tenant.TenantEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.TenantCrudService;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorActivity extends EditorActivityBase<TenantDTO> implements TenantEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public TenantEditorActivity(Place place) {
        super((TenantEditorView) TenantViewFactory.instance(TenantEditorView.class), (AbstractCrudService<TenantDTO>) GWT.create(TenantCrudService.class),
                TenantDTO.class);
        setPlace(place);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<TenantDTO> callback) {
        ((TenantEditorView) view).showSelectTypePopUp(new AsyncCallback<Tenant.Type>() {
            @Override
            public void onSuccess(Tenant.Type type) {
                TenantDTO entity = EntityFactory.create(entityClass);
                entity.type().setValue(type);

                callback.onSuccess(entity);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }
}
