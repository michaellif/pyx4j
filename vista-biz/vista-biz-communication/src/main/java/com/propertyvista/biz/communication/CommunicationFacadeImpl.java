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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.admin.domain.security.AdminUserCredential;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.security.AdminUser;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.server.domain.security.CustomerUserCredential;

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
    public void sendApplicantApplicationInvitation(Tenant tenant) {
        sendInvitationEmail(tenant, EmailTemplateType.ApplicationCreatedApplicant);
    }

    @Override
    public void sendCoApplicantApplicationInvitation(Tenant tenant) {
        sendInvitationEmail(tenant, EmailTemplateType.ApplicationCreatedCoApplicant);
    }

    @Override
    public void sendGuarantorApplicationInvitation(Guarantor guarantor) {
        sendInvitationEmail(guarantor, EmailTemplateType.ApplicationCreatedGuarantor);
    }

    private static void sendInvitationEmail(LeaseParticipant leaseParticipant, EmailTemplateType emailTemplateType) {
        if (disabled) {
            return;
        }
        String token = AccessKey.createAccessToken(leaseParticipant.customer().user(), CustomerUserCredential.class, 10);
        if (token == null) {
            throw new UserRuntimeException(GENERIC_FAILED_MESSAGE);
        }
        MailMessage m = MessageTemplates.createTenantInvitationEmail(leaseParticipant, emailTemplateType, token);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    @Override
    public void sendApplicationStatus(Tenant tenantId) {
        if (disabled) {
            return;
        }
        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());
        Persistence.service().retrieve(tenant.leaseV());
        EmailTemplateType emailType;
        switch (tenant.leaseV().holder().leaseApplication().status().getValue()) {
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
    public void sendTenantInvitation(Tenant tenant) {
        if (disabled) {
            return;
        }
        CustomerUser user = tenant.customer().user();
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
    public void sendAdminPasswordRetrievalToken(AdminUser user) {
        if (disabled) {
            return;
        }
        String token = AccessKey.createAccessToken(user, AdminUserCredential.class, 1);
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
        MailMessage m = MessageTemplates.createOnboardingUserPasswordResetEmail(user, token, onboardingSystemBaseUrl);
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
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

}
