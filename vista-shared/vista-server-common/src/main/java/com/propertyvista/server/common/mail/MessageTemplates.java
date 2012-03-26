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
package com.propertyvista.server.common.mail;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
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

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.server.common.mail.templates.EmailTemplateManager;
import com.propertyvista.server.common.mail.templates.EmailTemplateRootObjectLoader;
import com.propertyvista.server.common.mail.templates.model.EmailTemplateContext;
import com.propertyvista.server.common.policy.PolicyManager;

public class MessageTemplates {

    private static final I18n i18n = I18n.get(MessageTemplates.class);

    private static final Logger log = LoggerFactory.getLogger(MessageTemplates.class);

    public static String getSender() {
        return ServerSideConfiguration.instance().getApplicationEmailSender();
    }

    /**
     * Warning: can return <code>null</code> if the policy is not found.
     * 
     * @param type
     * @param building
     * @return
     */

    public static EmailTemplate getEmailTemplate(EmailTemplateType type, PolicyNode policyNode) {
        EmailTemplatesPolicy policy = PolicyManager.obtainEffectivePolicy(policyNode, EmailTemplatesPolicy.class).duplicate();
        return fetchEmailTemplate(type, policy);
    }

    private static EmailTemplate fetchEmailTemplate(EmailTemplateType type, EmailTemplatesPolicy policy) {
        for (EmailTemplate emt : policy.templates()) {
            if (emt.type().getValue() == type)
                return emt;
        }
        return null;
    }

    public static void createApplicationApprovedEmail(MailMessage email, TenantInLease tenantInLease) {
        createApplicationApprovedDeclinedEmail(email, tenantInLease, EmailTemplateType.ApplicationApproved);
    }

    public static void createApplicationDeclinedEmail(MailMessage email, TenantInLease tenantInLease) {
        createApplicationApprovedDeclinedEmail(email, tenantInLease, EmailTemplateType.ApplicationDeclined);
    }

    private static void createApplicationApprovedDeclinedEmail(MailMessage email, TenantInLease tenantInLease, EmailTemplateType type) {
        // get building policy node
        Persistence.service().retrieve(tenantInLease.leaseV());
        Persistence.service().retrieve(tenantInLease.leaseV().holder());
        Persistence.service().retrieve(tenantInLease.leaseV().holder().unit());
        Persistence.service().retrieve(tenantInLease.leaseV().holder().unit().belongsTo());
        EmailTemplate emailTemplate = getEmailTemplate(type, tenantInLease.leaseV().holder().unit().belongsTo());

        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        // populate context properties required by template type
        context.tenantInLease().set(tenantInLease);

        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(type)) {
            // ObjectLoader will load required T-Objects using context data
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }
        email.setSubject(emailTemplate.subject().getValue());
        // parse template and set email body
        email.setHtmlBody(EmailTemplateManager.parseTemplate(emailTemplate.content().getValue(), data));
    }

    public static void createMasterApplicationInvitationEmail(MailMessage email, TenantUser tenantUser, EmailTemplateType emailType, Lease lease, String token) {
        // get building policy node
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().belongsTo());
        Building building = lease.unit().belongsTo();
        if (building == null || building.isNull()) {
            throw new Error("No building node found");
        }
        EmailTemplate emailTemplate = getEmailTemplate(emailType, building);
        // create required data context
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.user().set(tenantUser);
        context.lease().set(lease);
        context.accessToken().setValue(token);
        // load data objects for template variable lookup
        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(emailType)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }
        email.setSubject(emailTemplate.subject().getValue());
        email.setHtmlBody(EmailTemplateManager.parseTemplate(emailTemplate.content().getValue(), data));
    }

    public static void createPasswordResetEmail(MailMessage email, VistaBasicBehavior application, AbstractUser user, String token) {
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.accessToken().setValue(token);
        context.user().set(user);

        // get company policy node
        EntityQueryCriteria<OrganizationPoliciesNode> nodeCrit = EntityQueryCriteria.create(OrganizationPoliciesNode.class);
        PolicyNode policyNode = Persistence.service().retrieve(nodeCrit);

        EmailTemplateType templateType = null;

        switch (application) {
        case CRM:
            templateType = EmailTemplateType.PasswordRetrievalCrm;
            break;
        case ProspectiveApp:
        case TenantPortal:
            templateType = EmailTemplateType.PasswordRetrievalTenant;
            // get building policy node form the first available TenantInLease entry
            EntityQueryCriteria<TenantInLease> tilCrit = EntityQueryCriteria.create(TenantInLease.class);
            tilCrit.add(PropertyCriterion.eq(tilCrit.proto().tenant().user(), user));
            TenantInLease til = Persistence.service().retrieve(tilCrit);
            if (til != null) {
                Persistence.service().retrieve(til.leaseV());
                Persistence.service().retrieve(til.leaseV().holder());
                Persistence.service().retrieve(til.leaseV().holder().unit());
                Persistence.service().retrieve(til.leaseV().holder().unit().belongsTo());
                Building bldNode = til.leaseV().holder().unit().belongsTo();
                if (bldNode != null && !bldNode.isNull()) {
                    policyNode = bldNode;
                }
            }
            break;
        default:
            throw new Error("Not implemented behavior");
        }

        EmailTemplate emailTemplate = getEmailTemplate(templateType, policyNode);
        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(templateType)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }
        email.setSubject(emailTemplate.subject().getValue());
        email.setHtmlBody(EmailTemplateManager.parseTemplate(emailTemplate.content().getValue(), data));
    }

    @Deprecated
    public static String createPasswordResetEmail(VistaBasicBehavior application, String name, String token) {
        String url;
        switch (application) {
        case CRM:
            url = ServerSideConfiguration.instance().getMainApplicationURL()
                    + AppPlaceInfo.absoluteUrl(DeploymentConsts.CRM_URL, CrmSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, token);
            break;
        case ProspectiveApp:
            url = ServerSideConfiguration.instance().getMainApplicationURL()
                    + AppPlaceInfo.absoluteUrl(DeploymentConsts.PTAPP_URL, PtSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, token);
            break;
        case TenantPortal:
            url = ServerSideConfiguration.instance().getMainApplicationURL() + DeploymentConsts.TENANT_URL + '?' + AuthenticationService.AUTH_TOKEN_ARG + '='
                    + token;
            break;
        default:
            throw new Error("TODO");
        }

        return wrapHtml(i18n.tr("Dear {0},<br/>\n"
                + "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n"
                + "Click the link below to go to the Property Vista site and create new password for your account:<br/>\n"
                + "    <a style=\"color:#929733\" href=\"{1}\">Change Your Password</a>", name, url));
    }

    @Deprecated
    public static String createMasterApplicationInvitationEmail(String name, String token) {
        String url = ServerSideConfiguration.instance().getMainApplicationURL()
                + AppPlaceInfo.absoluteUrl(DeploymentConsts.PTAPP_URL, PtSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, token);

        return wrapHtml(i18n.tr("Dear {0},<br/>\n" + "This email was sent to you in response to your request to apply for Property Vista apartments.<br/>\n"
                + "Click the link below to go to the Property Vista site:<br/>\n" + "    <a style=\"color:#929733\" href=\"{1}\">Application</a>", name, url));
    }

    public static String wrapHtml(String text) {
        try {
            String html = IOUtils.getTextResource("email/template-basic.html");
            return html.replace("{MESSAGE}", text);
        } catch (IOException e) {
            log.error("template error", e);
            return text;
        }
    }
}
