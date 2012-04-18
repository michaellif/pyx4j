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
package com.propertyvista.portal.server.portal.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.portal.domain.dto.ResidentDTO;
import com.propertyvista.portal.rpc.portal.services.PersonalInfoCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class PersonalInfoCrudServiceImpl implements PersonalInfoCrudService {

    @Override
    public void retrieve(AsyncCallback<ResidentDTO> callback, Key entityId, RetrieveTraget retrieveTraget) {
        try {
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
            // add current address if any
            EntityQueryCriteria<PersonScreening> critAddr = EntityQueryCriteria.create(PersonScreening.class);
            critAddr.add(PropertyCriterion.eq(critAddr.proto().screene(), tenant));
            List<PersonScreening> result = Persistence.service().query(critAddr);
            if (result.size() > 0) {
                dto.currentAddress().set(result.get(0).currentAddress());
            }
            callback.onSuccess(dto);
        } catch (Exception e) {
            callback.onFailure(new Throwable("Operation failed. No data found."));
        }
    }

    @Override
    public void save(AsyncCallback<ResidentDTO> callback, ResidentDTO dto) {
        try {
            CustomerUser currentUser = TenantAppContext.getCurrentUser();
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), currentUser));
            Customer tenant = Persistence.service().retrieve(criteria);
            tenant.person().set(dto);
            tenant.emergencyContacts().set(dto.emergencyContacts());
            Persistence.service().persist(tenant);
            Persistence.service().commit();
            callback.onSuccess(dto);
        } catch (Exception e) {
            callback.onFailure(new Throwable("Operation failed. No data found."));
        }
    }

    @Override
    public void create(AsyncCallback<ResidentDTO> callback, ResidentDTO editableEntity) {
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
