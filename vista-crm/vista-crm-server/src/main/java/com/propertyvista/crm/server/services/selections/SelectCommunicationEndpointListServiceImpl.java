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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.selections.SelectCommunicationEndpointListService;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.dto.CommunicationEndpointDTO;

public class SelectCommunicationEndpointListServiceImpl extends AbstractListServiceImpl<CommunicationEndpoint> implements
        SelectCommunicationEndpointListService {

    public SelectCommunicationEndpointListServiceImpl() {
        super(CommunicationEndpoint.class);
    }

    @Override
    protected void bind() {
        bind(toProto.id(), boProto.id());
        bindCompleteObject();
    }

    @Override
    public void getEndpointForSelection(AsyncCallback<Vector<CommunicationEndpointDTO>> callback, EntityListCriteria<CommunicationEndpoint> criteria) {
        EntityListCriteria<CrmUser> crmUserCriteria = EntityListCriteria.create(CrmUser.class);
        EntityListCriteria<CustomerUser> tenantUserCriteria = EntityListCriteria.create(CustomerUser.class);

        EntitySearchResult<CrmUser> crmUsers = Persistence.secureQuery(crmUserCriteria);
        EntitySearchResult<CustomerUser> tenants = Persistence.secureQuery(tenantUserCriteria);
        Vector<CommunicationEndpointDTO> dtos = new Vector<CommunicationEndpointDTO>(crmUsers.getData().size() + tenants.getData().size());
        for (CommunicationEndpoint ep : crmUsers.getData()) {
            dtos.add(generateEndpointDTO(ep));
        }
        for (CommunicationEndpoint ep : tenants.getData()) {
            dtos.add(generateEndpointDTO(ep));
        }
        callback.onSuccess(dtos);
    }

    private CommunicationEndpointDTO generateEndpointDTO(CommunicationEndpoint entity) {
        if (entity == null) {
            return null;
        }
        CommunicationEndpointDTO rec = EntityFactory.create(CommunicationEndpointDTO.class);
        rec.endpoint().set(entity);

        if (entity.getInstanceValueClass().equals(SystemEndpoint.class)) {
            SystemEndpoint e = entity.cast();
            rec.name().setValue(e.name().getValue());
            rec.type().setValue(ContactType.System);
        } else if (entity.getInstanceValueClass().equals(CrmUser.class)) {
            CrmUser e = entity.cast();
            rec.name().set(e.name());
            rec.type().setValue(ContactType.Employee);
        } else if (entity.getInstanceValueClass().equals(CustomerUser.class)) {
            CustomerUser e = entity.cast();
            rec.name().set(e.name());
            rec.type().setValue(ContactType.Tenants);
        }
        return rec;
    }
}
