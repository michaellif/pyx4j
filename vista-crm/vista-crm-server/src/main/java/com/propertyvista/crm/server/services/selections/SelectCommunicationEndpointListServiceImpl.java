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
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.crm.rpc.services.selections.SelectCommunicationEndpointListService;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
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

        accumulate(dtos, createByPatternCriteria(CustomerUser.class, pageSize, namePattern));
        accumulate(dtos, createByPatternCriteria(CrmUser.class, pageSize, namePattern));
        accumulate(dtos, createByPatternCriteria(Building.class, pageSize, namePattern));
        accumulate(dtos, createByPatternCriteria(Portfolio.class, pageSize, namePattern));
        accumulate(dtos, createByPatternCriteria(AptUnit.class, pageSize, namePattern));

        callback.onSuccess(dtos);
    }

    private <T extends CommunicationEndpoint> void accumulate(Vector<CommunicationEndpointDTO> accumulatedDtos, EntityListCriteria<T> criteria) {
        if (criteria.getPageSize() > 0 && accumulatedDtos.size() >= criteria.getPageSize()) {
            return;
        }
        EntitySearchResult<T> endpoints = Persistence.secureQuery(criteria);

        if (endpoints != null && endpoints.getData() != null && endpoints.getData().size() > 0) {
            for (CommunicationEndpoint ep : endpoints.getData()) {
                if (criteria.getPageSize() > 0 && accumulatedDtos.size() >= criteria.getPageSize()) {
                    return;
                }
                accumulatedDtos.add((ServerSideFactory.create(CommunicationMessageFacade.class).generateEndpointDTO(ep)));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CommunicationEndpoint> EntityListCriteria<T> createByPatternCriteria(Class<T> entityClass, int pageSize, String namePattern) {
        EntityListCriteria<T> criteria = EntityListCriteria.create(entityClass);
        criteria.setPageSize(pageSize);

        if (entityClass.equals(CustomerUser.class)) {
            criteria.like(((EntityListCriteria<CustomerUser>) criteria).proto().name(), namePattern);
        } else if (entityClass.equals(CrmUser.class)) {
            criteria.like(((EntityListCriteria<CrmUser>) criteria).proto().name(), namePattern);
        } else if (entityClass.equals(Building.class)) {
            criteria.like(((EntityListCriteria<Building>) criteria).proto().propertyCode(), namePattern);
        } else if (entityClass.equals(Portfolio.class)) {
            criteria.like(((EntityListCriteria<Portfolio>) criteria).proto().name(), namePattern);
        } else if (entityClass.equals(AptUnit.class)) {
            criteria.like(((EntityListCriteria<AptUnit>) criteria).proto().info().number(), namePattern);
        }
        return criteria;
    }

}
