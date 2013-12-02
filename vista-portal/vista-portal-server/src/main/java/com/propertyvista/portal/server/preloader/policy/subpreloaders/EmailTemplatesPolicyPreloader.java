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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.communication.mail.template.EmailTemplateManager;
import com.propertyvista.biz.communication.mail.template.model.ApplicationT;
import com.propertyvista.biz.communication.mail.template.model.AutopayAgreementT;
import com.propertyvista.biz.communication.mail.template.model.BuildingT;
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
        policy.templates().add(defaultEmailTemplatePaymentReceiptWithConvenienceFee());
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
                "    <a style=\"color:#929733\" href=\"{2}\">Change Your Password</a>",
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
                "    <a style=\"color:#929733\" href=\"{2}\">Change Your Password</a>",
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
                "    <a style=\"color:#929733\" href=\"{2}\">Change Your Password</a>",
                EmailTemplateManager.getVarname(pwdReqT.RequestorName()),
                EmailTemplateManager.getVarname(portalT.CompanyName()),
                EmailTemplateManager.getVarname(pwdReqT.PasswordResetUrl())
        ));//@formatter:on
        return template;
    }

    //TODO
    private EmailTemplate defaultEmailTemplateProspectWelcome() {
        EmailTemplateType type = EmailTemplateType.ProspectWelcome;

        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Lease Application Created"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Your lease application has been created. The Application Reference Number is: {1}<br/><br/>" +
                "You can now start completing it online by logging to your account using following link: <br/><br/>" +
                "{2}<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{3}<br/>",
                EmailTemplateManager.getVarname(appT.ApplicantName()),
                EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                EmailTemplateManager.getVarname(portalT.ProspectPortalUrl()),
                EmailTemplateManager.getVarname(portalT.CompanyName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedApplicant() {
        EmailTemplateType type = EmailTemplateType.ApplicationCreatedApplicant;

        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Lease Application Created"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Your lease application has been created. The Application Reference Number is: {1}<br/><br/>" +
                "You can now start completing it online by logging to your account using following link: <br/><br/>" +
                "{2}<br/><br/>" +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at " +
                "{3}. Please have your Application Reference Number available.<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{4}<br/>" +
                "{5}<br/>",
                EmailTemplateManager.getVarname(appT.ApplicantName()),
                EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                EmailTemplateManager.getVarname(appT.SignUpUrl()),
                EmailTemplateManager.getVarname(bldT.MainOffice().Phone()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(bldT.Administrator().ContactName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedCoApplicant() {
        EmailTemplateType type = EmailTemplateType.ApplicationCreatedCoApplicant;

        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Lease Application Created"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Your lease application has been created. The Application Reference Number is: {1}<br/><br/>" +
                "You can now start completing it online by logging to your account using following link: <br/><br/>" +
                "{2}<br/><br/>" +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at " +
                "{3}. Please have your Application Reference Number available.<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{4}<br/>" +
                "{5}<br/>",
                EmailTemplateManager.getVarname(appT.ApplicantName()),
                EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                EmailTemplateManager.getVarname(appT.SignUpUrl()),
                EmailTemplateManager.getVarname(bldT.MainOffice().Phone()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(bldT.Administrator().ContactName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedGuarantor() {
        EmailTemplateType type = EmailTemplateType.ApplicationCreatedGuarantor;

        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Guarantor Application Created"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Your guarantor application has been created. The Application Reference Number is: {1}<br/><br/>" +
                "You can now start completing it online by logging to your account using following link: <br/><br/>" +
                "{2}<br/><br/>" +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at " +
                "{3}. Please have your Application Reference Number available.<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{4}<br/>" +
                "{5}<br/>",
                EmailTemplateManager.getVarname(appT.ApplicantName()),
                EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                EmailTemplateManager.getVarname(appT.SignUpUrl()),
                EmailTemplateManager.getVarname(bldT.MainOffice().Phone()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(bldT.Administrator().ContactName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationApproved() {
        EmailTemplateType type = EmailTemplateType.ApplicationApproved;

        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
        LeaseT leaseT = EmailTemplateManager.getProto(type, LeaseT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Application To Lease has been approved"));
        template.content().setValue(i18n.tr(//@formatter:off
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
                EmailTemplateManager.getVarname(appT.ApplicantName()),
                EmailTemplateManager.getVarname(leaseT.StartDateWeekDay()),
                EmailTemplateManager.getVarname(leaseT.StartDate()),
                EmailTemplateManager.getVarname(portalT.SiteHomeUrl()),
                EmailTemplateManager.getVarname(portalT.TenantPortalUrl()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(bldT.Administrator().ContactName())
        ));//@formatter:on
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationDeclined() {
        EmailTemplateType type = EmailTemplateType.ApplicationDeclined;

        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
        ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
        BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your Application has been declined"));
        template.content().setValue(i18n.tr(//@formatter:off
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
                EmailTemplateManager.getVarname(appT.ApplicantName()),          
                EmailTemplateManager.getVarname(portalT.ProspectPortalUrl()),
                EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                EmailTemplateManager.getVarname(bldT.Administrator().ContactName())
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
                "    <a style=\"color:#929733\" href=\"{2}\">Reset Your Password</a><br/>" +
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
                "<a href=\"{13}\">Request ID: {10}</a><br/>" +
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
                "<a href=\"{13}\">Request ID: {10}</a><br/>" +
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
                "<a href=\"{13}\">Request ID: {10}</a><br/>" +
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
                "<a href=\"{13}\">Request ID: {10}</a><br/>" +
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
                "Please complete the survey to rate your experience <a href='{16}'>here</a>.<br/>" +
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
                "<a href=\"{13}\">Request ID: {10}</a><br/>" +
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
                "<a href=\"{13}\">Request ID: {10}</a><br/>" +
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
        template.subject().setValue(i18n.tr("Your payment has been Submitted"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Thank you for submitting your payment.<br/><br/>" +
                "Your payment of <b>{1}</b> has been submitted successfully on <b>{2}</b>.<br/><br/>" + 
                "Please keep in mind, your payment is not considered paid until it gets processed by the bank successfully, which can take 1-3 business days.<br/><br/>" +
                "Your Payment Identification Reference Number for this payment is:<br/><br/>" + 
                "<div style=\"margin-left:80px\"><b>{3}</b></div><br/><br/>" +
                "You can review the status of your payment at anytime in your myCommunity portal here {4}<br/><br/>" +
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
        template.subject().setValue(i18n.tr("Your payment has been Processed"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Thank you for submitting your payment.<br/><br/>" +
                "Your payment of <b>{1}</b> has been successfully processed on <b>{2}</b> and your file has been updated accordingly.<br/><br/>" + 
                "Your Payment Identification Reference Number for this payment is:<br/><br/>" + 
                "<div style=\"margin-left:80px\"><b>{3}</b></div><br/><br/>" +
                "You can review the status of your payment at anytime in your myCommunity portal here {4}<br/><br/>" +
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

    private EmailTemplate defaultEmailTemplatePaymentReceiptWithConvenienceFee() {
        EmailTemplateType type = EmailTemplateType.PaymentReceiptWithConvenienceFee;

        PaymentT paymentT = EmailTemplateManager.getProto(type, PaymentT.class);
        TenantT tenantT = EmailTemplateManager.getProto(type, TenantT.class);
        PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);

        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
        template.useHeader().setValue(Boolean.TRUE);
        template.useFooter().setValue(Boolean.TRUE);
        template.type().setValue(type);
        template.subject().setValue(i18n.tr("Your payment has been Processed"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Thank you for submitting your payment.<br/><br/>" +
                "Your payment of <b>{1}</b> and your convenience fee of <b>{2}</b> has been submitted successfully on <b>{3}</b>. " + 
                "You will see two transaction lines for the payments above from your payment provider. The convenience fee will be shown as TBD<br/><br/>" +
                "Your Payment Identification Reference Number for this payments are:<br/><br/>" + 
                "<div style=\"margin-left:80px\">{4}</div><br/><br/>" +
                "You can review the status of your payment at anytime in your myCommunity portal here {5}<br/><br/>" +
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
        template.subject().setValue(i18n.tr("YOUR PAYMENT WAS NOT PROCESSED"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Your payment of <b>{1}</b> on <b>{2}</b> was <b>not</b> successfully processed for the following reason:<br/><br/>" +
                "<div style=\"margin-left:80px\"><b>{3}</b></div><br/><br/>" +
                "Where applicable, an administrative fee has been added to your account for this payment reversal as per your agreement.<br/><br/>" + 
                "<b>Please sign in to your myCommunity account here {4} to resubmit your payment to avoid any legal consequences.</b> <br/><br/>" +
                "For your reference, your payment Reference number for this transaction is:<br/><br/>" + 
                "<div style=\"margin-left:80px\"><b>{5}</b></div><br/><br/>" +
                "You can review the status of your arrears on your myCommunity portal at anytime. To access your myCommunity Resident Portal click {6}<br/><br/>" +
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
        template.subject().setValue(i18n.tr("AutoPay Setup Confirmation"));
        template.content().setValue(i18n.tr(//@formatter:off
                "Dear {0},<br/><br/>" +
                "Thank you for setting up your AutoPay payment.<br/><br/>" + 
                "Your payment of <b>{1}</b> has been successfully setup and will be processed automatically on the 1st of the month.<br/><br/>" + 
                "Your first payment will be processed on <b>{2}</b><br/><br/>" + 
                "You can review the status of your payment at anytime in your myCommunity portal here {3} and easily make any changes to your AutoPay payment via your myCommunity portal.<br/><br/>" +
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
