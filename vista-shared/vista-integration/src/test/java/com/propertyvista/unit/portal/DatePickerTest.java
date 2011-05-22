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

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.User;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;
import com.propertyvista.portal.server.generator.PTGenerator;
import com.propertyvista.portal.server.generator.SharedData;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CCompDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.D;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.datepicker.DatePickerIDs;

public class DatePickerTest extends WizardBaseSeleniumTestCase {

    private final IDebugId gridId = new CompositeDebugId(DatePickerIDs.DatePicker, "0");

    private final IDebugId backMonth = DatePickerIDs.MonthSelectorButton_BackwardsMonth;

    private final IDebugId backYear = DatePickerIDs.MonthSelectorButton_BackwardsYear;

    private final IDebugId forwardYear = DatePickerIDs.MonthSelectorButton_ForwardYear;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedData.init();
        selenium.setFocusOnGetValue(true);
    }

    public void testDatePicker() throws Exception {
        PTGenerator generator = new PTGenerator(DemoData.PT_GENERATION_SEED);
        User user = generator.createUser(1);
        Application application = generator.createApplication(user);

        selenium.click(VistaFormsDebugId.Auth_Login);
        selenium.type(D.id(proto(AuthenticationRequest.class).email()), user.email().getValue());
        selenium.type(D.id(proto(AuthenticationRequest.class).password()), user.email().getValue());
        selenium.click(CrudDebugId.Criteria_Submit);
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(PtSiteMap.Apartment.class)));

        startRentDateTest();
    }

    private void startRentDateTest() {
        selenium.click(D.id(D.id(proto(UnitSelection.class).selectionCriteria().availableFrom()), CCompDebugId.trigger));
        navigateToDate(1990, 1);
        selenium.click(getCellDebugId(1, 1, gridId));
        selenium.click(D.id(D.id(proto(UnitSelection.class).selectionCriteria().availableTo()), CCompDebugId.trigger));
        navigateToDate(2011, 9);
        selenium.click(getCellDebugId(1, 1, gridId));

        selenium.click(VistaFormsDebugId.Available_Units_Change);
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Apartment.class));
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 0, proto(AptUnitDTO.class).unitType()));
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 0, "leaseTerm_12"));

        selenium.click(D.id(D.id(proto(UnitSelection.class).rentStart()), CCompDebugId.trigger));
        navigateToDate(1995, 1);
        selenium.click(getCellDebugId(1, 1, gridId));

        saveAndContinue();
    }

    private IDebugId getCellDebugId(int row, int column, IDebugId parent) {
        return new StringDebugId(parent.debugId() + "_" + row + "_" + column);
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
