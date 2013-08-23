/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.communication.mail.template.EmailTemplateManager;
import com.propertyvista.biz.communication.mail.template.EmailTemplateRootObjectLoader;
import com.propertyvista.biz.communication.mail.template.model.EmailTemplateContext;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestAdminT;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class MessageTemplates {

    private final static I18n i18n = I18n.get(MessageTemplates.class);

    private final static Logger log = LoggerFactory.getLogger(MessageTemplates.class);

    private static String getSender() {
        return ServerSideConfiguration.instance().getApplicationEmailSender();
    }

    /**
     * Warning: can return <code>null</code> if the policy is not found.
     * 
     * @param type
     * @param building
     * @return
     */
    private static EmailTemplate getEmailTemplate(EmailTemplateType type, PolicyNode policyNode) {
        EmailTemplatesPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode, EmailTemplatesPolicy.class).duplicate();
        return fetchEmailTemplate(type, policy);
    }

    private static EmailTemplate fetchEmailTemplate(EmailTemplateType type, EmailTemplatesPolicy policy) {
        for (EmailTemplate emt : policy.templates()) {
            if (emt.type().getValue() == type)
                return emt;
        }
        return null;
    }

    public static MailMessage createApplicationStatusEmail(LeaseTermTenant tenantInLease, EmailTemplateType type) {
        // get building policy node
        Persistence.service().retrieve(tenantInLease.leaseTermV());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit().building());
        EmailTemplate emailTemplate = getEmailTemplate(type, tenantInLease.leaseTermV().holder().lease().unit().building());

        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        // populate context properties required by template type
        context.leaseParticipant().set(tenantInLease);

        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(type)) {
            // ObjectLoader will load required T-Objects using context data
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }
        MailMessage email = new MailMessage();
        CustomerUser user = tenantInLease.leaseParticipant().customer().user();
        if (user.isValueDetached()) {
            Persistence.service().retrieve(tenantInLease.leaseParticipant().customer().user());
        }
        email.setTo(user.email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);

        return email;
    }

    public static MailMessage createTenantInvitationEmail(LeaseTermParticipant leaseParticipant, EmailTemplateType emailType, String token) {
        Persistence.service().retrieve(leaseParticipant.leaseTermV());
        Persistence.service().retrieve(leaseParticipant.leaseTermV().holder().lease());
        Persistence.service().retrieve(leaseParticipant.leaseTermV().holder().lease().unit());
        Persistence.service().retrieve(leaseParticipant.leaseTermV().holder().lease().unit().building());

        Persistence.service().retrieve(leaseParticipant.leaseParticipant().customer().user());

        EmailTemplate emailTemplate = getEmailTemplate(emailType, leaseParticipant.leaseTermV().holder().lease().unit().building());

        // create required data context
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.user().set(leaseParticipant.leaseParticipant().customer().user());
        context.lease().set(leaseParticipant.leaseTermV().holder().lease());
        context.leaseParticipant().set(leaseParticipant);
        context.accessToken().setValue(token);
        // load data objects for template variable lookup
        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(emailType)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }
        MailMessage email = new MailMessage();
        email.setTo(leaseParticipant.leaseParticipant().customer().user().email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);
        return email;
    }

    public static MailMessage createCustomerPasswordResetEmail(EmailTemplateType templateType, AbstractUser user, String token) {
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.accessToken().setValue(token);
        context.user().set(user);

        ArrayList<IEntity> data = new ArrayList<IEntity>();
        EmailTemplate emailTemplate = null;

        // get company policy node
        EntityQueryCriteria<OrganizationPoliciesNode> nodeCrit = EntityQueryCriteria.create(OrganizationPoliciesNode.class);
        PolicyNode policyNode = Persistence.service().retrieve(nodeCrit);
        // get building policy node form the first available TenantInLease entry
        EntityQueryCriteria<LeaseTermTenant> tilCrit = EntityQueryCriteria.create(LeaseTermTenant.class);
        tilCrit.add(PropertyCriterion.eq(tilCrit.proto().leaseParticipant().customer().user(), user));

        // TODO Fix this!
        LeaseTermTenant til = Persistence.service().retrieve(tilCrit);
        if (til != null) {
            Persistence.service().retrieve(til.leaseTermV());
            Persistence.service().retrieve(til.leaseTermV().holder().lease());
            Persistence.service().retrieve(til.leaseTermV().holder().lease().unit());
            Persistence.service().retrieve(til.leaseTermV().holder().lease().unit().building());
            if (!til.leaseTermV().holder().lease().unit().building().isNull()) {
                policyNode = til.leaseTermV().holder().lease().unit().building();
            }
        }

        emailTemplate = getEmailTemplate(templateType, policyNode);
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(templateType)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }

        MailMessage email = new MailMessage();
        email.setTo(user.email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);

        return email;
    }

    public static MailMessage createCrmPasswordResetEmail(AbstractUser user, String token) {
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.accessToken().setValue(token);
        context.user().set(user);

        ArrayList<IEntity> data = new ArrayList<IEntity>();
        // get company policy node
        EntityQueryCriteria<OrganizationPoliciesNode> nodeCrit = EntityQueryCriteria.create(OrganizationPoliciesNode.class);
        PolicyNode policyNode = Persistence.service().retrieve(nodeCrit);

        EmailTemplate emailTemplate = getEmailTemplate(EmailTemplateType.PasswordRetrievalCrm, policyNode);
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(EmailTemplateType.PasswordRetrievalCrm)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }

        MailMessage email = new MailMessage();
        email.setTo(user.email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);

        return email;
    }

    public static MailMessage createAdminPasswordResetEmail(AbstractUser user, String token) {
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.accessToken().setValue(token);
        context.user().set(user);

        ArrayList<IEntity> data = new ArrayList<IEntity>();

        EmailTemplate emailTemplate = emailTemplatePasswordRetrievalAdmin();
        PasswordRequestAdminT pwdReqT = EntityFactory.create(PasswordRequestAdminT.class);
        pwdReqT.RequestorName().set(user.name());
        pwdReqT.PasswordResetUrl().setValue(
                AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.operations, true), true,
                        OperationsSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, token));
        data.add(pwdReqT);

        MailMessage email = new MailMessage();
        email.setTo(user.email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);

        return email;
    }

    public static MailMessage createNewPmcEmail(OnboardingUser user, Pmc pmc) {

        EmailTemplate emailTemplate = emailTemplateNewPmc(user, pmc);

        ArrayList<IEntity> data = new ArrayList<IEntity>();

        MailMessage email = new MailMessage();
        email.setTo(user.email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);

        return email;
    }

    private static String getTenantSureSender() {
        return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureEmailSender();
    }

    public static MailMessage createTenantSurePaymentNotProcessedEmail(LogicalDate gracePeriodEndDate, LogicalDate cancellationDate) {

        EmailTemplate emailTemplate = emailTemplateTenantSurePaymentNotProcessed(gracePeriodEndDate, cancellationDate);
        MailMessage email = new MailMessage();
        email.setSender(getTenantSureSender());
        buildSimpleEmail(email, emailTemplate);

        return email;
    }

    public static MailMessage createTenantSurePaymentsResumedEmail() {

        EmailTemplate emailTemplate = emailTemplateTenantSurePaymentsResumed();
        MailMessage email = new MailMessage();
        email.setSender(getTenantSureSender());
        buildSimpleEmail(email, emailTemplate);

        return email;
    }

    public static MailMessage createOnlinePaymentSetupCompletedEmail(String userName) {
        EmailTemplate emailTemplate = emailOnlinePaymentSetupCompleted(userName);
        MailMessage email = new MailMessage();
        email.setSender(getSender());
        buildSimpleEmail(email, emailTemplate);

        return email;
    }

    public static MailMessage createNsfNotificationEmail(PaymentRecord paymentRecord) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());

        String crmUrl = VistaDeployment.getBaseApplicationURL(VistaDeployment.getCurrentPmc(), VistaApplication.crm, true);
        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, paymentRecord.billingAccount().getPrimaryKey());

        Persistence.service().retrieve(billingAccount.lease());
        String leaseId = billingAccount.lease().leaseId().getValue();
        String leaseUrl = AppPlaceInfo.absoluteUrl(crmUrl, true, new CrmSiteMap.Tenants.Lease().formViewerPlace(billingAccount.lease().getPrimaryKey()));

        Persistence.ensureRetrieve(paymentRecord.leaseTermParticipant(), AttachLevel.Attached);

        String tenantUrl = AppPlaceInfo.absoluteUrl(crmUrl, true,
                new CrmSiteMap.Tenants.Tenant().formViewerPlace(paymentRecord.leaseTermParticipant().leaseParticipant().getPrimaryKey()));
        String tenantId = paymentRecord.leaseTermParticipant().leaseParticipant().participantId().getStringView();

        String tenantName = paymentRecord.leaseTermParticipant().leaseParticipant().customer().person().name().getStringView();

        String unitId = billingAccount.lease().unit().info().number().getValue();
        Persistence.service().retrieve(billingAccount.lease().unit().building(), AttachLevel.ToStringMembers);
        String buildingId = billingAccount.lease().unit().building().getStringView();

        String paymentRecordUrl = AppPlaceInfo.absoluteUrl(crmUrl, true, new CrmSiteMap.Finance.Payment().formViewerPlace(paymentRecord.getPrimaryKey()));
        String paymentId = paymentRecord.getPrimaryKey().toString();
        String paymentAmount = i18n.tr("${0,number,#,##0.00}", paymentRecord.amount().getValue());

        String rejectionReason = i18n.tr("UNKNOWN");
        if (CommonsStringUtils.isStringSet(paymentRecord.transactionErrorMessage().getValue())) {
            rejectionReason = paymentRecord.transactionErrorMessage().getValue();
        }

        email.setSubject(i18n.tr("NSF Alert for Building {0}, Unit {1}, Lease {2}, Tenant {3} {4}", buildingId, unitId, leaseId, tenantId, tenantName));
        String emailBody = "";
        try {
            emailBody = IOUtils.getTextResource("email/nsf-notification.html");
        } catch (IOException e) {
            throw new Error("Failed to load email template for nsf notifications", e);
        }
        //@formatter:off
        emailBody = emailBody
                .replace("${buildingId}", buildingId)
                .replace("${unitId}", unitId)
                .replace("${leaseId}", leaseId)
                .replace("${leaseUrl}", leaseUrl)
                .replace("${tenantId}", tenantId)
                .replace("${tenantName}", tenantName)
                .replace("${tenantUrl}", tenantUrl)
                .replace("${paymentId}", paymentId)
                .replace("${paymentAmount}", paymentAmount)
                .replace("${paymentUrl}", paymentRecordUrl)
                .replace("${rejectionReason}", rejectionReason);
        //@formatter:on
        email.setHtmlBody(wrapAdminHtml(emailBody));

        return email;
    }

    public static MailMessage createPapSuspentionNotificationEmail(Lease leaseId) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());

        String emailBody = "";
        try {
            emailBody = IOUtils.getTextResource("email/pap-suspension-notification.html");
        } catch (IOException e) {
            throw new Error("Failed to load email template for pap suspension notifications", e);
        }

        String crmUrl = VistaDeployment.getBaseApplicationURL(VistaDeployment.getCurrentPmc(), VistaApplication.crm, true);
        String leaseUrl = AppPlaceInfo.absoluteUrl(crmUrl, true, new CrmSiteMap.Tenants.Lease().formViewerPlace(leaseId.getPrimaryKey()));
        emailBody = emailBody.replace("${leaseUrl}", leaseUrl);
        String leaseStringView = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey(), AttachLevel.ToStringMembers).getStringView();
        email.setSubject(i18n.tr("PAP suspension alert for lease {0}", leaseStringView));
        email.setHtmlBody(wrapAdminHtml(emailBody));
        return email;
    }

    public static MailMessage createMaintenanceRequestEmail(EmailTemplateType emailType, MaintenanceRequest request) {
        EmailTemplate emailTemplate = getEmailTemplate(emailType, request.building());

        // create required data context
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.maintenanceRequest().set(request);

        // load data objects for template variable lookup
        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(emailType)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }
        MailMessage email = new MailMessage();
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);
        return email;
    }

    private static String bodyRaw;

    public static String getEmailHTMLBody() {
        if (bodyRaw == null) {
            try {
                bodyRaw = IOUtils.getTextResource("email/template-basic-body.html");
            } catch (IOException e) {
                throw new Error("Unable to load template html wrapper resource", e);
            }
        }
        return bodyRaw;
    }

    private static void buildEmail(MailMessage email, EmailTemplate emailTemplate, Collection<IEntity> data) {
        email.setSubject(EmailTemplateManager.parseTemplate(emailTemplate.subject().getValue(), data));

        Object contentHtml = EmailTemplateManager.parseTemplate(emailTemplate.content().getValue(), data);

        Object headerHtml;
        Object footerHtml;

        if (emailTemplate.useHeader().isBooleanTrue()) {
            headerHtml = EmailTemplateManager.parseTemplate(emailTemplate.policy().header().getValue(), data);
        } else {
            headerHtml = "";
        }

        if (emailTemplate.useHeader().isBooleanTrue()) {
            footerHtml = EmailTemplateManager.parseTemplate(emailTemplate.policy().footer().getValue(), data);
        } else {
            footerHtml = "";
        }

        email.setHtmlBody(SimpleMessageFormat.format(getEmailHTMLBody(),//@formatter:off
                headerHtml,
                contentHtml,
                footerHtml
            ));//@formatter:on
    }

    private static void buildSimpleEmail(MailMessage email, EmailTemplate emailTemplate) {
        email.setSubject(emailTemplate.subject().getValue());
        email.setHtmlBody(emailTemplate.content().getValue());
    }

    private static String wrapAdminHtml(String text) {
        try {
            String html = IOUtils.getTextResource("email/template-admin.html");
            return html.replace("{MESSAGE}", text);
        } catch (IOException e) {
            log.error("template error", e);
            return text;
        }
    }

    private static String wrapTenantSureHtml(String text) {
        try {
            String html = IOUtils.getTextResource("email/template-tenantsure.html");
            return html.replace("{MESSAGE}", text);
        } catch (IOException e) {
            log.error("template error", e);
            return text;
        }
    }

    private static EmailTemplate emailTemplatePasswordRetrievalAdmin() {
        PasswordRequestAdminT pwdReqT = EntityFactory.create(PasswordRequestAdminT.class);
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.subject().setValue(i18n.tr("New Password Retrieval"));
        template.content().setValue(wrapAdminHtml(i18n.tr(//@formatter:off
                "Dear {0},<br/>\n" +
                "This email was sent to you in response to your request to modify your Property Vista Support Administration account password.<br/>\n" +
                "Click the link below to go to the Property Vista Operations site and create new password for your account:<br/>\n" +
                "    <a href=\"{1}\">Change Your Password</a>",
                EmailTemplateManager.getVarname(pwdReqT.RequestorName()),
                EmailTemplateManager.getVarname(pwdReqT.PasswordResetUrl())
        )));//@formatter:on
        return template;
    }

    private static EmailTemplate emailTemplateNewPmc(OnboardingUser user, Pmc pmc) {
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.subject().setValue(i18n.tr("New PMC Created"));
        try {
            String body = IOUtils.getTextResource("email/new-pmc.html");
            body = body.replace("${ownerName}", user.firstName().getValue());
            body = body.replace("${crmLink}", VistaDeployment.getBaseApplicationURL(pmc, VistaApplication.crm, true));
            body = body.replace("${portalLink}", VistaDeployment.getBaseApplicationURL(pmc, VistaApplication.residentPortal, true));

            // TODO i18n body
            template.content().setValue(wrapAdminHtml(body));

            return template;

        } catch (IOException e) {
            throw new UserRuntimeException("Unable to send invitation email");
        }
    }

    private static String passwordResetUrl(String onboardingSystemBaseUrl, String authToken) {
        StringBuilder resetUrl = new StringBuilder();
        resetUrl.append(onboardingSystemBaseUrl);
        if (!onboardingSystemBaseUrl.contains("?")) {
            resetUrl.append("?");
        } else {
            resetUrl.append("&");
        }
        resetUrl.append(AuthenticationService.AUTH_TOKEN_ARG).append("=").append(authToken);

        return resetUrl.toString();
    }

    private static EmailTemplate emailTemplateTenantSurePaymentNotProcessed(LogicalDate gracePeriodEndDate, LogicalDate cancellationDate) {
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.subject().setValue(i18n.tr("15 Day Notice of Cancellation for Non-payment of Premium"));
        DateFormat dateFormat = new SimpleDateFormat(i18n.tr("yyyy-MM-dd"));

        try {
            String body = IOUtils.getTextResource("email/tenantsure-payment-not-processed.html");
            body = body.replace("${cancellationDate}", dateFormat.format(cancellationDate));
            body = body.replace("${gracePeriodEndDate}", dateFormat.format(gracePeriodEndDate));
            body = body.replace("${paymentMethodLink}", AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.residentPortal, true)
                    + DeploymentConsts.TENANT_URL_PATH, true, PortalSiteMap.Resident.TenantInsurance.TenantSure.Management.UpdateCreditCard.class));
            // TODO i18n body
            template.content().setValue(wrapTenantSureHtml(body));

            return template;

        } catch (IOException e) {
            throw new UserRuntimeException("Unable to send TenantSure email");
        }
    }

    public static EmailTemplate emailTemplateTenantSurePaymentsResumed() {
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.subject().setValue(i18n.tr("Payment Processing Resumed"));
        try {
            String body = IOUtils.getTextResource("email/tenantsure-payments-resumed.html");
            // TODO i18n body
            template.content().setValue(wrapTenantSureHtml(body));

            return template;

        } catch (IOException e) {
            throw new UserRuntimeException("Unable to send TenantSure email");
        }
    }

    private static EmailTemplate emailOnlinePaymentSetupCompleted(String userName) {
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.subject().setValue(i18n.tr("Your Online Payment Setup Is Complete"));
        try {
            // TODO add email html
            String body = IOUtils.getTextResource("email/online-payment-setup-completed.html");
            body = body.replace("${userName}", userName);
            // TODO i18n body
            template.content().setValue(body);
            return template;

        } catch (IOException e) {
            throw new UserRuntimeException("Unable to send TenantSure email");
        }
    }
}
