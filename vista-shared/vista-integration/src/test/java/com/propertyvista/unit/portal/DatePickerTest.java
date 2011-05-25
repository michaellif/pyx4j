/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-18
 * @author leont
 * @version $Id$
 */
package com.propertyvista.unit.portal;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CCompDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.D;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.datepicker.DatePickerIDs;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.User;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;
import com.propertyvista.portal.server.generator.PTGenerator;
import com.propertyvista.portal.server.generator.SharedData;

public class DatePickerTest extends WizardBaseSeleniumTestCase {

    private final IDebugId gridId = new CompositeDebugId(DatePickerIDs.DatePicker, "0");

    private final IDebugId backMonth = DatePickerIDs.MonthSelectorButton_BackwardsMonth;

    private final IDebugId backYear = DatePickerIDs.MonthSelectorButton_BackwardsYear;

    private final IDebugId forwardYear = DatePickerIDs.MonthSelectorButton_ForwardYear;

    private final IDebugId datePickerTextBoxId = D.id(proto(UnitSelection.class).rentStart());

    private final IDebugId datePickerId = D.id(datePickerTextBoxId, CCompDebugId.trigger);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedData.init();
        selenium.setFocusOnGetValue(true);
    }

    public void testDatePicker() throws Exception {
        PTGenerator generator = new PTGenerator(DemoData.PT_GENERATION_SEED);
        User user = generator.createUser(1);

        selenium.click(VistaFormsDebugId.Auth_Login);
        selenium.type(D.id(proto(AuthenticationRequest.class).email()), user.email().getValue());
        selenium.type(D.id(proto(AuthenticationRequest.class).password()), user.email().getValue());
        selenium.click(CrudDebugId.Criteria_Submit);
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(PtSiteMap.Apartment.class)));

        validateTextBoxPropagation();
        validateValidationMessage();
        validateDatePickerNavigation();
        validateDatePickerLabels();
        validateDatePickerPropagation();
    }

    private void validateTextBoxPropagation() {
        Format formatter = new SimpleDateFormat("MM/dd/yyyy");
        Calendar testDate = Calendar.getInstance();
        testDate.set(2011, 5, 23);

        selenium.setValue(datePickerTextBoxId, formatter.format(testDate.getTime()));
        selenium.click(datePickerId);
        assertEquals(getMonth(testDate), selenium.getText(DatePickerIDs.MonthSelectorLabel_Month));
    }

    private void validateValidationMessage() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, 1, 21);
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Apartment.class));
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 0, proto(AptUnitDTO.class).unitType()));
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 0, "leaseTerm_12"));

        navigateToDateAndClick(datePickerId, calendar);
        //TODO Leon
        //assertVisible(validationwindow)

        //TODO Leon
        //remove validation message and run test again
    }

    private void validateDatePickerNavigation() {
        Calendar navigationDate = Calendar.getInstance();
        navigationDate.add(Calendar.MONTH, 6);
        navigateToDate(datePickerId, navigationDate);
        assertEquals(getMonth(navigationDate), selenium.getText(DatePickerIDs.MonthSelectorLabel_Month));
        assertEquals(getYear(navigationDate), selenium.getText(DatePickerIDs.MonthSelectorLabel_Year));
        navigationDate.add(Calendar.MONTH, -12);
        navigateToDate(datePickerId, navigationDate);
        assertEquals(getMonth(navigationDate), selenium.getText(DatePickerIDs.MonthSelectorLabel_Month));
        assertEquals(getYear(navigationDate), selenium.getText(DatePickerIDs.MonthSelectorLabel_Year));
    }

    private void validateDatePickerLabels() {
        Calendar testDate = Calendar.getInstance();
        testDate.set(2000, 1, 29);
        testDate.get(Calendar.DATE);
        navigateToDate(datePickerId, testDate);
        assertEquals(Integer.toString(testDate.get(Calendar.DATE)), selenium.getText(getCellDebugId(testDate)));
        assertEquals(getMonth(testDate), selenium.getText(DatePickerIDs.MonthSelectorLabel_Month));
        assertEquals(Integer.toString(testDate.get(Calendar.YEAR)), selenium.getText(DatePickerIDs.MonthSelectorLabel_Year));

    }

    private void validateDatePickerPropagation() {
        Format formatter = new SimpleDateFormat("MM/dd/yyyy");
        Calendar testDate = Calendar.getInstance();
        testDate.set(1994, 2, 2);
        testDate.get(Calendar.DATE);
        navigateToDateAndClick(datePickerId, testDate);
        assertEquals(formatter.format(testDate.getTime()), selenium.getValue(datePickerTextBoxId));
    }

    //---------------Helper methods------------------

    private String getYear(Calendar calendar) {
        return Integer.toString(calendar.get(Calendar.YEAR));
    }

    private String getMonth(Calendar calendar) {
        return DatePickerIDs.monthName[calendar.get(Calendar.MONTH)];
    }

    private int getMonth(String name) throws ParseException {
        Date date = new SimpleDateFormat("MMMMM", Locale.ENGLISH).parse(name);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    private void navigateToDateAndClick(IDebugId id, Calendar calendar) {
        navigateToDate(id, calendar);
        selenium.click(getCellDebugId(calendar));
    }

    private void navigateToDate(IDebugId id, Calendar calendar) {
        selenium.click(id);
        navigateToDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
    }

    private IDebugId getCellDebugId(Calendar calendar) {
        return new StringDebugId(gridId.debugId() + "_" + Integer.toString(calendar.get(Calendar.WEEK_OF_MONTH)) + "_"
                + Integer.toString(calendar.get(Calendar.DAY_OF_WEEK) - 1));
    }

    private void navigateToDate(int year, int month) {
        selenium.getText(DatePickerIDs.MonthSelectorLabel_Month);
        int currentYear;
        String currentMonth;
        IDebugId button;

        button = backMonth;
        currentMonth = selenium.getText(DatePickerIDs.MonthSelectorLabel_Month);
        while (!currentMonth.equals(DatePickerIDs.monthName[month])) {
            selenium.click(button);
            currentMonth = selenium.getText(DatePickerIDs.MonthSelectorLabel_Month);
        }

        if (year < 1000) {
            year += 1900;
        }
        currentYear = Integer.parseInt(selenium.getText(DatePickerIDs.MonthSelectorLabel_Year));
        if (currentYear < year) {
            button = forwardYear;
        } else {
            button = backYear;
        }

        while (currentYear != year) {
            selenium.click(button);
            currentYear = Integer.parseInt(selenium.getText(DatePickerIDs.MonthSelectorLabel_Year));
        }

    }

}
