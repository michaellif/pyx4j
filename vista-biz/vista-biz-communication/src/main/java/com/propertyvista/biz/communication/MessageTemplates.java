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
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.biz.communication.mail.template.EmailTemplateManager;
import com.propertyvista.biz.communication.mail.template.EmailTemplateRootObjectLoader;
import com.propertyvista.biz.communication.mail.template.model.EmailTemplateContext;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestAdminT;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;
import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

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

    public static MailMessage createApplicationStatusEmail(Tenant tenantInLease, EmailTemplateType type) {
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
        CustomerUser user = tenantInLease.leaseCustomer().customer().user();
        if (user.isValueDetached()) {
            Persistence.service().retrieve(tenantInLease.leaseCustomer().customer().user());
        }
        email.setTo(user.email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);

        return email;
    }

    public static MailMessage createTenantInvitationEmail(LeaseParticipant leaseParticipant, EmailTemplateType emailType, String token) {
        Persistence.service().retrieve(leaseParticipant.leaseTermV());
        Persistence.service().retrieve(leaseParticipant.leaseTermV().holder().lease());
        Persistence.service().retrieve(leaseParticipant.leaseTermV().holder().lease().unit());
        Persistence.service().retrieve(leaseParticipant.leaseTermV().holder().lease().unit().building());

        Persistence.service().retrieve(leaseParticipant.leaseCustomer().customer().user());

        EmailTemplate emailTemplate = getEmailTemplate(emailType, leaseParticipant.leaseTermV().holder().lease().unit().building());

        // create required data context
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.user().set(leaseParticipant.leaseCustomer().customer().user());
        context.lease().set(leaseParticipant.leaseTermV().holder().lease());
        context.leaseParticipant().set(leaseParticipant);
        context.accessToken().setValue(token);
        // load data objects for template variable lookup
        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(emailType)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }
        MailMessage email = new MailMessage();
        email.setTo(leaseParticipant.leaseCustomer().customer().user().email().getValue());
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
        EntityQueryCriteria<Tenant> tilCrit = EntityQueryCriteria.create(Tenant.class);
        tilCrit.add(PropertyCriterion.eq(tilCrit.proto().leaseCustomer().customer().user(), user));

        // TODO Fix this!
        Tenant til = Persistence.service().retrieve(tilCrit);
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
                AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.Admin, true), AdminSiteMap.LoginWithToken.class,
                        AuthenticationService.AUTH_TOKEN_ARG, token));
        data.add(pwdReqT);

        MailMessage email = new MailMessage();
        email.setTo(user.email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);

        return email;
    }

    public static MailMessage createOnboardingUserPasswordResetEmail(AbstractUser user, String token, String onboardingSystemBaseUrl) {
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.accessToken().setValue(token);
        context.user().set(user);

        ArrayList<IEntity> data = new ArrayList<IEntity>();

        EmailTemplate emailTemplate = emailTemplatePasswordRetrievalOnboardingUser();
        PasswordRequestAdminT pwdReqT = EntityFactory.create(PasswordRequestAdminT.class);
        pwdReqT.RequestorName().set(user.name());
        pwdReqT.PasswordResetUrl().setValue(passwordResetUrl(onboardingSystemBaseUrl, token));
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
        email.setSubject(emailTemplate.subject().getValue());

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

    private static String wrapAdminHtml(String text) {
        try {
            String html = IOUtils.getTextResource("email/template-admin.html");
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
                "Click the link below to go to the Property Vista Administration site and create new password for your account:<br/>\n" +
                "    <a href=\"{1}\">Change Your Password</a>",
                EmailTemplateManager.getVarname(pwdReqT.RequestorName()),
                EmailTemplateManager.getVarname(pwdReqT.PasswordResetUrl())
        )));//@formatter:on
        return template;
    }

    private static EmailTemplate emailTemplatePasswordRetrievalOnboardingUser() {
        PasswordRequestAdminT pwdReqT = EntityFactory.create(PasswordRequestAdminT.class);
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.subject().setValue(i18n.tr("New Password Retrieval"));
        template.content().setValue(wrapAdminHtml(i18n.tr(//@formatter:off
                "Dear {0},<br/>\n" +
                "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n" +
                "Click the link below to go to the Property Vista site and create new password for your account:<br/>\n" +
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
            body = body.replace("${ownerName}", EmailTemplateManager.getVarname(user.firstName()));
            body = body.replace("${crmLink}", VistaDeployment.getBaseApplicationURL(pmc, VistaBasicBehavior.CRM, true));
            body = body.replace("${portalLink}", VistaDeployment.getBaseApplicationURL(pmc, VistaBasicBehavior.TenantPortal, true));

            template.content().setValue(wrapAdminHtml(i18n.tr(//@formatter:off
                body
        )));//@formatter:on

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
}
