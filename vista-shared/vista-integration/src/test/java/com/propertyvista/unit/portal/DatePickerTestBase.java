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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.propertvista.generator.PTGenerator;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.D;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.datepicker.DatePickerIDs;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.User;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;
import com.propertyvista.server.common.reference.SharedData;

public class DatePickerTestBase extends WizardSeleniumTestBase {

    private final IDebugId gridId = new CompositeDebugId(DatePickerIDs.DatePicker, "0");

    private final IDebugId backMonth = DatePickerIDs.MonthSelectorButton_BackwardsMonth;

    private final IDebugId backYear = DatePickerIDs.MonthSelectorButton_BackwardsYear;

    private final IDebugId forwardYear = DatePickerIDs.MonthSelectorButton_ForwardYear;

    public static String[] monthName = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
            "December" };

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedData.init();
        selenium.setFocusOnGetValue(true);
    }

    protected void login() {
        PTGenerator generator = new PTGenerator(DemoData.PT_GENERATION_SEED, VistaDevPreloadConfig.createTest());
        User user = generator.createUser(1);
        selenium.click(VistaFormsDebugId.Auth_Login);
        selenium.type(D.id(proto(AuthenticationRequest.class).email()), user.email().getValue());
        selenium.type(D.id(proto(AuthenticationRequest.class).password()), user.email().getValue());
        selenium.click(CrudDebugId.Criteria_Submit);
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(PtSiteMap.Apartment.class)));

        //navigate to tenants page
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Apartment.class));
        saveAndContinue();
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Tenants.class));
    }

    protected String getYear(Calendar calendar) {
        return Integer.toString(calendar.get(Calendar.YEAR));
    }

    protected String getMonth(Calendar calendar) {
        return monthName[calendar.get(Calendar.MONTH)];
    }

    protected void typeInDate(IDebugId id, Calendar calendar) {
        Format formatter = new SimpleDateFormat("MM/dd/yyyy");
        selenium.setValue(id, formatter.format(calendar.getTime()));
    }

    protected void navigateToDateAndClick(IDebugId id, Calendar calendar) {
        navigateToDate(id, calendar);
        selenium.click(getCellDebugId(calendar));
    }

    protected void navigateToDate(IDebugId id, Calendar calendar) {
        selenium.click(id);
        navigateToDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
    }

    protected IDebugId getCellDebugId(Calendar calendar) {
        return new StringDebugId(gridId.debugId() + "_" + Integer.toString(calendar.get(Calendar.WEEK_OF_MONTH)) + "_"
                + Integer.toString(calendar.get(Calendar.DAY_OF_WEEK) - 1));
    }

    protected void navigateToDate(int year, int month) {
        selenium.getText(DatePickerIDs.MonthSelectorLabel_Month);
        int currentYear;
        String currentMonth;
        IDebugId button;

        button = backMonth;
        currentMonth = selenium.getText(DatePickerIDs.MonthSelectorLabel_Month);
        while (!currentMonth.equals(monthName[month])) {
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
