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

import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;

import com.pyx4j.essentials.j2se.J2SEServiceConnector;
import com.pyx4j.essentials.j2se.J2SEServiceConnector.Credentials;
import com.pyx4j.selenium.BaseSeleniumTestCase;
import com.pyx4j.selenium.DefaultSeleniumTestConfiguration;
import com.pyx4j.selenium.ISeleniumTestConfiguration;
import com.pyx4j.site.rpc.AppPlaceInfo;

import org.openqa.selenium.WebDriver;

public class GaeAppLoginTest extends BaseSeleniumTestCase {

    final public String testsite = "http://www22.birchwoodsoftwaregroup.com/tester";

    final public String mainsite = "http://www22.birchwoodsoftwaregroup.com/";

    //final public String mainsite = "http://localhost:8888/vista/";

    final public String blankpage = "about:blank";

    final public String testUser = "Jasper";

    final public String emailAt = "@local.com";

    private final String strNow = new SimpleDateFormat("yyyyMMdd-hhmmss").format( Calendar.getInstance().getTime()); //need to persist across test cases 

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new DefaultSeleniumTestConfiguration() {
            @Override
            public String getTestUrl() {
                return mainsite;
            }

            @Override
            public boolean reuseBrowser() {
                return false;
            }
        };
    }

    public void loginToMainSite() throws Exception {
        Credentials credentials = J2SEServiceConnector.getCredentials(System.getProperty("user.dir", ".") + "/credentials.properties");

        /*** page 1 ***/
        selenium.click("id=googleSignIn");

        /*** page 2 ***/
        selenium.type("id=Email", credentials.email);
        selenium.type("id=Passwd", credentials.password);
        selenium.click("id=signIn");

        /*** page 3 ***/
        selenium.click("id=continue");

        /*** page 4 ***/

        //PLEASE ADD BREAKPOINT ON THE NEXT LINE//
        selenium.waitFor(By.id("gwt-debug-Login"), 10);
        // Initialize access to GlassPanel
        selenium.setGlassPanelAware();
        // wait while site will initialize
        selenium.waitWhileWorking();
    
    }
    
    public void testLoginExistingUser() throws Exception {
    	
    	loginToMainSite();

        selenium.click("id=gwt-debug-Login");

        /*** page 5 ***/
        selenium.type("id=gwt-debug-AuthenticationRequest$email", "cust001@pyx4j.com");
        System.out.println("fedor@pyx4j.com");
        selenium.type("id=gwt-debug-AuthenticationRequest$password", "cust001@pyx4j.com");
        System.out.println("fedor@pyx4j.com");

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
        //selenium.type("UnitSelection$selectionCriteria$availableTo", strTo);    	
        selenium.click(VistaFormsDebugId.Available_Units_Change);

        selenium.click("UnitSelection$availableUnits$units_row-1_ApartmentUnit$unitType");
        String strAvailFrom = selenium.getText("UnitSelection$availableUnits$units_row-1_ApartmentUnit$avalableForRent");

        java.util.Date dateAvail = sdf.parse(strAvailFrom);
        Calendar cdl = Calendar.getInstance();
        cdl.setTime(dateAvail);
        cdl.add(Calendar.DATE, 1); // add one extra day
        String strStartRent = sdf.format(cdl.getTime());

        selenium.type("UnitSelection$rentStart", strStartRent);
        selenium.click("UnitSelection$availableUnits$units_row-1_leaseTerm_12-input");
        // do not save, just a test for now
        //selenium.click("Crud_Save");

        selenium.click("logout");

        ////sample from Slava
        //selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class));

    }

