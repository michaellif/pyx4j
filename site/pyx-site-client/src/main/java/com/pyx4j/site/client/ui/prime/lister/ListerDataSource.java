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
package com.pyx4j.site.client.ui.prime.lister;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.forms.client.ui.AsyncLoadingHandler;
import com.pyx4j.forms.client.ui.EntityDataSource;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

public class ListerDataSource<E extends IEntity> implements EntityDataSource<E> {

    private final Class<E> entityClass;

    private final AbstractListCrudService<E> service;

    private Criterion parentFiltering;

    private List<Criterion> preDefinedFilters = new LinkedList<Criterion>();

    public ListerDataSource(Class<E> entityClass, AbstractListCrudService<E> service) {
        this.entityClass = entityClass;
        this.service = service;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    @Override
    public AsyncLoadingHandler obtain(EntityQueryCriteria<E> criteria, final AsyncCallback<EntitySearchResult<E>> handlingCallback) {
        service.list(new DefaultAsyncCallback<EntitySearchResult<E>>() {

            @Override
            public void onSuccess(EntitySearchResult<E> result) {
                handlingCallback.onSuccess(result);
            }

        }, updateCriteria((EntityListCriteria<E>) criteria));
        return null;
    }

    // filtering mechanics:

    public void setParentFiltering(Key parentID) {
        setParentFiltering(parentID, null);
    }

    public void setParentFiltering(Key parentID, Class<? extends IEntity> parentClass) {
        String ownerMemberName = EntityFactory.getEntityMeta(entityClass).getOwnerMemberName();
        assert (ownerMemberName != null) : "No @Owner in " + entityClass;

        Serializable searchBy;
        if (parentClass != null) {
            searchBy = EntityFactory.create(parentClass);
            ((IEntity) searchBy).setPrimaryKey(parentID);
        } else {
            searchBy = parentID;
        }

        parentFiltering = new PropertyCriterion(new Path(entityClass, ownerMemberName), Restriction.EQUAL, searchBy);
    }

    public void clearParentFiltering() {
        parentFiltering = null;
    }

    public void setPreDefinedFilters(List<Criterion> preDefinedFilters) {
        this.preDefinedFilters = preDefinedFilters;
    }

    public void addPreDefinedFilters(List<Criterion> preDefinedFilters) {
        this.preDefinedFilters.addAll(preDefinedFilters);
    }

    public void addPreDefinedFilter(Criterion preDefinedFilter) {
        this.preDefinedFilters.add(preDefinedFilter);
    }

    public void clearPreDefinedFilters() {
        this.preDefinedFilters.clear();
    }

    protected EntityListCriteria<E> updateCriteria(EntityListCriteria<E> criteria) {
        List<Criterion> currentFilters = new LinkedList<Criterion>();

        // combine filters:
        if (parentFiltering != null) {
            currentFilters.add(parentFiltering);
        }
        if (!preDefinedFilters.isEmpty()) {
            currentFilters.addAll(preDefinedFilters);
        }

        // update search criteria:
        for (Criterion fd : currentFilters) {
            if (fd instanceof PropertyCriterion) {
                if (((PropertyCriterion) fd).isValid()) {
                    criteria.add(fd);
                }
            } else {
                criteria.add(fd);
            }
        }

        return criteria;
    }

    @Override
    public HandlerRegistration addDataChangeHandler(ValueChangeHandler<Class<E>> handler) {
        // TODO Auto-generated method stub
        return null;
    }
}