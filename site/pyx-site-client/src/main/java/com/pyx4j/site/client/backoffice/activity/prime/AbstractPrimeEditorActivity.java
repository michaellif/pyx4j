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
 */
package com.pyx4j.site.client.backoffice.activity.prime;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveOperation;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView.EditMode;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView.IPrimeEditorPresenter;
import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class AbstractPrimeEditorActivity<E extends IEntity> extends AbstractPrimeActivity<IPrimeEditorView<?>> implements IPrimeEditorPresenter {

    private static final I18n i18n = I18n.get(AbstractPrimeEditorActivity.class);

    private final AbstractCrudService<E> service;

    private final Class<E> entityClass;

    private Key entityId;

    private Key parentId;

    private int tabIndex;

    private boolean mayStop;

    private boolean discarded = false;

    public AbstractPrimeEditorActivity(Class<E> entityClass, CrudAppPlace place, IPrimeEditorView<E> view, AbstractCrudService<E> service) {
        super(view, place);
        // development correctness checks:
        assert (view != null);
        assert (service != null);
        assert (entityClass != null);

        this.service = service;
        this.entityClass = entityClass;

        entityId = null;
        parentId = null;
        tabIndex = -1;

        String val;
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
            entityId = new Key(val);
        }
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            parentId = new Key(val);
        }
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_TAB_IDX)) != null) {
            tabIndex = Integer.parseInt(val);
        }
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

    public final Key getParentId() {
        return parentId;
    }

    @Override
    public CrudAppPlace getPlace() {
        return (CrudAppPlace) super.getPlace();
    }

    @SuppressWarnings("unchecked")
    @Override
    public IPrimeEditorView<E> getView() {
        return (IPrimeEditorView<E>) super.getView();
    }

    protected E getValue() {
        return getView().getValue();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        mayStop = false;
        // should be called first in start - some views can set appropriate form according to the current mode
        getView().setEditMode(isNewEntity() ? EditMode.newItem : EditMode.existingItem);
        getView().setPresenter(this);
        onStart();
        populate();
        panel.setWidget(getView());
    }

    protected void onDiscard() {
        discarded = true;
        getView().reset();
        getView().setPresenter(null);
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
                            if (!discarded) {
                                setEntityParent(result, false);
                                onPopulateSuccess(result);
                                onPopulate();
                            }
                        }
                    }, result);
                }
            });
        } else {
            service.retrieve(new DefaultAsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    if (!discarded) {
                        onPopulateSuccess(result);
                        onPopulate();
                    }
                }
            }, entityId, AbstractCrudService.RetrieveOperation.Edit);
        }
    }

    @Override
    public void refresh() {
        if (!isNewEntity()) {
            service.retrieve(new DefaultAsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    if (!discarded) {
                        onPopulateSuccess(result);
                        onPopulate();
                    }
                }
            }, entityId, RetrieveOperation.Edit);
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
        if (getPlace().getInitializationData() != null) {
            callback.onSuccess(getPlace().getInitializationData());
        } else {
            callback.onSuccess(null);
        }
    }

    protected boolean isNewEntity() {
        return (entityId == null);
    }

    /**
     * Called after data is shown/propagated to UI components
     */
    protected void onPopulate() {
    }

    /**
     * TODO refactoring will be done at EOD 1.4.5
     *
     * @deprecated use onPopulate
     */
    @Deprecated
    public void onPopulateSuccess(E result) {
        populateView(result);
    }

    protected void populateView(E result) {
        int activeTab = tabIndex;
        if (activeTab < 0) {
            activeTab = getView().getActiveTab();
        }
        getView().populate(result);
        getView().setActiveTab(activeTab);
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
            History.fireCurrentHistoryState();
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
            service.create(callback, getView().getValue());
        } else {
            service.save(callback, getView().getValue());
        }
    }

    protected void onSaved(Key result) {
    }

    protected void onApplySuccess(Key result) {
        mayStop = true;
        // switch new item to regular editing after successful apply!..
        goToEditor(result);
    }

    protected void onSaveSuccess(Key result) {
        mayStop = true;
        goToViewer(result);
    }

    protected void onSaveFail(Throwable caught) {
        if (!getView().onSaveFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    protected void goToViewer(Key entityID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getPlace().getClass()).formViewerPlace(entityID, getView().getActiveTab()));
    }

    protected void goToEditor(Key entityID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getPlace().getClass()).formEditorPlace(entityID, getView().getActiveTab()));
    }

    @Override
    public String mayStop() {
        if (!mayStop && getView().isDirty()) {
            String entityName = getView().getValue().getStringView();
            if (CommonsStringUtils.isEmpty(entityName)) {
                return i18n.tr("Changes to {0} were not saved", getView().getValue().getEntityMeta().getCaption());
            } else {
                return i18n.tr("Changes to {0} ''{1}'' were not saved", getView().getValue().getEntityMeta().getCaption(), entityName);
            }
        } else {
            return null;
        }
    }
}
