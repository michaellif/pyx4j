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
 */
package com.propertyvista.portal.server.portal.resident.services.movein;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementSigningProgressFacade;
import com.propertyvista.domain.company.Notification.AlertType;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementConfirmationTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementLegalTerm;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.tenant.lease.AgreementDigitalSignatures;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.SignedAgreementConfirmationTerm;
import com.propertyvista.domain.tenant.lease.SignedAgreementLegalTerm;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementDTO;
import com.propertyvista.portal.rpc.portal.resident.services.movein.LeaseSigningCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

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
        to.utilities().setValue(retrieveUtilities(lease.currentTerm()));

        Persistence.ensureRetrieve(lease.unit().building().landlord(), AttachLevel.Attached);
        to.landlordInfo().name().setValue(lease.unit().building().landlord().name().getValue());
        to.landlordInfo().address().setValue(lease.unit().building().landlord().address().getStringView());

        Persistence.ensureRetrieve(lease.currentTerm().agreementLegalTerms(), AttachLevel.Attached);
        for (LeaseAgreementLegalTerm term : lease.currentTerm().agreementLegalTerms()) {
            SignedAgreementLegalTerm signedTerm = EntityFactory.create(SignedAgreementLegalTerm.class);
            signedTerm.term().set(term);
            signedTerm.signature().signatureFormat().set(term.signatureFormat());
            to.legalTerms().add(signedTerm);
        }

        Persistence.ensureRetrieve(lease.currentTerm().agreementConfirmationTerms(), AttachLevel.Attached);
        for (LeaseAgreementConfirmationTerm term : lease.currentTerm().agreementConfirmationTerms()) {
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
        agreementSignatures.leaseParticipant().set(ResidentPortalContext.getLeaseParticipant());
        agreementSignatures.legalTermsSignatures().addAll(editableEntity.legalTerms());
        agreementSignatures.confirmationTermSignatures().addAll(editableEntity.confirmationTerms());

        Persistence.secureSave(agreementSignatures);

        final Lease lease = editableEntity.leaseTerm().lease();
        if (ServerSideFactory.create(LeaseTermAgreementSigningProgressFacade.class).isEmployeeSignatureRequired(lease)) {
            Persistence.service().addTransactionCompletionHandler(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    ServerSideFactory.create(NotificationFacade.class).leaseApplicationNotification(lease, AlertType.ApplicationLeaseSigning);
                    return null;
                }
            });
        }

        Persistence.service().commit();

        ServerContext.getVisit().setAclRevalidationRequired();
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

    private String retrieveUtilities(LeaseTerm term) {
        assert (!term.isValueDetached());

        Persistence.ensureRetrieve(term.version().utilities(), AttachLevel.Attached);

        StringBuffer res = new StringBuffer();
        for (BuildingUtility utility : term.version().utilities()) {
            if (res.length() > 0) {
                res.append(", ");
            }
            res.append(utility.name().getStringView());
        }

        return res.toString();
    }
}
