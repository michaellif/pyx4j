/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.editors;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.editors.IEditorView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.AbstractCrudService;

public class EditorActivityBase<E extends IEntity> extends AbstractActivity implements IEditorView.Presenter {

    private final IEditorView<E> view;

    private final AbstractCrudService<E> service;

    private final Class<E> entityClass;

    private Key entityId;

    @Inject
    public EditorActivityBase(IEditorView<E> view, AbstractCrudService<E> service, Class<E> entityClass) {
        this.view = view;
        this.service = service;
        this.entityClass = entityClass;
        view.setPresenter(this);
    }

    public EditorActivityBase<E> withPlace(Place place) {
        entityId = new Key(((AppPlace) place).getArgs().get(CrmSiteMap.ARG_NAME_ITEM_ID));
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        populate();
    }

    protected AbstractCrudService<E> getService() {
        return service;
    }

    @Override
    public void populate() {
        if (entityId.equals(CrmSiteMap.ARG_VALUE_NEW_ITEM)) {
            view.populate(EntityFactory.create(entityClass));
        } else {
            service.retrieve(new AsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    view.populate(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                }
            }, entityId);
        }
    }

    @Override
    public void save() {
        service.save(new AsyncCallback<E>() {

            @Override
            public void onSuccess(E result) {
                History.back();
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        }, view.getValue());
    }

    @Override
    public void cancel() {
        History.back();
    }
}
