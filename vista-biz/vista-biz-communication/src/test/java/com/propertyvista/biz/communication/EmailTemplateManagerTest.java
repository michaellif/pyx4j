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
package com.propertyvista.biz.communication;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.communication.mail.template.EmailTemplateManager;
import com.propertyvista.biz.communication.mail.template.model.ApplicationT;
import com.propertyvista.biz.communication.mail.template.model.BuildingT;
import com.propertyvista.biz.communication.mail.template.model.LeaseT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestCrmT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestProspectT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestTenantT;
import com.propertyvista.biz.communication.mail.template.model.PortalLinksT;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.config.tests.VistaTestDBSetup;
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
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteTitles;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class EmailTemplateManagerTest extends VistaDBTestBase {

    private final static Logger log = LoggerFactory.getLogger(EmailTemplateManagerTest.class);

    private Building building;

    private OrganizationPoliciesNode orgNode;

    private SiteDescriptor siteDescriptor;

    private final String copyright = "© Property Vista Software Inc. 2012";

    private final String company = "Property Vista";

    private CrmUser crmUser;

    private Tenant mainAplt;

    private Tenant coAplt;

    private Lease lease;

    private OnlineApplication mainApp;

    private OnlineApplication coApp;

    private final String token = "qwerty12345";

    private String appUrl;

    private String adminName;

    private String officePhone;

    private String ptappHomeUrl;

    private String portalHomeUrl;

    private String tenantHomeUrl;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        VistaTestDBSetup.createPmc();

        portalHomeUrl = VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.TenantPortal, false);
        tenantHomeUrl = VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.TenantPortal, true) + DeploymentConsts.TENANT_URL;
        ptappHomeUrl = VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.ProspectiveApp, true);

        appUrl = AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.ProspectiveApp, true), PtSiteMap.LoginWithToken.class,
                AuthenticationService.AUTH_TOKEN_ARG, token);
    }

    public void testTemplates() {
        log.info("STEP 1. Loading domain entities...");
        loadDomain();

        log.info("STEP 2. Validating generated emails...");
        EmailTemplateType type = EmailTemplateType.ApplicationApproved;
        String expected = getTemplateContent(type, true);
        MailMessage email = null;
        email = MessageTemplates.createApplicationStatusEmail(mainAplt, type);
        String received = email.getHtmlBody();
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);

        type = EmailTemplateType.ApplicationDeclined;
        expected = getTemplateContent(type, true);
        email = MessageTemplates.createApplicationStatusEmail(mainAplt, type);
        received = email.getHtmlBody();
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);

        type = EmailTemplateType.PasswordRetrievalTenant;
        expected = getTemplateContent(type, true);
        email = MessageTemplates.createCustomerPasswordResetEmail(EmailTemplateType.PasswordRetrievalTenant, mainAplt.customer().user(), token);
        received = email.getHtmlBody();
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);

        type = EmailTemplateType.PasswordRetrievalProspect;
        expected = getTemplateContent(type, true);
        email = MessageTemplates.createCustomerPasswordResetEmail(EmailTemplateType.PasswordRetrievalProspect, mainAplt.customer().user(), token);
        received = email.getHtmlBody();
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);

        type = EmailTemplateType.PasswordRetrievalCrm;
        expected = getTemplateContent(type, true);
        email = MessageTemplates.createCrmPasswordResetEmail(crmUser, token);
        received = email.getHtmlBody();
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);

        type = EmailTemplateType.TenantInvitation;
        expected = getTemplateContent(type, true);
        email = MessageTemplates.createTenantInvitationEmail(mainAplt, type, token);
        received = email.getHtmlBody();
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);

        type = EmailTemplateType.ApplicationCreatedApplicant;
        expected = getTemplateContent(type, true);
        email = MessageTemplates.createTenantInvitationEmail(mainAplt, type, token);
        received = email.getHtmlBody();
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);

        type = EmailTemplateType.ApplicationCreatedCoApplicant;
        expected = getTemplateContent(type, true);
        email = MessageTemplates.createTenantInvitationEmail(coAplt, type, token);
        received = email.getHtmlBody();
        assertEquals(type.toString(), expected, received);
        log.info(type.toString() + " content: " + received);

        // TODO implement guarantor template test
        type = EmailTemplateType.ApplicationCreatedGuarantor;
    }

    public String getTemplateFormat(EmailTemplateType type) {
        String templateFmt = null;
        switch (type) {//@formatter:off
        case PasswordRetrievalCrm:
        case PasswordRetrievalProspect:
        case PasswordRetrievalTenant:
            templateFmt = "Dear {0},<br/>" +
                "This email was sent to you in response to your request to modify your Property Vista account password.<br/>" +
                "Click the link below to go to the Property Vista site and create new password for your account:<br/>" +
                "    <a style=\"color:#929733\" href=\"{1}\">Change Your Password</a>";
            break;
        case ApplicationCreatedApplicant:
        case ApplicationCreatedCoApplicant:
            templateFmt = "Dear {0},<br/>" +
                "Your lease application has been created. The Application Reference Number is: {1}.<br/><br/>" +
                "You can now start completing it online by logging to your account using following link: <br/><br/>" +
                "{2}<br/><br/>" +
                "In the meantime, should you have any concerns or questions, please do not hesitate to contact us directly at {3} " +
                "Please have your Application Reference Number available.<br/><br/>" +
                "Sincerely,<br/><br/>" +
                "{4}<br/>" +
                "{5}<br/>";
            break;
        case ApplicationCreatedGuarantor:
            templateFmt = "Dear {0},<br/>" +
                "Your guarantor application has been created. The Application Reference Number is: {1}.<br/><br/>" +
                "You can now start completing it online by logging to your account using following link: <br/><br/>" +
                "{2}<br/><br/>" +
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
        case TenantInvitation:
            templateFmt = "Welcome {0}!<br/><br/>" +                        
            "We are excited to have you join the Online Tenant Portal of {1} that we created just for you! " +
            "To access the site and create new password for your account please follow the link below:<br/>\n" +
            "{2}<br/>" +
            "Please keep your username and password as they will be required to access your Portal. " +
            "You can visit it anytime by going to<br/>" +
            "{3} and clicking on Residents menu tab. You will be redirected to Online Tenant Portal immediately. " +
            "Or, alternatively, you can reach that page directly by going to<br/>" +
            "{4}.<br/><br/>" +
            "Should you have any concerns or questions, please do not hesitate to contact us directly.<br/><br/>" +
            "Sincerely,<br/><br/>" +
            "{5}<br/>" +
            "{6}<br/>";
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
                    AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.CRM, true), CrmSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, 
                    token)
                };
                fmtArgs = args;
            } else {
                PasswordRequestCrmT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestCrmT.class);
                String[] args = {
                    EmailTemplateManager.getVarname(pwdReqT.RequestorName()),
                    EmailTemplateManager.getVarname(pwdReqT.PasswordResetUrl())
                };
                fmtArgs = args;
            }
            break;
        case PasswordRetrievalTenant:
            if (asString) {
                String[] args = {
                    mainAplt.customer().user().name().getValue(),
                    VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.TenantPortal, true) + DeploymentConsts.TENANT_URL + '?' + AuthenticationService.AUTH_TOKEN_ARG + '='
                    + token
                };
                fmtArgs = args;
            } else {
                PasswordRequestTenantT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestTenantT.class);
                String[] args = {
                    EmailTemplateManager.getVarname(pwdReqT.RequestorName()),
                    EmailTemplateManager.getVarname(pwdReqT.PasswordResetUrl())
                };
                fmtArgs = args;
            }
            break;
        case PasswordRetrievalProspect:
            if (asString) {
                String[] args = {
                    mainAplt.customer().user().name().getValue(),
                    AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.ProspectiveApp, true), PtSiteMap.LoginWithToken.class,
                            AuthenticationService.AUTH_TOKEN_ARG, token)
                };
                fmtArgs = args;
            } else {
                PasswordRequestProspectT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestProspectT.class);
                String[] args = {
                    EmailTemplateManager.getVarname(pwdReqT.RequestorName()),
                    EmailTemplateManager.getVarname(pwdReqT.PasswordResetUrl())
                };
                fmtArgs = args;
            }
            break;
        case ApplicationCreatedApplicant:
            if (asString) {
                String[] args = {
                    mainAplt.customer().user().name().getValue(),
                    mainApp.id().getStringView(),
                    appUrl,
                    officePhone,
                    building.marketing().name().getValue(),
                    adminName
                };
                fmtArgs = args;
            } else {
                ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
                BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
                String[] args = {
                    EmailTemplateManager.getVarname(appT.ApplicantName()),
                    EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                    EmailTemplateManager.getVarname(appT.SignUpUrl()),
                    EmailTemplateManager.getVarname(bldT.MainOffice().Phone()),
                    EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                    EmailTemplateManager.getVarname(bldT.Administrator().ContactName())
                };
                fmtArgs = args;
            }
            break;
        case ApplicationCreatedCoApplicant:
        case ApplicationCreatedGuarantor:
            if (asString) {
                String[] args = {
                    coAplt.customer().user().name().getValue(),
                    coApp.id().getStringView(),
                    appUrl,
                    officePhone,
                    building.marketing().name().getValue(),
                    adminName
                };
                fmtArgs = args;
            } else {
                ApplicationT appT = EmailTemplateManager.getProto(type, ApplicationT.class);
                BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
                String[] args = {
                    EmailTemplateManager.getVarname(appT.ApplicantName()),
                    EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                    EmailTemplateManager.getVarname(appT.SignUpUrl()),
                    EmailTemplateManager.getVarname(bldT.MainOffice().Phone()),
                    EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                    EmailTemplateManager.getVarname(bldT.Administrator().ContactName())
                };
                fmtArgs = args;
            }
            break;
        case ApplicationApproved:
            if (asString) {
                String[] args = {
                    mainAplt.customer().user().name().getValue(),
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
                    EmailTemplateManager.getVarname(appT.ApplicantName()),
                    EmailTemplateManager.getVarname(leaseT.StartDateWeekDay()),
                    EmailTemplateManager.getVarname(leaseT.StartDate()),
                    EmailTemplateManager.getVarname(portalT.PortalHomeUrl()),
                    EmailTemplateManager.getVarname(portalT.TenantPortalUrl()),
                    EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                    EmailTemplateManager.getVarname(bldT.Administrator().ContactName())
                };
                fmtArgs = args;
            }
            break;
        case ApplicationDeclined:
            if (asString) {
                String[] args = {
                    mainAplt.customer().user().name().getValue(),
                    mainApp.id().getStringView(),
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
                    EmailTemplateManager.getVarname(appT.ApplicantName()),
                    EmailTemplateManager.getVarname(appT.ReferenceNumber()),
                    EmailTemplateManager.getVarname(portalT.ProspectPortalUrl()),
                    EmailTemplateManager.getVarname(bldT.PropertyMarketingName()),
                    EmailTemplateManager.getVarname(bldT.Administrator().ContactName())
                };
                fmtArgs = args;
            }
            break;
        case TenantInvitation:
            if (asString) {
                String[] args = {
                    mainAplt.customer().user().name().getValue(),
                    company,
                    VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.TenantPortal, true) + DeploymentConsts.TENANT_URL + '?' + AuthenticationService.AUTH_TOKEN_ARG + '='
                    + token,
                    portalHomeUrl,
                    tenantHomeUrl,
                    building.marketing().name().getValue(),
                    adminName
                };
                fmtArgs = args;
            } else {
                PortalLinksT portalT = EmailTemplateManager.getProto(type, PortalLinksT.class);
                PasswordRequestTenantT pwdReqT = EmailTemplateManager.getProto(type, PasswordRequestTenantT.class);
                BuildingT bldT = EmailTemplateManager.getProto(type, BuildingT.class);
                String[] args = {
                        EmailTemplateManager.getVarname(pwdReqT.RequestorName()),
                        EmailTemplateManager.getVarname(portalT.CompanyName()),
                        EmailTemplateManager.getVarname(pwdReqT.PasswordResetUrl()),
                        EmailTemplateManager.getVarname(portalT.PortalHomeUrl()),
                        EmailTemplateManager.getVarname(portalT.TenantPortalUrl()),
                        EmailTemplateManager.getVarname(bldT.PropertyMarketingName()) ,
                        EmailTemplateManager.getVarname(bldT.Administrator().ContactName())
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

        // site descriptor
        siteDescriptor = EntityFactory.create(SiteDescriptor.class);
        SiteTitles titles = EntityFactory.create(SiteTitles.class);
        titles.copyright().setValue(copyright);
        titles.residentPortalTitle().setValue(company);
        siteDescriptor.siteTitles().add(titles);
        Persistence.service().persist(siteDescriptor);
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
        admin.type().setValue(PropertyContact.PropertyContactType.administrator);
        admin.name().setValue(TestLoaderRandomGen.getFirstName() + " " + TestLoaderRandomGen.getLastName());
        admin.phone().setValue(TestLoaderRandomGen.createPhone());
        admin.email().setValue(admin.name().getValue().toLowerCase().replace(" ", ".") + "@propertyvista.com");
        building.contacts().propertyContacts().add(admin);
        adminName = admin.name().getValue();
        // creaate office contact
        PropertyContact office = EntityFactory.create(PropertyContact.class);
        office.type().setValue(PropertyContact.PropertyContactType.mainOffice);
        office.name().setValue(TestLoaderRandomGen.getFirstName() + " " + TestLoaderRandomGen.getLastName());
        office.phone().setValue(TestLoaderRandomGen.createPhone());
        office.email().setValue(office.name().getValue().toLowerCase().replace(" ", ".") + "@propertyvista.com");
        building.contacts().propertyContacts().add(office);
        officePhone = office.phone().getValue();
        // creaate super contact
        PropertyContact superint = EntityFactory.create(PropertyContact.class);
        superint.type().setValue(PropertyContact.PropertyContactType.superintendent);
        superint.name().setValue(TestLoaderRandomGen.getFirstName() + " " + TestLoaderRandomGen.getLastName());
        superint.phone().setValue(TestLoaderRandomGen.createPhone());
        superint.email().setValue(superint.name().getValue().toLowerCase().replace(" ", ".") + "@propertyvista.com");
        building.contacts().propertyContacts().add(superint);
        Persistence.service().persist(building);

        // create lease
        AptUnit unit = EntityFactory.create(AptUnit.class);
        unit.belongsTo().set(building);
        Persistence.service().persist(unit);
        lease = EntityFactory.create(Lease.class);
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        lease.unit().set(unit);
        lease.leaseFrom().setValue(new LogicalDate());
        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(lease);

        // load main applicant
        CustomerUser user = EntityFactory.create(CustomerUser.class);
        user.name().setValue(TestLoaderRandomGen.getFirstName() + " " + TestLoaderRandomGen.getLastName());
        Persistence.service().persist(user);
        Customer tenant = EntityFactory.create(Customer.class);
        tenant.user().set(user);
        Persistence.service().persist(tenant);
        mainAplt = EntityFactory.create(Tenant.class);
        mainAplt.customer().set(tenant);
        mainAplt.role().setValue(LeaseParticipant.Role.Applicant);
        mainAplt.leaseV().set(lease.version());
        Persistence.service().persist(mainAplt);

        // load main co-applicant
        user = EntityFactory.create(CustomerUser.class);
        user.name().setValue(TestLoaderRandomGen.getFirstName() + " " + TestLoaderRandomGen.getLastName());
        Persistence.service().persist(user);
        tenant = EntityFactory.create(Customer.class);
        tenant.user().set(user);
        Persistence.service().persist(tenant);
        coAplt = EntityFactory.create(Tenant.class);
        coAplt.customer().set(tenant);
        coAplt.role().setValue(LeaseParticipant.Role.CoApplicant);
        coAplt.application().set(mainApp);
        coAplt.leaseV().set(lease.version());
        Persistence.service().persist(coAplt);

        // TODO load guarantor
        // ...

        // create applications
        MasterOnlineApplication mApp = EntityFactory.create(MasterOnlineApplication.class);
        Persistence.service().persist(mApp);
        mainApp = EntityFactory.create(OnlineApplication.class);
        mainApp.masterOnlineApplication().set(mApp);
        mainApp.customer().set(mainAplt.customer());
        Persistence.service().persist(mainApp);
        mainAplt.application().set(mainApp);
        Persistence.service().merge(mainAplt);

        coApp = EntityFactory.create(OnlineApplication.class);
        coApp.masterOnlineApplication().set(mApp);
        coApp.customer().set(coAplt.customer());
        Persistence.service().persist(coApp);
        coAplt.application().set(coApp);
        Persistence.service().merge(coAplt);

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
