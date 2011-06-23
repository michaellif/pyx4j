/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-14
 * @author leon
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.unit.portal;

import java.util.Calendar;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.forms.client.ui.CCompDebugId;
import com.pyx4j.selenium.D;

import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;
import com.propertyvista.portal.domain.ptapp.PotentialTenant.Status;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenantList;

public class DatePickerValidationTest extends DatePickerTestBase {

    public void testDatePickerValidation() {
        login();
        validateBirthDateOfApplicant();
    }

    private void validateBirthDateOfApplicant() {
        //Test applicant
        validateBirthDateLessThen18(0);
        //Test co-applicant        
        validateCoApplicant();
        //Test second applicant
        validateSecondApplicant();
        //Test future dates
        validateBirthDateInFuture(0);
        //Test missing date
        validateMissingDate();
        //Test Incorrect format
        validateIncorrectFormat();
        //Validate that age is < 150
        validateOldAge();
    }

    private void validateOldAge() {
        // TODO Auto-generated method stub

    }

    private void validateIncorrectFormat() {
        // TODO Auto-generated method stub

    }

    private void validateMissingDate() {
        //selenium.setValue(datePickerTextBoxId(0), "");
    }

    private void validateSecondApplicant() {
        //TODO Leon 2.3
    }

    private void validateCoApplicant() {
        //Change birthdate to now-20 to ensure change of status
        int index = 1;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -20);
        navigateToDate(datePickerId(index), calendar);
        selenium.setValue(D.id(D.id(proto(PotentialTenantList.class).tenants(), index), D.id(proto(PotentialTenantInfo.class).status())),
                Status.CoApplicant.toString());
        validateBirthDateLessThen18(index);
    }

    private void validateBirthDateLessThen18(int index) {
        //Validate less then 18 on lost focus
        Calendar calendar = Calendar.getInstance();
        navigateToDateAndClick(datePickerId(index), calendar);
        assertVisible(validation(index));
        //clear validation message
        calendar.add(Calendar.YEAR, -20);
        navigateToDate(datePickerId(index), calendar);
        //Validate less then 18 on save
        calendar.add(Calendar.YEAR, 5);
        typeInDate(datePickerTextBoxId(index), calendar);
        saveAndContinue(false);
        assertMessages(UserMessageType.WARN);
        assertVisible(validation(index));
        //clear validation message
        calendar.add(Calendar.YEAR, -20);
        navigateToDate(datePickerId(index), calendar);
    }

    private void validateBirthDateInFuture(int index) {
        //Validate with lost focus
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        navigateToDateAndClick(datePickerId(index), calendar);
        assertVisible(validation(index));
        //clear validation message
        calendar.add(Calendar.YEAR, -20);
        navigateToDate(datePickerId(index), calendar);
        //Validate with click on save
        calendar.add(Calendar.YEAR, 22);
        typeInDate(datePickerTextBoxId(index), calendar);
        saveAndContinue(false);
        assertMessages(UserMessageType.WARN);
        assertVisible(validation(index));
        //clear validation message
        calendar.add(Calendar.YEAR, -22);
        navigateToDateAndClick(datePickerId(index), calendar);
    }

    private IDebugId datePickerTextBoxId(int index) {
        return D.id(D.id(proto(PotentialTenantList.class).tenants(), index), D.id(proto(PotentialTenantInfo.class).birthDate()));
    }

    private IDebugId datePickerId(int index) {
        return D.id(datePickerTextBoxId(index), CCompDebugId.trigger);
    }

    private IDebugId validation(int index) {
        return new CompositeDebugId(D.id(proto(PotentialTenantList.class).tenants(), index), IFolderEditorDecorator.DecoratorsIds.Label);
    }
}
