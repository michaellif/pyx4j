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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.RangeCriterion;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.security.shared.SecurityController;

public abstract class AbstractListServiceDtoImpl<BO extends IEntity, TO extends IEntity> implements AbstractListCrudService<TO>, CursorSource<TO> {

    protected Class<BO> boClass;

    protected Class<TO> toClass;

    protected final BO boProto;

    protected final TO toProto;

    protected final EntityBinder<BO, TO> binder;

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
    protected Path convertPropertyDTOPathToDBOPath(Path path, BO boProto, TO toProto) {
        throw new Error("Unsupported query property path " + path);
    }

    /**
     *
     * @deprecated TODO VladS switch to EntityQueryCriteriaBinder
     */
    @Deprecated
    private Collection<Criterion> convertFilters(EntityListCriteria<BO> criteria, Collection<? extends Criterion> toFilters) {
        Collection<Criterion> boFilters = new ArrayList<Criterion>();
        for (Criterion cr : toFilters) {
            Criterion criterion = convertCriterion(criteria, cr);
            if (criterion != null) {
                boFilters.add(criterion);
            }
        }
        return boFilters;
    }

    /**
     *
     * @deprecated TODO VladS switch to EntityQueryCriteriaBinder
     */
    @Deprecated
    protected Criterion convertCriterion(EntityListCriteria<BO> criteria, Criterion cr) {
        if (cr instanceof PropertyCriterion) {
            PropertyCriterion propertyCriterion = (PropertyCriterion) cr;
            Path path = binder.getBoundBOMemberPath(propertyCriterion.getPropertyPath());
            if (path == null) {
                path = convertPropertyDTOPathToDBOPath(propertyCriterion.getPropertyPath(), boProto, toProto);
            }
            return new PropertyCriterion(path, propertyCriterion.getRestriction(), convertValue(criteria, propertyCriterion));
        } else if (cr instanceof OrCriterion) {
            OrCriterion criterion = new OrCriterion();
            criterion.addRight(convertFilters(criteria, ((OrCriterion) cr).getFiltersRight()));
            criterion.addLeft(convertFilters(criteria, ((OrCriterion) cr).getFiltersLeft()));
            return criterion;
        } else if (cr instanceof AndCriterion) {
            AndCriterion criterion = new AndCriterion();
            criterion.addAll(convertFilters(criteria, ((AndCriterion) cr).getFilters()));
            return criterion;
        } else if (cr instanceof RangeCriterion) {
            AndCriterion criterion = new AndCriterion();
            criterion.addAll(convertFilters(criteria, ((RangeCriterion) cr).getFilters()));
            return criterion;
        } else {
            throw new IllegalArgumentException("Can't convert " + cr.getClass() + " criteria");
        }
    }

    /**
     *
     * @deprecated TODO VladS switch to EntityQueryCriteriaBinder
     */
    @Deprecated
    protected Serializable convertValue(EntityListCriteria<BO> criteria, PropertyCriterion propertyCriterion) {
        Serializable value = propertyCriterion.getValue();
        if (value instanceof Path) {
            Path path = binder.getBoundBOMemberPath((Path) value);
            if (path == null) {
                path = convertPropertyDTOPathToDBOPath((Path) value, boProto, toProto);
            }
            return path;
        } else if (value instanceof Criterion) {
            return convertCriterion(criteria, (Criterion) value);
        } else {
            return value;
        }
    }

    /**
     *
     * @deprecated TODO VladS switch to EntityQueryCriteriaBinder
     */
    @Deprecated
    protected void enhanceListCriteria(EntityListCriteria<BO> boCriteria, EntityListCriteria<TO> toCriteria) {
        if ((toCriteria.getFilters() != null) && (!toCriteria.getFilters().isEmpty())) {
            boCriteria.addAll(convertFilters(boCriteria, toCriteria.getFilters()));
        }
        if ((toCriteria.getSorts() != null) && (!toCriteria.getSorts().isEmpty())) {
            for (Sort s : toCriteria.getSorts()) {
                Path path = binder.getBoundBOMemberPath(s.getPropertyPath());
                if (path == null) {
                    path = convertPropertyDTOPathToDBOPath(s.getPropertyPath(), boCriteria.proto(), toCriteria.proto());
                }
                if (s.isDescending()) {
                    boCriteria.desc(path);
                } else {
                    boCriteria.asc(path);
                }
            }
        }
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
        EntityListCriteria<BO> criteria = EntityListCriteria.create(boClass);
        criteria.setPageNumber(dtoCriteria.getPageNumber());
        criteria.setPageSize(dtoCriteria.getPageSize());
        criteria.setVersionedCriteria(dtoCriteria.getVersionedCriteria());
        enhanceListCriteria(criteria, dtoCriteria);

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
        EntityListCriteria<BO> criteria = EntityListCriteria.create(boClass);
        criteria.setPageNumber(dtoCriteria.getPageNumber());
        criteria.setPageSize(dtoCriteria.getPageSize());
        criteria.setVersionedCriteria(dtoCriteria.getVersionedCriteria());
        enhanceListCriteria(criteria, dtoCriteria);
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
