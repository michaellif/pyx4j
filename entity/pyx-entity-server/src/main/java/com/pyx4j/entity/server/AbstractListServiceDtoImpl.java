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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Filter;
import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveOperation;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.ListerCapability;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.cursor.CursorSource;
import com.pyx4j.entity.shared.utils.BindingContext;
import com.pyx4j.entity.shared.utils.BindingContext.BindingType;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.entity.shared.utils.EntityQueryCriteriaBinder;
import com.pyx4j.gwt.server.IOUtils;
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
     *
     * This called before boFilter
     *
     * Used to retrieve bound detached members before they are copied to DTO
     *
     * To make it work magically we have implemented retriveDetachedMember
     *
     * @param retrieveOperation
     */
    protected void onBeforeBind(BO bo, RetrieveOperation retrieveOperation) {
    }

    /**
     *
     * This is called after toFilter when all required data is loaded to TO
     *
     * This method called for every entity returned to the GWT client for listing. As opposite to single entity in retrieve/save operations.
     * This function is empty no need to call when you override this method
     *
     * @param retrieveOperation
     */
    protected void onAfterBind(BO bo, TO to, RetrieveOperation retrieveOperation) {
    }

    @Override
    public void obtainListerCapabilities(AsyncCallback<Vector<ListerCapability>> callback) {
        boolean hasInMemoryFilter = boFilter(null) != null;

        if (hasInMemoryFilter) {
            callback.onSuccess(ListerCapability.sequentialPaginationCapabilities);
        } else {
            callback.onSuccess(ListerCapability.allCapabilities);
        }
    }

    /**
     * This can be called two times, once to detect Capabilities of the service, second in regular list retrieval flow
     *
     * @param criteria,
     *            can be null when detecting Capabilities
     * @return null if there are no In Memory Filter. This changes ListerCapabilities @see obtainListerCapabilities
     */
    protected Filter<BO> boFilter(EntityQueryCriteria<BO> criteria) {
        if (false) {
            return null;
        }
        // TODO remove after initial UI testing.
        return new Filter<BO>() {
            @Override
            public boolean accept(BO input) {
                return true;
            }
        };
    }

    public final ICursorIterator<BO> getBOCursor(String encodedCursorReference, EntityQueryCriteria<BO> criteria, AttachLevel attachLevel) {
        Filter<BO> inMemoryFilter = boFilter(criteria);

        EntityQueryCriteria<BO> actualCriteria = criteria;

        if (inMemoryFilter != null && criteria instanceof EntityListCriteria) {
            EntityListCriteria<BO> criteriaAsListCriteria = (EntityListCriteria<BO>) criteria;
            // Ignore page size and pagination in request.
            actualCriteria = criteriaAsListCriteria.asEntityQueryCriteria();
        }

        ICursorIterator<BO> boFilterIterator = Persistence.secureQuery(encodedCursorReference, actualCriteria, attachLevel);
        if (inMemoryFilter != null) {
            // Wrap iterator using InMemory Filter
            boFilterIterator = new CursorIteratorFilter<BO>(boFilterIterator, inMemoryFilter);
        }

        return new CursorIteratorDelegate<BO, BO>(boFilterIterator) {

            @Override
            public BO next() {
                BO bo = unfiltered.next();
                onBeforeBind(bo, RetrieveOperation.List);
                return bo;
            }

        };
    }

    protected Filter<TO> toFilter(EntityQueryCriteria<TO> criteria) {
        return null;
    }

    @Override
    public final ICursorIterator<TO> getCursor(String encodedCursorReference, EntityQueryCriteria<TO> dtoCriteria, AttachLevel attachLevel) {
        EntityListCriteria<BO> criteria = criteriaBinder.convertListCriteria(dtoCriteria);

        ICursorIterator<TO> toCreateIterator = new CursorIteratorDelegate<TO, BO>(getBOCursor(encodedCursorReference, criteria, attachLevel)) {

            @Override
            public TO next() {
                BO bo = unfiltered.next();
                TO to = binder.createTO(bo, new BindingContext(BindingType.List));
                onAfterBind(bo, to, RetrieveOperation.List);
                return to;
            }

        };

        Filter<TO> inMemoryFilter = toFilter(dtoCriteria);
        if (inMemoryFilter == null) {
            return toCreateIterator;
        } else {
            return new CursorIteratorFilter<TO>(toCreateIterator, inMemoryFilter);
        }
    }

    @Override
    public final void list(AsyncCallback<EntitySearchResult<TO>> callback, EntityListCriteria<TO> toCriteria) {
        callback.onSuccess(list(toCriteria).execute());
    }

    protected Executable<EntitySearchResult<TO>, RuntimeException> list(final EntityListCriteria<TO> toCriteria) {
        return new Executable<EntitySearchResult<TO>, RuntimeException>() {

            @Override
            public EntitySearchResult<TO> execute() {
                return listImpl(toCriteria);
            }
        };
    }

    private final EntitySearchResult<TO> listImpl(EntityListCriteria<TO> toCriteria) {
        if (!toCriteria.getEntityClass().equals(toClass)) {
            throw new Error("Service " + this.getClass().getName() + " declaration error. " + toClass + "!=" + toCriteria.getEntityClass());
        }
        EntitySearchResult<TO> result = new EntitySearchResult<TO>();
        ICursorIterator<TO> cursor = null;
        String criteriaEncodedCursorReference = toCriteria.getEncodedCursorReference();
        try {
            cursor = getCursor(criteriaEncodedCursorReference, toCriteria, AttachLevel.Attached);
            while (cursor.hasNext()) {
                TO to = cursor.next();
                result.getData().add(to);
                if ((toCriteria.getPageSize() > 0) && result.getData().size() >= toCriteria.getPageSize()) {
                    break;
                }
            }
            // The position is important, hasNext may retrieve one more row.
            result.setEncodedCursorReference(cursor.encodedCursorReference());
            result.hasMoreData(cursor.hasNext());
        } finally {
            IOUtils.closeQuietly(cursor);
        }

        EntityListCriteria<BO> criteria = criteriaBinder.convertListCriteria(toCriteria);
        result.setTotalRows(Persistence.secureCount(criteria));

        return result;
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
