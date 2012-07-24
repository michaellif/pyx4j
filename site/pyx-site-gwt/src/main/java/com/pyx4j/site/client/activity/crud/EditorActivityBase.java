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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ReferenceDataManager;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTraget;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.client.ui.crud.form.IEditorView.EditMode;
import com.pyx4j.site.rpc.CrudAppPlace;

public class EditorActivityBase<E extends IEntity> extends AbstractActivity implements IEditorView.Presenter {

    private static final I18n i18n = I18n.get(EditorActivityBase.class);

    private final IEditorView<E> view;

    private final AbstractCrudService<E> service;

    private final Class<? extends CrudAppPlace> placeClass;

    private final CrudAppPlace place;

    private final Class<E> entityClass;

    private Key entityId;

    private String parentClassName;

    private Key parentId;

    private int tabIndex;

    public EditorActivityBase(CrudAppPlace place, IEditorView<E> view, AbstractCrudService<E> service, Class<E> entityClass) {
        // development correctness checks:
        assert (view != null);
        assert (service != null);
        assert (entityClass != null);

        this.place = place;
        this.view = view;
        this.service = service;
        this.entityClass = entityClass;

        entityId = null;
        parentId = null;
        parentClassName = null;
        tabIndex = -1;

        view.getMemento().setCurrentPlace(place);

        placeClass = place.getClass();

        String val;
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
            entityId = new Key(val);
            // Validate argument
            try {
                entityId.asLong();
            } catch (NumberFormatException e) {
                entityId = null;
            }

        }
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            parentId = new Key(val);
            // Validate argument
            try {
                parentId.asLong();
            } catch (NumberFormatException e) {
                parentId = null;
            }
        }
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_PARENT_CLASS)) != null) {
            // TODO: currently we can't restore java class by it's name in GWT - so use just name instead - find the solution...
            parentClassName = val;
        }
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_TAB_IDX)) != null) {
            tabIndex = Integer.parseInt(val);
        }
    }

    public IEditorView<E> getView() {
        return view;
    }

    public AbstractCrudService<E> getService() {
        return service;
    }

    public Class<? extends CrudAppPlace> getPlaceClass() {
        return placeClass;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public Key getEntityId() {
        return entityId;
    }

    public String getParentClassName() {
        return parentClassName;
    }

    public Key getParentId() {
        return parentId;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        // should be called first in start - some views can set appropriate form according to the current mode
        view.setEditMode(isNewEntity() ? EditMode.newItem : EditMode.existingItem);
        view.setPresenter(this);
        populate();
        panel.setWidget(view);
    }

    @Override
    public void onStop() {
        view.reset();
        view.setPresenter(null);
        super.onStop();
    }

    @Override
    public void populate() {
        if (isNewEntity()) {
            createNewEntity(new DefaultAsyncCallback<E>() {
                @Override
                public void onSuccess(E entity) {
                    setEntityParent(entity, false);
                    onPopulateSuccess(entity);
                }
            });
        } else {
            service.retrieve(new DefaultAsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    onPopulateSuccess(result);
                }
            }, entityId, AbstractCrudService.RetrieveTraget.Edit);
        }
    }

    @Override
    public void refresh() {
        if (!isNewEntity()) {
            service.retrieve(new DefaultAsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    onPopulateSuccess(result);
                }
            }, entityId, RetrieveTraget.Edit);
        }
    }

    private void setEntityParent(E entity, boolean force) {
        if (parentId != null) {
            String ownerName = entity.getEntityMeta().getOwnerMemberName();
            if (ownerName != null) {
                if (force || ((IEntity) entity.getMember(ownerName)).getPrimaryKey() == null) {
                    ((IEntity) entity.getMember(ownerName)).setPrimaryKey(parentId);
                }
            }
        }
    }

    /**
     * Descendants may override this method to perform some initialization.
     * 
     * @param callback
     */
    protected void createNewEntity(AsyncCallback<E> callback) {
        if (place.getNewItem() != null) {
            callback.onSuccess((E) place.getNewItem());
        } else {
            callback.onSuccess(EntityFactory.create(entityClass));
        }
    }

    protected boolean isNewEntity() {
        return (entityId == null);
    }

    public void onPopulateSuccess(E result) {
        populateView(result);
    }

    protected void populateView(E result) {
        int activeTab = tabIndex;
        if (activeTab < 0) {
            activeTab = view.getActiveTab();
        }
        view.reset();
        view.populate(result);
        view.setActiveTab(activeTab);
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
            AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
        } else {
            goToViewer(entityId);
        }
    }

    public void trySave(final boolean apply) {

        if (isNewEntity()) {
            service.create(new AsyncCallback<Key>() {
                @Override
                public void onSuccess(Key result) {
                    ReferenceDataManager.invalidate(entityClass);
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
            service.save(new AsyncCallback<Key>() {
                @Override
                public void onSuccess(Key result) {
                    ReferenceDataManager.invalidate(entityClass);
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

    protected void onSaved(Key result) {
    }

    protected void onApplySuccess(Key result) {
        view.reset();
        // switch new item to regular editing after successful apply!..
        goToEditor(result);
    }

    protected void onSaveSuccess(Key result) {
        view.reset();
        goToViewer(result);
    }

    protected void onSaveFail(Throwable caught) {
        if (!view.onSaveFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    protected void goToViewer(Key entityID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(placeClass).formViewerPlace(entityID, view.getActiveTab()));
    }

    protected void goToEditor(Key entityID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(placeClass).formEditorPlace(entityID, view.getActiveTab()));
    }

    @Override
    public String mayStop() {
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
    }
}
