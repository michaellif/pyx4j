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
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.biz.communication.notifications.NotificationsUtils;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.operations.domain.security.OperationsUser;
import com.propertyvista.operations.domain.security.OperationsUserCredential;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.server.domain.security.CustomerUserCredential;

public class CommunicationFacadeImpl implements CommunicationFacade {

    private static final I18n i18n = I18n.get(CommunicationFacadeImpl.class);

    final static String GENERIC_FAILED_MESSAGE = "Invalid User Account";

    @Override
    public void sendProspectWelcome(LeaseTermTenant tenant) {
        MailMessage m = MessageTemplates.createProspectWelcome(tenant);
        Mail.send(m);
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

    private static void sendInvitationEmail(LeaseTermParticipant leaseParticipant, EmailTemplateType emailTemplateType) {
        String token = AccessKey.createAccessToken(leaseParticipant.leaseParticipant().customer().user(), CustomerUserCredential.class, 10);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }
        MailMessage m = MessageTemplates.createTenantInvitationEmail(leaseParticipant, emailTemplateType, token);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    @Override
    public void sendApplicationStatus(LeaseTermTenant tenantId) {
        LeaseTermTenant tenant = Persistence.service().retrieve(LeaseTermTenant.class, tenantId.getPrimaryKey());
        Persistence.service().retrieve(tenant.leaseTermV());
        Persistence.service().retrieve(tenant.leaseTermV().holder().lease());

        EmailTemplateType emailType;
        switch (tenant.leaseTermV().holder().lease().leaseApplication().status().getValue()) {
        case Approved:
            emailType = EmailTemplateType.ApplicationApproved;
            break;
        case Declined:
            emailType = EmailTemplateType.ApplicationDeclined;
            break;
        default:
            throw new IllegalArgumentException();
        }
        MailMessage m = MessageTemplates.createApplicationStatusEmail(tenant, emailType);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }

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

        MailMessage m = MessageTemplates.createTenantInvitationEmail(tenant, emailType, token);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException("Mail delivery failed: " + user.email().getValue());
        }
    }

    @Override
    public void sendProspectPasswordRetrievalToken(Customer customer) {
        String token = AccessKey.createAccessToken(customer.user(), CustomerUserCredential.class, 1);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }
        MailMessage m = MessageTemplates.createCustomerPasswordResetEmail(EmailTemplateType.PasswordRetrievalProspect, customer.user(), token);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    @Override
    public void sendTenantPasswordRetrievalToken(Customer customer) {
        String token = AccessKey.createAccessToken(customer.user(), CustomerUserCredential.class, 1);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }
        MailMessage m = MessageTemplates.createCustomerPasswordResetEmail(EmailTemplateType.PasswordRetrievalTenant, customer.user(), token);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    @Override
    public void sendAdminPasswordRetrievalToken(OperationsUser user) {
        String token = AccessKey.createAccessToken(user, OperationsUserCredential.class, 1);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }
        MailMessage m = MessageTemplates.createAdminPasswordResetEmail(user, token);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    @Override
    public void sendCrmPasswordRetrievalToken(CrmUser user) {
        String token = AccessKey.createAccessToken(user, CrmUserCredential.class, 1);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }
        MailMessage m = MessageTemplates.createCrmPasswordResetEmail(user, token);

        CrmUserCredential credential = Persistence.service().retrieve(CrmUserCredential.class, user.getPrimaryKey());
        if (!credential.recoveryEmail().isNull()) {
            m.setTo(credential.recoveryEmail().getValue());
        }

        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    @Override
    public void sendNewPmcEmail(OnboardingUser user, Pmc pmc) {
        MailMessage m = MessageTemplates.createNewPmcEmail(user, pmc);

        m.setTo(user.email().getValue());

        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    private static IMailServiceConfigConfiguration getTenantSureConfig() {
        return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureMailServiceConfiguration();
    }

    @Override
    public void sendTenantSurePaymentNotProcessedEmail(String tenantEmail, LogicalDate gracePeriodEndDate, LogicalDate cancellationDate) {
        MailMessage m = MessageTemplates.createTenantSurePaymentNotProcessedEmail(gracePeriodEndDate, cancellationDate);

        m.setTo(tenantEmail);

        if (MailDeliveryStatus.Success != Mail.send(m, getTenantSureConfig())) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }

    }

    @Override
    public void sendTenantSurePaymentsResumedEmail(String tenantEmail) {
        MailMessage m = MessageTemplates.createTenantSurePaymentsResumedEmail();

        m.setTo(tenantEmail);

        if (MailDeliveryStatus.Success != Mail.send(m, getTenantSureConfig())) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }

    }

    @Override
    public void sendOnlinePaymentSetupCompletedEmail(String userName, String userEmail) {
        MailMessage m = MessageTemplates.createOnlinePaymentSetupCompletedEmail(userName);

        m.setTo(userEmail);

        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    @Override
    public void sendPaymentRejectedNotification(List<String> targetEmails, PaymentRecord paymentRecord, boolean applyNSF) {
        MailMessage m = MessageTemplates.createPaymentRejectedNotificationEmail(paymentRecord, applyNSF);
        m.setTo(targetEmails);
        Mail.send(m);
    }

    @Override
    public void sendPaymentYardiUnableToRejectNotification(List<String> targetEmails, PaymentRecord paymentRecord, boolean applyNSF, String yardiErrorMessage) {
        MailMessage m = MessageTemplates.createPostToYardiFailedNotificationEmail(paymentRecord, applyNSF, yardiErrorMessage);
        m.setTo(targetEmails);
        Mail.send(m);
    }

    @Override
    public void sendAutoPayReviewRequiredNotification(List<String> targetEmails, List<Lease> leaseIds) {
        MailMessage m = MessageTemplates.createAutoPayReviewRequiredNotificationEmail(leaseIds);
        m.setTo(targetEmails);
        Mail.send(m);
    }

    @Override
    public void sendAutoPayCancelledBySystemNotification(List<String> targetEmails, List<Lease> leaseIds, Map<Lease, List<AutopayAgreement>> canceledAgreements) {
        MailMessage m = MessageTemplates.createAutoPayCancelledBySystemNotificationEmail(leaseIds, canceledAgreements);
        m.setTo(targetEmails);
        Mail.send(m);
    }

    @Override
    public void sendAutoPayCancelledByResidentNotification(List<String> targetEmails, Lease leaseId, List<AutopayAgreement> canceledAgreements) {
        MailMessage m = MessageTemplates.createAutoPayCancelledByResidentNotificationEmail(leaseId, canceledAgreements);
        m.setTo(targetEmails);
        Mail.send(m);
    }

    @Override
    public void sendTenantOneTimePaymentSubmitted(PaymentRecord paymentRecord) {
        MailMessage m = MessageTemplates.createTenantOneTimePaymentSubmitted(paymentRecord);
        Mail.send(m);
    }

    @Override
    public void sendTenantPaymenttRejected(PaymentRecord paymentRecord, boolean applyNSF) {
        MailMessage m = MessageTemplates.createTenantPaymenttRejected(paymentRecord, applyNSF);
        Mail.send(m);
    }

    @Override
    public void sendTenantPaymentCleared(PaymentRecord paymentRecord) {
        MailMessage m = MessageTemplates.createTenantPaymentCleared(paymentRecord);
        Mail.send(m);
    }

    @Override
    public void sendTenantAutopaySetupCompleted(AutopayAgreement autopayAgreement) {
        MailMessage m = MessageTemplates.createTenantAutopaySetupCompleted(autopayAgreement);
        Mail.send(m);
    }

    @Override
    public void sendMaintenanceRequestCreatedPMC(MaintenanceRequest request) {
        for (Employee employee : NotificationsUtils.getNotificationTraget(request.building(), Notification.NotificationType.MaintenanceRequest)) {
            sendMaintenanceRequestEmail(employee.email().getValue(), EmailTemplateType.MaintenanceRequestCreatedPMC, request);
        }
    }

    @Override
    public void sendMaintenanceRequestCreatedTenant(MaintenanceRequest request) {
        String sendTo = request.reporter().customer().person().email().getValue();
        sendMaintenanceRequestEmail(sendTo, EmailTemplateType.MaintenanceRequestCreatedTenant, request);
    }

    @Override
    public void sendMaintenanceRequestUpdated(MaintenanceRequest request) {
        String sendTo = request.reporter().customer().person().email().getValue();
        sendMaintenanceRequestEmail(sendTo, EmailTemplateType.MaintenanceRequestUpdated, request);
    }

    @Override
    public void sendMaintenanceRequestCompleted(MaintenanceRequest request) {
        String sendTo = request.reporter().customer().person().email().getValue();
        sendMaintenanceRequestEmail(sendTo, EmailTemplateType.MaintenanceRequestCompleted, request);
    }

    @Override
    public void sendMaintenanceRequestCancelled(MaintenanceRequest request) {
        String sendTo = request.reporter().customer().person().email().getValue();
        sendMaintenanceRequestEmail(sendTo, EmailTemplateType.MaintenanceRequestCancelled, request);
    }

    @Override
    public MailMessage sendMaintenanceRequestEntryNotice(MaintenanceRequest request) {
        String sendTo = request.reporter().customer().person().email().getValue();
        return sendMaintenanceRequestEmail(sendTo, EmailTemplateType.MaintenanceRequestEntryNotice, request);
    }

    private MailMessage sendMaintenanceRequestEmail(String sendTo, EmailTemplateType emailType, MaintenanceRequest request) {
        if (sendTo == null) {
            return null;
        }
        MailMessage m = MessageTemplates.createMaintenanceRequestEmail(emailType, request);
        m.setTo(sendTo);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
        return m;
    }

}
