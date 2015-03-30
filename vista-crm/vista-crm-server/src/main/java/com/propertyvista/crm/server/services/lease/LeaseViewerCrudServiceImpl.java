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
 */
package com.propertyvista.crm.server.services.lease;

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
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.legal.eviction.EvictionCaseFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementSigningProgressFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.server.services.lease.common.LeaseViewerCrudServiceBaseImpl;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.blob.LeaseTermAgreementDocumentBlob;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.company.Notification.AlertType;
import com.propertyvista.domain.eviction.EvictionCaseStatus;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.security.CrmUserSignature;
import com.propertyvista.domain.tenant.lease.AgreementInkSignatures;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseAgreementDocumentsDTO;
import com.propertyvista.dto.LeaseAgreementSigningProgressDTO;
import com.propertyvista.dto.LeaseAgreementStakeholderSigningProgressDTO;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerCrudServiceImpl extends LeaseViewerCrudServiceBaseImpl<LeaseDTO> implements LeaseViewerCrudService {

    private final static I18n i18n = I18n.get(LeaseViewerCrudServiceImpl.class);

    public LeaseViewerCrudServiceImpl() {
        super(LeaseDTO.class);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Lease> dbCriteria, EntityListCriteria<LeaseDTO> dtoCriteria) {
        for (PropertyCriterion papCriteria : dtoCriteria.getCriterions(dtoCriteria.proto().preauthorizedPaymentPresent())) {
            if (papCriteria != null) {
                dtoCriteria.removeCriterions(papCriteria);
                @SuppressWarnings("unchecked")
                List<Boolean> values = (List<Boolean>) papCriteria.getValue();
                if (values != null && values.size() == 1) {
                    AndCriterion notDeleted = new AndCriterion();
                    notDeleted.eq(dbCriteria.proto().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments().$().isDeleted(), false);
                    if (values.contains(Boolean.FALSE)) {
                        dbCriteria.notExists(dbCriteria.proto().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments(), notDeleted);
                    } else {
                        dbCriteria.add(notDeleted);
                        dbCriteria.isNotNull(dbCriteria.proto().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments());
                    }
                }
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
        if (retrieveTarget != RetrieveTarget.View) {
            throw new Error("Shouldn't be called - view service only");
        }

        super.enhanceRetrieved(in, to, retrieveTarget);

        EntityQueryCriteria<LeaseTerm> criteria = EntityQueryCriteria.create(LeaseTerm.class);
        criteria.eq(criteria.proto().lease(), in);
        criteria.eq(criteria.proto().status(), LeaseTerm.Status.Historic);
        to.historyPresent().setValue(Persistence.service().exists(criteria));

        if (Lease.Status.isApplicationUnitSelected(to)) {
            to.transactionHistory().set(ServerSideFactory.create(ARFacade.class).getTransactionHistory(to.billingAccount()));
            to.carryforwardBalance().setValue(to.billingAccount().carryforwardBalance().getValue());
        }

        to.isMoveOutWithinNextBillingCycle().setValue(ServerSideFactory.create(LeaseFacade.class).isMoveOutWithinNextBillingCycle(in));
        to.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(in));

        loadCurrentLegalStatus(to);
    }

    @Override
    protected boolean persist(Lease dbo, LeaseDTO in) {
        throw new Error("Shouldn't be called - view service only");
    }

    @Override
    protected void loadCurrentTerm(LeaseDTO to) {
        assert (!to.currentTerm().isNull());

        to.currentTerm().set(Persistence.retriveFinalOrDraft(LeaseTerm.class, to.currentTerm().getPrimaryKey(), AttachLevel.Attached));

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

        ServerSideFactory.create(LeaseFacade.class).moveOut(leaseId, SystemDateManager.getLogicalDate());

        // complete actually, if it already finished:
        Lease lease = Persistence.secureRetrieve(Lease.class, entityId);
        if (!lease.leaseTo().isNull() && lease.leaseTo().getValue().before(SystemDateManager.getLogicalDate())) {
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
                            "''Send Mail'' operation failed: unable to send send \"{0}\" for Guarantor. Please validate email address of recipient(s).",
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

        ServerSideFactory.create(LeaseFacade.class).approve(leaseId, CrmAppContext.getCurrentUserEmployee(), "Existing Lease Activation");

        // activate actually, if it already runs:
        if (!Persistence.secureRetrieve(Lease.class, entityId).leaseFrom().getValue().after(SystemDateManager.getLogicalDate())) {
            ServerSideFactory.create(LeaseFacade.class).activate(leaseId);
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void closeLease(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);

        ServerSideFactory.create(LeaseFacade.class).close(leaseId, CrmAppContext.getCurrentUserEmployee(), decisionReason);

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
    public void updateFromYardiDeferred(AsyncCallback<String> callback, Key entityId) {
        Lease lease = Persistence.service().retrieve(boClass, entityId);
        callback.onSuccess(DeferredProcessRegistry.fork(new LeaseYardiUpdateDeferredProcess(lease), ThreadPoolNames.IMPORTS));
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
    public void signLease(AsyncCallback<String> callback, Lease leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());

        CrmUserSignature signature = EntityFactory.create(CrmUserSignature.class);
        signature.signatureFormat().setValue(SignatureFormat.FullName);
        signature.agree().setValue(true);
        signature.fullName().setValue(CrmAppContext.getCurrentUserEmployee().name().getStringView());

        Persistence.service().merge(signature);

        // Update only one field.
        {
            LeaseTerm update = EntityFactory.create(LeaseTerm.class);
            update.employeeSignature().set(signature);
            Persistence.update(lease.currentTerm().getPrimaryKey(), update);
        }

        Persistence.service().commit();

        // Hack me VISTA-4525
        lease.currentTerm().employeeSignature().set(signature);

        String correlationId = DeferredProcessRegistry.fork(new SignedLeaseTermAgreementDocumentCreatorDeferredProcess(lease.currentTerm()),
                DeferredProcessRegistry.THREAD_POOL_DOWNLOADS);
        callback.onSuccess(correlationId);

    }

    @Override
    public void getLeaseAgreementDocuments(AsyncCallback<LeaseAgreementDocumentsDTO> callback, Lease leaseId) {
        LeaseAgreementDocumentsDTO leaseAgreementDocuments = EntityFactory.create(LeaseAgreementDocumentsDTO.class);
        leaseAgreementDocuments.signingProgress().set(ServerSideFactory.create(LeaseTermAgreementSigningProgressFacade.class).getSigningProgress(leaseId));

        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());
        Persistence.ensureRetrieve(lease.currentTerm().agreementDocuments(), AttachLevel.Attached);

        leaseAgreementDocuments.inkSignedDocuments().addAll(lease.currentTerm().agreementDocuments());
        Iterator<LeaseTermAgreementDocument> i = leaseAgreementDocuments.inkSignedDocuments().iterator();
        while (i.hasNext()) {
            LeaseTermAgreementDocument doc = i.next();
            if (!doc.isSignedByInk().getValue(false)) {
                leaseAgreementDocuments.digitallySignedDocument().set(doc);
                i.remove();
                break;
            }
        }
        if (leaseAgreementDocuments.digitallySignedDocument().isNull() && !lease.currentTerm().employeeSignature().isEmpty()) {
            throw new UserRuntimeException(i18n.tr("Generation of signed agreement document is in progress. Please try again later!"));
        }
        callback.onSuccess(leaseAgreementDocuments);
    }

    @Override
    public void updateLeaseAgreementDocuments(AsyncCallback<VoidSerializable> callback, Lease leaseId, LeaseAgreementDocumentsDTO documents) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());
        Persistence.ensureRetrieve(lease.currentTerm().agreementDocuments(), AttachLevel.Attached);

        List<LeaseTermAgreementDocument> newDocs = new LinkedList<>();
        for (LeaseTermAgreementDocument incomingInkSignedDoc : documents.inkSignedDocuments()) {
            if (incomingInkSignedDoc.getPrimaryKey() == null) {
                newDocs.add(incomingInkSignedDoc);
            }
        }

        List<LeaseTermAgreementDocument> deletedDocs = new LinkedList<>();
        for (LeaseTermAgreementDocument doc : lease.currentTerm().agreementDocuments()) {
            if (doc.isSignedByInk().getValue(false) == false) {
                // we cannot allow user to delete digitally signed docs
                continue;
            }

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
                Persistence.ensureRetrieve(participant.leaseParticipant().agreementSignatures(), AttachLevel.IdOnly);
                AgreementInkSignatures inkSignature = participant.leaseParticipant().agreementSignatures().duplicate(AgreementInkSignatures.class);
                Persistence.service().delete(inkSignature);
            }
        }

        for (LeaseTermAgreementDocument newDoc : newDocs) {
            newDoc.leaseTerm().set(lease.currentTerm());
            newDoc.isSignedByInk().setValue(true);
            Persistence.service().merge(newDoc);

            for (LeaseTermParticipant<?> participant : newDoc.signedParticipants()) {
                AgreementInkSignatures signature = EntityFactory.create(AgreementInkSignatures.class);
                signature.leaseParticipant().set(participant.leaseParticipant());
                Persistence.service().persist(signature);
            }
        }

        if (isEmployeeSignatureRequired(lease)) {
            final Lease leaseFinal = leaseId;
            Persistence.service().addTransactionCompletionHandler(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    ServerSideFactory.create(NotificationFacade.class).leaseApplicationNotification(leaseFinal, AlertType.ApplicationLeaseSigning);
                    return null;
                }
            });
        }

        Persistence.service().commit();

        callback.onSuccess(null);
    }

    private void loadCurrentLegalStatus(LeaseDTO lease) {
        EvictionCaseStatus status = ServerSideFactory.create(EvictionCaseFacade.class).getCurrentEvictionStatus(lease);
        if (status != null) {
            lease.currentLegalStatus().set(status.evictionStep().name());
        }
    }

    private boolean isEmployeeSignatureRequired(Lease lease) {
        boolean isRequired = false;
        if (lease.currentTerm().employeeSignature().isEmpty()) {
            isRequired = true;
            LeaseAgreementSigningProgressDTO signingProgress = ServerSideFactory.create(LeaseTermAgreementSigningProgressFacade.class)
                    .getSigningProgress(lease);
            for (LeaseAgreementStakeholderSigningProgressDTO progress : signingProgress.stackholdersProgressBreakdown()) {
                if (!progress.hasSigned().getValue(false)) {
                    isRequired = false;
                    break;
                }
            }
        }
        return isRequired;
    }
}