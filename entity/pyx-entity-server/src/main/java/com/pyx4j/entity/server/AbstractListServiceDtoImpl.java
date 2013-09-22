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
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.AndCriterion;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;
import com.pyx4j.security.shared.SecurityController;

public abstract class AbstractListServiceDtoImpl<E extends IEntity, DTO extends IEntity> extends EntityDtoBinder<E, DTO> implements AbstractListService<DTO> {

    protected Class<E> entityClass;

    protected AbstractListServiceDtoImpl(Class<E> entityClass, Class<DTO> dtoClass) {
        super(entityClass, dtoClass);
        this.entityClass = entityClass;
    }

    /**
     * This method called for every entity returned to the GWT client for listing. As opposite to single entity in retrieve/save operations.
     * This function is empty no need to call when you override this method
     */
    protected void enhanceListRetrieved(E entity, DTO dto) {
    }

    /**
     * Used to retrieve bound detached members before they are copied to DTO
     * TODO To make it work magically we have implemented retriveDetachedMember
     */
    protected void retrievedForList(E entity) {
    }

    @Override
    protected boolean retriveDetachedMember(IEntity dboMember) {
        return Persistence.service().retrieve(dboMember);
    }

    protected Path convertPropertyDTOPathToDBOPath(String path, E dboProto, DTO dtoProto) {
        throw new Error("Unsupported query property path " + path);
    }

    private Collection<Criterion> convertFilters(Collection<Criterion> dtoFilters) {
        Collection<Criterion> dboFilters = new ArrayList<Criterion>();
        for (Criterion cr : dtoFilters) {
            dboFilters.add(convertCriterion(cr));
        }
        return dboFilters;
    }

    public Criterion convertCriterion(Criterion cr) {
        if (cr instanceof PropertyCriterion) {
            PropertyCriterion propertyCriterion = (PropertyCriterion) cr;
            Path path = getBoundDboMemberPath(new Path(propertyCriterion.getPropertyPath()));
            if (path == null) {
                path = convertPropertyDTOPathToDBOPath(propertyCriterion.getPropertyPath(), dboProto, dtoProto);
            }
            return new PropertyCriterion(path, propertyCriterion.getRestriction(), convertValue(propertyCriterion));
        } else if (cr instanceof OrCriterion) {
            OrCriterion criterion = new OrCriterion();
            criterion.addRight(convertFilters(((OrCriterion) cr).getFiltersRight()));
            criterion.addLeft(convertFilters(((OrCriterion) cr).getFiltersLeft()));
            return criterion;
        } else if (cr instanceof AndCriterion) {
            AndCriterion criterion = new AndCriterion();
            criterion.addAll(convertFilters(((AndCriterion) cr).getFilters()));
            return criterion;
        } else {
            throw new IllegalArgumentException("Can't convert " + cr.getClass() + " criteria");
        }
    }

    public Serializable convertValue(PropertyCriterion propertyCriterion) {
        Serializable value = propertyCriterion.getValue();
        if (value instanceof Path) {
            Path path = getBoundDboMemberPath((Path) value);
            if (path == null) {
                path = convertPropertyDTOPathToDBOPath(value.toString(), dboProto, dtoProto);
            }
            return path;
        } else if (value instanceof Criterion) {
            return convertCriterion((Criterion) value);
        } else {
            return value;
        }
    }

    protected void enhanceListCriteria(EntityListCriteria<E> dbCriteria, EntityListCriteria<DTO> dtoCriteria) {
        if ((dtoCriteria.getFilters() != null) && (!dtoCriteria.getFilters().isEmpty())) {
            dbCriteria.addAll(convertFilters(dtoCriteria.getFilters()));
        }
        if ((dtoCriteria.getSorts() != null) && (!dtoCriteria.getSorts().isEmpty())) {
            for (Sort s : dtoCriteria.getSorts()) {
                Path path = getBoundDboMemberPath(new Path(s.getPropertyPath()));
                if (path == null) {
                    path = convertPropertyDTOPathToDBOPath(s.getPropertyPath(), dbCriteria.proto(), dtoCriteria.proto());
                }
                if (s.isDescending()) {
                    dbCriteria.desc(path.toString());
                } else {
                    dbCriteria.asc(path.toString());
                }
            }
        }
    }

    protected EntitySearchResult<E> query(EntityListCriteria<E> criteria) {
        return Persistence.secureQuery(criteria);
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<DTO>> callback, EntityListCriteria<DTO> dtoCriteria) {
        if (!dtoCriteria.getEntityClass().equals(dtoClass)) {
            throw new Error("Service " + this.getClass().getName() + " declaration error. " + dtoClass + "!=" + dtoCriteria.getEntityClass());
        }
        EntityListCriteria<E> criteria = EntityListCriteria.create(dboClass);
        criteria.setPageNumber(dtoCriteria.getPageNumber());
        criteria.setPageSize(dtoCriteria.getPageSize());
        criteria.setVersionedCriteria(dtoCriteria.getVersionedCriteria());
        enhanceListCriteria(criteria, dtoCriteria);
        EntitySearchResult<E> dbResults = query(criteria);

        EntitySearchResult<DTO> result = new EntitySearchResult<DTO>();
        result.setEncodedCursorReference(dbResults.getEncodedCursorReference());
        result.hasMoreData(dbResults.hasMoreData());
        result.setTotalRows(dbResults.getTotalRows());
        for (E entity : dbResults.getData()) {
            retrievedForList(entity);
            DTO dto = createDTO(entity);
            enhanceListRetrieved(entity, dto);
            result.getData().add(dto);
        }
        callback.onSuccess(result);

    }

    protected void delete(E actualEntity) {
        Persistence.service().delete(actualEntity);
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        SecurityController.assertPermission(new EntityPermission(entityClass, EntityPermission.DELETE));
        E actualEntity = Persistence.service().retrieve(entityClass, entityId);
        SecurityController.assertPermission(EntityPermission.permissionDelete(actualEntity));
        delete(actualEntity);
        Persistence.service().commit();
        callback.onSuccess(true);
    }

}
