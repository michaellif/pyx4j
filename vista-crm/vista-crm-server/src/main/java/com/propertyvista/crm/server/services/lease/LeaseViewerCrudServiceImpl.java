/*
 *
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.legal.LeaseLegalFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.system.YardiARFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.server.services.lease.common.LeaseViewerCrudServiceBaseImpl;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.blob.LeaseTermAgreementDocumentBlob;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementLegalTerm;
import com.propertyvista.domain.security.CrmUserSignature;
import com.propertyvista.domain.tenant.lease.AgreementDigitalSignatures;
import com.propertyvista.domain.tenant.lease.AgreementInkSignatures;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.SignedAgreementLegalTerm;
import com.propertyvista.dto.LeaseAgreementDocumentsDTO;
import com.propertyvista.dto.LeaseAgreementSigningProgressDTO;
import com.propertyvista.dto.LeaseAgreementStackholderSigningProgressDTO;
import com.propertyvista.dto.LeaseAgreementStackholderSigningProgressDTO.SignatureType;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerCrudServiceImpl extends LeaseViewerCrudServiceBaseImpl<LeaseDTO> implements LeaseViewerCrudService {

    private final static I18n i18n = I18n.get(LeaseViewerCrudServiceImpl.class);

    public LeaseViewerCrudServiceImpl() {
        super(LeaseDTO.class);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Lease> dbCriteria, EntityListCriteria<LeaseDTO> dtoCriteria) {
        PropertyCriterion papCriteria = dtoCriteria.getCriterion(dtoCriteria.proto().preauthorizedPaymentPresent());
        if (papCriteria != null) {
            dtoCriteria.getFilters().remove(papCriteria);

            AndCriterion notDeleted = new AndCriterion();
            notDeleted.eq(dbCriteria.proto().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments().$().isDeleted(), Boolean.FALSE);
            if (papCriteria.getValue() == Boolean.FALSE) {
                dbCriteria.notExists(dbCriteria.proto().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments(), notDeleted);
            } else {
                dbCriteria.add(notDeleted);
                dbCriteria.isNotNull(dbCriteria.proto().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments());
            }
        }

        super.enhanceListCriteria(dbCriteria, dtoCriteria);
    }

    @Override
    protected void enhanceListRetrieved(Lease in, LeaseDTO dto) {
        super.enhanceListRetrieved(in, dto);

        {
            EntityQueryCriteria<AutopayAgreement> criteria = EntityQueryCriteria.create(AutopayAgreement.class);
            criteria.eq(criteria.proto().tenant().lease(), in);
            criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
            dto.preauthorizedPaymentPresent().setValue(Persistence.service().count(criteria) != 0);
        }
    }

    @Override
    protected void enhanceRetrieved(Lease in, LeaseDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(in, to, retrieveTarget);

        if (!to.billingAccount().isNull()) {
            to.transactionHistory().set(ServerSideFactory.create(ARFacade.class).getTransactionHistory(to.billingAccount()));
            to.carryforwardBalance().setValue(to.billingAccount().carryforwardBalance().getValue());
        }

        to.isMoveOutWithinNextBillingCycle().setValue(ServerSideFactory.create(LeaseFacade.class).isMoveOutWithinNextBillingCycle(in));
    }

    @Override
    protected void loadCurrentTerm(LeaseDTO to) {
        assert (!to.currentTerm().isNull());

        Persistence.service().retrieve(to.currentTerm());
        if (to.currentTerm().version().isNull()) {
            to.currentTerm().set(Persistence.secureRetrieveDraft(LeaseTerm.class, to.currentTerm().getPrimaryKey()));
        }

        Persistence.service().retrieveMember(to.currentTerm().version().tenants());
        Persistence.service().retrieveMember(to.currentTerm().version().guarantors());
    }

    @Override
    public void createCompletionEvent(AsyncCallback<VoidSerializable> callback, Key entityId, Lease.CompletionType completionType, LogicalDate eventDate,
            LogicalDate moveOutDate, LogicalDate leseEndDate) {
        ServerSideFactory.create(LeaseFacade.class).createCompletionEvent(EntityFactory.createIdentityStub(Lease.class, entityId), completionType, eventDate,
                moveOutDate, leseEndDate);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void isCancelCompletionEventAvailable(AsyncCallback<CancelMoveOutConstraintsDTO> callback, Key entityId) {
        Lease lease = Persistence.service().retrieve(boClass, entityId);
        CancelMoveOutConstraintsDTO result = ServerSideFactory.create(OccupancyFacade.class).getCancelMoveOutConstraints(lease.unit().getPrimaryKey());
        if (!result.leaseStub().isNull()) {
            Persistence.service().retrieve(result.leaseStub());
        }
        callback.onSuccess(result);
    }

    @Override
    public void cancelCompletionEvent(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason) {
        ServerSideFactory.create(LeaseFacade.class).cancelCompletionEvent(EntityFactory.createIdentityStub(Lease.class, entityId),
                CrmAppContext.getCurrentUserEmployee(), decisionReason);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void moveOut(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);

        ServerSideFactory.create(LeaseFacade.class).moveOut(leaseId, new LogicalDate(SystemDateManager.getDate()));

        // complete actually, if it already finished:
        Lease lease = Persistence.secureRetrieve(Lease.class, entityId);
        if (!lease.leaseTo().isNull() && lease.leaseTo().getValue().before(new LogicalDate(SystemDateManager.getDate()))) {
            ServerSideFactory.create(LeaseFacade.class).complete(leaseId);
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void sendMail(AsyncCallback<String> callback, Key entityId, Vector<LeaseTermParticipant<?>> users, EmailTemplateType emailType) {
        Lease lease = Persistence.service().retrieve(boClass, entityId);
        if ((lease == null) || (lease.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(boClass).getCaption() + "' " + entityId + " NotFound");
        }
        if (!lease.status().getValue().isCurrent()) {
            throw new UserRuntimeException(i18n.tr("Can't send tenant email for inactive Lease"));
        }
        if (users.isEmpty()) {
            throw new UserRuntimeException(i18n.tr("No customer was selected to send email"));
        }

        // check that all lease participants have an associated user entity (email)
        for (LeaseTermParticipant<?> user : users) {
            if (user.leaseParticipant().customer().user().isNull()) {
                throw new UserRuntimeException(i18n.tr("''Send Email'' operation failed, email of lease participant {0} was not found", user.leaseParticipant()
                        .customer().person().name().getStringView()));
            }
        }

        if (emailType == EmailTemplateType.TenantInvitation) {
            // check that selected users can be used for this template
            for (LeaseTermParticipant<?> user : users) {
                if (user.isInstanceOf(LeaseTermGuarantor.class)) {
                    throw new UserRuntimeException(i18n.tr(
                            "''Send Mail'' operation failed: can''t send \"{0}\" for Guarantor. Please re-send e-mail for all valid recipients.",
                            EmailTemplateType.TenantInvitation));
                }
            }

            // send e-mails
            CommunicationFacade commFacade = ServerSideFactory.create(CommunicationFacade.class);
            for (LeaseTermParticipant<?> user : users) {
                if (user.isInstanceOf(LeaseTermTenant.class)) {
                    LeaseTermTenant tenant = user.duplicate(LeaseTermTenant.class);
                    commFacade.sendTenantInvitation(tenant);
                }
            }
        } else {
            new Error(SimpleMessageFormat.format("sending mails for {0} is not yet implemented", emailType));
        }

        Persistence.service().commit();
        String message = users.size() > 1 ? i18n.tr("Emails were sent successfully") : i18n.tr("Email was sent successfully");
        callback.onSuccess(message);
    }

    @Override
    public void activate(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);

        ServerSideFactory.create(LeaseFacade.class).approve(leaseId, null, null);

        // activate actually, if it already runs:
        if (!Persistence.secureRetrieve(Lease.class, entityId).leaseFrom().getValue().after(new LogicalDate(SystemDateManager.getDate()))) {
            ServerSideFactory.create(LeaseFacade.class).activate(leaseId);
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void closeLease(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);

        ServerSideFactory.create(LeaseFacade.class).close(leaseId);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void cancelLease(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);

        ServerSideFactory.create(LeaseFacade.class).cancelLease(leaseId, CrmAppContext.getCurrentUserEmployee(), decisionReason);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void updateFromYardi(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Lease lease = Persistence.service().retrieve(boClass, entityId);

        try {
            ServerSideFactory.create(YardiARFacade.class).updateLease(lease);
        } catch (RemoteException e) {
            throw new UserRuntimeException(i18n.tr("Yardi connection problem"), e);
        } catch (YardiServiceException e) {
            throw new UserRuntimeException(i18n.tr("Error updating lease form Yardi"), e);
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    /**
     * This is a temporary solution for lease renewal (see VISTA-1789 and VISTA-2245)
     */
    @Override
    public void simpleLeaseRenew(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate leaseEndDate) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);

        ServerSideFactory.create(LeaseFacade.class).simpleLeaseRenew(leaseId, leaseEndDate);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void issueN4(AsyncCallback<VoidSerializable> defaultAsyncCallback, N4BatchRequestDTO n4GenerationQuery) {
        // TODO implement this 
    }

    @Override
    public void setLegalStatus(AsyncCallback<VoidSerializable> callback, Lease leaseId, LegalStatus status) {
        ServerSideFactory.create(LeaseLegalFacade.class).setLegalStatus(leaseId, status.status().getValue(), status.details().getValue(),
                "set manually via CRM", CrmAppContext.getCurrentUser());
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void signLease(AsyncCallback<String> callback, Lease leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());

        Persistence.ensureRetrieve(lease.currentTerm(), AttachLevel.Attached);

        CrmUserSignature signature = EntityFactory.create(CrmUserSignature.class);
        signature.signatureFormat().setValue(SignatureFormat.FullName);
        signature.agree().setValue(true);
        signature.fullName().setValue(CrmAppContext.getCurrentUserEmployee().name().getStringView());

        lease.currentTerm().version().employeeSignature().set(signature);
        Persistence.service().merge(lease.currentTerm().version());
        Persistence.service().commit();

        String correlationId = DeferredProcessRegistry.fork(
                new LeaseSignedTermAgreementCreatorDeferredProcess(lease.currentTerm(), CrmAppContext.getCurrentUser()),
                DeferredProcessRegistry.THREAD_POOL_DOWNLOADS);
        callback.onSuccess(correlationId);

    }

    @Override
    public void getLeaseAgreementDocuments(AsyncCallback<LeaseAgreementDocumentsDTO> callback, Lease leaseId) {
        LeaseAgreementDocumentsDTO leaseAgreementDocuments = EntityFactory.create(LeaseAgreementDocumentsDTO.class);
        leaseAgreementDocuments.signingProgress().set(getLeaseAgreementSigningProgress(leaseId));

        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());
        Persistence.ensureRetrieve(lease.currentTerm().version().agreementDocuments(), AttachLevel.Attached);

        leaseAgreementDocuments.inkSignedDocuments().addAll(lease.currentTerm().version().agreementDocuments());
        Iterator<LeaseTermAgreementDocument> i = leaseAgreementDocuments.inkSignedDocuments().iterator();
        while (i.hasNext()) {
            LeaseTermAgreementDocument doc = i.next();
            if (!doc.isSignedByInk().isBooleanTrue()) {
                leaseAgreementDocuments.digitallySignedDocument().set(doc);
                i.remove();
                break;
            }
        }
        if (leaseAgreementDocuments.digitallySignedDocument().isNull() && !lease.currentTerm().version().employeeSignature().isEmpty()) {
            throw new UserRuntimeException(i18n.tr("Generation of signed agreement document is in progress. Please try again later!"));
        }
        callback.onSuccess(leaseAgreementDocuments);
    }

    @Override
    public void updateLeaseAgreementDocuments(AsyncCallback<VoidSerializable> callback, Lease leaseId, LeaseAgreementDocumentsDTO documents) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());
        Persistence.ensureRetrieve(lease.currentTerm().version().agreementDocuments(), AttachLevel.Attached);

        List<LeaseTermAgreementDocument> newDocs = new LinkedList<>();
        for (LeaseTermAgreementDocument incomingDoc : documents.inkSignedDocuments()) {
            if (incomingDoc.getPrimaryKey() == null) {
                newDocs.add(incomingDoc);
            }
        }

        List<LeaseTermAgreementDocument> deletedDocs = new LinkedList<>();
        for (LeaseTermAgreementDocument doc : lease.currentTerm().version().agreementDocuments()) {
            boolean found = false;
            for (LeaseTermAgreementDocument inkSignedDocument : documents.inkSignedDocuments()) {
                if (doc.getPrimaryKey().equals(inkSignedDocument.getPrimaryKey())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                deletedDocs.add(doc);
            }
        }

        for (LeaseTermAgreementDocument deletedDoc : deletedDocs) {
            Persistence.service().delete(LeaseTermAgreementDocumentBlob.class, deletedDoc.file().blobKey().getValue());
            Persistence.service().delete(LeaseTermAgreementDocument.class, deletedDoc.getPrimaryKey());

            for (LeaseTermParticipant<?> participant : deletedDoc.signedParticipants()) {
                Persistence.ensureRetrieve(participant.agreementSignatures(), AttachLevel.IdOnly);
                AgreementInkSignatures inkSignature = participant.agreementSignatures().duplicate(AgreementInkSignatures.class);
                Persistence.service().delete(inkSignature);
            }
        }

        for (LeaseTermAgreementDocument newDoc : newDocs) {
            newDoc.leaseTermV().set(lease.currentTerm().version());
            newDoc.isSignedByInk().setValue(true);
            Persistence.service().merge(newDoc);

            for (LeaseTermParticipant<?> participant : newDoc.signedParticipants()) {
                AgreementInkSignatures signature = EntityFactory.create(AgreementInkSignatures.class);
                signature.leaseTermParticipant().set(participant);
                Persistence.service().persist(signature);
            }
        }

        Persistence.service().commit();

        callback.onSuccess(null);
    }

    private LeaseAgreementSigningProgressDTO getLeaseAgreementSigningProgress(Lease leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());

        List<LeaseTermParticipant<?>> stakeholderParticipants = getStakeholderParticipants(lease.currentTerm());

        LeaseAgreementSigningProgressDTO progress = EntityFactory.create(LeaseAgreementSigningProgressDTO.class);

        for (LeaseTermParticipant<?> participant : stakeholderParticipants) {
            Persistence.ensureRetrieve(participant.agreementSignatures(), AttachLevel.Attached);

            LeaseAgreementStackholderSigningProgressDTO stakeholdersProgress = EntityFactory.create(LeaseAgreementStackholderSigningProgressDTO.class);
            stakeholdersProgress.name().setValue(participant.leaseParticipant().customer().person().name().getStringView());
            stakeholdersProgress.role().setValue(participant.role().getStringView());

            boolean hasSigned = true;
            Iterator<LeaseAgreementLegalTerm> legalTerms = lease.currentTerm().version().agreementLegalTerms().iterator();

            while (hasSigned && legalTerms.hasNext()) {
                LeaseAgreementLegalTerm legalTerm = legalTerms.next();
                if (legalTerm.signatureFormat().getValue() != SignatureFormat.None) {
                    if (!participant.agreementSignatures().isNull()
                            && participant.agreementSignatures().getInstanceValueClass().equals(AgreementInkSignatures.class)) {
                        stakeholdersProgress.singatureType().setValue(SignatureType.Ink);
                    } else if (participant.agreementSignatures().getInstanceValueClass().equals(AgreementDigitalSignatures.class)) {
                        stakeholdersProgress.singatureType().setValue(SignatureType.Digital);
                        AgreementDigitalSignatures signatures = participant.agreementSignatures().duplicate(AgreementDigitalSignatures.class);

                        boolean foundSignedTerm = false;
                        for (SignedAgreementLegalTerm signedTerm : signatures.legalTermsSignatures()) {
                            if (signedTerm.term().getPrimaryKey().equals(legalTerm.getPrimaryKey())) {
                                foundSignedTerm = true;
                                break;
                            }
                        }
                        if (!foundSignedTerm) {
                            hasSigned = false;
                            break;
                        }
                    } else {
                        hasSigned = false;
                        break;
                    }
                }
            }
            stakeholdersProgress.hasSigned().setValue(hasSigned);
            progress.stackholdersProgressBreakdown().add(stakeholdersProgress);
        }
        return progress;
    }

    private List<LeaseTermParticipant<?>> getStakeholderParticipants(LeaseTerm leaseTerm) {
        List<LeaseTermParticipant<?>> stakeholderParticipants = new ArrayList<>();
        Persistence.ensureRetrieve(leaseTerm.version().tenants(), AttachLevel.Attached);
        Persistence.ensureRetrieve(leaseTerm.version().guarantors(), AttachLevel.Attached);

        stakeholderParticipants.addAll(leaseTerm.version().tenants());
        stakeholderParticipants.addAll(leaseTerm.version().guarantors());
        Iterator<LeaseTermParticipant<?>> i = stakeholderParticipants.iterator();
        while (i.hasNext()) {
            LeaseTermParticipant<?> participant = i.next();
            Persistence.ensureRetrieve(participant.leaseParticipant(), AttachLevel.Attached);
            if (!shouldSign(participant)) {
                i.remove();
            }
        }
        return stakeholderParticipants;
    }

    private boolean shouldSign(LeaseTermParticipant<?> participant) {
        // TODO put this in a facade
        return participant.role().getValue() == Role.Guarantor || participant.role().getValue() == Role.Applicant
                || participant.role().getValue() == Role.CoApplicant;
    }

}