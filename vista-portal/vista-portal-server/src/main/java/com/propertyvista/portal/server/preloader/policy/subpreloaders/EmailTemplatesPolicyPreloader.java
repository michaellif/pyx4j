/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;
import com.propertyvista.server.common.mail.templates.EmailTemplateManager;
import com.propertyvista.server.common.mail.templates.model.ApplicationT;
import com.propertyvista.server.common.mail.templates.model.BuildingT;
import com.propertyvista.server.common.mail.templates.model.LeaseT;
import com.propertyvista.server.common.mail.templates.model.PasswordRequestCrmT;
import com.propertyvista.server.common.mail.templates.model.PasswordRequestT;
import com.propertyvista.server.common.mail.templates.model.PortalLinksT;

public class EmailTemplatesPolicyPreloader extends AbstractPolicyPreloader<EmailTemplatesPolicy> {

    private final static I18n i18n = I18n.get(EmailTemplatesPolicyPreloader.class);

    private final static Logger log = LoggerFactory.getLogger(EmailTemplatesPolicyPreloader.class);

    public EmailTemplatesPolicyPreloader() {
        super(EmailTemplatesPolicy.class);
    }

    @Override
    protected EmailTemplatesPolicy createPolicy(StringBuilder log) {
        return createDefaultEmailTemplatesPolicy();
    }

    private EmailTemplatesPolicy createDefaultEmailTemplatesPolicy() {
        EmailTemplatesPolicy policy = EntityFactory.create(EmailTemplatesPolicy.class);

        policy.templates().add(defaultEmailTemplatePasswordRetrievalCrm());
        policy.templates().add(defaultEmailTemplatePasswordRetrievalTenant());
        policy.templates().add(defaultEmailTemplateApplicationCreatedApplicant());
        policy.templates().add(defaultEmailTemplateApplicationCreatedCoApplicant());
        policy.templates().add(defaultEmailTemplateApplicationCreatedGuarantor());
        policy.templates().add(defaultEmailTemplateApplicationApproved());
        policy.templates().add(defaultEmailTemplateApplicationDeclined());
        policy.templates().add(defaultEmailTemplateTenantInvitation());

        return policy;
    }

    public static String wrapHtml(String text) {
        try {
            String html = IOUtils.getTextResource("email/template-basic.html");
            return html.replace("{MESSAGE}", text);
        } catch (IOException e) {
            throw new Error("template error", e);
        }
    }

