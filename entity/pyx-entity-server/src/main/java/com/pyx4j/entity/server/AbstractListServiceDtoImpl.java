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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.lister.EntityLister;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
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
     * Used to retrieve bound detached members
     * To make it work magically we have implemented retriveDetachedMember
     */
    protected void retrievedForList(E entity) {
    }

    @Override
    protected boolean retriveDetachedMember(IEntity dboMemeber) {
        return Persistence.service().retrieve(dboMemeber);
    }

    protected void enhanceListRetrieved(E entity, DTO dto) {
    }

    protected Path convertPropertyDTOPathToDBOPath(String path, E dboProto, DTO dtoProto) {
        throw new Error("Unsupported Sort property " + path);
    }

    protected void enhanceListCriteria(EntityListCriteria<E> dbCriteria, EntityListCriteria<DTO> dtoCriteria) {
        if ((dtoCriteria.getFilters() != null) && (!dtoCriteria.getFilters().isEmpty())) {
            for (Criterion cr : dtoCriteria.getFilters()) {
                if (cr instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) cr;
                    Path path = getBoundDboMemberPath(new Path(propertyCriterion.getPropertyName()));
                    if (path == null) {
                        path = convertPropertyDTOPathToDBOPath(propertyCriterion.getPropertyName(), dbCriteria.proto(), dtoCriteria.proto());
                    }
                    dbCriteria.add(new PropertyCriterion(path.toString(), propertyCriterion.getRestriction(), propertyCriterion.getValue()));
                }
            }
        }
        if ((dtoCriteria.getSorts() != null) && (!dtoCriteria.getSorts().isEmpty())) {
            for (Sort s : dtoCriteria.getSorts()) {
                Path path = getBoundDboMemberPath(new Path(s.getPropertyName()));
                if (path == null) {
                    path = convertPropertyDTOPathToDBOPath(s.getPropertyName(), dbCriteria.proto(), dtoCriteria.proto());
                }
                if (s.isDescending()) {
                    dbCriteria.desc(path.toString());
                } else {
                    dbCriteria.asc(path.toString());
                }
            }
        }
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<DTO>> callback, EntityListCriteria<DTO> dtoCriteria) {
        if (!dtoCriteria.getEntityClass().equals(dtoClass)) {
            throw new Error("Service " + this.getClass().getName() + " declaration error. " + dtoClass + "!=" + dtoCriteria.getEntityClass());
        }
        EntityListCriteria<E> criteria = EntityListCriteria.create(dboClass);
        criteria.setPageNumber(dtoCriteria.getPageNumber());
        criteria.setPageSize(dtoCriteria.getPageSize());
        enhanceListCriteria(criteria, dtoCriteria);
        EntitySearchResult<E> dbResults = EntityLister.secureQuery(criteria);

        EntitySearchResult<DTO> result = new EntitySearchResult<DTO>();
        result.setEncodedCursorReference(dbResults.getEncodedCursorReference());
        result.hasMoreData(dbResults.hasMoreData());
        result.setTotalRows(dbResults.getTotalRows());
        result.setData(new Vector<DTO>());
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
        callback.onSuccess(true);
    }

}
