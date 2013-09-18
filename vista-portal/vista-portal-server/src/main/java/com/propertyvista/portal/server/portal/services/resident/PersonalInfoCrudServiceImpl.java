/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.portal.domain.dto.ResidentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PersonalInfoCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class PersonalInfoCrudServiceImpl implements PersonalInfoCrudService {

    @Override
    public void retrieve(AsyncCallback<ResidentDTO> callback, Key entityId, RetrieveTarget retrieveTarget) {
        CustomerUser currentUser = TenantAppContext.getCurrentUser();
        // find associated tenant entry
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), currentUser));
        Customer tenant = Persistence.service().retrieve(criteria);
        Persistence.service().retrieve(tenant.emergencyContacts());
        // build dto
        ResidentDTO dto = EntityFactory.create(ResidentDTO.class);
        dto.setValue(tenant.person().getValue());
        dto.emergencyContacts().addAll(tenant.emergencyContacts());

        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<Key> callback, ResidentDTO dto) {
        CustomerUser currentUser = TenantAppContext.getCurrentUser();
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), currentUser));
        Customer tenant = Persistence.service().retrieve(criteria);

        tenant.person().set(dto.cast());
        tenant.emergencyContacts().clear();
        tenant.emergencyContacts().addAll(dto.emergencyContacts());

        Persistence.service().persist(tenant);
        Persistence.service().commit();

        // Update name label in UI
        Context.getVisit().getUserVisit().setName(tenant.person().name().getStringView());

        callback.onSuccess(tenant.getPrimaryKey());
    }

    @Override
    public void init(AsyncCallback<ResidentDTO> callback, InitializationData initializationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void create(AsyncCallback<Key> callback, ResidentDTO editableEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<ResidentDTO>> callback, EntityListCriteria<ResidentDTO> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new UnsupportedOperationException();
    }
}
