/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.selections;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.crm.rpc.services.selections.SelectCommunicationEndpointListService;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.CommunicationEndpointDTO;

public class SelectCommunicationEndpointListServiceImpl extends AbstractListServiceImpl<CommunicationEndpoint> implements
        SelectCommunicationEndpointListService {

    public SelectCommunicationEndpointListServiceImpl() {
        super(CommunicationEndpoint.class);
    }

    @Override
    public void getEndpointForSelection(AsyncCallback<Vector<CommunicationEndpointDTO>> callback, EntityListCriteria<CommunicationEndpointDTO> criteria) {
        PropertyCriterion nameCriteria = criteria.getCriterion(criteria.proto().name());
        String namePattern = null;
        if (nameCriteria != null) {
            namePattern = nameCriteria.getValue().toString();
        }

        int pageSize = criteria.getPageSize();
        Vector<CommunicationEndpointDTO> dtos = new Vector<CommunicationEndpointDTO>();

        accumulate(dtos, createByPatternCriteria(LeaseParticipant.class, pageSize, namePattern), LeaseParticipant.class);
        accumulate(dtos, createByPatternCriteria(Employee.class, pageSize, namePattern), Employee.class);
        accumulate(dtos, createByPatternCriteria(Building.class, pageSize, namePattern), Building.class);
        accumulate(dtos, createByPatternCriteria(Portfolio.class, pageSize, namePattern), Portfolio.class);

        callback.onSuccess(dtos);
    }

    private <T extends IEntity> void accumulate(Vector<CommunicationEndpointDTO> accumulatedDtos, EntityListCriteria<T> criteria, Class<T> entityClass) {
        if (criteria.getPageSize() > 0 && accumulatedDtos.size() >= criteria.getPageSize()) {
            return;
        }
        EntitySearchResult<T> endpoints = Persistence.secureQuery(criteria);

        if (endpoints != null && endpoints.getData() != null && endpoints.getData().size() > 0) {
            for (IEntity ep : endpoints.getData()) {
                if (criteria.getPageSize() > 0 && accumulatedDtos.size() >= criteria.getPageSize()) {
                    return;
                }
                IEntity processed = ep;
                if (entityClass.equals(Building.class)) {
                    CommunicationGroup cg = EntityFactory.create(CommunicationGroup.class);
                    cg.building().set(ep);
                    processed = cg;
                } else if (entityClass.equals(Portfolio.class)) {
                    CommunicationGroup cg = EntityFactory.create(CommunicationGroup.class);
                    cg.portfolio().set(ep);
                    processed = cg;
                }

                accumulatedDtos.add((ServerSideFactory.create(CommunicationMessageFacade.class).generateEndpointDTO((CommunicationEndpoint) processed)));
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T extends IEntity> EntityListCriteria<T> createByPatternCriteria(Class<T> entityClass, int pageSize, String namePattern) {
        EntityListCriteria<T> criteria = EntityListCriteria.create(entityClass);
        criteria.setPageSize(pageSize);

        if (entityClass.equals(LeaseParticipant.class)) {
            criteria.like(((EntityListCriteria<LeaseParticipant>) criteria).proto().customer().user().name(), namePattern);
        } else if (entityClass.equals(Employee.class)) {
            criteria.like(((EntityListCriteria<Employee>) criteria).proto().user().name(), namePattern);
        } else if (entityClass.equals(Building.class)) {
            criteria.like(((EntityListCriteria<Building>) criteria).proto().propertyCode(), namePattern);
        } else if (entityClass.equals(Portfolio.class)) {
            criteria.like(((EntityListCriteria<Portfolio>) criteria).proto().name(), namePattern);
        }
        return criteria;
    }

}
