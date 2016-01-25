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
 * Created on 2011-05-17
 * @author Vlad
 */
package com.pyx4j.site.client.backoffice.activity.prime;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractCrudService.DuplicateData;
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView.IPrimeViewerPresenter;
import com.pyx4j.site.client.memento.MementoManager;
import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class AbstractPrimeViewerActivity<E extends IEntity> extends AbstractPrimeActivity<IPrimeViewerView<?>> implements IPrimeViewerPresenter {

    protected final Class<E> entityClass;

    private final AbstractCrudService<E> service;

    private Key entityId;

    private int tabIndex;

    private E populatedValue;

    private boolean discarded = false;

    public AbstractPrimeViewerActivity(Class<E> entityClass, CrudAppPlace place, IPrimeViewerView<E> view, AbstractCrudService<E> service) {
        super(view, place);
        // development correctness checks:
        assert (view != null);
        assert (service != null);

        this.entityClass = entityClass;
        this.service = service;

        entityId = null;
        tabIndex = -1;

        String val;
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
            entityId = new Key(val);
        }
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_TAB_IDX)) != null) {
            tabIndex = Integer.parseInt(val);
        }

        assert (entityId != null);
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

    public Key setEntityIdAsCurrentKey() {
        return entityId = entityId.asCurrentKey();
    }

    @Override
    public CrudAppPlace getPlace() {
        return (CrudAppPlace) super.getPlace();
    }

    @SuppressWarnings("unchecked")
    @Override
    public IPrimeViewerView<E> getView() {
        return (IPrimeViewerView<E>) super.getView();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        getView().setPresenter(this);
        populate();
        panel.setWidget(getView());
        MementoManager.restoreState(getView(), getPlace());
    }

    protected void onDiscard() {
        MementoManager.saveState(getView(), getPlace());
        this.populatedValue = null;
        this.discarded = true;
        getView().reset();
        getView().setPresenter(null);
        getView().hideVisor();
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
        service.retrieve(new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                if (!discarded) {
                    onPopulateSuccess(result);
                }
            }
        }, entityId, AbstractCrudService.RetrieveOperation.View);
    }

    @Override
    public void refresh() {
        service.retrieve(new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                if (!discarded) {
                    onPopulateSuccess(result);
                }
            }
        }, entityId, AbstractCrudService.RetrieveOperation.View);
    }

    protected void onPopulateSuccess(E result) {
        populateView(result);
    }

    protected void populateView(E result) {
        int activeTab = tabIndex;
        if (activeTab < 0) {
            activeTab = getView().getActiveTab();
        }
        populatedValue = result;
        getView().populate(result);
        getView().setActiveTab(activeTab);
    }

    protected E getValue() {
        return populatedValue;
    }

    @Override
    public boolean canEdit() {
        if (EntityFactory.getEntityMeta(getEntityClass()).isAnnotationPresent(SecurityEnabled.class)) {
            return SecurityController.check(getValue(), DataModelPermission.permissionUpdate(getEntityClass()));
        } else {
            return true;
        }
    }

    @Override
    public void edit() {
        if (canEdit()) {
            goToEditor(entityId);
        }
    }

    @Override
    public void duplicate() {
        if (canEdit()) {
            goToDuplicate(entityId);
        }
    }

    @Override
    public void cancel() {
        History.back();
    }

    @Override
    public void view(Key entityId) {
        this.entityId = entityId;
        populate();
    }

    @Override
    public void approveFinal() {
        if (service instanceof AbstractVersionedCrudService) {
            ((AbstractVersionedCrudService<?>) service).approveFinal(new DefaultAsyncCallback<VoidSerializable>() {
                @Override
                public void onSuccess(VoidSerializable result) {
                    goToViewer(entityId.asCurrentKey());
                }
            }, entityId);
        }
    }

    protected void goToViewer(Key entityID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getPlace().getClass()).formViewerPlace(entityID, getView().getActiveTab()));
    }

    protected void goToEditor(Key entityID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getPlace().getClass()).formEditorPlace(entityID, getView().getActiveTab()));
    }

    protected void goToDuplicate(Key entityID) {
        DuplicateData duplicateData = EntityFactory.create(DuplicateData.class);
//        duplicateData.originalEntityId().set(EntityFactory.createIdentityStub(entityClass, entityID.asCurrentKey()));
        duplicateData.originalEntityKey().setValue(entityID.asCurrentKey());
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getPlace().getClass()).formNewItemPlace(duplicateData));
    }
}
