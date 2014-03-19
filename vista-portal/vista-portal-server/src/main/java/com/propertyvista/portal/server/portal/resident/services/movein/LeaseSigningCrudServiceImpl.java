/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.movein;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.rpc.AuthorizationChangedSystemNotification;
import com.pyx4j.security.rpc.AuthorizationChangedSystemNotification.ChangeType;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.policy.policies.domain.LeaseAgreementConfirmationTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementLegalTerm;
import com.propertyvista.domain.tenant.lease.AgreementDigitalSignatures;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.SignedAgreementConfirmationTerm;
import com.propertyvista.domain.tenant.lease.SignedAgreementLegalTerm;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementDTO;
import com.propertyvista.portal.rpc.portal.resident.services.movein.LeaseSigningCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.portal.server.portal.resident.services.ResidentAuthenticationServiceImpl;

public class LeaseSigningCrudServiceImpl implements LeaseSigningCrudService {

    @Override
    public void init(AsyncCallback<LeaseAgreementDTO> callback, InitializationData initializationData) {
        LeaseAgreementDTO to = EntityFactory.create(LeaseAgreementDTO.class);

        Lease lease = ResidentPortalContext.getLease();
        Persistence.service().retrieve(lease.unit().building());
        Persistence.service().retrieve(lease.unit().floorplan());
        Persistence.service().retrieve(lease.currentTerm().version().tenants());

        to.unit().set(lease.unit());
        to.leaseTerm().set(lease.currentTerm());

        for (LeaseAgreementLegalTerm term : lease.currentTerm().version().agreementLegalTerms()) {
            SignedAgreementLegalTerm signedTerm = EntityFactory.create(SignedAgreementLegalTerm.class);
            signedTerm.term().set(term);
            signedTerm.signature().signatureFormat().set(term.signatureFormat());
            to.legalTerms().add(signedTerm);
        }

        for (LeaseAgreementConfirmationTerm term : lease.currentTerm().version().agreementConfirmationTerm()) {
            SignedAgreementConfirmationTerm signedTerm = EntityFactory.create(SignedAgreementConfirmationTerm.class);
            signedTerm.term().set(term);
            signedTerm.signature().signatureFormat().set(term.signatureFormat());
            to.confirmationTerms().add(signedTerm);
        }
        callback.onSuccess(to);
    }

    @Override
    public void create(AsyncCallback<Key> callback, LeaseAgreementDTO editableEntity) {
        AgreementDigitalSignatures agreementSignatures = EntityFactory.create(AgreementDigitalSignatures.class);
        agreementSignatures.leaseTermParticipant().set(ResidentPortalContext.getLeaseTermParticipant());
        agreementSignatures.legalTermsSignatures().addAll(editableEntity.legalTerms());
        Persistence.secureSave(agreementSignatures);
        Persistence.service().commit();

        new ResidentAuthenticationServiceImpl().reAuthorize(ResidentPortalContext.getLeaseIdStub());
        Context.addResponseSystemNotification(new AuthorizationChangedSystemNotification(ChangeType.behavioursChanged));
        callback.onSuccess(null);
    }

    @Override
    public void save(AsyncCallback<Key> callback, LeaseAgreementDTO editableEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retrieve(AsyncCallback<LeaseAgreementDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<LeaseAgreementDTO>> callback, EntityListCriteria<LeaseAgreementDTO> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new UnsupportedOperationException();
    }

}
