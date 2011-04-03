/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-02
 * @author vlads
 * @version $Id$
 */
package org.selenium;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.propertyvista.unit.VistaDevLogin;
import com.propertyvista.unit.config.ApplicationId;
import com.propertyvista.unit.config.VistaSeleniumTestConfiguration;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.selenium.BaseSeleniumTestCase;
import com.pyx4j.selenium.ISeleniumTestConfiguration;
import com.pyx4j.site.rpc.AppPlaceInfo;

public class GaeAppLoginTest extends BaseSeleniumTestCase {

    private static final Logger log = LoggerFactory.getLogger(GaeAppLoginTest.class);

    final public String blankpage = "about:blank";

    final public String testUser = "Bob";

    final public String emailAt = "@local.com";

    private final String strNow = new SimpleDateFormat("yyyyMMdd-hhmmss").format(Calendar.getInstance().getTime()); //need to persist across test cases 

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new VistaSeleniumTestConfiguration(ApplicationId.portal);
    }

    public void loginToMainSite() throws Exception {

        VistaDevLogin.login(selenium);

        //PLEASE ADD BREAKPOINT ON THE NEXT LINE//
        selenium.waitFor(By.id("gwt-debug-Auth_Login"), 10);
        // Initialize access to GlassPanel
        selenium.setGlassPanelAware();
        // wait while site will initialize
        selenium.waitWhileWorking();

    }

    public void testLoginExistingUser() throws Exception {
        loginToMainSite();

        selenium.click("Auth_Login");

        /*** page 5 ***/
        selenium.type("id=gwt-debug-AuthenticationRequest$email", "cust001@pyx4j.com");
        selenium.type("id=gwt-debug-AuthenticationRequest$password", "cust001@pyx4j.com");
        //PLEASE ADD BREAKPOINT ON THE NEXT LINE//
        selenium.click("id=gwt-debug-Criteria_Submit");

        doTestAppartment1();

    }

    public void doTestAppartment1() throws Exception {

        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class));
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar fromDate = Calendar.getInstance(); //current day-time
        fromDate.add(Calendar.DATE, 10); //add 10 extra days
        String strFrom = sdf.format(fromDate.getTime());
        fromDate.add(Calendar.MONTH, 1); ////add yet 1 extra month
        String strTo = sdf.format(fromDate.getTime());

        selenium.type("UnitSelection$selectionCriteria$availableFrom", strFrom);
        selenium.type("UnitSelection$selectionCriteria$availableTo", strTo);
        selenium.click(VistaFormsDebugId.Available_Units_Change);

        selenium.click("UnitSelection$availableUnits$units_row-2_ApartmentUnit$unitType");
        String strAvailFrom = selenium.getText("UnitSelection$availableUnits$units_row-2_ApartmentUnit$avalableForRent");

        java.util.Date dateAvail = sdf.parse(strAvailFrom);
        Calendar cdl = Calendar.getInstance();
        cdl.setTime(dateAvail);
        cdl.add(Calendar.DATE, 1); // add one extra day
        String strStartRent = sdf.format(cdl.getTime());

        selenium.type("UnitSelection$rentStart", strStartRent);
        selenium.click("UnitSelection$availableUnits$units_row-2_leaseTerm_12-input");
        assertNoMessages();
        // we do not save, just a test in this case

        selenium.click("logout");

    }

    public void testLoginNewTenant() throws Exception {
        String theurl = selenium.getCurrentUrl();
        if (!theurl.startsWith("http://localhost") && !theurl.startsWith("http://127.0.0.1")) {
            loginToMainSite();
        }

        // Initialize access to GlassPanel
        selenium.setGlassPanelAware();
        // wait while site will initialize
        selenium.waitWhileWorking();

        doLoginNewTenant();
        doReLoginTenant();
        doTenantInfo();

    }

    public void doLoginNewTenant() throws Exception {

        //starting from login screen:
        String ulogin = testUser + strNow + emailAt;
        selenium.type("AccountCreationRequest$email", ulogin);
        selenium.type("AccountCreationRequest$password", ulogin);
        selenium.type("id=recaptcha_response_field", "x");
        selenium.click("id=gwt-debug-Auth_LetsStart");

        selenium.waitWhileWorking();

        log.info("User {} created", ulogin);

        // APARTMENT PAGE
        selenium.click("UnitSelection$availableUnits$units_row-2_ApartmentUnit$unitType");
        String strAvailFrom = selenium.getText("UnitSelection$availableUnits$units_row-2_ApartmentUnit$avalableForRent");

        selenium.type("UnitSelection$rentStart", strAvailFrom); //to make sure it's the same date
        selenium.click("gwt-debug-UnitSelection$availableUnits$units_row-2_leaseTerm_6-input");
        selenium.click("Crud_Save");

        //current time in this format appended to the user name
        //strNow =  new SimpleDateFormat("yyyyMMdd").format( Calendar.getInstance().getTime());

        //  TENANTS PAGE
        selenium.type("PotentialTenantList$tenants_row-1_PotentialTenantInfo$firstName", testUser + strNow);
        selenium.type("PotentialTenantList$tenants_row-1_PotentialTenantInfo$lastName", testUser);
        selenium.type("PotentialTenantList$tenants_row-1_PotentialTenantInfo$middleName", "M");
        Calendar cal = Calendar.getInstance(); //current day-time
        cal.add(Calendar.YEAR, -25); //get a 25 Years old tenant
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
        selenium.type("PotentialTenantList$tenants_row-1_PotentialTenantInfo$birthDate", sdf2.format(cal.getTime()));

        // add a co-tenant
        selenium.click("PotentialTenantList$tenants_fd__Form_Add");
        selenium.type("PotentialTenantList$tenants_row-2_PotentialTenantInfo$firstName", testUser + "sha");
        selenium.type("PotentialTenantList$tenants_row-2_PotentialTenantInfo$lastName", testUser);
        selenium.type("PotentialTenantList$tenants_row-2_PotentialTenantInfo$middleName", "D");
        cal.add(Calendar.YEAR, 1); //get a 24 Years old co-tenant
        selenium.type("PotentialTenantList$tenants_row-2_PotentialTenantInfo$birthDate", sdf2.format(cal.getTime()));
        selenium.type("PotentialTenantList$tenants_row-2_PotentialTenantInfo$email", testUser + "sha" + strNow + emailAt);
        selenium.setValue("id=gwt-debug-PotentialTenantList$tenants_row-2_PotentialTenantInfo$relationship", "Spouse");
        assertEnabled("id=gwt-debug-PotentialTenantList$tenants_row-2_PotentialTenantInfo$status");  // status must be enabled for spouse
        selenium.setValue("id=gwt-debug-PotentialTenantList$tenants_row-2_PotentialTenantInfo$status", "Co-applicant");

        selenium.click("Crud_Save");
        assertNoMessages();
        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Tenants.class));

        assertEnabled("id=gwt-debug-PotentialTenantList$tenants_row-2_PotentialTenantInfo$status");  // status must be enabled for spouse
        assertEquals(selenium.getValue("id=gwt-debug-PotentialTenantList$tenants_row-2_PotentialTenantInfo$status"), "Co-applicant");
        
        
        selenium.click("logout");

    }

    public void doReLoginTenant() throws Exception {

        //starts with page with ID prompt
        selenium.click("Auth_Login");

        Calendar cal = Calendar.getInstance(); //current day-time
        selenium.type("AuthenticationRequest$email", testUser + strNow + emailAt);
        selenium.type("AuthenticationRequest$password", testUser + strNow + emailAt);
        selenium.click("Criteria_Submit");

        //if dialog frame is present, click OK:
        //if(selenium.findElement(By.id("gwt-debug-Dialog_Ok")) != null ){
        //	selenium.click("Dialog_Ok"); 		
        //}

        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Tenants.class));
        // add a co-tenant - child
        selenium.click("PotentialTenantList$tenants_fd__Form_Add");
        selenium.type("PotentialTenantList$tenants_row-3_PotentialTenantInfo$firstName", testUser + "ovich");
        selenium.type("PotentialTenantList$tenants_row-3_PotentialTenantInfo$middleName", "M");
        selenium.type("PotentialTenantList$tenants_row-3_PotentialTenantInfo$lastName", testUser);
        cal.add(Calendar.YEAR, -3); //get a 24 Years old co-tenant
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
        selenium.type("PotentialTenantList$tenants_row-3_PotentialTenantInfo$birthDate", sdf2.format(cal.getTime()));
        selenium.type("PotentialTenantList$tenants_row-3_PotentialTenantInfo$email", testUser + "ovich" + strNow + emailAt);
        selenium.setValue("id=gwt-debug-PotentialTenantList$tenants_row-3_PotentialTenantInfo$relationship", "Son");
        assertNotEnabled("id=gwt-debug-PotentialTenantList$tenants_row-3_PotentialTenantInfo$status");  // status must be enabled for spouse

        selenium.click("Crud_Save");
        assertNoMessages();
    }

    public void assertNoMessages() {
        for (UserMessageType type : UserMessageType.values()) {
            assertNotVisible(new CompositeDebugId(VistaFormsDebugId.UserMessage_Prefix, type));
        }
    }

    public void doTenantInfo() throws Exception {

        // Initialize access to GlassPanel
        selenium.setGlassPanelAware();
        // wait while site will initialize
        selenium.waitWhileWorking();

        selenium.type("PotentialTenantInfo$homePhone", "123-456-7890");
        selenium.type("PotentialTenantInfo$mobilePhone", "123-456-7890");
        selenium.type("PotentialTenantInfo$driversLicense", "1234567789");

        selenium.type("PotentialTenantInfo$secureIdentifier", "123456789");
        selenium.type("PotentialTenantInfo$secureIdentifier", "134567890");
        selenium.click("PotentialTenantInfo$legalQuestions$suedForRent_N-input");
        selenium.click("PotentialTenantInfo$legalQuestions$suedForDamages_N-input");
        selenium.click("PotentialTenantInfo$legalQuestions$everEvicted_N-input");
        selenium.click("PotentialTenantInfo$legalQuestions$defaultedOnLease_N-input");
        selenium.click("PotentialTenantInfo$legalQuestions$convictedOfFelony_N-input");
        selenium.click("PotentialTenantInfo$legalQuestions$legalTroubles_N-input");
        selenium.click("PotentialTenantInfo$legalQuestions$filedBankruptcy_N-input");

        // next part of page
        selenium.type("PotentialTenantInfo$currentAddress_Address$street1", "123 dundas");
        selenium.type("PotentialTenantInfo$currentAddress_Address$city", "Toronto");
        selenium.type("PotentialTenantInfo$currentAddress_Address$postalCode", "h3b1g9");
        selenium.type("PotentialTenantInfo$currentAddress_Address$country", "Canada");
        selenium.click("PotentialTenantInfo$currentAddress_Address$rented_Rented-input");

        selenium.type("PotentialTenantInfo$currentAddress_Address$phone", "098-765-4321");
        selenium.click("PotentialTenantInfo$currentAddress_Address$rented_Rented-input");
        //selenium.click("PotentialTenantInfo$emergencyContacts_fd__Form_Add");
        selenium.type("PotentialTenantInfo$emergencyContacts_row-1_EmergencyContact$firstName", "contact1");
        selenium.type("PotentialTenantInfo$emergencyContacts_row-1_EmergencyContact$lastName", "UFO");
        selenium.type("PotentialTenantInfo$emergencyContacts_row-1_EmergencyContact$middleName", "NLO");
        selenium.type("PotentialTenantInfo$emergencyContacts_row-1_EmergencyContact$address$city", "Andromeda");
        selenium.type("PotentialTenantInfo$emergencyContacts_row-1_EmergencyContact$homePhone", "911-234-5678");
        selenium.type("PotentialTenantInfo$emergencyContacts_row-1_EmergencyContact$address$street1", "Galaxy H3b4567");
        selenium.type("PotentialTenantInfo$emergencyContacts_row-1_EmergencyContact$address$city", "");
        selenium.type("PotentialTenantInfo$emergencyContacts_row-1_EmergencyContact$address$street2", "Andromeda");
        selenium.type("PotentialTenantInfo$emergencyContacts_row-1_EmergencyContact$address$city", "NewNew York");
        selenium.type("PotentialTenantInfo$emergencyContacts_row-1_EmergencyContact$address$postalCode", "12345");
        selenium.type("PotentialTenantInfo$currentAddress_Address$street1", "123 dundas st., apt.12345");

        selenium.click("PotentialTenantInfo$vehicles_fd__Form_Add");
        selenium.type("PotentialTenantInfo$vehicles_row-1_Vehicle$plateNumber", "UFO1234");
        selenium.type("PotentialTenantInfo$vehicles_row-1_Vehicle$make", "VAZ");
        selenium.type("PotentialTenantInfo$vehicles_row-1_Vehicle$model", "LADA-01");
        selenium.type("PotentialTenantInfo$vehicles_row-1_Vehicle$country", "Russia");

        //INFO PAGE DOES NOT HAVE PROPER DEBUF IDs BELOW:
        selenium.setValue("id=gwt-debug-PotentialTenantInfo$driversLicenseState", "Ontario");
        selenium.setValue("id=gwt-debug-PotentialTenantInfo$currentAddress_Address$province", "Ontario");
        selenium.setValue("id=gwt-debug-PotentialTenantInfo$vehicles_row-1_Vehicle$province", "Ontario");
        selenium.setValue("id=gwt-debug-PotentialTenantInfo$emergencyContacts_row-1_EmergencyContact$address$province", "Ontario");
        selenium.setValue("id=gwt-debug-PotentialTenantInfo$vehicles_row-1_Vehicle$year_yy", "2001");

        //INFO PAGE DOES NOT HAV PROPER DEBUF IDs ABOVE, so save does not work...
        selenium.click("Crud_Save");
        selenium.click("MainNavigation_Prefix_apartment");
        assertNoMessages();

        selenium.click("logout");

    }

}
