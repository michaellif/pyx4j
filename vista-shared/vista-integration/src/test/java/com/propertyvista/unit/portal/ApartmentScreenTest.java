/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2011
 * @author vadym
 * @version $Id$
 */
package com.propertyvista.unit.portal;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.selenium.D;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.rpc.ptapp.AccountCreationRequest;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;

public class ApartmentScreenTest extends WizardSeleniumTestBase {

    private static final Logger log = LoggerFactory.getLogger(ApartmentScreenTest.class);

    final public String testUser = "Bob";

    final static public String emailAt = "@local.com";

    final static public String warnTerm = "Please select the Lease Terms";

    final static public String warnFromFmt = "Field 'From' is not valid";

    final static public String warnToFmt = "Field 'To' is not valid";

    final static public String warnRentDateFmt = "Field 'Start Rent Date' is not valid";

    final static public String warnNoUnit = "Please select the Unit";

    final static public String warnRentDateBefore = "Field 'Start Rent Date' is not valid. Start Rent Date for this unit can not be before";

    final static public String warnRentDateAfter = "Field 'Start Rent Date' is not valid. Start Rent Date for this unit can not be later than";

    public void testDateTimePickers() throws Exception {
        String strNow = new SimpleDateFormat("yyyyMMdd-hhmmss").format(Calendar.getInstance().getTime());

        String ulogin = testUser + strNow + emailAt;
        selenium.type(proto(AccountCreationRequest.class).email(), ulogin);
        selenium.type(proto(AccountCreationRequest.class).password(), ulogin);

        selenium.type("id=recaptcha_response_field", "x");
        selenium.click(VistaFormsDebugId.Auth_LetsStart);
        selenium.waitWhileWorking();
        log.info("User {} created", ulogin);

        doTestWrongDateTimeFormat();

    }

    protected void doTestWrongDateTimeFormat() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar fromDate = Calendar.getInstance(); //current day-time
        fromDate.add(Calendar.DATE, 1); //add 1 extra day
        String strFrom = sdf.format(fromDate.getTime());
        fromDate.add(Calendar.MONTH, 1); ////add yet 1 extra month
        String strTo = sdf.format(fromDate.getTime());
        selenium.type(D.id(proto(UnitSelection.class).selectionCriteria().availableFrom()), strFrom);
        selenium.type(D.id(proto(UnitSelection.class).selectionCriteria().availableTo()), strTo);
        selenium.click(VistaFormsDebugId.Available_Units_Change);
        assertNoMessages();

        //put From and To date to the past. 
        fromDate = Calendar.getInstance(); //current day-time
        fromDate.add(Calendar.DATE, -1); ////Subtract 1 extra day
        strTo = sdf.format(fromDate.getTime());
        fromDate.add(Calendar.MONTH, -1); ////Subtract yet 1 extra month
        strFrom = sdf.format(fromDate.getTime());
        selenium.type(proto(UnitSelection.class).selectionCriteria().availableFrom(), strFrom);
        selenium.type(D.id(proto(UnitSelection.class).selectionCriteria().availableTo()), strTo);
        selenium.click(VistaFormsDebugId.Available_Units_Change);
        assertNoMessages(); //this reflects current behavior...

        selenium.type(D.id(proto(UnitSelection.class).selectionCriteria().availableFrom()), "00/00/123");
        selenium.type(D.id(proto(UnitSelection.class).selectionCriteria().availableTo()), "13/32/20011");
        selenium.click(VistaFormsDebugId.Available_Units_Change);
        selenium.setValue(D.id(proto(UnitSelection.class).rentStart()), "32/32/32");
        selenium.click(CrudDebugId.Crud_Save);

        assertVisible(new CompositeDebugId(VistaFormsDebugId.UserMessage_Prefix, UserMessageType.WARN));
        String warns = selenium.getText(VistaFormsDebugId.UserMessage_Prefix, UserMessageType.WARN);
        assertTrue(warns.indexOf(warnFromFmt) >= 0 && warns.indexOf(warnToFmt) >= 0 && warns.indexOf(warnNoUnit) >= 0 && //this is in the error list too, but it's not the purpose of this test
                warns.indexOf(warnRentDateFmt) >= 0);

