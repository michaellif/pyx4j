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
package com.pyx4j.site.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.IEditor;
import com.pyx4j.site.client.ui.prime.form.IEditor.EditMode;
import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class AbstractEditorActivity<E extends IEntity> extends AbstractActivity implements IEditor.Presenter {

    private static final I18n i18n = I18n.get(AbstractEditorActivity.class);

    private final IEditor<E> view;

    private final AbstractCrudService<E> service;

    private final CrudAppPlace place;

    private final Class<E> entityClass;

    private Key entityId;

    private String parentClassName;

    private Key parentId;

    private int tabIndex;

    public AbstractEditorActivity(CrudAppPlace place, IEditor<E> view, AbstractCrudService<E> service, Class<E> entityClass) {
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

    public IEditor<E> getView() {
        return view;
    }

    public AbstractCrudService<E> getService() {
        return service;
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

    public final Key getParentId() {
        return parentId;
    }

    @Override
    public CrudAppPlace getPlace() {
        return place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        // should be called first in start - some views can set appropriate form according to the current mode
        view.setEditMode(isNewEntity() ? EditMode.newItem : EditMode.existingItem);
        view.setPresenter(this);
        populate();
        panel.setWidget(view);
    }

    protected void onDiscard() {
        view.reset();
        view.setPresenter(null);
    }

    @Override
    public void onCancel() {
        onDiscard();
        super.onCancel();
    }

    @Override
    public void onStop() {
        onDiscard();
        super.onStop();
    }

    @Override
    public void populate() {
        if (isNewEntity()) {
            obtainInitializationData(new DefaultAsyncCallback<AbstractCrudService.InitializationData>() {
                @Override
                public void onSuccess(InitializationData result) {
                    service.init(new DefaultAsyncCallback<E>() {
                        @Override
                        public void onSuccess(E result) {
                            setEntityParent(result, false);
                            onPopulateSuccess(result);
                        }
                    }, result);
                }
            });
        } else {
            service.retrieve(new DefaultAsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    onPopulateSuccess(result);
                }
            }, entityId, AbstractCrudService.RetrieveTarget.Edit);
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
            }, entityId, RetrieveTarget.Edit);
        }
    }

    private void setEntityParent(E entity, boolean force) {
        if (getParentId() != null) {
            String ownerName = entity.getEntityMeta().getOwnerMemberName();
            if (ownerName != null) {
                IEntity parent = ((IEntity) entity.getMember(ownerName));
                if (force || parent.getPrimaryKey() == null) {
                    if (parent.isNull()) {
                        parent.setAttachLevel(AttachLevel.IdOnly);
                    }
                    parent.setPrimaryKey(getParentId());
                }
            }
        }
    }

    /**
     * Descendants may override this method to supply some initialization info.
     * 
     */
    protected void obtainInitializationData(AsyncCallback<InitializationData> callback) {
        if (place.getInitializationData() != null) {
            callback.onSuccess(place.getInitializationData());
        } else {
            callback.onSuccess(null);
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

    private void trySave(final boolean apply) {
        AsyncCallback<Key> callback = new AsyncCallback<Key>() {
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
        };
        if (isNewEntity()) {
            service.create(callback, view.getValue());
        } else {
            service.save(callback, view.getValue());
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
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(place.getClass()).formViewerPlace(entityID, view.getActiveTab()));
    }

    protected void goToEditor(Key entityID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(place.getClass()).formEditorPlace(entityID, view.getActiveTab()));
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
