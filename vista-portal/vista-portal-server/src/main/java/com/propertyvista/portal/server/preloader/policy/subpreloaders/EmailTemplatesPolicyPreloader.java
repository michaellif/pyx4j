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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

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

        Persistence.service().persist(policy);
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
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);

        template.type().setValue(EmailTemplateType.PasswordRetrievalCrm);
        template.subject().setValue(i18n.tr("here be subject"));
        template.content().setValue(
                wrapHtml(i18n.tr("Dear {userName},<br/>\n"
                        + "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n"
                        + "Click the link below to go to the Property Vista site and create new password for your account:<br/>\n"
                        + "    <a style=\"color:#929733\" href=\"{passwordResetUrl}\">Change Your Password</a>")));
        return template;
    }

    private EmailTemplate defaultEmailTemplatePasswordRetrievalTenant() {
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);

        template.type().setValue(EmailTemplateType.PasswordRetrievalTenant);
        template.subject().setValue(i18n.tr("here be subject"));
        template.content().setValue(wrapHtml(i18n.tr("here be body")));
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedApplicant() {
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);

        template.type().setValue(EmailTemplateType.ApplicationCreatedApplicant);
        template.subject().setValue(i18n.tr("here be subject"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "<h3>Congratulations {applicant.firstname}!</h3><br/><br/>" +
                "You have successfully completed your application online.br/><br/>" +
                "Please find attached a PDF copy of your application for your personal records.<br/><br/>" +
                
                "The Application Reference Number is: {#reference}<br/><br/>" +
                
                "Please allow us some time to review your application in full. " + 
                "We will be in touch with you shortly, typically within 48 hours, with our decision. " + 
                "Should you wish to check on the status of your application, " + 
                "you can go online to {pmc.prospectportalsite.com} and use your username and password to access your account." +
                "In the meantime, should you have any concerns or questions, " +
                "please do not hesitate to contact us directly and have your Application Reference Number available.<br/<br/>" +
                
                "Sincerely,<br/><br/>" +
                
                "{PMC COMPANY NAME} Team<br/>" +
                "{PMC CHOSEN CONTACT PERSON... could be the Superintendent, the office, the admin, etc}<br/>"
                )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedCoApplicant() {
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);

        template.type().setValue(EmailTemplateType.ApplicationCreatedCoApplicant);
        template.subject().setValue(i18n.tr("here be subject"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "<h3>Thank you, {co-applicant.firstname}!<h3/><br/><br/>" +

                "You have successfully completed your part of the application online." + 

                "Please find attached the PDF copy of your application for your reference." +

                "Your Application Reference Number is: {#reference}" +

                "Once the entire application is completed successfully, we will process the application and a seperate email notification will be sent out to you." + 

                "To check on the status of the application, you can go to {pmc.prospecportalsite.com} and use your existing username and password to access the site." +

                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at {PMC CHOSEN NUMBER} and have your Application Reference Number available." +


                "Sincerely," +
                
                "{PMC COMPANY NAME} Team <br/>" +
                "{PMC CHOSEN CONTACT PERSON... could be the Superintendent, the office, the admin, etc} " 
               
                )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedGuarantor() {
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);

        template.type().setValue(EmailTemplateType.ApplicationCreatedGuarantor);
        template.subject().setValue(i18n.tr("here be subject"));
        template.content().setValue(wrapHtml(i18n.tr(//@formatter:off
                "<h3>Thank you, {guarantor.firstname}!<h3/>" +

                "You have successfully completed your Guarantor agreement.<br/><br/>" + 

                "Please find attached the PDF copy of your Guarantor Agreement for your reference. <br/><br/>" +

                "Your Application Reference Number is: {#reference}<br/><br/>" +

                "Once the entire application is completed successfully, we will process the application and a seperate email notification will be sent out to you." +
                
                "To check on the status of the application, you can go to {pmc.prospecportalsite.com} and use your existing username and password to access the site." +

                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at {PMC CHOSEN NUMBER} and have your Application Reference Number available." +


                "Sincerely," +

                "(Options here)" +
                "{PMC COMPANY NAME} Team " +
                "{PMC CHOSEN CONTACT PERSON... could be the Superintendent, the office, the admin, etc}"                
                )));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationApproved() {
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);

        template.type().setValue(EmailTemplateType.ApplicationApproved);
        template.subject().setValue(i18n.tr("Your Application To Lease has been approved"));
        template.content().setValue(
                wrapHtml(i18n.tr(//@formatter:off
                        "<h3>Welcome {applicant.firstName} to your new home!</h3><br/><br/>" +                        
                        
                        "Your Application To Lease has been approved!</br><br/> +" +
                        
                        
                        "As per your application, your lease start date is on {lease.startDateWeekday}, {lease.startDate}<br/></br>" +
                        
                        "We are excited to have you live with us. Please maintain your username and password that you have used for the application process. " +
                        "This username and password will stay with you throughout your tenancy and will give you access to our Online Tenant Portal. " +
                        "You can access the Portal at anytime by going to our website {pmc.website} and clicking under residents. " +
                        "Alternatively you can reach the site directly by going to {pmc.tenantportalsite.com}.<br/><br/>" +
                        
                        "A member of our team will be in touch with you shortly to make move-in arrangements.<br/><br/>" +
                        
                        "In the meantime, should you have any concerns or questions, please do not hesistate to contact us directly.<br/><br/>" +
                        
                        "Sincerely,<br/><br/>" +
                        
                        "{pmc.companyName} Team<br/>" +
                        "{pmc.contanctPerson}<br/>"
                        )));//@formatter:on                        
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationDeclined() {
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);

        template.type().setValue(EmailTemplateType.ApplicationDeclined);
        template.subject().setValue(i18n.tr("here be subject"));
        template.content().setValue(wrapHtml(i18n.tr("here be body")));
        return template;
    }
}
