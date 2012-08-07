/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 5, 2012
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.notes;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.visor.IVisorController;

public abstract class VisorControllerBase<E extends IEntity> implements IVisorController<E> {

    private final Key entityId;

    private final Class<E> entityClass;

    private final Key parentId;

    private final Class<? extends IEntity> parentClass;

    public VisorControllerBase(Class<E> entityClass, Class<? extends IEntity> parentClass, Key parentId) {
        this(entityClass, null, parentClass, parentId);
    }

    public VisorControllerBase(Class<E> entityClass, Key entityId, Class<? extends IEntity> parentClass, Key parentId) {
        this.entityClass = entityClass;
        this.entityId = entityId;
        this.parentClass = parentClass;
        this.parentId = parentId;
    }

    public Class<? extends IEntity> getParentClass() {
        return parentClass;
    }

    public Key getParentId() {
        return parentId;
    }

    @Override
    public void show(IView parentView) {
        IsWidget visor = getView();
        parentView.showVisor(visor, visor.asWidget().getTitle());
    }

    protected E getNewItem() {
        return null;
    }

    /*
     * the methods below have been mainly copied from com.pyx4j.site.client.activity.crud.EditorActivityBase
     */
    public void populate(EntityListCriteria<E> criteria, DefaultAsyncCallback<EntitySearchResult<E>> callback) {
        getService().list(callback, criteria);
    }

    @Override
    public void populate(DefaultAsyncCallback<E> callback) {
        if (isNewEntity()) {
            createNewEntity(callback);
        } else {
            getService().retrieve(callback, entityId, AbstractCrudService.RetrieveTraget.Edit);
        }
    }

    protected void createNewEntity(AsyncCallback<E> callback) {
        if (getNewItem() != null) {
            callback.onSuccess(getNewItem());
        } else {
            callback.onSuccess(EntityFactory.create(getEntityClass()));
        }
    }

    protected boolean isNewEntity() {
        return (entityId == null);
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

}