        selenium.click(VistaFormsDebugId.Auth_LogOutTop);
    }

    public void testDateTimePast() throws Exception {
        String strNow = new SimpleDateFormat("yyyyMMdd-hhmmss").format(Calendar.getInstance().getTime());

        String ulogin = testUser + strNow + emailAt;
        selenium.type(proto(AccountCreationRequest.class).email(), ulogin);
        selenium.type(proto(AccountCreationRequest.class).password(), ulogin);
        selenium.type("id=recaptcha_response_field", "x");
        selenium.click(VistaFormsDebugId.Auth_LetsStart);

        log.info("User {} logged in", ulogin);
        //if we go on some other screen, return back to APARTMENT screen
        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(PtSiteMap.Apartment.class));

        doTestDateTimePast();
        doTestStartRent10DaysAfter();
        doTestStartRent10DaysBefore();
    }

    protected void doTestDateTimePast() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar fromDate = Calendar.getInstance(); //current day-time
        fromDate.add(Calendar.MONTH, -1); //minus 1  month
        String strFrom = sdf.format(fromDate.getTime());
        fromDate.add(Calendar.MONTH, -1); ////minus 1 more month
        String strTo = sdf.format(fromDate.getTime());
        selenium.type(proto(UnitSelection.class).selectionCriteria().availableFrom(), strFrom);
        selenium.type(proto(UnitSelection.class).selectionCriteria().availableTo(), strTo);
        selenium.click(VistaFormsDebugId.Available_Units_Change);
        assertNotPresent(D.id(proto(UnitSelection.class).availableUnits().units(), 0, proto(AptUnitDTO.class).unitType()));

        selenium.click(CrudDebugId.Crud_Save);

        assertVisible(new CompositeDebugId(VistaFormsDebugId.UserMessage_Prefix, UserMessageType.WARN));
        String warns = selenium.getText(VistaFormsDebugId.UserMessage_Prefix, UserMessageType.WARN);
        assertTrue(warns.indexOf(warnRentDateFmt) >= 0);
        assertTrue(warns.indexOf(warnNoUnit) >= 0);
        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(PtSiteMap.Apartment.class));
    }

    protected void doTestStartRent10DaysAfter() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar fromDate = Calendar.getInstance(); //current day-time
        fromDate.add(Calendar.DATE, 1); //add 1  day
        String strFrom = sdf.format(fromDate.getTime());
        fromDate.add(Calendar.MONTH, 1); ////add 1  month
        String strTo = sdf.format(fromDate.getTime());
        selenium.type(D.id(proto(UnitSelection.class).selectionCriteria().availableFrom()), strFrom);
        selenium.type(D.id(proto(UnitSelection.class).selectionCriteria().availableTo()), strTo);
        selenium.click(VistaFormsDebugId.Available_Units_Change);

        //selenium.click(meta(UnitSelection.class).availableUnits().units().$(2).unitType());
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, proto(AptUnitDTO.class).unitType()));
        String strAvailFrom = selenium.getText(D.id(proto(UnitSelection.class).availableUnits().units(), 1, proto(AptUnitDTO.class).avalableForRent()));

        java.util.Date dateAvail = sdf.parse(strAvailFrom);
        Calendar cdl = Calendar.getInstance();
        cdl.setTime(dateAvail);
        cdl.add(Calendar.DATE, 1000); // add 1000 extra days
        String strStartRent = sdf.format(cdl.getTime());
        selenium.setValue(D.id(proto(UnitSelection.class).rentStart()), strStartRent);
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, "leaseTerm_12"));

        selenium.click(CrudDebugId.Crud_Save);
        assertVisible(new CompositeDebugId(VistaFormsDebugId.UserMessage_Prefix, UserMessageType.WARN));
        String warns = selenium.getText(VistaFormsDebugId.UserMessage_Prefix, UserMessageType.WARN);
        assertTrue(warns.indexOf(warnRentDateAfter) >= 0);

    }

    protected void doTestStartRent10DaysBefore() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar fromDate = Calendar.getInstance(); //current day-time
        fromDate.add(Calendar.DATE, 1); //minus 2  months
        String strFrom = sdf.format(fromDate.getTime());
        fromDate.add(Calendar.MONTH, 1); ////add 1  month
        String strTo = sdf.format(fromDate.getTime());
        selenium.type(D.id(proto(UnitSelection.class).selectionCriteria().availableFrom()), strFrom);
        selenium.type(D.id(proto(UnitSelection.class).selectionCriteria().availableTo()), strTo);
        selenium.click(VistaFormsDebugId.Available_Units_Change);

        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, proto(AptUnitDTO.class).unitType()));
        String strAvailFrom = selenium.getText(D.id(proto(UnitSelection.class).availableUnits().units(), 1, proto(AptUnitDTO.class).avalableForRent()));
        java.util.Date dateAvail = sdf.parse(strAvailFrom);
        Calendar cdl = Calendar.getInstance();
        cdl.setTime(dateAvail);
        cdl.add(Calendar.DATE, -10); // add one extra day
        String strStartRent = sdf.format(cdl.getTime());
        selenium.setValue(D.id(proto(UnitSelection.class).rentStart()), strStartRent);
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, "leaseTerm_12"));

        selenium.click(CrudDebugId.Crud_Save);
        assertVisible(new CompositeDebugId(VistaFormsDebugId.UserMessage_Prefix, UserMessageType.WARN));
        String warns = selenium.getText(VistaFormsDebugId.UserMessage_Prefix, UserMessageType.WARN);
        assertTrue(warns.indexOf(warnRentDateBefore) >= 0);

        //selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class));
        selenium.click(VistaFormsDebugId.Auth_LogOutTop);
    }

}
