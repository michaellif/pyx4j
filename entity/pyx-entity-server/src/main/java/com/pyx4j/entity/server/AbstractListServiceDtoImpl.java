/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Nov 6, 2011
 * @author vlads
 */
package com.pyx4j.entity.server;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.entity.shared.utils.EntityQueryCriteriaBinder;
import com.pyx4j.security.shared.SecurityController;

public abstract class AbstractListServiceDtoImpl<BO extends IEntity, TO extends IEntity> implements AbstractListCrudService<TO>, CursorSource<TO> {

    protected Class<BO> boClass;

    protected Class<TO> toClass;

    protected final BO boProto;

    protected final TO toProto;

    protected final EntityBinder<BO, TO> binder;

    protected final EntityQueryCriteriaBinder<BO, TO> criteriaBinder;

    protected final boolean strictDataModelPermissions = ServerSideConfiguration.instance().strictDataModelPermissions();

    protected AbstractListServiceDtoImpl(Class<BO> boClass, Class<TO> toClass) {
        this(new CrudEntityBinder<BO, TO>(boClass, toClass) {

            @Override
            protected void bind() {
                bindCompleteObject();
            }

        });
    }

    protected AbstractListServiceDtoImpl(EntityBinder<BO, TO> binder) {
        this.boClass = binder.boClass();
        this.toClass = binder.toClass();
        boProto = EntityFactory.getEntityPrototype(boClass);
        toProto = EntityFactory.getEntityPrototype(toClass);
        this.binder = binder;
        this.criteriaBinder = EntityQueryCriteriaBinder.create(binder);

    }

    @Deprecated
    protected void bind() {

    }

    @Deprecated
    protected void bindCompleteObject() {

    }

    /**
     * Allows to map BO to id of different TO entity.
     * if changed, need to change getTOKey
     *
     * @experimental
     *
     * @param toId
     * @return primary key of BO entity
     */
    protected Key getBOKey(TO to) {
        return to.getPrimaryKey();
    }

    /**
     * Default implementation does noting since the keys mapped one to one.
     *
     * @experimental
     *
     * @param bo
     * @param to
     */
    protected Key getTOKey(BO bo, TO to) {
        return bo.getPrimaryKey();
    }

    /**
     * This method called for every entity returned to the GWT client for listing. As opposite to single entity in retrieve/save operations.
     * This function is empty no need to call when you override this method
     */
    protected void enhanceListRetrieved(BO bo, TO to) {
    }

    /**
     * Used to retrieve bound detached members before they are copied to DTO
     * TODO To make it work magically we have implemented retriveDetachedMember
     */
    protected void retrievedForList(BO bo) {
    }

    /**
     *
     * @deprecated TODO VladS switch to EntityQueryCriteriaBinder
     */
    @Deprecated
    protected void enhanceListCriteria(EntityListCriteria<BO> boCriteria, EntityListCriteria<TO> toCriteria) {
        throw new Error("deprecated");
    }

    @Deprecated
    // TODO use getQueryCursor
    protected EntitySearchResult<BO> query(EntityListCriteria<BO> criteria) {
        return Persistence.secureQuery(criteria);
    }

    public final ICursorIterator<BO> getQueryCursor(String encodedCursorReference, EntityQueryCriteria<BO> criteria, AttachLevel attachLevel) {
        return new CursorIteratorDelegate<BO, BO>(Persistence.secureQuery(encodedCursorReference, criteria, attachLevel)) {

            @Override
            public BO next() {
                BO bo = unfiltered.next();
                retrievedForList(bo);
                return bo;
            }

        };
    }

    @Override
    public ICursorIterator<TO> getCursor(String encodedCursorReference, EntityListCriteria<TO> dtoCriteria, AttachLevel attachLevel) {
        EntityListCriteria<BO> criteria = criteriaBinder.convertListCriteria(dtoCriteria);

        return new CursorIteratorDelegate<TO, BO>(getQueryCursor(encodedCursorReference, criteria, attachLevel)) {

            @Override
            public TO next() {
                BO bo = unfiltered.next();
                TO to = binder.createTO(bo);
                enhanceListRetrieved(bo, to);
                return to;
            }

        };
    }

    //TODO make it final
    @Override
    public void list(AsyncCallback<EntitySearchResult<TO>> callback, EntityListCriteria<TO> dtoCriteria) {
        if (!dtoCriteria.getEntityClass().equals(toClass)) {
            throw new Error("Service " + this.getClass().getName() + " declaration error. " + toClass + "!=" + dtoCriteria.getEntityClass());
        }
        EntityListCriteria<BO> criteria = criteriaBinder.convertListCriteria(dtoCriteria);
        EntitySearchResult<BO> dbResults = query(criteria);

        EntitySearchResult<TO> result = new EntitySearchResult<TO>();
        result.setEncodedCursorReference(dbResults.getEncodedCursorReference());
        result.hasMoreData(dbResults.hasMoreData());
        result.setTotalRows(dbResults.getTotalRows());
        for (BO bo : dbResults.getData()) {
            retrievedForList(bo);
            TO dto = binder.createTO(bo);
            enhanceListRetrieved(bo, dto);
            result.getData().add(dto);
        }
        callback.onSuccess(result);

    }

    protected void delete(BO bo) {
        Persistence.service().delete(bo);
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        SecurityController.assertPermission(new EntityPermission(boClass, EntityPermission.DELETE));
        TO to = EntityFactory.createIdentityStub(toClass, entityId);
        BO bo = Persistence.service().retrieve(boClass, getBOKey(to));
        SecurityController.assertPermission(bo, EntityPermission.permissionDelete(bo));
        delete(bo);
        Persistence.service().commit();
        callback.onSuccess(true);
    }

}
