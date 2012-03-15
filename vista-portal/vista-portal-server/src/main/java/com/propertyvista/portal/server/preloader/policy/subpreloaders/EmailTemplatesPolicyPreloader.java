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

        return policy;
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

    private EmailTemplate defaultEmailTemplatePasswordRetrievalCrm() {
        EmailTemplateType type = EmailTemplateType.PasswordRetrievalCrm;

        PasswordRequestCrmT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestCrmT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("New Password Retrieval"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "Dear " +
                EmailTemplateManager.getVarname(pwdReqT.requestorName()) + ",<br/>\n" +
                "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n" +
                "Click the link below to go to the Property Vista site and create new password for your account:<br/>\n" +
                "    <a style=\"color:#929733\" href=\"" +
                EmailTemplateManager.getVarname(pwdReqT.passwordResetUrl()) +
                "\">Change Your Password</a>"
        )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplatePasswordRetrievalTenant() {
        EmailTemplateType type = EmailTemplateType.PasswordRetrievalTenant;

        PasswordRequestT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("New Password Retrieval"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "Dear " +
                EmailTemplateManager.getVarname(pwdReqT.requestorName()) + ",<br/>\n" +
                "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n" +
                "Click the link below to go to the Property Vista site and create new password for your account:<br/>\n" +
                "    <a style=\"color:#929733\" href=\"" +
                EmailTemplateManager.getVarname(pwdReqT.passwordResetUrl()) +
                "\">Change Your Password</a>"
        )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedApplicant() {
        EmailTemplateType type = EmailTemplateType.ApplicationCreatedApplicant;

        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Application Confirmation"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "<h3>Congratulations " +
                EmailTemplateManager.getVarname(appT.applicant()) + "!</h3><br/><br/>" +

                "You have successfully completed your application online.<br/><br/>" +

                "Please find attached a PDF copy of your application for your personal records.<br/><br/>" +

                "The Application Reference Number is: " +
                EmailTemplateManager.getVarname(appT.refNumber()) + "<br/><br/>" +

                "Please allow us some time to review your application in full. " + 
                "We will be in touch with you shortly, typically within 48 hours, with our decision. " + 
                "Should you wish to check on the status of your application, " + 
                "you can go online to " +
                EmailTemplateManager.getVarname(portalT.ptappHomeUrl()) + " " +
                "and use your username and password to access your account." +
                "In the meantime, should you have any concerns or questions, " +
                "please do not hesitate to contact us directly and have your Application Reference Number available.<br/<br/>" +
                
                "Sincerely,<br/><br/>" +
                
                EmailTemplateManager.getVarname(bldT.propertyName()) + "<br/>" +
                EmailTemplateManager.getVarname(bldT.administrator().name()) + "<br/>"
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
        template.subject().setValue(i18n.tr("Your Application Confirmation"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "<h3>Thank you, " +
                EmailTemplateManager.getVarname(appT.applicant()) + "!</h3><br/><br/>" +

                "You have successfully completed your part of the application online." + 
                "Please find attached the PDF copy of your application for your reference." +
                "Your Application Reference Number is: " +
                EmailTemplateManager.getVarname(appT.refNumber()) + "<br/><br/>" +

                "Once the entire application is completed successfully, we will process the application and a seperate email notification will be sent out to you." + 
                "To check on the status of the application, you can go to " +
                EmailTemplateManager.getVarname(portalT.ptappHomeUrl()) + " " +
                "and use your existing username and password to access the site." +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at " +
                EmailTemplateManager.getVarname(bldT.mainOffice().phone()) +
                "Please have your Application Reference Number available.<br/><br/>" +

                "Sincerely,<br/><br/>" +
                
                EmailTemplateManager.getVarname(bldT.propertyName()) + "<br/>" +
                EmailTemplateManager.getVarname(bldT.administrator().name()) + "<br/>"
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
        template.subject().setValue(i18n.tr("Your Application Confirmation"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "<h3>Thank you, " +
                EmailTemplateManager.getVarname(appT.applicant()) + "!</h3><br/><br/>" +

                "You have successfully completed your Guarantor agreement.<br/><br/>" + 

                "Please find attached the PDF copy of your Guarantor Agreement for your reference. <br/><br/>" +

                "Your Application Reference Number is: " +
                EmailTemplateManager.getVarname(appT.refNumber()) + "<br/><br/>" +

                "Once the entire application is completed successfully, we will process the application and a seperate email notification will be sent out to you." +
                "To check on the status of the application, you can go to " +
                EmailTemplateManager.getVarname(portalT.ptappHomeUrl()) + " " +
                "and use your existing username and password to access the site." +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at "+
                EmailTemplateManager.getVarname(bldT.mainOffice().phone()) +
                "Please have your Application Reference Number available.<br/><br/>" +

                "Sincerely,<br/><br/>" +

                EmailTemplateManager.getVarname(bldT.propertyName()) + "<br/>" +
                EmailTemplateManager.getVarname(bldT.administrator().name()) + "<br/>"
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
                "<h3>Welcome " +
                EmailTemplateManager.getVarname(appT.applicant()) + " to your new home!</h3><br/><br/>" +                        

                "Your Application To Lease has been approved!<br/><br/> +" +

                "As per your application, your lease start date is on " +
                EmailTemplateManager.getVarname(leaseT.startDateWeekday()) + ", " +
                EmailTemplateManager.getVarname(leaseT.startDate()) + "<br/></br>" +

                "We are excited to have you live with us. Please maintain your username and password that you have used for the application process. " +
                "This username and password will stay with you throughout your tenancy and will give you access to our Online Tenant Portal. " +
                "You can access the Portal at anytime by going to our website " +
                EmailTemplateManager.getVarname(portalT.portalHomeUrl()) + " " +
                "and clicking under residents. " +
                "Alternatively you can reach the site directly by going to " +
                EmailTemplateManager.getVarname(portalT.tenantHomeUrl()) + ".<br/><br/>" +

                "A member of our team will be in touch with you shortly to make move-in arrangements.<br/><br/>" +

                "In the meantime, should you have any concerns or questions, please do not hesistate to contact us directly.<br/><br/>" +
                        
                "Sincerely,<br/><br/>" +
                        
                EmailTemplateManager.getVarname(bldT.propertyName()) + "<br/>" +
                EmailTemplateManager.getVarname(bldT.administrator().name()) + "<br/>"
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
                "Dear "+
                EmailTemplateManager.getVarname(appT.applicant()) + ",<br/><br/>" +                        

                "Unfortunately, based on the information provided your application has been DECLINED.<br/><br/>" +

                "We do encourage you to add more information to your application that could assist us in re-assessing this application.<br/>" +
                "Typically, additional Proof of Income or Guarantor(s)can change the application decision and allow us to re-evaluate the entire application.<br/>" +
                "We welcome you to access the application again utilizing the username and password you have previously created at " +
                EmailTemplateManager.getVarname(portalT.ptappHomeUrl()) + " " +
                "to add more information.<br/>" +
                "Should you wish the cancel the application procedure at this time completely, no further actions are required.<br/><br/>" +

                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly and reference your Application Reference Number " +
                EmailTemplateManager.getVarname(appT.refNumber()) + "<br/><br/>" +

                "Sincerely,<br/><br/>" +

                EmailTemplateManager.getVarname(bldT.propertyName()) + "<br/>" +
                EmailTemplateManager.getVarname(bldT.administrator().name()) + "<br/>"
        )));//@formatter:on
        return template;
    }
}
