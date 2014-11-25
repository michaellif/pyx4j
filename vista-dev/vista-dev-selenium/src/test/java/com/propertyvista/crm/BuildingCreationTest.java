/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 25, 2014
 * @author acer
 * @version $Id$
 */
package com.propertyvista.crm;

public class BuildingCreationTest extends CrmSeleniumTestBase {

    public void testBuildingCreation() throws InterruptedException {

        //---Loggin Start---//
        selenium.type("id=gwt-debug-AuthenticationRequest-email", "m001@pyx4j.com");

        selenium.type("id=gwt-debug-AuthenticationRequest-password", "m001@pyx4j.com");

        selenium.click("id=gwt-debug-Criteria_Submit");
        //---Loggin End---//

        //--- Create building Start ---//
        selenium.click("id=gwt-debug-navig-Properties");

        selenium.click("id=gwt-debug-navig-properties.building");
        //click 'new building', this element doesnt have id that is why we have to use path
        selenium.click("xpath=//*[contains(text(), 'New Building')]");

        selenium.type("id=gwt-debug-BuildingDTO-name", "TestBuilding");

        selenium.selectOption("id=gwt-debug-BuildingDTO-shape", "L-shape");

        selenium.selectOption("id=gwt-debug-BuildingDTO-type", "Condo");

        selenium.type("id=gwt-debug-BuildingDTO-address-streetNumber", "100");

        selenium.type("id=gwt-debug-BuildingDTO-address-streetName", "King St W");

        selenium.selectOption("id=gwt-debug-BuildingDTO-address-province", "Ontario");

        selenium.type("id=gwt-debug-BuildingDTO-address-city", "Toronto");

        selenium.type("id=gwt-debug-BuildingDTO-address-postalCode", "M5H 1A1");

        selenium.click("id=gwt-debug-Crud_Save");
        //--- Create building End ---//

        //--- Test parameters Start ---//
        selenium.assertText("id=gwt-debug-BuildingDTO-name", "TestBuilding", 5);
        selenium.assertText("id=gwt-debug-BuildingDTO-shape", "L-shape", 5);
        selenium.assertText("id=gwt-debug-BuildingDTO-type", "Condo", 5);
        selenium.assertText("id=gwt-debug-BuildingDTO-address-streetNumber", "100", 5);
        selenium.assertText("id=gwt-debug-BuildingDTO-address-streetName", "King St W", 5);
        selenium.assertText("id=gwt-debug-BuildingDTO-address-province", "Ontario", 5);
        selenium.assertText("id=gwt-debug-BuildingDTO-address-city", "Toronto", 5);
        selenium.assertText("id=gwt-debug-BuildingDTO-address-postalCode", "M5H 1A1", 5);
        //--- Test parameters End ---//
    }
}
