/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.tenant.insurance.TenantSureFacade;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.InsurancePaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.resident.services.services.TenantSurePaymentMethodCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class TenantSurePaymentMethodCrudServiceImpl implements TenantSurePaymentMethodCrudService {

    @Override
    public void init(AsyncCallback<InsurancePaymentMethodDTO> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        InsurancePaymentMethodDTO dto = EntityFactory.create(InsurancePaymentMethodDTO.class);

        dto.newPaymentMethod().set(EntityFactory.create(InsurancePaymentMethod.class));
        dto.newPaymentMethod().type().setValue(PaymentType.CreditCard);
        dto.newPaymentMethod().preAuthorizedAgreementSignature().signatureFormat().setValue(SignatureFormat.AgreeBox);
        dto.preauthorizedPaymentAgreement().setValue("TODO use PortalVistaTermsService");

        dto.currentPaymentMethod().set(
                ServerSideFactory.create(PaymentMethodFacade.class).retrieveInsurancePaymentMethod(
                        ResidentPortalContext.getTenant().<Tenant> createIdentityStub()));

        callback.onSuccess(dto);
    }

    @Override
    public void create(AsyncCallback<Key> callback, InsurancePaymentMethodDTO editableEntity) {
        editableEntity.newPaymentMethod().tenant().set(ResidentPortalContext.getTenant());
        ServerSideFactory.create(TenantSureFacade.class).updatePaymentMethod(editableEntity.newPaymentMethod(), ResidentPortalContext.getTenant());
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<InternationalAddress> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddressSimple(ResidentPortalContext.getTenant()));
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, InsurancePaymentMethodDTO editableEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<InsurancePaymentMethodDTO>> callback, EntityListCriteria<InsurancePaymentMethodDTO> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retrieve(AsyncCallback<InsurancePaymentMethodDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        throw new UnsupportedOperationException();
    }

}
