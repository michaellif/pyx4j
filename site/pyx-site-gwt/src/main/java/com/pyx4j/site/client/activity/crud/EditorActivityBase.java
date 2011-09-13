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
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.IEditorView;
import com.pyx4j.site.client.ui.crud.IEditorView.EditMode;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.services.AbstractCrudService;

public class EditorActivityBase<E extends IEntity> extends AbstractActivity implements IEditorView.Presenter {

    protected final IEditorView<E> view;

    protected final AbstractCrudService<E> service;

    protected final Class<E> entityClass;

    protected Key entityID;

    protected Key parentID;

    protected int tabIndex;

    protected Class<? extends CrudAppPlace> placeClass;

    public EditorActivityBase(IEditorView<E> view, AbstractCrudService<E> service, Class<E> entityClass) {
        // development correctness checks:
        assert (view != null);
        assert (service != null);
        assert (entityClass != null);

        this.view = view;
        this.service = service;
        this.entityClass = entityClass;
        view.setPresenter(this);
    }

    @Override
    public void setPlace(Place place) {
        entityID = null;
        parentID = null;
        tabIndex = -1;

        view.getMemento().setCurrentPlace(place);

        assert (place instanceof CrudAppPlace);
        placeClass = ((CrudAppPlace) place).getClass();

        String val;
        if ((val = ((CrudAppPlace) place).getFirstArg(CrudAppPlace.ARG_NAME_ITEM_ID)) != null) {
            entityID = new Key(val);
        }
        if ((val = ((CrudAppPlace) place).getFirstArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            parentID = new Key(val);
        }
        if ((val = ((CrudAppPlace) place).getFirstArg(CrudAppPlace.ARG_NAME_TAB_IDX)) != null) {
            tabIndex = Integer.parseInt(val);
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setEditMode(isNewItem() ? EditMode.newItem : EditMode.existingItem);
        panel.setWidget(view);
        populate();
    }

    @Override
    public void populate() {

        if (isNewItem()) {
            createNewItem(new AsyncCallback<E>() {
                @Override
                public void onSuccess(E entity) {
                    if (parentID != null) {
                        String ownerName = entity.getEntityMeta().getOwnerMemberName();
                        if (ownerName != null) {
                            ((IEntity) entity.getMember(ownerName)).setPrimaryKey(parentID);
                        }
                    }

                    initNewItem(entity); // let descendant to initialise item... 
                    onPopulateSuccess(entity);
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }
            });

        } else {
            service.retrieve(new AsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    onPopulateSuccess(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }
            }, entityID);
        }
    }

    protected void createNewItem(AsyncCallback<E> callback) {
        callback.onSuccess(EntityFactory.create(entityClass));
    }

    public void onPopulateSuccess(E result) {
        view.populate(result);
        view.setActiveTab(tabIndex);
    }

    @Override
    public void apply() {
        trySave(true);
    }

    @Override
    public void save() {
        trySave(false);
    }

    @Override
    public void cancel() {
        if (isNewItem()) {
            History.back();
        } else {
            goToViewer(entityID);
        }
    }

    public void trySave(final boolean apply) {

        if (isNewItem()) {
            service.create(new AsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    if (apply) {
                        onApplySuccess(result);
                    } else {
                        onSaveSuccess(result);
                    }
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
                    if (apply) {
                        onApplySuccess(result);
                    } else {
                        onSaveSuccess(result);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    onSaveFail(caught);
                }
            }, view.getValue());
        }
    }

    protected void onApplySuccess(E result) {
        view.onApplySuccess();

        if (isNewItem()) { // switch new item to regular editing after successful apply!..
            entityID = result.getPrimaryKey();
            view.setEditMode(isNewItem() ? EditMode.newItem : EditMode.existingItem);
            populate();
        }
    }

    protected void onSaveSuccess(E result) {
        view.onSaveSuccess();
        goToViewer(result.getPrimaryKey());
    }

    protected void onSaveFail(Throwable caught) {
        if (!view.onSaveFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    protected boolean isNewItem() {
        return (entityID.toString().equals(CrudAppPlace.ARG_VALUE_NEW_ITEM));
    }

    protected void initNewItem(E entity) {
    }

    protected void goToViewer(Key entityID) {
        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(placeClass);
        place.formViewerPlace(entityID, view.getActiveTab());
        AppSite.getPlaceController().goTo(place);
    }
}
