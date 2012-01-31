/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 16, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.BuildingsGenerator;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.policy.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.PolicyAtNode;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.policy.policies.LeaseTermsPolicy;
import com.propertyvista.domain.policy.policies.MiscPolicy;
import com.propertyvista.domain.policy.policies.PetPolicy;
import com.propertyvista.domain.policy.policies.specials.EmailTemplate;
import com.propertyvista.domain.policy.policies.specials.IdentificationDocument;
import com.propertyvista.domain.policy.policies.specials.LegalTermsContent;
import com.propertyvista.domain.policy.policies.specials.LegalTermsDescriptor;
import com.propertyvista.domain.policy.policies.specials.PetConstraints;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.shared.CompiledLocale;

public class PolicyPreloader extends BaseVistaDevDataPreloader {

    private static final I18n i18n = I18n.get(PolicyPreloader.class);

    private static final Logger log = LoggerFactory.getLogger(PolicyPreloader.class);

    @Override
    public String create() {
        // Create the node for default policies and default policies
        OrganizationPoliciesNode organizationalPoliciesNode = EntityFactory.create(OrganizationPoliciesNode.class);
        Persistence.service().persist(organizationalPoliciesNode);

        List<? extends Policy> defaults = Arrays.asList(//@formatter:off
                createMiscPolicy(),
                createDefaultApplicationDocumentationPolicy(),
                createDefaultLeaseTermsPolicy(),
                createDefaultPetPolicy(),
                createDefaultEmailTemplatesPolicy()
        );//@formatter:on

        for (Policy policy : defaults) {
            PolicyAtNode policyAtNode = EntityFactory.create(PolicyAtNode.class);
            policyAtNode.node().set(organizationalPoliciesNode);
            policyAtNode.policy().set(policy);
            Persistence.service().persist(policyAtNode);
        }

        return "Created default global policies";
    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(PolicyAtNode.class, IdentificationDocument.class, OrganizationPoliciesNode.class);
        } else {
            return "This is production";
        }
    }

    private MiscPolicy createMiscPolicy() {
        MiscPolicy misc = EntityFactory.create(MiscPolicy.class);

        misc.occupantsOver18areApplicants().setValue(false);
        misc.occupantsPerBedRoom().setValue(2d);
        misc.oneMonthDeposit().setValue(false);
        misc.maxParkingSpots().setValue(3);
        misc.maxPets().setValue(4);
        Persistence.service().persist(misc);

        return misc;
    }

    private ApplicationDocumentationPolicy createDefaultApplicationDocumentationPolicy() {
        ApplicationDocumentationPolicy policy = EntityFactory.create(ApplicationDocumentationPolicy.class);
        policy.numberOfRequiredIDs().setValue(2);

        IdentificationDocument id = EntityFactory.create(IdentificationDocument.class);
        id.name().setValue(i18n.tr("Passport"));
        policy.allowedIDs().add(id);

        id = EntityFactory.create(IdentificationDocument.class);
        id.name().setValue(i18n.tr("Drivers License"));
        policy.allowedIDs().add(id);

        id = EntityFactory.create(IdentificationDocument.class);
        id.name().setValue(i18n.tr("Citizenship Card"));
        policy.allowedIDs().add(id);

        Persistence.service().persist(policy);
        return policy;
    }

    private PetPolicy createDefaultPetPolicy() {
        PetPolicy petPolicy = EntityFactory.create(PetPolicy.class);

        EntityQueryCriteria<ProductItemType> criteria = new EntityQueryCriteria<ProductItemType>(ProductItemType.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().featureType(), Feature.Type.pet));
        for (ProductItemType pet : Persistence.service().query(criteria)) {
            PetConstraints constraints = EntityFactory.create(PetConstraints.class);
            if (i18n.tr("Cat").equals(pet.name().getValue())) {
                constraints.maxNumber().setValue(10);
                constraints.maxWeight().setValue(5.0);
            } else if (i18n.tr("Dog").equals(pet.name().getValue())) {
                constraints.maxNumber().setValue(3);
                constraints.maxWeight().setValue(50.0);
            } else {
                constraints.maxNumber().setValue(0);
                constraints.maxWeight().setValue(0.0);
            }
            constraints.pet().set(pet);
            petPolicy.constraints().add(constraints);
        }
        Persistence.service().persist(petPolicy);
        return petPolicy;
    }

    private EmailTemplatesPolicy createDefaultEmailTemplatesPolicy() {
        EmailTemplatesPolicy policy = EntityFactory.create(EmailTemplatesPolicy.class);

        policy.passwordRetrievalCrm().set(defaultEmailTemplatePasswordRetrievalCrm());
        policy.passwordRetrievalTenant().set(defaultEmailTemplatePasswordRetrievalTenant());
        policy.applicationCreatedApplicant().set(defaultEmailTemplateApplicationCreatedApplicant());
        policy.applicationCreatedCoApplicant().set(defaultEmailTemplateApplicationCreatedCoApplicant());
        policy.applicationCreatedGuarantor().set(defaultEmailTemplateApplicationCreatedGuarantor());
        policy.applicationApproved().set(defaultEmailTemplateApplicationApproved());
        policy.applicationDeclined().set(defaultEmailTemplateApplicationDeclined());

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
        template.subject().setValue(i18n.tr("here be subject"));
        template.content().setValue(wrapHtml(i18n.tr("here be body")));
        return template;
    }

    private EmailTemplate defaultEmailTemplateApplicationCreatedApplicant() {
        EmailTemplate template = EntityFactory.create(EmailTemplate.class);
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
        template.subject().setValue(i18n.tr("here be subject"));
        template.content().setValue(wrapHtml(i18n.tr("here be body")));
        return template;
    }

    private LeaseTermsPolicy createDefaultLeaseTermsPolicy() {
        LeaseTermsPolicy policy = EntityFactory.create(LeaseTermsPolicy.class);

        String termsContentText = "failed to get lease terms during the preload phase";
        try {
            termsContentText = IOUtils.getTextResource("leaseTerms.html", BuildingsGenerator.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Find Local if exists
        EntityQueryCriteria<AvailableLocale> criteria = EntityQueryCriteria.create(AvailableLocale.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lang(), CompiledLocale.en));
        AvailableLocale en = Persistence.service().retrieve(criteria);
        if (en == null) {
            en = EntityFactory.create(AvailableLocale.class);
            en.lang().setValue(CompiledLocale.en);
            Persistence.service().persist(en);
        }

        String caption = i18n.tr("Mockup Summary Terms");
        policy.summaryTerms().add(createLegalTermsDescriptor(caption, "", createLegalTermsContent(caption, en, termsContentText)));

        caption = i18n.tr("Mockup One Time Payment Terms");
        policy.oneTimePaymentTerms().set(createLegalTermsDescriptor(caption, "", createLegalTermsContent(caption, en, termsContentText)));

        caption = i18n.tr("Mockup Recurrent Time Payment Terms");
        policy.recurrentPaymentTerms().set(createLegalTermsDescriptor(caption, "", createLegalTermsContent(caption, en, termsContentText)));

        Persistence.service().merge(policy);

        return policy;
    }

    private LegalTermsDescriptor createLegalTermsDescriptor(String name, String description, LegalTermsContent... content) {
        LegalTermsDescriptor descriptor = EntityFactory.create(LegalTermsDescriptor.class);
        descriptor.name().setValue(name);
        descriptor.description().setValue(description);
        for (LegalTermsContent c : content) {
            descriptor.content().add(c);
        }
        return descriptor;
    }

    private LegalTermsContent createLegalTermsContent(String caption, AvailableLocale locale, String contentText) {
        LegalTermsContent content = EntityFactory.create(LegalTermsContent.class);
        content.locale().set(locale);
        content.localizedCaption().setValue(caption);
        content.content().setValue(contentText);
        return content;
    }

}
