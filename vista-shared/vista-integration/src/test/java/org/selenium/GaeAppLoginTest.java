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
import org.openqa.selenium.WebElement;
import com.google.gwt.place.shared.Place;

import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.pyx4j.essentials.j2se.J2SEServiceConnector;
import com.pyx4j.essentials.j2se.J2SEServiceConnector.Credentials;
import com.pyx4j.selenium.BaseSeleniumTestCase;
import com.pyx4j.selenium.DefaultSeleniumTestConfiguration;
import com.pyx4j.selenium.ISeleniumTestConfiguration;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;
import static com.propertyvista.portal.rpc.pt.VistaFormsDebugId.*;

public class GaeAppLoginTest extends BaseSeleniumTestCase {

    final public String testsite = "http://www22.birchwoodsoftwaregroup.com/vista/tester";

    final public String mainsite = "http://www33.birchwoodsoftwaregroup.com/";

    final public String blankpage = "about:blank";
    
    final public String testUser = "FFasper";
    
    final public String emailAt = "@local.com";
    private String strNow = null;  //need to persist across test cases 

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new DefaultSeleniumTestConfiguration() {
            @Override
            public String getTestUrl() {
                return mainsite;
            }
            
            @Override
            public boolean reuseBrowser() {
            	 return true;
            }
        };
    }
    
    public void testLoginToMainSite() throws Exception {
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
        WebElement we = selenium.waitFor(By.id("gwt-debug-Login"), 10);
        if(we == null){
        	selenium.get(mainsite + "#login");
        	System.out.println(mainsite + "#login");
        }
        else
        {
        	System.out.println("id=gwt-debug-Login");
        	selenium.click("id=gwt-debug-Login");
        	System.out.println("id=gwt-debug-Login");
        }
        
        /*** page 5 ***/
        selenium.type("id=gwt-debug-AuthenticationRequest$email", "fedor@pyx4j.com");
    	System.out.println("fedor@pyx4j.com");
        selenium.type("id=gwt-debug-AuthenticationRequest$password", "fedor");
    	System.out.println("fedor@pyx4j.com");
    	
        //PLEASE ADD BREAKPOINT ON THE NEXT LINE//
        selenium.click("id=gwt-debug-Criteria_Submit");
        
    }
    
    public void testAppartment1() throws Exception {

    	if(selenium.findElement(By.id("gwt-debug-Dialog_Ok")) != null ){
    		selenium.click("Dialog_Ok"); 		
    	}

        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class));
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    	Calendar fromDate = Calendar.getInstance();  //current day-time
    	fromDate.add(Calendar.DATE, 10);             //add 10 extra days
    	String strFrom = sdf.format( fromDate.getTime());
    	fromDate.add(Calendar.MONTH, 1);             ////add yet 1 extra month
    	String strTo = sdf.format( fromDate.getTime());
 	
    	selenium.type("UnitSelection$selectionCriteria$availableFrom", strFrom);
    	//selenium.type("UnitSelection$selectionCriteria$availableTo", strTo);    	
    	selenium.click(VistaFormsDebugId.Available_Units_Change); 

    	selenium.click("UnitSelection$availableUnits$units_row-1_ApptUnit$unitType");
    	String strAvailFrom = selenium.getText("UnitSelection$availableUnits$units_row-1_ApptUnit$avalableForRent");

    	java.util.Date dateAvail = sdf.parse(strAvailFrom);
    	Calendar cdl = Calendar.getInstance();
    	cdl.setTime(dateAvail);
    	cdl.add(Calendar.DATE, 1);             // add one extra day
    	String strStartRent = sdf.format( cdl.getTime());

    	selenium.type("UnitSelection$rentStart", strStartRent);
    	selenium.click("UnitSelection$availableUnits$units_row-1_leaseTerm_12-input");
    	// do not save, just a test for now
    	//selenium.click("Crud_Save");

    	selenium.click("logout");
        
        ////sample from Slava
    	//selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class));

    }

}
