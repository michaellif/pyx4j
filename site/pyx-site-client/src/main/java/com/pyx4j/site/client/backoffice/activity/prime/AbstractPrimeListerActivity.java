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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.CriterionPathBound;
import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.forms.client.ui.datatable.DataTableModelEvent;
import com.pyx4j.forms.client.ui.datatable.DataTableModelListener;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IPrimeListerView;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IPrimeListerView.IPrimeListerPresenter;
import com.pyx4j.site.client.memento.MementoManager;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class AbstractPrimeListerActivity<E extends IEntity> extends AbstractPrimeActivity<IPrimeListerView<?>> implements IPrimeListerPresenter<E> {

    private static final Logger log = LoggerFactory.getLogger(AbstractPrimeListerActivity.class);

    private final Class<E> entityClass;

    // This is very bad filter since it is stored in View or Lister forever and never resets
    // And is not show in filter.
    // There is a solution in CEntityCollectionCrudHyperlink AppPlaceByOwnerBuilder
    @Deprecated
    private Key parentEntityId;

    private List<Criterion> externalFilters;

    //TODO Investigate why we need this.
    private boolean populateOnStart = true;

    public AbstractPrimeListerActivity(Class<E> entityClass, AppPlace place, IPrimeListerView<E> view) {
        super(view, place);
        // development correctness checks:
        assert (entityClass != null);

        this.entityClass = entityClass;

        String val;
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            parentEntityId = new Key(val);
        }

        EntityFiltersBuilder<E> filters = EntityFiltersBuilder.create(entityClass);
        parseExternalFilters(place, entityClass, filters);
        if (filters.getFilters().size() > 0) {
            externalFilters = filters.getFilters();
        }

        view.getDataTablePanel().getDataTableModel().addDataTableModelListener(new DataTableModelListener() {

            @Override
            public void onDataTableModelChanged(DataTableModelEvent event) {
                if (event.getType() == DataTableModelEvent.Type.REBUILD) {
                    onPopulate();
                }
            }
        });
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
        if (place instanceof CrudAppPlace) {
            EntityFiltersBuilder<?> initializeFilters = ((CrudAppPlace) place).getListerInitializeFilters();
            if (initializeFilters != null) {
                // TODO Considerer having rootEntityClass forgiving Path
                // Filters BO and TO are the same
                if (entityClass == initializeFilters.proto().getValueClass()) {
                    filters.addAll(initializeFilters.getFilters());
                } else {
                    // Convert filters BO to TO when possible
                    for (Criterion criterion : initializeFilters.getFilters()) {
                        if (criterion instanceof CriterionPathBound) {
                            // Convert to TO path
                            Path propertyPath = new Path(entityClass, ((CriterionPathBound) criterion).getPropertyPath().getPathMembers());
                            filters.add(((CriterionPathBound) criterion).duplicated(propertyPath));
                        } else {
                            throw new IllegalArgumentException("criterion conversion required " + criterion + " to path of " + entityClass);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        getView().discard();
        getView().getDataTablePanel().getDataSource().setParentEntityId(parentEntityId);
        getView().getDataTablePanel().setExternalFilters(externalFilters);
        getView().setPresenter(this);
        onStart();
        MementoManager.restoreState(getView(), getPlace());
        if (populateOnStart) {
            populate();
        }
        containerWidget.setWidget(getView());
    }

    /**
     * Called after data is shown/propagated to UI components
     */
    protected void onPopulate() {
    }

    public void onDiscard() {
        MementoManager.saveState(getView(), getPlace());

        getView().setPresenter(null);
        getView().getDataTablePanel().setExternalFilters(null);
        getView().getDataTablePanel().getDataSource().setParentEntityId(null);
        getView().discard();
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
