/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-31
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.lister.EntityLister;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

/**
 * Generic parameters:
 * DBO - Data Base Object
 * DTO - Data Transfer Object
 * 
 * enhanceXXX methods supposed to be overridden in ancestors to perform some DTO customisation.
 */
public abstract class GenericListServiceDtoImpl<DBO extends IEntity, DTO extends DBO> implements AbstractListService<DTO> {

    protected Class<DBO> dboClass;

    protected Class<DTO> dtoClass;

    public GenericListServiceDtoImpl(Class<DBO> dboClass, Class<DTO> dtoClass) {
        this.dboClass = dboClass;
        this.dtoClass = dtoClass;
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        Persistence.service().delete(dboClass, entityId);
        callback.onSuccess(true);
    }

    protected void enhanceDTO(DBO in, DTO dto, boolean fromList) {
    }

    protected void enhancePropertyCriterion(EntityListCriteria<DBO> dbCriteria, PropertyCriterion propertyCriterion) {
        throw new Error("Unsupported Criterion property " + propertyCriterion.getPropertyName());
    }

    protected String enhancePropertySorts(EntityListCriteria<DBO> dbCriteria, Sort sort) {
        throw new Error("Unsupported Sort property " + sort.getPropertyName());
    }

    protected String convertPropertyDTOPathToDBO(String path, DBO dboProto, DTO dtoProto) {
        return dboProto.getObjectClass().getSimpleName() + path.substring(path.indexOf(Path.PATH_SEPARATOR));
    }

    protected void enhanceListCriteria(EntityListCriteria<DBO> dbCriteria, EntityListCriteria<DTO> dtoCriteria) {
        if ((dtoCriteria.getFilters() != null) && (!dtoCriteria.getFilters().isEmpty())) {
            for (Criterion cr : dtoCriteria.getFilters()) {
                if (cr instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) cr;
                    String path = propertyCriterion.getPropertyName();
                    if (path.startsWith(dtoCriteria.proto().getObjectClass().getSimpleName())) {
                        String dbObjectPath = convertPropertyDTOPathToDBO(path, dbCriteria.proto(), dtoCriteria.proto());
                        dbCriteria.add(new PropertyCriterion(dbObjectPath, propertyCriterion.getRestriction(), propertyCriterion.getValue()));
                    } else {
                        enhancePropertyCriterion(dbCriteria, propertyCriterion);
                    }
                }
            }
        }
        if ((dtoCriteria.getSorts() != null) && (!dtoCriteria.getSorts().isEmpty())) {
            // Just copy all Sorts for now. Change to non sortable one that are failing
            for (Sort s : dtoCriteria.getSorts()) {
                String path = s.getPropertyName();

                if (path.startsWith(dtoCriteria.proto().getObjectClass().getSimpleName())) {
                    path = convertPropertyDTOPathToDBO(path, dbCriteria.proto(), dtoCriteria.proto());
                } else {
                    path = enhancePropertySorts(dbCriteria, s);
                    if (path == null) {
                        continue;
                    }
                }
                if (s.isDescending()) {
                    dbCriteria.desc(path);
                } else {
                    dbCriteria.asc(path);
                }
            }
        }
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<DTO>> callback, EntityListCriteria<DTO> criteria) {
        EntityListCriteria<DBO> c = EntityListCriteria.create(dboClass);
        c.setPageNumber(criteria.getPageNumber());
        c.setPageSize(criteria.getPageSize());
        enhanceListCriteria(c, criteria);
        EntitySearchResult<DTO> r = GenericConverter.convertDBO2DTO(EntityLister.secureQuery(c), dtoClass, new GenericConverter.EnhanceDTO<DBO, DTO>() {
            @Override
            public void enhanceDTO(DBO in, DTO dto) {
                GenericListServiceDtoImpl.this.enhanceDTO(in, dto, true);
            }
        });
        callback.onSuccess(r);
    }
}
