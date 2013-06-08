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
 * @version $Id$
 */
package com.pyx4j.site.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.IViewer;
import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class AbstractViewerActivity<E extends IEntity> extends AbstractActivity implements IViewer.Presenter {

    private final IViewer<E> view;

    private final AbstractCrudService<E> service;

    private final CrudAppPlace place;

    private Key entityId;

    private int tabIndex;

    public AbstractViewerActivity(CrudAppPlace place, IViewer<E> view, AbstractCrudService<E> service) {
        // development correctness checks:
        assert (view != null);
        assert (service != null);

        this.place = place;
        this.view = view;
        this.service = service;

        entityId = null;
        tabIndex = -1;

        view.getMemento().setCurrentPlace(place);

        String val;
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
            entityId = new Key(val);
        }
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_TAB_IDX)) != null) {
            tabIndex = Integer.parseInt(val);
        }

        assert (entityId != null);
    }

    public IViewer<E> getView() {
        return view;
    }

    public AbstractCrudService<E> getService() {
        return service;
    }

    public Key getEntityId() {
        return entityId;
    }

    public Key setEntityIdAsCurrentKey() {
        return entityId = entityId.asCurrentKey();
    }

    @Override
    public CrudAppPlace getPlace() {
        return place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        populate();
        panel.setWidget(view);
    }

    @Override
    public void onStop() {
        view.storeState(view.getMemento().getCurrentPlace());
        view.reset();
        view.setPresenter(null);
        view.hideVisor();
        super.onStop();
    }

    @Override
    public void populate() {
        service.retrieve(new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                onPopulateSuccess(result);
            }
        }, entityId, AbstractCrudService.RetrieveTarget.View);
    }

    @Override
    public void refresh() {
        service.retrieve(new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                onPopulateSuccess(result);
            }
        }, entityId, AbstractCrudService.RetrieveTarget.View);
    }

    protected void onPopulateSuccess(E result) {
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
    public boolean canEdit() {
        return true;
    }

    @Override
    public void edit() {
        if (canEdit()) {
            goToEditor(entityId);
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
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getPlace().getClass()).formViewerPlace(entityID, view.getActiveTab()));
    }

    protected void goToEditor(Key entityID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getPlace().getClass()).formEditorPlace(entityID, view.getActiveTab()));
    }
}
