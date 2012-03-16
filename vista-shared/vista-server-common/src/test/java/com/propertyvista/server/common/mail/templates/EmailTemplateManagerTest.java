/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.server.common.mail.templates;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.server.common.mail.MessageTemplates;
import com.propertyvista.server.common.mail.templates.model.ApplicationT;
import com.propertyvista.server.common.mail.templates.model.BuildingT;
import com.propertyvista.server.common.mail.templates.model.LeaseT;
import com.propertyvista.server.common.mail.templates.model.PasswordRequestCrmT;
import com.propertyvista.server.common.mail.templates.model.PasswordRequestT;
import com.propertyvista.server.common.mail.templates.model.PortalLinksT;

public class EmailTemplateManagerTest extends VistaDBTestBase {

    private final static Logger log = LoggerFactory.getLogger(EmailTemplateManagerTest.class);

    private Building building;

    private OrganizationPoliciesNode orgNode;

    private CrmUser crmUser;

    private TenantInLease mainApp;

    private TenantInLease coApp;

    private Lease lease;

    private Application application;

    private final String token = "qwerty12345";

    private String adminName;

    private String officePhone;

    private String ptappHomeUrl;

    private String portalHomeUrl;

    private String tenantHomeUrl;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // main app url
        System.setProperty("com.pyx4j.appUrl", "http://www.propertyvista.com/");

