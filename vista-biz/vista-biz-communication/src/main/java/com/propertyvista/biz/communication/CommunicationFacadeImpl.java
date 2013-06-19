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
import java.util.concurrent.Callable;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.OperationsUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.operations.domain.security.OperationsUserCredential;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.server.domain.security.CustomerUserCredential;
import com.propertyvista.server.jobs.TaskRunner;

public class CommunicationFacadeImpl implements CommunicationFacade {

    private static final I18n i18n = I18n.get(CommunicationFacadeImpl.class);

    private static boolean disabled = false;

    final static String GENERIC_FAILED_MESSAGE = "Invalid User Account";

    @Override
    public void setDisabled(boolean disabled) {
        CommunicationFacadeImpl.disabled = disabled;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
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
        if (disabled) {
            return;
        }
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
        if (disabled) {
            return;
        }
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
        if (disabled) {
            return;
        }
        CustomerUser user = tenant.leaseParticipant().customer().user();
        if (user.isValueDetached()) {
            Persistence.service().retrieve(user);
        }

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
        if (disabled) {
            return;
        }
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
        if (disabled) {
            return;
        }
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
        if (disabled) {
            return;
        }
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
    public void sendOnboardingPasswordRetrievalToken(OnboardingUser user, String onboardingSystemBaseUrl) {
        if (disabled) {
            return;
        }
        String token = AccessKey.createAccessToken(user, OnboardingUserCredential.class, 1);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }
        final MailMessage m = MessageTemplates.createOnboardingUserPasswordResetEmail(user, token, onboardingSystemBaseUrl);

        final OnboardingUserCredential credential = Persistence.service().retrieve(OnboardingUserCredential.class, user.getPrimaryKey());
        if (!credential.pmc().isNull() && (credential.pmc().status().getValue() != PmcStatus.Created)) {
            TaskRunner.runInTargetNamespace(credential.pmc().namespace().getValue(), new Callable<Void>() {
                @Override
                public Void call() {
                    CrmUserCredential crmCredential = Persistence.service().retrieve(CrmUserCredential.class, credential.crmUser().getValue());
                    if (!crmCredential.recoveryEmail().isNull()) {
                        m.setTo(crmCredential.recoveryEmail().getValue());
                    }
                    return null;
                }
            });

        }

        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    @Override
    public void sendCrmPasswordRetrievalToken(CrmUser user) {
        if (disabled) {
            return;
        }
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
        if (disabled) {
            return;
        }

        final MailMessage m = MessageTemplates.createNewPmcEmail(user, pmc);

        m.setTo(user.email().getValue());

        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    private static IMailServiceConfigConfiguration getTenantSureConfig() {
        return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureMailServiceConfigConfiguration();
    }

    @Override
    public void sendTenantSurePaymentNotProcessedEmail(String tenantEmail, LogicalDate gracePeriodEndDate, LogicalDate cancellationDate) {
        if (disabled) {
            return;
        }

        final MailMessage m = MessageTemplates.createTenantSurePaymentNotProcessedEmail(gracePeriodEndDate, cancellationDate);

        m.setTo(tenantEmail);

        if (MailDeliveryStatus.Success != Mail.send(m, getTenantSureConfig())) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }

    }

    @Override
    public void sendTenantSurePaymentsResumedEmail(String tenantEmail) {
        if (disabled) {
            return;
        }

        final MailMessage m = MessageTemplates.createTenantSurePaymentsResumedEmail();

        m.setTo(tenantEmail);

        if (MailDeliveryStatus.Success != Mail.send(m, getTenantSureConfig())) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }

    }

    @Override
    public void sendOnlinePaymentSetupCompletedEmail(String userName, String userEmail) {
        if (disabled) {
            return;
        }

        final MailMessage m = MessageTemplates.createOnlinePaymentSetupCompletedEmail(userName);

        m.setTo(userEmail);

        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    @Override
    public void sendPaymentReversalWithNsfNotification(List<String> targetEmails, YardiReceiptReversal paymentReversal) {
        if (disabled) {
            return;
        }
        final MailMessage m = MessageTemplates.createNsfNotificationEmail(paymentReversal);

        m.setTo(targetEmails);

        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    @Override
    public void sendPapSuspensionNotification(List<String> targetEmails, Lease leaseId) {
        if (disabled) {
            return;
        }
        final MailMessage m = MessageTemplates.createPapSuspentionNotificationEmail(leaseId);
        m.setTo(targetEmails);
    }

    @Override
    public void sendMaintenanceRequestEmail(String sendTo, String userName, MaintenanceRequest request, boolean isNewRequest, boolean toAdmin) {
        if (!disabled) {
            MailMessage m = MessageTemplates.createMaintenanceRequestEmail(userName, request, isNewRequest, toAdmin);
            m.setTo(sendTo);
            Mail.send(m);
        }
    }
}
