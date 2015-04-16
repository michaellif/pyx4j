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
 */
package com.pyx4j.site.client.backoffice.activity.prime;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IPrimeListerView;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IPrimeListerView.IPrimeListerPresenter;
import com.pyx4j.site.client.memento.MementoManager;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class AbstractPrimeListerActivity<E extends IEntity> extends AbstractPrimeActivity<IPrimeListerView<?>> implements IPrimeListerPresenter<E> {

    private final Class<E> entityClass;

    private List<Criterion> externalFilters;

    private boolean populateOnStart = true;

    public AbstractPrimeListerActivity(Class<E> entityClass, AppPlace place, IPrimeListerView<E> view) {
        super(view, place);
        // development correctness checks:
        assert (entityClass != null);

        this.entityClass = entityClass;

        view.setPresenter(this);

        EntityFiltersBuilder<E> filters = EntityFiltersBuilder.create(entityClass);
        parseExternalFilters(place, entityClass, filters);
        if (filters.getFilters().size() > 0) {
            externalFilters = filters.getFilters();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public IPrimeListerView<E> getView() {
        return (IPrimeListerView<E>) super.getView();
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    @Override
    public void setParentKey(Key parentId) {
        setParentKey(parentId, null);
    }

    @Override
    public void setParentKey(Key parentID, Class<? extends IEntity> parentClass) {
        getView().getDataTablePanel().getDataSource().setParentEntityId(parentID, parentClass);
    }

    @Override
    public void setPreDefinedFilters(List<Criterion> filters) {
        getView().getDataTablePanel().getDataSource().setPreDefinedFilters(filters);
    }

    @Override
    public void addPreDefinedFilters(List<Criterion> filters) {
        getView().getDataTablePanel().getDataSource().addPreDefinedFilters(filters);
    }

    @Override
    public void addPreDefinedFilter(Criterion filter) {
        getView().getDataTablePanel().getDataSource().addPreDefinedFilter(filter);
    }

    @Override
    public void clearPreDefinedFilters() {
        getView().getDataTablePanel().getDataSource().clearPreDefinedFilters();
    }

    @Override
    public void populate() {
        getView().getDataTablePanel().populate();
    }

    @Override
    public void refresh() {
        getView().getDataTablePanel().populate();
    }

    protected void parseExternalFilters(AppPlace place, Class<E> entityClass, EntityFiltersBuilder<E> filters) {
        String val;
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            getView().getDataTablePanel().getDataSource().setParentEntityId(new Key(val));
        }
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        getView().discard();
        getView().getDataTablePanel().setExternalFilters(externalFilters);
        getView().setPresenter(this);
        MementoManager.restoreState(getView(), getPlace());
        if (populateOnStart) {
            populate();
        }
        containerWidget.setWidget(getView());
    }

    public void onDiscard() {
        MementoManager.saveState(getView(), getPlace());
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
