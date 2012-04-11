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

    private final Class<E> entityClass;

    protected Key entityID;

    protected Key parentID;

    protected String parentClass;

    protected int tabIndex;

    protected Class<? extends CrudAppPlace> placeClass;

    private final CrudAppPlace place;

    public EditorActivityBase(CrudAppPlace place, IEditorView<E> view, AbstractCrudService<E> service, Class<E> entityClass) {

        // development correctness checks:
        assert (place instanceof CrudAppPlace);
        assert (view != null);
        assert (service != null);
        assert (entityClass != null);

        this.place = place;
        this.view = view;
        this.service = service;
        this.entityClass = entityClass;

        entityID = null;
        parentID = null;
        parentClass = null;
        tabIndex = -1;

        view.getMemento().setCurrentPlace(place);

        placeClass = place.getClass();

        String val;
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
            entityID = new Key(val);
            // Validate argument
            try {
                entityID.asLong();
            } catch (NumberFormatException e) {
                entityID = null;
            }

        }
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            parentID = new Key(val);
            // Validate argument
            try {
                parentID.asLong();
            } catch (NumberFormatException e) {
                parentID = null;
            }
        }
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_PARENT_CLASS)) != null) {
            // TODO: currently we can't restore java class by it's name in GWT - so use just name instead - find the solution...
            parentClass = val;
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

    public Class<E> getEntityClass() {
        return entityClass;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        // should be called first in start - some views can set appropriate form according to the current mode
        view.setEditMode(isNewEntity() ? EditMode.newItem : EditMode.existingItem);
        view.reset();
        view.setPresenter(this);
        populate();
        containerWidget.setWidget(view);
    }

    @Override
    public void onStop() {
        view.reset();
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
            }, entityID, AbstractCrudService.RetrieveTraget.Edit);
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
            }, entityID, RetrieveTraget.View);
        }
    }

    private void setEntityParent(E entity, boolean force) {
        if (parentID != null) {
            String ownerName = entity.getEntityMeta().getOwnerMemberName();
            if (ownerName != null) {
                if (force || ((IEntity) entity.getMember(ownerName)).getPrimaryKey() == null) {
                    ((IEntity) entity.getMember(ownerName)).setPrimaryKey(parentID);
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
            AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
        } else {
            goToViewer(entityID);
        }
    }

    public void trySave(final boolean apply) {

        if (isNewEntity()) {
            service.create(new AsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    ReferenceDataManager.created(result);
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
                    ReferenceDataManager.update(result);
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

    protected void onApplySuccess(E result) {
        if (isNewEntity()) { // switch new item to regular editing after successful apply!..
            view.reset();
            goToEditor(result.getPrimaryKey());
        } else {
            onPopulateSuccess(result);
        }
    }

    protected void onSaveSuccess(E result) {
        view.reset();
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