    private EmailTemplate defaultEmailTemplatePasswordRetrievalCrm() {
        EmailTemplateType type = EmailTemplateType.PasswordRetrievalCrm;

        PasswordRequestCrmT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestCrmT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("New Password Retrieval"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "Dear {0},<br/>\n" +
                "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n" +
                "Click the link below to go to the {1} site and create new password for your account:<br/>\n" +
                "    <a style=\"color:#929733\" href=\"{2}\">Change Your Password</a>",
                EmailTemplateManager.getVarname(pwdReqT.requestorName()),
                EmailTemplateManager.getVarname(portalT.companyName()),
                EmailTemplateManager.getVarname(pwdReqT.passwordResetUrl())
        )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplatePasswordRetrievalTenant() {
        EmailTemplateType type = EmailTemplateType.PasswordRetrievalTenant;

        PasswordRequestT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("New Password Retrieval"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "Dear {0},<br/>\n" +
                "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n" +
                "Click the link below to go to the {1} site and create new password for your account:<br/>\n" +
                "    <a style=\"color:#929733\" href=\"{2}\">Change Your Password</a>",
                EmailTemplateManager.getVarname(pwdReqT.requestorName()),
                EmailTemplateManager.getVarname(portalT.companyName()),
                EmailTemplateManager.getVarname(pwdReqT.passwordResetUrl())
        )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedApplicant() {
        EmailTemplateType type = EmailTemplateType.ApplicationCreatedApplicant;

        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Lease Application Created"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Your lease application has been created. The Application Reference Number is: {1}<br/><br/>" +
                "You can now start completing it online by logging to your account using following link: <br/><br/>" +
                "{2}<br/><br/>" +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at " +
                "{3}. Please have your Application Reference Number available.<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{4}<br/>" +
                "{5}<br/>",
                EmailTemplateManager.getVarname(appT.applicant()),
                EmailTemplateManager.getVarname(appT.refNumber()),
                EmailTemplateManager.getVarname(appT.applicationUrl()),
                EmailTemplateManager.getVarname(bldT.mainOffice().phone()),
                EmailTemplateManager.getVarname(bldT.propertyName()),
                EmailTemplateManager.getVarname(bldT.administrator().name())
        )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedCoApplicant() {
        EmailTemplateType type = EmailTemplateType.ApplicationCreatedCoApplicant;

        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Lease Application Created"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Your lease application has been created. The Application Reference Number is: {1}<br/><br/>" +
                "You can now start completing it online by logging to your account using following link: <br/><br/>" +
                "{2}<br/><br/>" +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at " +
                "{3}. Please have your Application Reference Number available.<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{4}<br/>" +
                "{5}<br/>",
                EmailTemplateManager.getVarname(appT.applicant()),
                EmailTemplateManager.getVarname(appT.refNumber()),
                EmailTemplateManager.getVarname(appT.applicationUrl()),
                EmailTemplateManager.getVarname(bldT.mainOffice().phone()),
                EmailTemplateManager.getVarname(bldT.propertyName()),
                EmailTemplateManager.getVarname(bldT.administrator().name())
        )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedGuarantor() {
        EmailTemplateType type = EmailTemplateType.ApplicationCreatedGuarantor;

        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Guarantor Application Created"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Your guarantor application has been created. The Application Reference Number is: {1}<br/><br/>" +
                "You can now start completing it online by logging to your account using following link: <br/><br/>" +
                "{2}<br/><br/>" +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at " +
                "{3}. Please have your Application Reference Number available.<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{4}<br/>" +
                "{5}<br/>",
                EmailTemplateManager.getVarname(appT.applicant()),
                EmailTemplateManager.getVarname(appT.refNumber()),
                EmailTemplateManager.getVarname(appT.applicationUrl()),
                EmailTemplateManager.getVarname(bldT.mainOffice().phone()),
                EmailTemplateManager.getVarname(bldT.propertyName()),
                EmailTemplateManager.getVarname(bldT.administrator().name())
        )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationApproved() {
        EmailTemplateType type = EmailTemplateType.ApplicationApproved;

        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
        LeaseT leaseT = EmailTemplateManager.getProto(type, LeaseT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Application To Lease has been approved"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "<h3>Welcome {0} to your new home!</h3><br/><br/>" +                        
                "Your Application To Lease has been approved!<br/><br/>" +
                "As per your application, your lease start date is on {1}, {2}<br/></br>" +
                "We are excited to have you live with us. Please maintain your username and password that you have used for the application process. " +
                "This username and password will stay with you throughout your tenancy and will give you access to our Online Tenant Portal. " +
                "You can access the Portal at anytime by going to our website {3} " +
                "and clicking under residents. Alternatively you can reach the site directly by going to {4}<br/><br/>" +
                "A member of our team will be in touch with you shortly to make move-in arrangements.<br/><br/>" +
                "In the meantime, should you have any concerns or questions, please do not hesistate to contact us directly.<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{5}<br/>" +
                "{6}<br/>",                        
                EmailTemplateManager.getVarname(appT.applicant()),
                EmailTemplateManager.getVarname(leaseT.startDateWeekday()),
                EmailTemplateManager.getVarname(leaseT.startDate()),
                EmailTemplateManager.getVarname(portalT.portalHomeUrl()),
                EmailTemplateManager.getVarname(portalT.tenantHomeUrl()),
                EmailTemplateManager.getVarname(bldT.propertyName()),
                EmailTemplateManager.getVarname(bldT.administrator().name())
        )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationDeclined() {
        EmailTemplateType type = EmailTemplateType.ApplicationDeclined;

        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Application has been declined"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>"+
                "Unfortunately, based on the information provided your application has been DECLINED.<br/><br/>" +
                "We do encourage you to add more information to your application that could assist us in re-assessing this application.<br/>" +
                "Typically, additional Proof of Income or Guarantor(s)can change the application decision and allow us to re-evaluate the entire application.<br/>" +
                "We welcome you to access the application again utilizing the username and password you have previously created at {1} " +
                "to add more information.<br/>" +
                "Should you wish the cancel the application procedure at this time completely, no further actions are required.<br/><br/>" +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly and reference " +
                "your Application Reference Number {2}<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{3}<br/>" +
                "{4}<br/>",                        
                EmailTemplateManager.getVarname(appT.applicant()),          
                EmailTemplateManager.getVarname(portalT.prospectPortalomeUrl()),
                EmailTemplateManager.getVarname(appT.refNumber()),
                EmailTemplateManager.getVarname(bldT.propertyName()),
                EmailTemplateManager.getVarname(bldT.administrator().name())
        )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateTenantInvitation() {
        EmailTemplateType type = EmailTemplateType.TenantInvitation;

        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
        LeaseT leaseT = EmailTemplateManager.getProto(type, LeaseT.class);
        PasswordRequestT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Visit our new site"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "<h3>Welcome {0}!</h3><br/><br/>" +                        

                "Click the link below to access {1} site and create new password for your account:<br/>\n" +
                "    <a style=\"color:#929733\" href=\"{2}\">Reset Your Password</a>" +
                
                "According to our records, your lease start date is on {3}, {4}<br/></br>" +

                "We are excited to have you join the Online Tenant Portal that we created just for you! Please keep " +
                "your username and password as they will be required to access your Portal. You can visit it anytime by " +
                "going to {5} and clicking on Residents menu tab. You will be redirected to Online Tenant Portal immediately. " +
                "Or, alternatively, you can reach that page directly by going to {6}.<br/><br/>" +

                "Should you have any concerns or questions, please do not hesistate to contact us directly.<br/><br/>" +
                        
                "Sincerely,<br/><br/>" +
                        
                "{6}<br/>" +
                "{7}<br/>",
                
                EmailTemplateManager.getVarname(pwdReqT.requestorName()),
                EmailTemplateManager.getVarname(portalT.companyName()),
                EmailTemplateManager.getVarname(pwdReqT.passwordResetUrl()),
                EmailTemplateManager.getVarname(leaseT.startDateWeekday()),
                EmailTemplateManager.getVarname(leaseT.startDate()),
                EmailTemplateManager.getVarname(portalT.portalHomeUrl()),
                EmailTemplateManager.getVarname(portalT.tenantHomeUrl()),
                EmailTemplateManager.getVarname(bldT.propertyName()) ,
                EmailTemplateManager.getVarname(bldT.administrator().name())
        )));//@formatter:on
        return template;
    }
}
