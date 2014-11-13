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
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.backoffice.activity;

import java.util.List;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.shared.IntegrityConstraintUserRuntimeException;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IListerView;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IListerView.IListerPresenter;
import com.pyx4j.site.client.memento.MementoManager;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class AbstractListerActivity<E extends IEntity> implements IListerPresenter<E>, Activity {

    private static final I18n i18n = I18n.get(AbstractListerActivity.class);

    private final IListerView<E> view;

    private final ListerDataSource<E> dataSource;

    private final AbstractListCrudService<E> service;

    private Key parentId;

    private Class<? extends IEntity> parentClass;

    private final AppPlace place;

    private List<Criterion> externalFilters;

    private boolean populateOnStart = true;

    public AbstractListerActivity(Class<E> entityClass, Place place, IListerView<E> view, AbstractListCrudService<E> service) {
        // development correctness checks:
        assert (entityClass != null);
        assert (view != null);
        assert (service != null);

        this.parentId = null;
        this.view = view;
        this.service = service;
        this.dataSource = new ListerDataSource<E>(entityClass, service);
        view.setPresenter(this);

        this.place = (AppPlace) place;

        EntityFiltersBuilder<E> filters = EntityFiltersBuilder.create(entityClass);
        parseExternalFilters((AppPlace) place, entityClass, filters);
        if (filters.getFilters().size() > 0) {
            externalFilters = filters.getFilters();
        }
    }

    public IListerView<E> getView() {
        return view;
    }

    public AbstractListCrudService<E> getService() {
        return service;
    }

    public Class<E> getEntityClass() {
        return getDataSource().getEntityClass();
    }

    @Override
    public ListerDataSource<E> getDataSource() {
        return dataSource;
    }

    @Override
    public Key getParent() {
        return parentId;
    }

    @Override
    public Class<? extends IEntity> getParentClass() {
        return parentClass;
    }

    @Override
    public void setParent(Key parentId) {
        setParent(parentId, null);
    }

    @Override
    public void setParent(Key parentID, Class<? extends IEntity> parentClass) {
        this.parentId = parentID; // save parent id for newItem creation...
        this.parentClass = parentClass; // save parent class for polymorphic queries...
        dataSource.setParentEntityId(parentID, parentClass);
    }

    @Override
    public void setPreDefinedFilters(List<Criterion> filters) {
        dataSource.setPreDefinedFilters(filters);
    }

    @Override
    public void addPreDefinedFilters(List<Criterion> filters) {
        dataSource.addPreDefinedFilters(filters);
    }

    @Override
    public void addPreDefinedFilter(Criterion filter) {
        dataSource.addPreDefinedFilter(filter);
    }

    @Override
    public void clearPreDefinedFilters() {
        dataSource.clearPreDefinedFilters();
    }

    @Override
    public void populate() {
        view.getDataTablePanel().populate();
    }

    @Override
    public void refresh() {
        view.getDataTablePanel().populate();
    }

    @Override
    public void view(Class<? extends CrudAppPlace> openPlaceClass, Key itemID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formViewerPlace(itemID));
    }

    @Override
    public void edit(Class<? extends CrudAppPlace> openPlaceClass, Key itemID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formEditorPlace(itemID));
    }

    @Override
    public void editNew(Class<? extends CrudAppPlace> openPlaceClass) {
        if (canCreateNewItem()) {
            if (getParentClass() != null) {
                AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formNewItemPlace(getParent(), getParentClass()));
            } else {
                AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formNewItemPlace(getParent()));
            }
        }
    }

    @Override
    public void editNew(Class<? extends CrudAppPlace> openPlaceClass, InitializationData initializationData) {
        if (canCreateNewItem()) {
            AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formNewItemPlace(initializationData));
        }
    }

    /**
     * Empty methods, implementations don't need to call it.
     */
    @Override
    public boolean canCreateNewItem() {
        if (EntityFactory.getEntityMeta(getEntityClass()).isAnnotationPresent(SecurityEnabled.class)) {
            return SecurityController.check(DataModelPermission.permissionCreate(getEntityClass()));
        } else {
            return true;
        }
    }

    @Override
    public void delete(final Key itemID) {
        service.delete(new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                onDeleted(itemID, true);
                populate();
            }

            @Override
            public void onFailure(Throwable caught) {
                onDeleted(itemID, false);
                if (caught instanceof IntegrityConstraintUserRuntimeException) {
                    MessageDialog.error(i18n.tr("Item Deletion"), caught.getMessage());
                } else {
                    throw new UnrecoverableClientError(caught);
                }
            }
        }, itemID);
    }

    protected void onDeleted(Key itemID, boolean isSuccessful) {
        view.onDeleted(itemID, isSuccessful);
    }

    protected void parseExternalFilters(AppPlace place, Class<E> entityClass, EntityFiltersBuilder<E> filters) {
        String val;
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            String ownerMemberName = EntityFactory.getEntityMeta(entityClass).getOwnerMemberName();
            IEntity owner = (IEntity) filters.proto().getMember(ownerMemberName);
            filters.eq(owner, EntityFactory.createIdentityStub(owner.getValueClass(), new Key(val)));
        }
    }

    @Override
    public AppPlace getPlace() {
        return place;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        getView().discard();
        getView().getDataTablePanel().setExternalFilters(externalFilters);
        getView().setPresenter(this);
        MementoManager.restoreState(getView(), place);
        if (populateOnStart) {
            populate();
        }
        containerWidget.setWidget(getView());
    }

    public void onDiscard() {
        MementoManager.saveState(getView(), place);
        getView().discard();
        getView().setPresenter(null);

    }

    @Override
    public void onCancel() {
        onDiscard();
    }

    @Override
    public void onStop() {
        onDiscard();
    }

    @Override
    public String mayStop() {
        return null;
    }

    public void setPopulateOnStart(boolean populateOnStart) {
        this.populateOnStart = populateOnStart;
    }

    public boolean isPopulateOnStart() {
        return populateOnStart;
    }
}
