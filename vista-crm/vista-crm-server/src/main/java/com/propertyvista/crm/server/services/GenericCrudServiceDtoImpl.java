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
package com.propertyvista.crm.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.lister.EntityLister;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.rpc.services.AbstractCrudService;

/**
 * Generic parameters:
 * DBO - Data Base Object
 * DTO - Data Transfer Object
 * 
 * enhanceXXX methods supposed to be overridden in ancestors to perform some DTO customisation.
 */
public abstract class GenericCrudServiceDtoImpl<DBO extends IEntity, DTO extends DBO> implements AbstractCrudService<DTO> {

    protected Class<DBO> dboClass;

    protected Class<DTO> dtoClass;

    public GenericCrudServiceDtoImpl(Class<DBO> dboClass, Class<DTO> dtoClass) {
        this.dboClass = dboClass;
        this.dtoClass = dtoClass;
    }

    protected void enhanceCreateDTO(DBO in, DTO dto) {
    }

    @Override
    public void create(AsyncCallback<DTO> callback, DTO dto) {
        DBO entity = GenericConverter.convertDTO2DBO(dto, dboClass);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        dto = GenericConverter.convertDBO2DTO(entity, dtoClass);
        enhanceCreateDTO(entity, dto);
        callback.onSuccess(dto);
    }

    protected void enhanceRetrieveDTO(DBO in, DTO dto) {
    }

    @Override
    public void retrieve(AsyncCallback<DTO> callback, Key entityId) {
        DBO entity = PersistenceServicesFactory.getPersistenceService().retrieve(dboClass, entityId);
        DTO dto = GenericConverter.convertDBO2DTO(entity, dtoClass);
        enhanceRetrieveDTO(entity, dto);
        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<DTO> callback, DTO dto) {
        DBO entity = GenericConverter.convertDTO2DBO(dto, dboClass);
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(GenericConverter.convertDBO2DTO(entity, dtoClass));
    }

    protected void enhanceSearchCriteria(EntitySearchCriteria<DBO> searchCriteria, EntitySearchCriteria<DTO> in) {
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<DTO>> callback, EntitySearchCriteria<DTO> criteria) {
        EntitySearchCriteria<DBO> c = GenericConverter.convertDTO2DBO(criteria, dboClass);
        enhanceSearchCriteria(c, criteria);
        callback.onSuccess(GenericConverter.convertDBO2DTO(EntityServicesImpl.secureSearch(c), dtoClass));
    }

    protected void enhanceListCriteria(EntityListCriteria<DBO> dbCriteria, EntityListCriteria<DTO> in) {
        if ((in.getFilters() != null) && (!in.getFilters().isEmpty())) {
            for (Criterion cr : in.getFilters()) {
                if (cr instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) cr;
                    String path = propertyCriterion.getPropertyName();
                    if (path.startsWith(in.proto().getObjectClass().getSimpleName())) {
                        String dbObjectPath = dbCriteria.proto().getObjectClass().getSimpleName() + path.substring(path.indexOf(Path.PATH_SEPARATOR));
                        dbCriteria.add(new PropertyCriterion(dbObjectPath, propertyCriterion.getRestriction(), propertyCriterion.getValue()));
                    }
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
        callback.onSuccess(GenericConverter.convertDBO2DTO(EntityLister.secureQuery(c), dtoClass));
    }
}