        ptappHomeUrl = ServerSideConfiguration.instance().getMainApplicationURL() + AppPlaceInfo.absoluteUrl(DeploymentConsts.PTAPP_URL, null);
        portalHomeUrl = ServerSideConfiguration.instance().getMainApplicationURL() + DeploymentConsts.PORTAL_URL;
        tenantHomeUrl = ServerSideConfiguration.instance().getMainApplicationURL() + DeploymentConsts.TENANT_URL;
    }

    public void testTemplates() {
        log.info("STEP 1. Loading domain entities...");
        loadDomain();

        log.info("STEP 2. Validating generated emails...");
        EmailTemplateType type = EmailTemplateType.ApplicationApproved;
        String expected = getTemplateContent(type, true);
        String received = MessageTemplates.createApplicationApprovedEmail(mainApp);
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);

        type = EmailTemplateType.PasswordRetrievalTenant;
        expected = getTemplateContent(type, true);
        received = MessageTemplates.createPasswordResetEmail(VistaBasicBehavior.TenantPortal, mainApp.tenant().user(), token);
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);

        type = EmailTemplateType.PasswordRetrievalTenant;
        expected = getTemplateContent(type, true);
        received = MessageTemplates.createPasswordResetEmail(VistaBasicBehavior.ProspectiveApp, mainApp.tenant().user(), token);
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);

        type = EmailTemplateType.PasswordRetrievalCrm;
        expected = getTemplateContent(type, true);
        received = MessageTemplates.createPasswordResetEmail(VistaBasicBehavior.CRM, crmUser, token);
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);
    }

    public String getTemplateFormat(EmailTemplateType type) {
        String templateFmt = null;
        switch (type) {//@formatter:off
        case PasswordRetrievalCrm:
        case PasswordRetrievalTenant:
            templateFmt = "Dear {0},<br/>" +
                "This email was sent to you in response to your request to modify your Property Vista account password.<br/>" +
                "Click the link below to go to the Property Vista site and create new password for your account:<br/>" +
                "    <a style=\"color:#929733\" href=\"{1}\">Change Your Password</a>";
            break;
        case ApplicationCreatedApplicant:
            templateFmt = "<h3>Congratulations {0}!</h3><br/><br/>" +
                "You have successfully completed your application online.<br/><br/>" +
                "Please find attached a PDF copy of your application for your personal records.<br/><br/>" +
                "The Application Reference Number is: {1}<br/><br/>" +
                "Please allow us some time to review your application in full. " + 
                "We will be in touch with you shortly, typically within 48 hours, with our decision. " + 
                "Should you wish to check on the status of your application, you can go online to {2} " +
                "and use your username and password to access your account." +
                "In the meantime, should you have any concerns or questions, " +
                "please do not hesitate to contact us directly and have your Application Reference Number available.<br/<br/>" +
                "Sincerely,<br/><br/>" +
                "{3}<br/>" +
                "{4}<br/>";
            break;
        case ApplicationCreatedCoApplicant:
        case ApplicationCreatedGuarantor:
            templateFmt = "<h3>Thank you, {0}!</h3><br/><br/>" +
                "You have successfully completed your part of the application online." + 
                "Please find attached the PDF copy of your application for your reference." +
                "Your Application Reference Number is: {1}<br/><br/>" +
                "Once the entire application is completed successfully, we will process the application and a seperate email notification will be sent out to you." + 
                "To check on the status of the application, you can go to {2} and use your existing username and password to access the site." +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at {3} " +
                "Please have your Application Reference Number available.<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{4}<br/>" +
                "{5}<br/>";
            break;
        case ApplicationApproved:
            templateFmt = "<h3>Welcome {0} to your new home!</h3><br/><br/>" +                        
                "Your Application To Lease has been approved!<br/><br/>" +
                "As per your application, your lease start date is on {1}, {2}<br/></br>" +
                "We are excited to have you live with us. Please maintain your username and password that you have used for the application process. " +
                "This username and password will stay with you throughout your tenancy and will give you access to our Online Tenant Portal. " +
                "You can access the Portal at anytime by going to our website {3} and clicking under residents. " +
                "Alternatively you can reach the site directly by going to {4}.<br/><br/>" +
                "A member of our team will be in touch with you shortly to make move-in arrangements.<br/><br/>" +
                "In the meantime, should you have any concerns or questions, please do not hesistate to contact us directly.<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{5}<br/>" +
                "{6}<br/>";
            break;
        case ApplicationDeclined:
            templateFmt = "Dear {0},<br/><br/>" +                        
                "Unfortunately, based on the information provided your application has been DECLINED.<br/><br/>" +
                "We do encourage you to add more information to your application that could assist us in re-assessing this application.<br/>" +
                "Typically, additional Proof of Income or Guarantor(s)can change the application decision and allow us to re-evaluate the entire application.<br/>" +
                "We welcome you to access the application again utilizing the username and password you have previously created at {1} " +
                "to add more information.<br/>" +
                "Should you wish the cancel the application procedure at this time completely, no further actions are required.<br/><br/>" +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly and reference your " +
                "Application Reference Number {2}<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{3}<br/>" +
                "{4}<br/>";
            break;
        default:
            throw new Error("Template Type not implemented");
        }//@formatter:on
        return templateFmt;
    }

    public String getTemplateContent(EmailTemplateType type, boolean asString) {
        String fmt = getTemplateFormat(type);
        Object[] fmtArgs = null;
        switch (type) {//@formatter:off
        case PasswordRetrievalCrm:
            if (asString) {
                String[] args = {
                    crmUser.name().getValue(),
                    ServerSideConfiguration.instance().getMainApplicationURL()
                    + AppPlaceInfo.absoluteUrl(DeploymentConsts.CRM_URL, CrmSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, 
                    token)
                };
                fmtArgs = args;
            } else {
                PasswordRequestCrmT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestCrmT.class);
                String[] args = {
                    EmailTemplateManager.getVarname(pwdReqT.requestorName()),
                    EmailTemplateManager.getVarname(pwdReqT.passwordResetUrl())
                };
                fmtArgs = args;
            }
            break;
        case PasswordRetrievalTenant:
            if (asString) {
                String[] args = {
                    mainApp.tenant().user().name().getValue(),
                    ServerSideConfiguration.instance().getMainApplicationURL() + DeploymentConsts.TENANT_URL + '?' + AuthenticationService.AUTH_TOKEN_ARG + '='
                    + token
                };
                fmtArgs = args;
            } else {
                PasswordRequestT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestT.class);
                String[] args = {
                    EmailTemplateManager.getVarname(pwdReqT.requestorName()),
                    EmailTemplateManager.getVarname(pwdReqT.passwordResetUrl())
                };
                fmtArgs = args;
            }
            break;
        case ApplicationCreatedApplicant:
            if (asString) {
                String[] args = {
                    mainApp.tenant().user().name().getValue(),
                    application.id().getStringView(),
                    ptappHomeUrl,
                    building.marketing().name().getValue(),
                    adminName
                };
                fmtArgs = args;
            } else {
                PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
                ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
                BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
                String[] args = {
                    EmailTemplateManager.getVarname(appT.applicant()),
                    EmailTemplateManager.getVarname(appT.refNumber()),
                    EmailTemplateManager.getVarname(portalT.ptappHomeUrl()),
                    EmailTemplateManager.getVarname(bldT.propertyName()),
                    EmailTemplateManager.getVarname(bldT.administrator().name())
                };
                fmtArgs = args;
            }
            break;
        case ApplicationCreatedCoApplicant:
        case ApplicationCreatedGuarantor:
            if (asString) {
                String[] args = {
                    coApp.tenant().user().name().getValue(),
                    application.id().getStringView(),
                    ptappHomeUrl,
                    officePhone,
                    building.marketing().name().getValue(),
                    adminName
                };
                fmtArgs = args;
            } else {
                PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
                ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
                BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
                String[] args = {
                    EmailTemplateManager.getVarname(appT.applicant()),
                    EmailTemplateManager.getVarname(appT.refNumber()),
                    EmailTemplateManager.getVarname(portalT.ptappHomeUrl()),
                    EmailTemplateManager.getVarname(bldT.mainOffice().phone()),
                    EmailTemplateManager.getVarname(bldT.propertyName()),
                    EmailTemplateManager.getVarname(bldT.administrator().name())
                };
                fmtArgs = args;
            }
            break;
        case ApplicationApproved:
            if (asString) {
                String[] args = {
                    mainApp.tenant().user().name().getValue(),
                    new SimpleDateFormat("EEEE").format(lease.leaseFrom().getValue()),
                    lease.leaseFrom().getStringView(),
                    portalHomeUrl,
                    tenantHomeUrl,
                    building.marketing().name().getValue(),
                    adminName
                };
                fmtArgs = args;
            } else {
                PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
                ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
                BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
                LeaseT leaseT = EmailTemplateManager.getProto(type, LeaseT.class);
                String[] args = {
                    EmailTemplateManager.getVarname(appT.applicant()),
                    EmailTemplateManager.getVarname(leaseT.startDateWeekday()),
                    EmailTemplateManager.getVarname(leaseT.startDate()),
                    EmailTemplateManager.getVarname(portalT.portalHomeUrl()),
                    EmailTemplateManager.getVarname(portalT.tenantHomeUrl()),
                    EmailTemplateManager.getVarname(bldT.propertyName()),
                    EmailTemplateManager.getVarname(bldT.administrator().name())
                };
                fmtArgs = args;
            }
            break;
        case ApplicationDeclined:
            if (asString) {
                String[] args = {
                    mainApp.tenant().user().name().getValue(),
                    application.id().getStringView(),
                    ptappHomeUrl,
                    building.marketing().name().getValue(),
                    adminName
                };
                fmtArgs = args;
            } else {
                PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
                ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
                BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
                String[] args = {
                    EmailTemplateManager.getVarname(appT.applicant()),
                    EmailTemplateManager.getVarname(appT.refNumber()),
                    EmailTemplateManager.getVarname(portalT.ptappHomeUrl()),
                    EmailTemplateManager.getVarname(bldT.propertyName()),
                    EmailTemplateManager.getVarname(bldT.administrator().name())
                };
                fmtArgs = args;
            }
            break;
        }//@formatter:on
        log.info("format template: " + type + " (" + fmtArgs.length + " args)");
        return SimpleMessageFormat.format(fmt, fmtArgs);
    }

    public void loadDomain() {
        // user,crmUser/tenant/tenantInLease:applicant,co-applicants,guarantor
        // application
        // lease
        // policyNodes:unit/floorplan/building/complex/organization
        // emailTemplatesPolicy

        // crmUser
        crmUser = EntityFactory.create(CrmUser.class);
        crmUser.name().setValue(TestLoaderRandomGen.getFirstName() + " " + TestLoaderRandomGen.getLastName());
        Persistence.service().persist(crmUser);
        // country
        Country country = EntityFactory.create(Country.class);
        country.name().setValue("CA");
        Persistence.service().persist(country);
        Province prov = EntityFactory.create(Province.class);
        prov.name().setValue("Ontario");
        prov.code().setValue("ON");
        prov.country().set(country);
        Persistence.service().persist(prov);
        // load building
        building = EntityFactory.create(Building.class);
        building.propertyCode().setValue("B001");
        building.info().address().streetName().setValue(TestLoaderRandomGen.getStreetName());
        building.info().address().streetType().setValue(StreetType.street);
        building.info().address().streetNumber().setValue(String.valueOf(100 + TestLoaderRandomGen.randomInt(99)));
        building.info().address().city().setValue("Toronto");
        building.info().address().postalCode().setValue("A1B2C3");
        building.info().address().province().set(prov);
        building.info().address().county().setValue("CA");
        building.marketing().name().setValue(SimpleMessageFormat.format(//@formatter:off
            "{0} {1} {2}",
            building.info().address().streetNumber().getValue(),
            building.info().address().streetName().getValue(),
            building.info().address().streetType().getValue().toString()
        ));//@formatter:on
        building.contacts().website().setValue("www.property-" + building.propertyCode().getValue() + ".com");
        // creaate admin contact
        PropertyContact admin = EntityFactory.create(PropertyContact.class);
        admin.type().setValue(PropertyContact.Type.administrator);
        admin.name().setValue(TestLoaderRandomGen.getFirstName() + " " + TestLoaderRandomGen.getLastName());
        admin.phone().setValue(TestLoaderRandomGen.createPhone());
        admin.email().setValue(admin.name().getValue().toLowerCase().replace(" ", ".") + "@propertyvista.com");
        building.contacts().propertyContacts().add(admin);
        adminName = admin.name().getValue();
        // creaate office contact
        PropertyContact office = EntityFactory.create(PropertyContact.class);
        office.type().setValue(PropertyContact.Type.mainOffice);
        office.name().setValue(TestLoaderRandomGen.getFirstName() + " " + TestLoaderRandomGen.getLastName());
        office.phone().setValue(TestLoaderRandomGen.createPhone());
        office.email().setValue(office.name().getValue().toLowerCase().replace(" ", ".") + "@propertyvista.com");
        building.contacts().propertyContacts().add(office);
        officePhone = office.phone().getValue();
        // creaate super contact
        PropertyContact superint = EntityFactory.create(PropertyContact.class);
        superint.type().setValue(PropertyContact.Type.superintendent);
        superint.name().setValue(TestLoaderRandomGen.getFirstName() + " " + TestLoaderRandomGen.getLastName());
        superint.phone().setValue(TestLoaderRandomGen.createPhone());
        superint.email().setValue(superint.name().getValue().toLowerCase().replace(" ", ".") + "@propertyvista.com");
        building.contacts().propertyContacts().add(superint);
        Persistence.service().persist(building);

        // create application
        MasterApplication mApp = EntityFactory.create(MasterApplication.class);
        Persistence.service().persist(mApp);
        application = EntityFactory.create(Application.class);
        application.belongsTo().set(mApp);
        Persistence.service().persist(application);

        // create lease
        AptUnit unit = EntityFactory.create(AptUnit.class);
        unit.belongsTo().set(building);
        Persistence.service().persist(unit);
        lease = EntityFactory.create(Lease.class);
        lease.unit().set(unit);
        lease.leaseFrom().setValue(new LogicalDate());
        Persistence.service().persist(lease);

        // load main applicant
        TenantUser user = EntityFactory.create(TenantUser.class);
        user.name().setValue(TestLoaderRandomGen.getFirstName() + " " + TestLoaderRandomGen.getLastName());
        Persistence.service().persist(user);
        Tenant tenant = EntityFactory.create(Tenant.class);
        tenant.user().set(user);
        Persistence.service().persist(tenant);
        mainApp = EntityFactory.create(TenantInLease.class);
        mainApp.tenant().set(tenant);
        mainApp.role().setValue(TenantInLease.Role.Applicant);
        mainApp.application().set(application);
        mainApp.lease().set(lease.version());
        Persistence.service().persist(mainApp);

        // load main co-applicant
        user = EntityFactory.create(TenantUser.class);
        user.name().setValue(TestLoaderRandomGen.getFirstName() + " " + TestLoaderRandomGen.getLastName());
        Persistence.service().persist(user);
        tenant = EntityFactory.create(Tenant.class);
        tenant.user().set(user);
        Persistence.service().persist(tenant);
        coApp = EntityFactory.create(TenantInLease.class);
        coApp.tenant().set(tenant);
        coApp.role().setValue(TenantInLease.Role.CoApplicant);
        coApp.application().set(application);
        coApp.lease().set(lease.version());
        Persistence.service().persist(coApp);

        // TODO load guarantor
        // ...

        // load email policy
        orgNode = EntityFactory.create(OrganizationPoliciesNode.class);
        Persistence.service().persist(orgNode);

        EmailTemplatesPolicy policy = EntityFactory.create(EmailTemplatesPolicy.class);
        for (EmailTemplateType type : EmailTemplateType.values()) {
            EmailTemplate template = EntityFactory.create(EmailTemplate.class);
            template.type().setValue(type);
            template.subject().setValue(type.toString());
            template.content().setValue(getTemplateContent(type, false));
            policy.templates().add(template);
        }
        policy.node().set(orgNode);
        Persistence.service().persist(policy);
    }
}
