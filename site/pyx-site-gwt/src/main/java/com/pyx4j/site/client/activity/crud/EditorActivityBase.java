/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.activity.crud;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.ui.crud.IEditorView;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.services.AbstractCrudService;

public class EditorActivityBase<E extends IEntity> extends AbstractActivity implements IEditorView.Presenter {

    private final IEditorView<E> view;

    private final AbstractCrudService<E> service;

    private final Class<E> entityClass;

    private Key entityID = null;

    private Key parentID = null;

    public EditorActivityBase(IEditorView<E> view, AbstractCrudService<E> service, Class<E> entityClass) {
        this.view = view;
        this.service = service;
        this.entityClass = entityClass;
        view.setPresenter(this);
    }

    public EditorActivityBase<E> withPlace(Place place) {
        entityID = null;
        parentID = null;

        String id;
        if ((id = ((CrudAppPlace) place).getArg(CrudAppPlace.ARG_NAME_ITEM_ID)) != null) {
            entityID = new Key(id);
        }
        if ((id = ((CrudAppPlace) place).getArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            parentID = new Key(id);
        }

        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        populate();
    }

    @Override
    public void populate() {
        assert (entityID != null);

        if (entityID.toString().equals(CrudAppPlace.ARG_VALUE_NEW_ITEM)) {
            createNewEntity(new AsyncCallback<E>() {

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }

                @Override
                public void onSuccess(E entity) {
                    if (parentID != null) {
                        String ownerName = entity.getEntityMeta().getOwnerMemberName();
                        if (ownerName != null) {
                            ((IEntity) entity.getMember(ownerName)).setPrimaryKey(parentID);
                        }
                    }
                    view.populate(entity);
                }

            });

        } else {
            service.retrieve(new AsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    view.populate(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }
            }, entityID);
        }
    }

    protected void createNewEntity(AsyncCallback<E> callback) {
        callback.onSuccess(EntityFactory.create(entityClass));
    }

    @Override
    public void save() {
        assert (entityID != null);

        if (entityID.toString().equals(CrudAppPlace.ARG_VALUE_NEW_ITEM)) {
            service.create(new AsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    onSaveSuccess(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    onSaveFail(caught);
                }
            }, view.getValue());
        } else {
            service.save(new AsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    onSaveSuccess(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    onSaveFail(caught);
                }
            }, view.getValue());
        }
    }

    @Override
    public void cancel() {
        History.back();
    }

    protected void onSaveSuccess(E result) {
        History.back();
    }

    protected void onSaveFail(Throwable caught) {
        throw new UnrecoverableClientError(caught);
    }
}
