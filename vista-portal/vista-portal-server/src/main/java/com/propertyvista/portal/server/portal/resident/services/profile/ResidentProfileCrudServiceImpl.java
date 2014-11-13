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
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.server.EmailValidator;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.portal.rpc.portal.resident.dto.ResidentProfileDTO;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentProfileCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class ResidentProfileCrudServiceImpl implements ResidentProfileCrudService {

    @Override
    public void retrieve(AsyncCallback<ResidentProfileDTO> callback, Key entityId, RetrieveTarget retrieveTarget) {
        // find associated tenant entry
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.eq(criteria.proto().user(), ResidentPortalContext.getCustomerUserIdStub());
        Customer customer = Persistence.service().retrieve(criteria);
        Persistence.service().retrieve(customer.emergencyContacts());
        Persistence.service().retrieve(customer.picture());
        // build To
        ResidentProfileDTO to = EntityFactory.create(ResidentProfileDTO.class);
        to.person().setValue(customer.person().getValue());
        if (customer.picture().hasValues()) {
            to.picture().set(customer.picture().duplicate());
            to.picture().customer().setAttachLevel(AttachLevel.IdOnly);
        }
        to.emergencyContacts().addAll(customer.emergencyContacts());

        // TO optimizations
        for (EmergencyContact i : to.emergencyContacts()) {
            i.customer().setAttachLevel(AttachLevel.IdOnly);
        }

        if (retrieveTarget == RetrieveTarget.Edit) {
            PolicyNode policyNode = ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(ResidentPortalContext.getLeaseIdStub());
            RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode, RestrictionsPolicy.class);
            to.emergencyContactsIsMandatory().setValue(restrictionsPolicy.emergencyContactsIsMandatory().getValue());
            to.emergencyContactsNumberRequired().setValue(restrictionsPolicy.emergencyContactsNumber().getValue());
        }

        callback.onSuccess(to);
    }

    @Override
    public void save(AsyncCallback<Key> callback, ResidentProfileDTO dto) {
        Customer customer = ResidentPortalContext.getCustomer();

        customer.person().set(dto.person().cast());
        customer.person().email().setValue(EmailValidator.normalizeEmailAddress(customer.person().email().getValue()));

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
        ServerContext.getVisit().getUserVisit().setName(customer.person().name().getStringView());

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
