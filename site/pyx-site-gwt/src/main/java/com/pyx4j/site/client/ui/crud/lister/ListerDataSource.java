/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2012
 * @author vladlouk
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud.lister;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.EntityDataSource;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData.Operators;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

public class ListerDataSource<E extends IEntity> implements EntityDataSource<E> {

    private final Class<E> entityClass;

    private final AbstractListService<E> service;

    private DataTableFilterData parentFiltering;

    private List<DataTableFilterData> preDefinedFilters = new LinkedList<DataTableFilterData>();;

    public ListerDataSource(Class<E> entityClass, AbstractListService<E> service) {
        this.entityClass = entityClass;
        this.service = service;
    }

    @Override
    public void obtain(EntityQueryCriteria<E> criteria, final AsyncCallback<EntitySearchResult<E>> handlingCallback) {
        service.list(new DefaultAsyncCallback<EntitySearchResult<E>>() {

            @Override
            public void onSuccess(EntitySearchResult<E> result) {
                handlingCallback.onSuccess(result);
            }

        }, updateCriteria((EntityListCriteria<E>) criteria));
    }

    // filtering mechanics:

    public void setParentFiltering(Key parentID, Class<? extends IEntity> parentClass) {
        String ownerMemberName = EntityFactory.getEntityMeta(entityClass).getOwnerMemberName();
        assert (ownerMemberName != null) : "No @Owner in " + entityClass;

        Serializable serchBy;
        if (parentClass != null) {
            serchBy = EntityFactory.create(parentClass);
            ((IEntity) serchBy).setPrimaryKey(parentID);
        } else {
            serchBy = parentID;
        }

        parentFiltering = new DataTableFilterData(new Path(entityClass, ownerMemberName), Operators.is, serchBy);
    }

    public void clearParentFiltering() {
        parentFiltering = null;
    }

    public List<DataTableFilterData> getPreDefinedFilters() {
        return preDefinedFilters;
    }

    public void setPreDefinedFilters(List<DataTableFilterData> preDefinedFilters) {
        this.preDefinedFilters = preDefinedFilters;
    }

    public void addPreDefinedFilters(List<DataTableFilterData> preDefinedFilters) {
        preDefinedFilters.addAll(preDefinedFilters);
    }

    public void addPreDefinedFilter(DataTableFilterData preDefinedFilter) {
        preDefinedFilters.add(preDefinedFilter);
    }

    protected EntityListCriteria<E> updateCriteria(EntityListCriteria<E> criteria) {
        List<DataTableFilterData> currentFilters = new LinkedList<DataTableFilterData>();

        // combine filters:
        if (parentFiltering != null) {
            currentFilters.add(parentFiltering);
        }
        if (!preDefinedFilters.isEmpty()) {
            currentFilters.addAll(preDefinedFilters);
        }

        // update search criteria:
        for (DataTableFilterData fd : currentFilters) {
            if (fd.isFilterOK()) {
                criteria.add(fd.convertToPropertyCriterion());
            }
        }

        return criteria;
    }
}