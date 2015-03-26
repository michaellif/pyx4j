/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-16
 * @author vlads
 */
package com.propertyvista.biz.communication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryCallback;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.biz.communication.NotificationFacade.BatchErrorType;
import com.propertyvista.biz.communication.notifications.NotificationsUtils;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.company.Notification.AlertType;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserCredential;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.CustomerUserCredential;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;
import com.propertyvista.server.common.security.AccessKey;

public class CommunicationFacadeImpl implements CommunicationFacade {

    private static final I18n i18n = I18n.get(CommunicationFacadeImpl.class);

    final static String GENERIC_FAILED_MESSAGE = "Invalid User Account";

    final static String GENERIC_UNAVAIL_MESSAGE = "Mail Service Is Temporary Unavailable. Please Try Again Later.";

    @Override
    public void sendProspectWelcome(LeaseTermTenant tenant) {
        MailMessage m = MessageTemplatesCustomizable.createProspectWelcome(tenant);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendApplicantApplicationInvitation(LeaseTermTenant tenant) {
        sendInvitationEmail(tenant, EmailTemplateType.ApplicationCreatedApplicant);
    }

    @Override
    public void sendCoApplicantApplicationInvitation(LeaseTermTenant tenant) {
        sendInvitationEmail(tenant, EmailTemplateType.ApplicationCreatedCoApplicant);
    }

    @Override
    public void sendGuarantorApplicationInvitation(LeaseTermGuarantor guarantor) {
        sendInvitationEmail(guarantor, EmailTemplateType.ApplicationCreatedGuarantor);
    }

    @Override
    public void sendApplicationDocumentCopy(LeaseApplicationDocument documentId) {
        MailMessage msg = MessageTemplatesCustomizable.createApplcationDocumentEmail(documentId);
        Mail.queue(msg, null, null);
    }

    private static void sendInvitationEmail(LeaseTermParticipant<?> leaseParticipant, EmailTemplateType emailTemplateType) {
        String token = AccessKey.createAccessToken(leaseParticipant.leaseParticipant().customer().user(), CustomerUserCredential.class, 10);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }
        MailMessage m = MessageTemplatesCustomizable.createTenantInvitationEmail(leaseParticipant, emailTemplateType, token);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendApplicationApproved(LeaseTermParticipant<?> participantId) {
        LeaseTermParticipant<?> participant = Persistence.service().retrieve(LeaseTermParticipant.class, participantId.getPrimaryKey());
        MailMessage m = MessageTemplatesCustomizable.createApplicationStatusEmail(participant, EmailTemplateType.ApplicationApproved);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendApplicationDeclined(LeaseTermParticipant<?> participantId) {
        LeaseTermParticipant<?> participant = Persistence.service().retrieve(LeaseTermParticipant.class, participantId.getPrimaryKey());
        MailMessage m = MessageTemplatesCustomizable.createApplicationStatusEmail(participant, EmailTemplateType.ApplicationDeclined);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendTenantInvitation(LeaseTermTenant tenant) {
        CustomerUser user = tenant.leaseParticipant().customer().user();
        Persistence.ensureRetrieve(user, AttachLevel.Attached);

        String token = AccessKey.createAccessToken(user, CustomerUserCredential.class, 10);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }

        EmailTemplateType emailType = EmailTemplateType.TenantInvitation;

        MailMessage m = MessageTemplatesCustomizable.createTenantInvitationEmail(tenant, emailType, token);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendProspectPasswordRetrievalToken(Customer customer) {
        String token = AccessKey.createAccessToken(customer.user(), CustomerUserCredential.class, 1);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }
        MailMessage m = MessageTemplatesCustomizable.createCustomerPasswordResetEmail(EmailTemplateType.PasswordRetrievalProspect, customer.user(), token);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr(GENERIC_UNAVAIL_MESSAGE));
        }
    }

