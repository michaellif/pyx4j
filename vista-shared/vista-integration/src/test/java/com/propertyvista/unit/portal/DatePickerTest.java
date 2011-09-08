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

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.forms.client.ui.CCompDebugId;
import com.pyx4j.selenium.D;
import com.pyx4j.widgets.client.datepicker.DatePickerIDs;

import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseListDTO;

public class DatePickerTest extends DatePickerTestBase {

    private final IDebugId datePickerTextBoxId = D.id(D.id(proto(TenantInLeaseListDTO.class).tenants(), 0),
            D.id(proto(TenantInLeaseDTO.class).person().birthDate()));

    private final IDebugId datePickerId = D.id(datePickerTextBoxId, CCompDebugId.trigger);

    private final IDebugId validation = new CompositeDebugId(D.id(proto(TenantInLeaseListDTO.class).tenants(), 0), IFolderEditorDecorator.DecoratorsIds.Label);

    public void testDatePicker() throws Exception {
        login();
        validateTextBoxPropagation();
        validateValidationMessage();
        validateDatePickerNavigation();
        validateDatePickerLabels();
        validateDatePickerPropagation();
    }

    private void validateTextBoxPropagation() {
        Calendar testDate = Calendar.getInstance();
        testDate.set(2011, 5, 23);
        typeInDate(datePickerTextBoxId, testDate);
        selenium.click(datePickerId);
        assertEquals(getMonth(testDate), selenium.getText(DatePickerIDs.MonthSelectorLabel_Month));
    }

    private void validateValidationMessage() {
        Calendar calendar = Calendar.getInstance();
        navigateToDateAndClick(datePickerId, calendar);
        assertVisible(validation.debugId());
        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -20);
        navigateToDateAndClick(datePickerId, calendar);
        assertNotVisible(validation.debugId());
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

}
