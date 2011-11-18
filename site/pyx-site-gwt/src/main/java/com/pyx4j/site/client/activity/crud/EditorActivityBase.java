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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.client.ui.crud.form.IEditorView.EditMode;
import com.pyx4j.site.rpc.CrudAppPlace;

public class EditorActivityBase<E extends IEntity> extends AbstractActivity implements IEditorView.Presenter {

    private static I18n i18n = I18n.get(EditorActivityBase.class);

    protected final IEditorView<E> view;

    protected final AbstractCrudService<E> service;

    protected final Class<E> entityClass;

    protected Key entityID;

    protected Key parentID;

    protected int tabIndex;

    protected Class<? extends CrudAppPlace> placeClass;

    public EditorActivityBase(Place place, IEditorView<E> view, AbstractCrudService<E> service, Class<E> entityClass) {
        // development correctness checks:
        assert (view != null);
        assert (service != null);
        assert (entityClass != null);

        this.view = view;
        this.service = service;
        this.entityClass = entityClass;
        view.setPresenter(this);
        setPlace(place);
    }

    private void setPlace(Place place) {
        entityID = null;
        parentID = null;
        tabIndex = -1;

        view.getMemento().setCurrentPlace(place);

        assert (place instanceof CrudAppPlace);
        placeClass = ((CrudAppPlace) place).getClass();

        String val;
        if ((val = ((CrudAppPlace) place).getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
            entityID = new Key(val);
            // Validate argument
            try {
                entityID.asLong();
            } catch (NumberFormatException e) {
                entityID = null;
            }

        }
        if ((val = ((CrudAppPlace) place).getFirstArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            parentID = new Key(val);
            // Validate argument
            try {
                parentID.asLong();
            } catch (NumberFormatException e) {
                parentID = null;
            }
        }
        if ((val = ((CrudAppPlace) place).getFirstArg(CrudAppPlace.ARG_NAME_TAB_IDX)) != null) {
            tabIndex = Integer.parseInt(val);
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setEditMode(isNewEntity() ? EditMode.newItem : EditMode.existingItem);
        panel.setWidget(view);
        populate();
    }

    @Override
    public void populate() {

        if (isNewEntity()) {
            createNewEntity(new AsyncCallback<E>() {
                @Override
                public void onSuccess(E entity) {
                    if (parentID != null) {
                        String ownerName = entity.getEntityMeta().getOwnerMemberName();
                        if (ownerName != null) {
                            ((IEntity) entity.getMember(ownerName)).setPrimaryKey(parentID);
                        }
                    }
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

    @Override
    public void refresh() {
        if (!isNewEntity()) {
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

    /**
     * Descendants may override this method to perform some initialization.
     * 
     * @param callback
     */
    protected void createNewEntity(AsyncCallback<E> callback) {
        callback.onSuccess(EntityFactory.create(entityClass));
    }

    protected boolean isNewEntity() {
        return (entityID == null);
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
        if (isNewEntity()) {
            History.back();
        } else {
            goToViewer(entityID);
        }
    }

    public void trySave(final boolean apply) {

        if (isNewEntity()) {
            service.create(new AsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    onSaved(result);
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
                    onSaved(result);
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

    protected void onSaved(E result) {

    }

    // TODO Misha please review this implementation with me!
    @Deprecated
    protected boolean __sugestedChangesForIsDirty__() {
        return false;
    }

    protected void onApplySuccess(E result) {
        view.onApplySuccess();
        if (__sugestedChangesForIsDirty__()) {
            // TODO find a better way to clear dirty flag in form
            view.populate(result);
        }
        if (isNewEntity()) { // switch new item to regular editing after successful apply!..
            entityID = result.getPrimaryKey();
            view.setEditMode(isNewEntity() ? EditMode.newItem : EditMode.existingItem);
            populate();
        }
    }

    protected void onSaveSuccess(E result) {
        view.onSaveSuccess();
        if (__sugestedChangesForIsDirty__()) {
            // TODO find a better way to clear dirty flag in form
            view.populate(result);
        }
        goToViewer(result.getPrimaryKey());
    }

    protected void onSaveFail(Throwable caught) {
        if (!view.onSaveFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    protected void goToViewer(Key entityID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(placeClass).formViewerPlace(entityID, view.getActiveTab()));
    }

    @Override
    public String mayStop() {
        if (__sugestedChangesForIsDirty__()) {
            if (view.isDirty()) {
                String entityName = view.getValue().getStringView();
                if (CommonsStringUtils.isEmpty(entityName)) {
                    return i18n.tr("Changes to {0} were not saved", view.getValue().getEntityMeta().getCaption());
                } else {
                    return i18n.tr("Changes to {0} ''{1}'' were not saved", view.getValue().getEntityMeta().getCaption(), entityName);
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
