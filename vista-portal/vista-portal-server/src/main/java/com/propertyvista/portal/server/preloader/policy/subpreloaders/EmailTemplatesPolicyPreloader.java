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

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.communication.mail.template.EmailTemplateManager;
import com.propertyvista.biz.communication.mail.template.model.ApplicationT;
import com.propertyvista.biz.communication.mail.template.model.AutopayAgreementT;
import com.propertyvista.biz.communication.mail.template.model.BuildingT;
import com.propertyvista.biz.communication.mail.template.model.CompanyInfoT;
import com.propertyvista.biz.communication.mail.template.model.LeaseT;
import com.propertyvista.biz.communication.mail.template.model.MaintenanceRequestT;
import com.propertyvista.biz.communication.mail.template.model.MaintenanceRequestWOT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestCrmT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestProspectT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestTenantT;
import com.propertyvista.biz.communication.mail.template.model.PaymentT;
import com.propertyvista.biz.communication.mail.template.model.PortalLinksT;
import com.propertyvista.biz.communication.mail.template.model.TenantT;
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

        loadHeaderAndFooter(policy);

        policy.templates().add(defaultEmailTemplatePasswordRetrievalCrm());
        policy.templates().add(defaultEmailTemplatePasswordRetrievalProspect());
        policy.templates().add(defaultEmailTemplatePasswordRetrievalTenant());
        policy.templates().add(defaultEmailTemplateProspectWelcome());
        policy.templates().add(defaultEmailTemplateApplicationCreatedApplicant());
        policy.templates().add(defaultEmailTemplateApplicationCreatedCoApplicant());
        policy.templates().add(defaultEmailTemplateApplicationCreatedGuarantor());
        policy.templates().add(defaultEmailTemplateApplicationApproved());
        policy.templates().add(defaultEmailTemplateApplicationDeclined());
        policy.templates().add(defaultEmailTemplateTenantInvitation());
        policy.templates().add(defaultEmailTemplateMaintenanceRequestCreatedPMC());
        policy.templates().add(defaultEmailTemplateMaintenanceRequestCreatedTenant());
        policy.templates().add(defaultEmailTemplateMaintenanceRequestUpdated());
        policy.templates().add(defaultEmailTemplateMaintenanceRequestCompleted());
        policy.templates().add(defaultEmailTemplateMaintenanceRequestCancelled());
        policy.templates().add(defaultEmailTemplateMaintenanceRequestEntryNotice());

        policy.templates().add(defaultEmailTemplateAutoPaySetupConfirmation());
        policy.templates().add(defaultEmailTemplateOneTimePaymentSubmitted());
        policy.templates().add(defaultEmailTemplatePaymentReceipt());
        policy.templates().add(defaultEmailTemplatePaymentReceiptWithWebPaymentFee());
        policy.templates().add(defaultEmailTemplatePaymentReturned());

        return policy;
    }

    private void loadHeaderAndFooter(EmailTemplatesPolicy policy) {
        String headerRaw;
        String footerRaw;
        try {
            headerRaw = IOUtils.getTextResource("email/template-basic-header.html");
            footerRaw = IOUtils.getTextResource("email/template-basic-footer.html");
        } catch (IOException e) {
            throw new Error("Unable to load template html wrapper resource", e);
        }

        // PortalLinksT is present on all template
        PortalLinksT portalT = EntityFactory.create(PortalLinksT.class);

        policy.header().setValue( SimpleMessageFormat.format(//@formatter:off
                headerRaw,
                EmailTemplateManager.getVarname(portalT.SiteHomeUrl()),
                EmailTemplateManager.getVarname(portalT.CompanyLogo()),
                EmailTemplateManager.getVarname(portalT.CompanyName()),
                EmailTemplateManager.getVarname(portalT.CopyrightNotice())
            ));//@formatter:on

        policy.footer().setValue( SimpleMessageFormat.format(//@formatter:off
                footerRaw,
                EmailTemplateManager.getVarname(portalT.SiteHomeUrl()),
                EmailTemplateManager.getVarname(portalT.CompanyLogo()),
                EmailTemplateManager.getVarname(portalT.CompanyName()),
                EmailTemplateManager.getVarname(portalT.CopyrightNotice())
            ));//@formatter:on

    }

    private EmailTemplate defaultEmailTemplatePasswordRetrievalCrm() {
        EmailTemplateType type = EmailTemplateType.PasswordRetrievalCrm;

        PasswordRequestCrmT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestCrmT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("New Password Retrieval"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/>\n" +
                "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n" +
                "Click the link below to go to the {1} site and create new password for your account:<br/>\n" +
                "    [[{2}|Change Your Password]]",
                EmailTemplateManager.getVarname(pwdReqT.RequestorName()),
                EmailTemplateManager.getVarname(portalT.CompanyName()),
                EmailTemplateManager.getVarname(pwdReqT.PasswordResetUrl())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplatePasswordRetrievalTenant() {
        EmailTemplateType type = EmailTemplateType.PasswordRetrievalTenant;

        PasswordRequestTenantT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestTenantT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("New Password Retrieval"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/>\n" +
                "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n" +
                "Click the link below to go to the {1} site and create new password for your account:<br/>\n" +
                "    [[{2}|Change Your Password]]",
                EmailTemplateManager.getVarname(pwdReqT.RequestorName()),
                EmailTemplateManager.getVarname(portalT.CompanyName()),
                EmailTemplateManager.getVarname(pwdReqT.PasswordResetUrl())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplatePasswordRetrievalProspect() {
        EmailTemplateType type = EmailTemplateType.PasswordRetrievalProspect;

        PasswordRequestProspectT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestProspectT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("New Password Retrieval"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/>\n" +
                "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n" +
                "Click the link below to go to the {1} site and create new password for your account:<br/>\n" +
                "    [[{2}|Change Your Password]]",
                EmailTemplateManager.getVarname(pwdReqT.RequestorName()),
                EmailTemplateManager.getVarname(portalT.CompanyName()),
                EmailTemplateManager.getVarname(pwdReqT.PasswordResetUrl())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateProspectWelcome() {
        EmailTemplateType type = EmailTemplateType.ProspectWelcome;

        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
        CompanyInfoT companyT = EmailTemplateManager.getProto(type, CompanyInfoT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Start your Application"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/>"+
                "<br/>" +
                "Welcome to your Online Application.<br/>"+
                "<br/>" + 
                "We have created a secure and safe environment for you that allows you to complete the Application at your leisure from any internet connected device. Once you login, you will have the opportunity to select the Rental Suite that is best suited to your needs.<br/>"+
                "<br/>" +
                "During this process you, your roommates, dependents and guarantors will have the opportunity to complete all necessary information needed to process your Application online. Do not worry, you can take a break at anytime and the information will be saved for you to complete where you left off when you are ready.<br/>"+
                "<br/>" +
                "Please keep in mind, Applications get processed on a first-come-first-served basis and will not be processed until completed in full.<br/>"+
                "<br/>" + 
                "If at anytime during the process you have any concerns or questions, please call us directly at <b>{1}</b> and have your Application Reference Number ready.<br/>"+
                "<br/>" +
                "Your Application Reference Number is: <b>{2}</b><br/>"+
                "<br/>" +
                "To get started, please login to your account <b>[[{3}|here]]</b><br/>"+
                "<br/>" +
                "<i><small>(If the link does not work please copy and paste the following URL:<br/>" +
                "{3} )<br/></small></i><br/>" + 
                "We look forward to making this application process as smooth as possible for you.<br/>"+
                "<br/>"+
                "Sincerely,<br/><br/>"+
                "{4}<br/>" +
                "{5}",
                EmailTemplateManager.getVarname(appT.Applicant().FirstName()),
                EmailTemplateManager.getVarname(companyT.Administrator().Phone()),
                EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                EmailTemplateManager.getVarname(portalT.ProspectPortalUrl()),
                EmailTemplateManager.getVarname(companyT.Administrator().ContactName()),
                EmailTemplateManager.getVarname(companyT.CompanyName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedApplicant() {
        EmailTemplateType type = EmailTemplateType.ApplicationCreatedApplicant;

        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
        CompanyInfoT companyT = EmailTemplateManager.getProto(type, CompanyInfoT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Start your Application"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/>"+
                "<br/>" +
                "Welcome to your Online Application.<br/>"+ 
                "<br/>" +
                "We have created a Secure and Safe Environment for you that allows you to complete the Application at your leisure from any internet connected device.<br/>"+ 
                "<br/>" +
                "During this process you, your roommates, dependents and guarantors will have the opportunity to complete all necessary information needed to process your Application online. Do not worry, you can take a break at anytime and the information will be saved for you to complete when you are ready from where you left off.<br/>"+
                "<br/>" +
                "Please keep in mind, Applications get processed on a First Come basis and will not be processed until completed in full. <br/>"+
                "<br/>" +
                "If at anytime during the process you have any concerns or questions, please call us directly at <b>{1}</b> and have your Application Reference Number ready.<br/>"+
                "<br/>" +
                "Your Application Reference Number is: <b>{2}</b><br/>"+
                "<br/>" +
                "To get started, please login to your account <b>[[{3}|here]]</b><br/>"+
                "<br/>" +
                "<i><small>(If the link does not work please copy and paste the following URL:<br/>"+ 
                "{3} )</small></i><br/>"+
                "<br/>" +
                "We look forward to making this application process as smooth as possible for you.<br/>"+
                "<br/>" +
                "Sincerely,<br/>"+
                "<br/>" +
                "{4}<br/>"+
                "{5}<br/>"+
                "{6}<br/>",
                EmailTemplateManager.getVarname(appT.Applicant().FirstName()),
                EmailTemplateManager.getVarname(bldT.Administrator().Phone()),
                EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                EmailTemplateManager.getVarname(appT.SignUpUrl()),
                EmailTemplateManager.getVarname(bldT.Administrator().ContactName()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(companyT.CompanyName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedCoApplicant() {
        EmailTemplateType type = EmailTemplateType.ApplicationCreatedCoApplicant;

        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
        CompanyInfoT companyT = EmailTemplateManager.getProto(type, CompanyInfoT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Start your Application"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/>"+
                "<br/>" +
                "Welcome to your Online Application.<br/>"+ 
                "<br/>" +
                "You have received this email invitation because {1} has indicated you as a co-applicant for {2}.<br/>"+ 
                "<br/>" +
                "We have created a Secure and Safe Environment for you that allows you to complete the Application at your leisure from any internet connected device.<br/>"+
                "<br/>" +
                "During this process you and your guarantors will have the opportunity to complete all necessary information needed to process your Application online. Do not worry, you can take a break at anytime and the information will be saved for you to complete when you are ready from where you left off.<br/>"+
                "<br/>" +
                "Please keep in mind, Applications get processed on a First Come basis and will not be processed until completed in full.<br/>"+ 
                "<br/>" +
                "If at anytime during the process you have any concerns or questions, please call us directly at <b>{3}</b> and have your Application Reference Number ready.<br/>"+
                "<br/>" +
                "Your Application Reference Number is: <b>{4}</b><br/>"+
                "<br/>" +
                "To get started, please login to your account <b>[[{5}|here]]</b><br/>"+
                "<br/>" +
                "<i><small>(If the link does not work please copy and paste the following URL:<br/>"+ 
                "{5} )</small></i><br/>"+
                "<br/>" +
                "We look forward to making this application process as smooth as possible for you.<br/>"+
                "<br/>" +
                "Sincerely,<br/>"+
                "<br/>" +
                "{6}<br/>"+
                "{7}<br/>"+
                "{8}<br/>",
                EmailTemplateManager.getVarname(appT.CoApplicant().Name()),
                EmailTemplateManager.getVarname(appT.Applicant().Name()),
                EmailTemplateManager.getVarname(appT.UnitAddress()),
                EmailTemplateManager.getVarname(bldT.Administrator().Phone()),
                EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                EmailTemplateManager.getVarname(appT.SignUpUrl()),
                EmailTemplateManager.getVarname(bldT.Administrator().ContactName()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(companyT.CompanyName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedGuarantor() {
        EmailTemplateType type = EmailTemplateType.ApplicationCreatedGuarantor;

        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
        CompanyInfoT companyT = EmailTemplateManager.getProto(type, CompanyInfoT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Start your Guarantor's Application"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/>"+
                "<br/>" +
                "Welcome to your online Guarantor''s agreement.<br/>"+ 
                "<br/>" +
                "You have been asked to be a Guarantor.<br/>"+
                "<br/>" +
                "You have received this email invitation because {1} has indicated you as a Guarantor for {2}.<br/>"+ 
                "<br/>" +
                "We have created a Secure and Safe Environment for you that allows you to complete the Guarantor Application at your leisure from any internet connected device.<br/>"+ 
                "<br/>" +
                "During this process you will have the opportunity to complete all necessary information needed to process your Guarantor Application online. Do not worry, you can take a break at anytime and the information will be saved for you to complete when you are ready from where you left off.<br/>"+
                "<br/>" +
                "Please keep in mind, the entire Application gets processed on a First Come basis and will not be processed until completed in full by all applicants and guarantors.<br/>"+ 
                "<br/>" +
                "If at anytime during the process you have any concerns or questions, please call us directly at <b>{3}</b> and have your Application Reference Number ready.<br/>"+
                "<br/>" +
                "Your Application Reference Number is: <b>{4}</b><br/>"+
                "<br/>" +
                "To get started, please login to your account <b>[[{5}|here]]</b><br/>"+
                "<br/>" +
                "<i><small>(If the link does not work please copy and paste the following URL:<br/>"+ 
                "{5} )</small></i><br/>"+
                "<br/>" +
                "We look forward to making this application process as smooth as possible for you.<br/>"+
                "<br/>" +
                "Sincerely,<br/>"+
                "<br/>" +
                "{6}<br/>"+
                "{7}<br/>"+
                "{8}<br/>",
                EmailTemplateManager.getVarname(appT.Guarantor().Name()),
                EmailTemplateManager.getVarname(appT.GuarantorRequester().Name()),
                EmailTemplateManager.getVarname(appT.UnitAddress()),
                EmailTemplateManager.getVarname(bldT.Administrator().Phone()),
                EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                EmailTemplateManager.getVarname(appT.SignUpUrl()),
                EmailTemplateManager.getVarname(bldT.Administrator().ContactName()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(companyT.CompanyName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationApproved() {
        EmailTemplateType type = EmailTemplateType.ApplicationApproved;

        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
        LeaseT leaseT = EmailTemplateManager.getProto(type, LeaseT.class);
        CompanyInfoT companyT = EmailTemplateManager.getProto(type, CompanyInfoT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Congratulations!"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0}<br/>"+
                "<br/>" +
                "Welcome to your new home!<br/>"+  
                "<br/>" +
                "Your application has been reviewed and has been successfully approved.<br/>"+
                "<br/>" +
                "We look forward to having you move-in to {1}.<br/>"+ 
                "<br/>" +
                "In order to make the transition as smooth as possible for you we have arranged an online portal for you to complete the signing of the lease, book your move-in date, setup your automatic payment withdrawals, along with other useful tips for your big moving day.<br/>"+ 
                "<br/>" +
                "You can use the same username and password that you have used during the application process to complete this process.<br/>"+ 
                "<br/>" +
                "In order to move to the next steps, click <b>[[{2}|here]]</b>.<br/>"+
                "<br/>" +
                "<i><small>(If the link does not work please copy and paste the following URL:<br/>"+ 
                "{2} )</small></i><br/>"+
                "If at anytime during the process you have any concerns or questions, please call us directly at {3} and have your Application Reference Number ready.<br/>"+
                "<br/>" +
                "Your Application Reference Number is: <b>{4}</b><br/>"+
                "<br/>" +
                "We look forward to making this application process as smooth as possible for you.<br/>"+
                "<br/>" +
                "Sincerely,<br/>"+
                "<br/>" +
                "{5}<br/>"+
                "{6}<br/>"+
                "{7}<br/>",
                EmailTemplateManager.getVarname(appT.ApplicantsAndGuarantorsNames()),
                EmailTemplateManager.getVarname(leaseT.UnitAddress()),
                EmailTemplateManager.getVarname(portalT.TenantPortalUrl()),
                EmailTemplateManager.getVarname(bldT.Administrator().Phone()),
                EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                EmailTemplateManager.getVarname(bldT.Administrator().ContactName()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(companyT.CompanyName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationDeclined() {
        EmailTemplateType type = EmailTemplateType.ApplicationDeclined;

        CompanyInfoT companyT = EmailTemplateManager.getProto(type, CompanyInfoT.class);
        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Application Declined"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0}<br/>"+
                "<br/>" +
                "Your application has been reviewed and, unfortunately, has not met our building criteria based on the information provided to us.<br/>"+ 
                "<br/>" +
                "If you have concerns about our decision you can contact us here {1} and have your Application Reference Number ready.<br/>"+
                "<br/>" +
                "Your Application Reference Number is: {2}<br/>"+
                "<br/>" +
                "We are sorry it did not work out for you for this building and wish you continued success in finding your perfect home.<br/>"+
                "<br/>" +
                "Sincerely,<br/>"+
                "<br/>" +
                "{3}<br/>" +
                "{4}<br/>" +
                "{5}<br/>",                      
                EmailTemplateManager.getVarname(appT.ApplicantsAndGuarantorsNames()),
                EmailTemplateManager.getVarname(bldT.Administrator().Phone()),
                EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                EmailTemplateManager.getVarname(bldT.Administrator().ContactName()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(companyT.CompanyName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateTenantInvitation() {
        EmailTemplateType type = EmailTemplateType.TenantInvitation;

        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
        PasswordRequestTenantT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestTenantT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Visit our new site"));
        template.content().setValue(i18n.tr(//@formatter:off
                "<h3>Welcome {0}!</h3><br/><br/>" +                        
                "We are excited to have you join the Online Tenant Portal of {1} that we created just for you! " +
                "To access the site and create new password for your account please follow the link below:<br/>\n" +
                "    [[{2}|Reset Your Password]]<br/>" +
                "Please keep your username and password as they will be required to access your Portal. " +
                "You can visit it anytime by going to<br/>" +
                "{3} and clicking on Residents menu tab. You will be redirected to Online Tenant Portal immediately. " +
                "Or, alternatively, you can reach that page directly by going to<br/>" +
                "{4}.<br/><br/>" +
                "Should you have any concerns or questions, please do not hesitate to contact us directly.<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{5}<br/>" +
                "{6}<br/>",
                EmailTemplateManager.getVarname(pwdReqT.RequestorName()),
                EmailTemplateManager.getVarname(portalT.CompanyName()),
                EmailTemplateManager.getVarname(pwdReqT.PasswordResetUrl()),
                EmailTemplateManager.getVarname(portalT.SiteHomeUrl()),
                EmailTemplateManager.getVarname(portalT.TenantPortalUrl()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()) ,
                EmailTemplateManager.getVarname(bldT.Administrator().ContactName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateMaintenanceRequestCreatedPMC() {
        EmailTemplateType type = EmailTemplateType.MaintenanceRequestCreatedPMC;

        MaintenanceRequestT requestT = EmailTemplateManager.getProto(type, MaintenanceRequestT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr(//@formatter:off
                "New Work Order - {0}{1,choice,!null#, ${1}}",
                EmailTemplateManager.getVarname(requestT.propertyCode()),
                EmailTemplateManager.getVarname(requestT.unitNo())
        ));
        template.content().setValue(i18n.tr(//@formatter:off
                "Building: {0}<br/>" +
                "Unit: {1}<br/>" +
                "Tenant: {2}<br/>" +
                "Summary: {3}<br/>" +
                "Issue: {4}<br/>" +
                "Priority: {5}<br/>" +
                "Description: {6}<br/>" +
                "Permission to enter: {7}<br/>" +
                "Preferred Times:<br/>" +
                " 1 - {8}<br/>" +
                " 2 - {9}<br/>" +
                // TODO
                // WO # : 01 <----- this should be a link to actual work order request
                // WO Created: June 6, 2013
                "[[{13}|Request ID: {10}]]<br/>" +
                "Request Submitted: {11}<br/>" +
                "Current Status: {12}<br/>",
                EmailTemplateManager.getVarname(requestT.propertyCode()),
                EmailTemplateManager.getVarname(requestT.unitNo()),
                EmailTemplateManager.getVarname(requestT.reporterName()),
                EmailTemplateManager.getVarname(requestT.summary()),
                EmailTemplateManager.getVarname(requestT.category()),
                EmailTemplateManager.getVarname(requestT.priority()),
                EmailTemplateManager.getVarname(requestT.description()),
                EmailTemplateManager.getVarname(requestT.permissionToEnter()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime1()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime2()),
                EmailTemplateManager.getVarname(requestT.requestId()),
                EmailTemplateManager.getVarname(requestT.submitted()),
                EmailTemplateManager.getVarname(requestT.status()),
                EmailTemplateManager.getVarname(requestT.requestViewUrl())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateMaintenanceRequestCreatedTenant() {
        EmailTemplateType type = EmailTemplateType.MaintenanceRequestCreatedTenant;

        MaintenanceRequestT requestT = EmailTemplateManager.getProto(type, MaintenanceRequestT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Maintenance Request Received!"));
        template.content().setValue(i18n.tr(//@formatter:off
                "<h3>Dear {2},</h3><br/>" + // TODO - is this the same as reporter?
                "<br/>" +
                "We are in receipt of your maintenance request. <br/>" +
                "If you are experiencing one of the EMERGENCY situations listed below, in addition to submitting " +
                "the online request, please call the emergency number provided to you or see your site staff " +
                "immediately.<br/>" +
                "<br/>" +
                "<b>EMERGENCY</b> Maintenance Issues:<br/>" +
                "  1. No heat in your unit<br/>" +
                "  2. Lock out, lost keys, or lock problem<br/>" +
                "  3. Leaking roof/ceiling<br/>" +
                "  4. Sink that will not drain/is backing up.<br/>" +
                "<br/>" +
                "<b><u>Time To Attend</u></b><br/>" +
                "While we endeavor to respond to every work order as soon as possible, please allow at least 2 " +
                "business days to receive your Notice of Entry (indicating when staff will attend to your request) " +
                "from the date you submit. Work orders are prioritized for attention.<br/>" +
                "<br/>" +
                "<b>PLEASE NOTE:</b>  Maintenance staff will review received requests and address them as required " +
                "on a priority basis. Generally items related to safety, heat, etc. are addressed first. We thank you " +
                "for your patience.<br/>" +
                "To review your request please login to your account at:<br/>" +
                "  {13}<br/>" +
                "The following Maintenance Request has been registered:<br/>" +
                "<br/>" +
                "Building: {14}<br/>" +
                "Address: {15}<br/>" +
                "Unit: {1}<br/>" +
                "Tenant: {2}<br/>" +
                "<br/>" +
                "Summary: {3}<br/>" +
                "<br/>" +
                "Issue: {4}<br/>" +
                "Priority: {5}<br/>" +
                "<br/>" +
                "Description: {6}<br/>" +
                "<br/>" +
                "Permission to enter: {7}<br/>" +
                "Preferred Times:<br/>" +
                " 1 - {8}<br/>" +
                " 2 - {9}<br/>" +
                "<br/>" +
                // TODO
                // WO # : 01 <----- this should be a link to actual work order request
                // WO Created: June 6, 2013
                "[[{13}|Request ID: {10}]]<br/>" +
                "Request Submitted: {11}<br/>" +
                "Current Status: {12}<br/>",
                EmailTemplateManager.getVarname(requestT.propertyCode()),
                EmailTemplateManager.getVarname(requestT.unitNo()),
                EmailTemplateManager.getVarname(requestT.reporterName()),
                EmailTemplateManager.getVarname(requestT.summary()),
                EmailTemplateManager.getVarname(requestT.category()),
                EmailTemplateManager.getVarname(requestT.priority()),
                EmailTemplateManager.getVarname(requestT.description()),
                EmailTemplateManager.getVarname(requestT.permissionToEnter()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime1()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime2()),
                EmailTemplateManager.getVarname(requestT.requestId()),
                EmailTemplateManager.getVarname(requestT.submitted()),
                EmailTemplateManager.getVarname(requestT.status()),
                EmailTemplateManager.getVarname(requestT.requestViewUrl()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(bldT.Address())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateMaintenanceRequestEntryNotice() {
        EmailTemplateType type = EmailTemplateType.MaintenanceRequestEntryNotice;

        MaintenanceRequestT requestT = EmailTemplateManager.getProto(type, MaintenanceRequestT.class);
        MaintenanceRequestWOT woT = EmailTemplateManager.getProto(type, MaintenanceRequestWOT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("NOTICE OF ENTRY"));
        template.content().setValue(i18n.tr(//@formatter:off
                "<h3>Dear {2},</h3><br/>" +
                "<br/>" +
                "This is to inform You that your landlord/agent will be entering your rental unit on {16} {17} to " +
                "perform maintenance or repair ({18}) in accordance with the following Maintenance Request:<br/>" +
                "<br/>" +
                "Building: {14}<br/>" +
                "Address: {15}<br/>" +
                "Unit: {1}<br/>" +
                "Tenant: {2}<br/>" +
                "<br/>" +
                "Summary: {3}<br/>" +
                "<br/>" +
                "Issue: {4}<br/>" +
                "Priority: {5}<br/>" +
                "<br/>" +
                "Description: {6}<br/>" +
                "<br/>" +
                "Permission to enter: {7}<br/>" +
                "Preferred Times:<br/>" +
                " 1 - {8}<br/>" +
                " 2 - {9}<br/>" +
                "<br/>" +
                // TODO
                // WO # : 01 <----- this should be a link to actual work order request
                // WO Created: June 6, 2013
                "[[{13}|Request ID: {10}]]<br/>" +
                "Request Submitted: {11}<br/>" +
                "Current Status: {12}<br/>",
                EmailTemplateManager.getVarname(requestT.propertyCode()),
                EmailTemplateManager.getVarname(requestT.unitNo()),
                EmailTemplateManager.getVarname(requestT.reporterName()),
                EmailTemplateManager.getVarname(requestT.summary()),
                EmailTemplateManager.getVarname(requestT.category()),
                EmailTemplateManager.getVarname(requestT.priority()),
                EmailTemplateManager.getVarname(requestT.description()),
                EmailTemplateManager.getVarname(requestT.permissionToEnter()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime1()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime2()),
                EmailTemplateManager.getVarname(requestT.requestId()),
                EmailTemplateManager.getVarname(requestT.submitted()),
                EmailTemplateManager.getVarname(requestT.status()),
                EmailTemplateManager.getVarname(requestT.requestViewUrl()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(bldT.Address()),
                EmailTemplateManager.getVarname(woT.scheduledDate()),
                EmailTemplateManager.getVarname(woT.scheduledTimeSlot()),
                EmailTemplateManager.getVarname(woT.workDescription())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateMaintenanceRequestUpdated() {
        EmailTemplateType type = EmailTemplateType.MaintenanceRequestUpdated;

        MaintenanceRequestT requestT = EmailTemplateManager.getProto(type, MaintenanceRequestT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Maintenance Request Updated"));
        template.content().setValue(i18n.tr(//@formatter:off
                "<h3>Dear {2},</h3><br/>" + // TODO - is this the same as reporter?
                "<br/>" +
                "This is to inform You that the Maintenance Request below has been updated:<br/>" +
                "<br/>" +
                "Building: {14}<br/>" +
                "Address: {15}<br/>" +
                "Unit: {1}<br/>" +
                "Tenant: {2}<br/>" +
                "<br/>" +
                "Summary: {3}<br/>" +
                "<br/>" +
                "Issue: {4}<br/>" +
                "Priority: {5}<br/>" +
                "<br/>" +
                "Description: {6}<br/>" +
                "<br/>" +
                "Permission to enter: {7}<br/>" +
                "Preferred Times:<br/>" +
                " 1 - {8}<br/>" +
                " 2 - {9}<br/>" +
                "<br/>" +
                // TODO
                // WO # : 01 <----- this should be a link to actual work order request
                // WO Created: June 6, 2013
                "[[{13}|Request ID: {10}]]<br/>" +
                "Request Submitted: {11}<br/>" +
                "Current Status: {12}<br/>",
                EmailTemplateManager.getVarname(requestT.propertyCode()),
                EmailTemplateManager.getVarname(requestT.unitNo()),
                EmailTemplateManager.getVarname(requestT.reporterName()),
                EmailTemplateManager.getVarname(requestT.summary()),
                EmailTemplateManager.getVarname(requestT.category()),
                EmailTemplateManager.getVarname(requestT.priority()),
                EmailTemplateManager.getVarname(requestT.description()),
                EmailTemplateManager.getVarname(requestT.permissionToEnter()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime1()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime2()),
                EmailTemplateManager.getVarname(requestT.requestId()),
                EmailTemplateManager.getVarname(requestT.submitted()),
                EmailTemplateManager.getVarname(requestT.status()),
                EmailTemplateManager.getVarname(requestT.requestViewUrl()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(bldT.Address())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateMaintenanceRequestCompleted() {
        EmailTemplateType type = EmailTemplateType.MaintenanceRequestCompleted;

        MaintenanceRequestT requestT = EmailTemplateManager.getProto(type, MaintenanceRequestT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Maintenance Request Completed"));
        template.content().setValue(i18n.tr(//@formatter:off
                "<h3>Dear {2},</h3><br/>" + // TODO - is this the same as reporter?
                "<br/>" +
                "This is to inform You that the Maintenance Request below has been completed and closed. " +
                "If requested work has not been done to your satisfaction, please call the office to advise " +
                "us accordingly.<br/>" +
                "Please complete the survey to rate your experience [[{16}|here]].<br/>" +
                "<br/>" +
                "The following Maintenance Request has been closed:<br/>" +
                "<br/>" +
                "Building: {14}<br/>" +
                "Address: {15}<br/>" +
                "Unit: {1}<br/>" +
                "Tenant: {2}<br/>" +
                "<br/>" +
                "Summary: {3}<br/>" +
                "<br/>" +
                "Issue: {4}<br/>" +
                "Priority: {5}<br/>" +
                "<br/>" +
                "Description: {6}<br/>" +
                "<br/>" +
                "Permission to enter: {7}<br/>" +
                "Preferred Times:<br/>" +
                " 1 - {8}<br/>" +
                " 2 - {9}<br/>" +
                "<br/>" +
                // TODO
                // WO # : 01 <----- this should be a link to actual work order request
                // WO Created: June 6, 2013
                "[[{13}|Request ID: {10}]]<br/>" +
                "Request Submitted: {11}<br/>" +
                "Request Completed: {16}<br/>",
                EmailTemplateManager.getVarname(requestT.propertyCode()),
                EmailTemplateManager.getVarname(requestT.unitNo()),
                EmailTemplateManager.getVarname(requestT.reporterName()),
                EmailTemplateManager.getVarname(requestT.summary()),
                EmailTemplateManager.getVarname(requestT.category()),
                EmailTemplateManager.getVarname(requestT.priority()),
                EmailTemplateManager.getVarname(requestT.description()),
                EmailTemplateManager.getVarname(requestT.permissionToEnter()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime1()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime2()),
                EmailTemplateManager.getVarname(requestT.requestId()),
                EmailTemplateManager.getVarname(requestT.submitted()),
                EmailTemplateManager.getVarname(requestT.status()),
                EmailTemplateManager.getVarname(requestT.requestViewUrl()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(bldT.Address()),
                EmailTemplateManager.getVarname(requestT.resolved()),
                "SurveyURL" // TODO
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateMaintenanceRequestCancelled() {
        EmailTemplateType type = EmailTemplateType.MaintenanceRequestCancelled;

        MaintenanceRequestT requestT = EmailTemplateManager.getProto(type, MaintenanceRequestT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Maintenance Request Cancelled"));
        template.content().setValue(i18n.tr(//@formatter:off
                "<h3>Dear {2},</h3><br/>" + // TODO - is this the same as reporter?
                "<br/>" +
                "This is to inform You that the Maintenance Request below has been cancelled for the following reason: " +
                "<br/>{16}<br/>" +
                "<br/>" +
                "Building: {14}<br/>" +
                "Address: {15}<br/>" +
                "Unit: {1}<br/>" +
                "Tenant: {2}<br/>" +
                "<br/>" +
                "Summary: {3}<br/>" +
                "<br/>" +
                "Issue: {4}<br/>" +
                "Priority: {5}<br/>" +
                "<br/>" +
                "Description: {6}<br/>" +
                "<br/>" +
                "Permission to enter: {7}<br/>" +
                "Preferred Times:<br/>" +
                " 1 - {8}<br/>" +
                " 2 - {9}<br/>" +
                "<br/>" +
                // TODO
                // WO # : 01 <----- this should be a link to actual work order request
                // WO Created: June 6, 2013
                "[[{13}|Request ID: {10}]]<br/>" +
                "Request Submitted: {11}<br/>" +
                "Current Status: {12}<br/>",
                EmailTemplateManager.getVarname(requestT.propertyCode()),
                EmailTemplateManager.getVarname(requestT.unitNo()),
                EmailTemplateManager.getVarname(requestT.reporterName()),
                EmailTemplateManager.getVarname(requestT.summary()),
                EmailTemplateManager.getVarname(requestT.category()),
                EmailTemplateManager.getVarname(requestT.priority()),
                EmailTemplateManager.getVarname(requestT.description()),
                EmailTemplateManager.getVarname(requestT.permissionToEnter()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime1()),
                EmailTemplateManager.getVarname(requestT.preferredDateTime2()),
                EmailTemplateManager.getVarname(requestT.requestId()),
                EmailTemplateManager.getVarname(requestT.submitted()),
                EmailTemplateManager.getVarname(requestT.status()),
                EmailTemplateManager.getVarname(requestT.requestViewUrl()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(bldT.Address()),
                EmailTemplateManager.getVarname(requestT.cancellationNote())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateOneTimePaymentSubmitted() {
        EmailTemplateType type = EmailTemplateType.OneTimePaymentSubmitted;

        PaymentT paymentT = EmailTemplateManager.getProto(type, PaymentT.class);
        TenantT tenantT = EmailTemplateManager.getProto(type, TenantT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("{0} - Your payment has been Submitted", EmailTemplateManager.getVarname(portalT.CompanyName())));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/>" +
                "<br/>" +
                "Thank you for submitting your payment.<br/>" +
                "<br/>" +
                "Your payment of <b>{1}</b> was submitted successfully on <b>{2}</b>.<br/>" +
                "<br/>" +
                "Please keep in mind, your payment is not considered paid until it processed by the bank successfully, which can take 1-3 business days.<br/>" +
                "<br/>" +
                "Your Payment Identification Reference Number for this payment is:<br/>" +
                "<br/>" +
                "<div style=\"margin-left:80px\">#<b>{3}</b></div><br/>" +
                "<br/>" +
                "You can review the status of your payment at anytime in your myCommunity portal <b>[[{4}|here]]</b><br/>" +
                "<br/>" +
                "Thank you for choosing {5}.",
                EmailTemplateManager.getVarname(tenantT.FirstName()),
                EmailTemplateManager.getVarname(paymentT.Amount()),
                EmailTemplateManager.getVarname(paymentT.Date()),
                EmailTemplateManager.getVarname(paymentT.ReferenceNumber()),
                EmailTemplateManager.getVarname(portalT.TenantPortalUrl()),
                EmailTemplateManager.getVarname(portalT.CompanyName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplatePaymentReceipt() {
        EmailTemplateType type = EmailTemplateType.PaymentReceipt;

        PaymentT paymentT = EmailTemplateManager.getProto(type, PaymentT.class);
        TenantT tenantT = EmailTemplateManager.getProto(type, TenantT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("{0} - Your payment has been Processed", EmailTemplateManager.getVarname(portalT.CompanyName())));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Thank you for submitting your payment.<br/><br/>" +
                "Your payment of <b>{1}</b> was successfully processed on <b>{2}</b> and your file has been updated accordingly.<br/><br/>" + 
                "Your Payment Identification Reference Number for this payment is:<br/><br/>" + 
                "<div style=\"margin-left:80px\">#<b>{3}</b></div><br/><br/>" +
                "You can review the status of your payment at anytime in your myCommunity portal <b>[[{4}|here]]</b><br/><br/>" +
                "Thank you for choosing {5}.",
                //TODO (If you do not wish to receive this notice any further you can opt out under your personal settings in your myCommunity portal here)
                EmailTemplateManager.getVarname(tenantT.FirstName()),
                EmailTemplateManager.getVarname(paymentT.Amount()),
                EmailTemplateManager.getVarname(paymentT.Date()),
                EmailTemplateManager.getVarname(paymentT.ReferenceNumber()),
                EmailTemplateManager.getVarname(portalT.TenantPortalUrl()),
                EmailTemplateManager.getVarname(portalT.CompanyName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplatePaymentReceiptWithWebPaymentFee() {
        EmailTemplateType type = EmailTemplateType.PaymentReceiptWithWebPaymentFee;

        PaymentT paymentT = EmailTemplateManager.getProto(type, PaymentT.class);
        TenantT tenantT = EmailTemplateManager.getProto(type, TenantT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("{0} - Your payment has been Processed", EmailTemplateManager.getVarname(portalT.CompanyName())));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Thank you for submitting your payment.<br/><br/>" +
                "Your payment of <b>{1}</b> and your convenience fee of <b>{2}</b> were submitted successfully on <b>{3}</b>. " + 
                "You will see two transaction lines for the payments above from your payment provider. The fee will appear as ''CCS*Web Payment Fee'' on your credit card statement<br/><br/>" +
                "Your Payment Identification Reference Number for these payments is:<br/><br/>" + 
                "<div style=\"margin-left:80px\">#<b>{4}</b></div><br/><br/>" +
                "You can review the status of your payment at anytime in your myCommunity portal <b>[[{5}|here]]</b><br/><br/>" +
                "Thank you for choosing {6}.",
                EmailTemplateManager.getVarname(tenantT.FirstName()),
                EmailTemplateManager.getVarname(paymentT.Amount()),
                EmailTemplateManager.getVarname(paymentT.ConvenienceFee()),
                EmailTemplateManager.getVarname(paymentT.Date()),
                EmailTemplateManager.getVarname(paymentT.ReferenceNumber()),
                EmailTemplateManager.getVarname(portalT.TenantPortalUrl()),
                EmailTemplateManager.getVarname(portalT.CompanyName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplatePaymentReturned() {
        EmailTemplateType type = EmailTemplateType.PaymentReturned;

        PaymentT paymentT = EmailTemplateManager.getProto(type, PaymentT.class);
        TenantT tenantT = EmailTemplateManager.getProto(type, TenantT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("{0} - YOUR PAYMENT WAS NOT PROCESSED", EmailTemplateManager.getVarname(portalT.CompanyName())));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Your payment of <b>{1}</b> on <b>{2}</b> was <b>not</b> successfully processed for the following reason:<br/><br/>" +
                "<div style=\"margin-left:80px\"><b>{3}</b></div><br/><br/>" +
                "Where applicable, an administrative fee has been added to your account for this payment reversal as per your agreement.<br/><br/>" + 
                "<b>Please sign in to your myCommunity account [[{4}|here]] to resubmit your payment to avoid any legal consequences.</b> <br/><br/>" +
                "For your reference, your payment Reference number for this transaction is:<br/><br/>" + 
                "<div style=\"margin-left:80px\">#<b>{5}</b></div><br/><br/>" +
                "You can review the status of your arrears on your myCommunity portal at anytime. To access your myCommunity Resident Portal click <b>[[{6}|here]]</b><br/><br/>" +
                "Thank you for choosing {7}.",
                EmailTemplateManager.getVarname(tenantT.FirstName()),
                EmailTemplateManager.getVarname(paymentT.Amount()),
                EmailTemplateManager.getVarname(paymentT.Date()),
                EmailTemplateManager.getVarname(paymentT.RejectReason()),
                EmailTemplateManager.getVarname(portalT.TenantPortalUrl()),
                EmailTemplateManager.getVarname(paymentT.ReferenceNumber()),
                EmailTemplateManager.getVarname(portalT.TenantPortalUrl()),
                EmailTemplateManager.getVarname(portalT.CompanyName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateAutoPaySetupConfirmation() {
        EmailTemplateType type = EmailTemplateType.AutoPaySetupConfirmation;

        AutopayAgreementT paymentT = EmailTemplateManager.getProto(type, AutopayAgreementT.class);
        TenantT tenantT = EmailTemplateManager.getProto(type, TenantT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("{0} - AutoPay Setup Confirmation", EmailTemplateManager.getVarname(portalT.CompanyName())));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Thank you for setting up your AutoPay payment.<br/><br/>" + 
                "Your payment of <b>{1}</b> has been successfully setup and will be processed automatically on the 1st of the month.<br/><br/>" + 
                "Your first payment will be processed on <b>{2}</b><br/><br/>" + 
                "You can review the status of your payment at anytime in your myCommunity portal <b>[[{3}|here]]</b> and easily make any changes to your AutoPay payment via your myCommunity portal.<br/><br/>" +
                "Thank you for choosing {4}.",
                EmailTemplateManager.getVarname(tenantT.FirstName()),
                EmailTemplateManager.getVarname(paymentT.Amount()),
                EmailTemplateManager.getVarname(paymentT.NextPaymentDate()),
                EmailTemplateManager.getVarname(portalT.TenantPortalUrl()),
                EmailTemplateManager.getVarname(portalT.CompanyName())
        ));//@formatter:on
        return template;
    }

}