    @Override
    public void sendTenantPasswordRetrievalToken(Customer customer) {
        String token = AccessKey.createAccessToken(customer.user(), CustomerUserCredential.class, 1);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }
        MailMessage m = MessageTemplatesCustomizable.createCustomerPasswordResetEmail(EmailTemplateType.PasswordRetrievalTenant, customer.user(), token);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr(GENERIC_UNAVAIL_MESSAGE));
        }
    }

    @Override
    public void sendCrmPasswordRetrievalToken(CrmUser user) {
        String token = AccessKey.createAccessToken(user, CrmUserCredential.class, 1);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }
        MailMessage m = MessageTemplatesCustomizable.createCrmPasswordResetEmail(user, token);

        CrmUserCredential credential = Persistence.service().retrieve(CrmUserCredential.class, user.getPrimaryKey());
        if (!credential.recoveryEmail().isNull()) {
            m.setTo(credential.recoveryEmail().getValue());
        }

        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr(GENERIC_UNAVAIL_MESSAGE));
        }
    }

    @Override
    public void sendCrmWelcomeEmailAction(CrmUser user) throws UserRuntimeException {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendNewPmcEmail(OnboardingUser user, Pmc pmc) {
        MailMessage m = MessageTemplatesCrmNotification.createNewPmcEmail(user, pmc);
        Mail.queue(m, null, null);
    }

    private static IMailServiceConfigConfiguration getTenantSureConfig() {
        return ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getTenantSureMailServiceConfiguration();
    }

    @Override
    public String sendTenantSurePaymentNotProcessed(Tenant tenant, BigDecimal amount, LogicalDate date, String reason, LogicalDate deadline,
            Class<? extends MailDeliveryCallback> callback) {
//TODO
//
//        Dear {1},
//        Your payment of {1} on {2} has not been processed for the following reason: ..
//                           {Rejected}
//        The premium payment deadline is {today}.
//        To resolve this issue, simply log in to your myCommunity [here] to update in your current payment information.
//
//Sincerely,

        return null;
    }

    @Override
    public String sendTenantSureNoticeOfCancellation(Tenant tenant, LogicalDate gracePeriodEndDate, LogicalDate cancellationDate,
            Class<? extends MailDeliveryCallback> callback) {
        MailMessage m = MessageTemplatesTenantSure.createTenantSureNoticeOfCancellation(tenant, gracePeriodEndDate, cancellationDate);
        if (Mail.queue(m, callback, getTenantSureConfig())) {
            return m.getMailMessageObjectId();
        } else {
            return null;
        }
    }

    @Override
    public String sendTenantSurePaymentsResumed(Tenant tenant, Class<? extends MailDeliveryCallback> callback) {
        MailMessage m = MessageTemplatesTenantSure.createTenantSurePaymentsResumed(tenant);
        if (Mail.queue(m, callback, getTenantSureConfig())) {
            return m.getMailMessageObjectId();
        } else {
            return null;
        }
    }

    @Override
    public String sendTenantSureCCExpiring(Person tenant, String ccLastDigits, LogicalDate ccExpiry, Class<? extends MailDeliveryCallback> callback) {
        MailMessage m = MessageTemplatesTenantSure.createTenantSureCCExpiring(tenant, ccLastDigits, ccExpiry);
        if (Mail.queue(m, callback, getTenantSureConfig())) {
            return m.getMailMessageObjectId();
        } else {
            return null;
        }

    }

    @Override
    public String sendTenantSureRenewal(String tenantEmail, TenantSureInsurancePolicy policy, Class<? extends MailDeliveryCallback> callback) {
        MailMessage m = MessageTemplatesTenantSure.createTenantSureRenewalEmail(policy);
        m.setTo(tenantEmail);
        if (Mail.queue(m, callback, getTenantSureConfig())) {
            return m.getMailMessageObjectId();
        } else {
            return null;
        }
    }

    @Override
    public void sendPaymentRejectedNotification(List<String> targetEmails, PaymentRecord paymentRecord, boolean applyNSF) {
        MailMessage m = MessageTemplatesCrmNotification.createPaymentRejectedNotificationEmail(paymentRecord, applyNSF);
        m.setTo(targetEmails);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendPaymentYardiUnableToRejectNotification(List<String> targetEmails, PaymentRecord paymentRecord, boolean applyNSF, String yardiErrorMessage) {
        MailMessage m = MessageTemplatesCrmNotification.createPostToYardiFailedNotificationEmail(paymentRecord, applyNSF, yardiErrorMessage);
        m.setTo(targetEmails);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendAutoPayReviewRequiredNotification(List<String> targetEmails, List<Lease> leaseIds) {
        MailMessage m = MessageTemplatesCrmNotification.createAutoPayReviewRequiredNotificationEmail(leaseIds);
        m.setTo(targetEmails);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendAutoPayCancelledBySystemNotification(List<String> targetEmails, List<Lease> leaseIds, Map<Lease, List<AutopayAgreement>> canceledAgreements) {
        MailMessage m = MessageTemplatesCrmNotification.createAutoPayCancelledBySystemNotificationEmail(leaseIds, canceledAgreements);
        m.setTo(targetEmails);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendAutoPayCancelledByResidentNotification(List<String> targetEmails, Lease leaseId, List<AutopayAgreement> canceledAgreements) {
        MailMessage m = MessageTemplatesCrmNotification.createAutoPayCancelledByResidentNotificationEmail(leaseId, canceledAgreements);
        m.setTo(targetEmails);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendAutoPayCreatedByResidentNotification(List<String> targetEmails, Lease leaseId, AutopayAgreement createdAgreement) {
        MailMessage m = MessageTemplatesCrmNotification.createAutoPayCreatedByResidentNotificationEmail(leaseId, createdAgreement);
        m.setTo(targetEmails);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendTenantOneTimePaymentSubmitted(PaymentRecord paymentRecord) {
        MailMessage m = MessageTemplatesCustomizable.createTenantOneTimePaymentSubmitted(paymentRecord);
        if (m != null) {
            Mail.queue(m, null, null);
        }
    }

    @Override
    public void sendTenantPaymentRejected(PaymentRecord paymentRecord, boolean applyNSF) {
        MailMessage m = MessageTemplatesCustomizable.createTenantPaymenttRejected(paymentRecord, applyNSF);
        if (m != null) {
            Mail.queue(m, null, null);
        }
    }

    @Override
    public void sendTenantPaymentCleared(PaymentRecord paymentRecord) {
        MailMessage m = MessageTemplatesCustomizable.createTenantPaymentCleared(paymentRecord);
        if (m != null) {
            Mail.queue(m, null, null);
        }
    }

    @Override
    public void sendTenantAutoPaySetupCompleted(AutopayAgreement autopayAgreement) {
        MailMessage m = MessageTemplatesCustomizable.createTenantAutoPaySetupCompleted(autopayAgreement);
        if (m != null) {
            Mail.queue(m, null, null);
        }
    }

    @Override
    public void sendTenantAutoPayChanges(AutopayAgreement autopayAgreement) {
        MailMessage m = MessageTemplatesCustomizable.createTenantAutoPayChanges(autopayAgreement);
        if (m != null) {
            Mail.queue(m, null, null);
        }
    }

    @Override
    public void sendTenantAutoPayCancellation(AutopayAgreement autopayAgreement) {
        MailMessage m = MessageTemplatesCustomizable.createTenantAutoPayCancellation(autopayAgreement);
        if (m != null) {
            Mail.queue(m, null, null);
        }
    }

    @Override
    public void sendOnlinePaymentSetupCompletedEmail(String userName, String userEmail) {
        MailMessage m = MessageTemplatesCrmNotification.createOnlinePaymentSetupCompletedEmail(userName);
        m.setTo(userEmail);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendBillingAlertNotification(List<String> targetEmails, List<Lease> leaseIds, Map<Lease, List<String>> billingAlerts) {
        MailMessage m = MessageTemplatesCrmNotification.createBillingAlertNotificationEmail(leaseIds, billingAlerts);
        m.setTo(targetEmails);
        Mail.queue(m, null, null);
    }

    @Override
    public void sendMaintenanceRequestCreatedPMC(MaintenanceRequest request) {
        for (Employee employee : NotificationsUtils.getNotificationTraget(request.building(), Notification.AlertType.MaintenanceRequest)) {
            sendMaintenanceRequestEmail(AddresseUtils.getCompleteEmail(employee.name().getStringView(), employee.email().getValue()),
                    EmailTemplateType.MaintenanceRequestCreatedPMC, request);
        }
    }

    @Override
    public MailMessage sendMaintenanceRequestCreatedTenant(MaintenanceRequest request) {
        return sendMaintenanceRequestEmail(TenantAccess.getActiveEmail(request.reporter()), EmailTemplateType.MaintenanceRequestCreatedTenant, request);
    }

    @Override
    public MailMessage sendMaintenanceRequestUpdated(MaintenanceRequest request) {
        return sendMaintenanceRequestEmail(TenantAccess.getActiveEmail(request.reporter()), EmailTemplateType.MaintenanceRequestUpdated, request);
    }

    @Override
    public MailMessage sendMaintenanceRequestCompleted(MaintenanceRequest request) {
        return sendMaintenanceRequestEmail(TenantAccess.getActiveEmail(request.reporter()), EmailTemplateType.MaintenanceRequestCompleted, request);
    }

    @Override
    public MailMessage sendMaintenanceRequestCancelled(MaintenanceRequest request) {
        return sendMaintenanceRequestEmail(TenantAccess.getActiveEmail(request.reporter()), EmailTemplateType.MaintenanceRequestCancelled, request);
    }

    @Override
    public MailMessage sendMaintenanceRequestEntryNotice(MaintenanceRequest request, Class<? extends MailDeliveryCallback> callback) {
        return sendMaintenanceRequestEmail(TenantAccess.getActiveEmail(request.reporter()), EmailTemplateType.MaintenanceRequestEntryNotice, request, callback);
    }

    private MailMessage sendMaintenanceRequestEmail(String sendTo, EmailTemplateType emailType, MaintenanceRequest request) {
        return sendMaintenanceRequestEmail(sendTo, emailType, request, null);
    }

    private MailMessage sendMaintenanceRequestEmail(String sendTo, EmailTemplateType emailType, MaintenanceRequest request,
            Class<? extends MailDeliveryCallback> callback) {
        if (sendTo == null) {
            return null;
        } else {
            MailMessage m = MessageTemplatesCustomizable.createMaintenanceRequestEmail(emailType, request);
            m.setTo(sendTo);
            Mail.queue(m, callback, null);
            return m;
        }
    }

    @Override
    public void sendYardiConfigurationNotification(List<String> sendTo, String message) {
        MailMessage m = MessageTemplatesCrmNotification.createYardiConfigurationNotificationEmail(message);
        m.setTo(sendTo);
        Mail.queueUofW(m, null, null);
    }

    @Override
    public void sendUnableToPostPaymentBatchNotification(List<String> sendTo, BatchErrorType batchErrorType, String batchId, String errorMessage) {
        MailMessage m = MessageTemplatesCrmNotification.sendUnableToPostPaymentBatchNotification(batchErrorType, batchId, errorMessage);
        m.setTo(sendTo);
        Mail.queueUofW(m, null, null);
    }

    @Override
    public void sendDirectDebitAccountChangedNote(LeaseTermTenant tenant) {
        MailMessage m = MessageTemplatesCustomizable.createDirectDebitAccountChangesEmail(tenant);
        if (m != null) {
            Mail.queue(m, null, null);
        }
    }

    @Override
    public void sendLeaseApplicationNotification(List<String> targetEmails, Lease lease, AlertType alertType) {
        MailMessage m = MessageTemplatesCrmNotification.createLeaseApplicationNotificationEmail(lease, alertType);
        m.setTo(targetEmails);
        Mail.queue(m, null, null);
    }
}