//==========================================================================================

    public void testLoginNewTenant() throws Exception {
    	loginToMainSite();

        doLoginNewTenant();
        doReLoginTenant();

    }
    
    public void doLoginNewTenant() throws Exception {
    	
    	//starting from login screen:
    	String ulogin = testUser + strNow + emailAt;
    	selenium.type("AccountCreationRequest$email", ulogin);
    	selenium.type("AccountCreationRequest$password", ulogin);
    	selenium.type("id=recaptcha_response_field", "x");
        selenium.click("id=gwt-debug-Criteria_Submit");
        
        //selenium.waitWhileWorking();
    	
        // APARTMENT PAGE
    	selenium.click("UnitSelection$availableUnits$units_row-1_ApptUnit$unitType");
    	String strAvailFrom = selenium.getText("UnitSelection$availableUnits$units_row-1_ApptUnit$avalableForRent");
    	
    	selenium.type("UnitSelection$rentStart", strAvailFrom); //to make sure it's the same date
    	selenium.click("UnitSelection$availableUnits$units_row-1_leaseTerm_12-input");
    	selenium.click("Crud_Save");
    	
        //current time in this format appended to the user name
    	//strNow =  new SimpleDateFormat("yyyyMMdd").format( Calendar.getInstance().getTime());

    	//  TENANTS PAGE
    	selenium.type("PotentialTenantList$tenants_row-1_PotentialTenantInfo$firstName", testUser + strNow);
    	selenium.type("PotentialTenantList$tenants_row-1_PotentialTenantInfo$lastName", testUser);
    	selenium.type("PotentialTenantList$tenants_row-1_PotentialTenantInfo$middleName", "M");
    	Calendar cal = Calendar.getInstance();  //current day-time
    	cal.add(Calendar.YEAR, -25);  //get a 25 Years old tenant
    	SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
    	selenium.type("PotentialTenantList$tenants_row-1_PotentialTenantInfo$birthDate", sdf2.format(cal.getTime()));

    	// add a co-tenant
    	selenium.click("PotentialTenantList$tenants_fd__Form_Add");
    	selenium.type("PotentialTenantList$tenants_row-2_PotentialTenantInfo$firstName", testUser + "sha");
    	selenium.type("PotentialTenantList$tenants_row-2_PotentialTenantInfo$lastName", testUser);
    	selenium.type("PotentialTenantList$tenants_row-2_PotentialTenantInfo$middleName", "D");
    	cal.add(Calendar.YEAR, 1);  //get a 24 Years old co-tenant
    	selenium.type("PotentialTenantList$tenants_row-2_PotentialTenantInfo$birthDate", sdf2.format(cal.getTime()));
    	selenium.type("PotentialTenantList$tenants_row-2_PotentialTenantInfo$email", testUser + "sha" + strNow + emailAt);
    	selenium.select("gwt-debug-PotentialTenantList$tenants_row-2_PotentialTenantInfo$status-item0");
    	selenium.select("gwt-debug-PotentialTenantList$tenants_row-2_PotentialTenantInfo$relationship-item0");
    	selenium.click("Crud_Save");
    	selenium.click("logout");
  	
    }
    
    public void doReLoginTenant() throws Exception {

    	//starts with page with ID prompt
        selenium.click("id=gwt-debug-Login");
        
        //current time in this format appended to the user name
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  //"yyyyMMdd-HHmmss"
    	Calendar cal = Calendar.getInstance();  //current day-time
    	//strNow = sdf.format( cal.getTime());
        //
        selenium.type("AuthenticationRequest$email", testUser + strNow + emailAt);
        selenium.type("AuthenticationRequest$password", testUser + strNow + emailAt);
        selenium.click("Criteria_Submit");
    	
    	//if dialog frame is present, click OK:
    	if(selenium.findElement(By.id("gwt-debug-Dialog_Ok")) != null ){
    		selenium.click("Dialog_Ok"); 		
    	}

    	// add a co-tenant - child
    	selenium.click("PotentialTenantList$tenants_fd__Form_Add");
    	selenium.type("PotentialTenantList$tenants_row-3_PotentialTenantInfo$firstName", testUser + "ovich");
    	selenium.type("PotentialTenantList$tenants_row-3_PotentialTenantInfo$middleName", "M");
    	selenium.type("PotentialTenantList$tenants_row-3_PotentialTenantInfo$lastName", testUser);
    	cal.add(Calendar.YEAR, -3);  //get a 24 Years old co-tenant
    	SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
    	selenium.type("PotentialTenantList$tenants_row-3_PotentialTenantInfo$birthDate", sdf2.format(cal.getTime()));
    	selenium.type("PotentialTenantList$tenants_row-3_PotentialTenantInfo$email", testUser + "ovich" + strNow + emailAt);
    	selenium.click("PotentialTenantList$tenants_row-3_PotentialTenantInfo$relationship");
    	selenium.click("PotentialTenantList$tenants_row-3_PotentialTenantInfo$relationship-item1");
    	selenium.click("Crud_Save");
    }
    
    
}
