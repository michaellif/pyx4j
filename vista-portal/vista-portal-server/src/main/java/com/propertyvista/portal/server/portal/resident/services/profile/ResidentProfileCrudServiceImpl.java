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
package com.propertyvista.portal.server.portal.resident.services.profile;

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
import com.propertyvista.portal.rpc.portal.resident.dto.ResidentProfileDTO;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentProfileCrudService;
import com.propertyvista.portal.server.security.TenantAppContext;

public class ResidentProfileCrudServiceImpl implements ResidentProfileCrudService {

    @Override
    public void retrieve(AsyncCallback<ResidentProfileDTO> callback, Key entityId, RetrieveTarget retrieveTarget) {
        CustomerUser currentUser = TenantAppContext.getCurrentUser();
        // find associated tenant entry
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), currentUser));
        Customer customer = Persistence.service().retrieve(criteria);
        Persistence.service().retrieve(customer.emergencyContacts());
        Persistence.service().retrieve(customer.picture());
        // build dto
        ResidentProfileDTO dto = EntityFactory.create(ResidentProfileDTO.class);
        dto.person().setValue(customer.person().getValue());
        if (customer.picture().hasValues()) {
            dto.picture().set(customer.picture().detach());
        }
        dto.emergencyContacts().addAll(customer.emergencyContacts());

        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<Key> callback, ResidentProfileDTO dto) {
        CustomerUser currentUser = TenantAppContext.getCurrentUser();
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), currentUser));
        Customer customer = Persistence.service().retrieve(criteria);

        customer.person().set(dto.person().cast());

        customer.picture().set(dto.picture());

        customer.emergencyContacts().clear();
        customer.emergencyContacts().addAll(dto.emergencyContacts());

        Persistence.service().merge(customer);

        // Update user
        Persistence.service().retrieve(customer.user());
        customer.user().name().setValue(customer.person().name().getStringView());
        customer.user().email().setValue(customer.person().email().getValue());
        Persistence.service().merge(customer.user());

        Persistence.service().commit();

        // Update name label in UI
        Context.getVisit().getUserVisit().setName(customer.person().name().getStringView());

        callback.onSuccess(customer.getPrimaryKey());
    }

    @Override
    public void init(AsyncCallback<ResidentProfileDTO> callback, InitializationData initializationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void create(AsyncCallback<Key> callback, ResidentProfileDTO editableEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<ResidentProfileDTO>> callback, EntityListCriteria<ResidentProfileDTO> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new UnsupportedOperationException();
    }
}
