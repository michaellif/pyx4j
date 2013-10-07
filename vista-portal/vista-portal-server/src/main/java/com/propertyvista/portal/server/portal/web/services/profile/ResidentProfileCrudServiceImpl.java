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
package com.propertyvista.portal.server.portal.web.services.profile;

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
import com.propertyvista.portal.rpc.portal.web.dto.ResidentProfileDTO;
import com.propertyvista.portal.rpc.portal.web.services.profile.ResidentProfileCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class ResidentProfileCrudServiceImpl implements ResidentProfileCrudService {

    @Override
    public void retrieve(AsyncCallback<ResidentProfileDTO> callback, Key entityId, RetrieveTarget retrieveTarget) {
        CustomerUser currentUser = TenantAppContext.getCurrentUser();
        // find associated tenant entry
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), currentUser));
        Customer tenant = Persistence.service().retrieve(criteria);
        Persistence.service().retrieve(tenant.emergencyContacts());
        // build dto
        ResidentProfileDTO dto = EntityFactory.create(ResidentProfileDTO.class);
        dto.setValue(tenant.person().getValue());
        dto.emergencyContacts().addAll(tenant.emergencyContacts());

        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<Key> callback, ResidentProfileDTO dto) {
        CustomerUser currentUser = TenantAppContext.getCurrentUser();
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), currentUser));
        Customer customer = Persistence.service().retrieve(criteria);

        customer.person().set(dto.cast());
        customer.emergencyContacts().clear();
        customer.emergencyContacts().addAll(dto.emergencyContacts());

        Persistence.service().persist(customer);

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
