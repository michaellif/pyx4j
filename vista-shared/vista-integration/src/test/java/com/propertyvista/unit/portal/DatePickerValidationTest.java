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
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.CCompDebugId;
import com.pyx4j.selenium.D;

import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseListDTO;

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
        validateDependant();
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
        int index = 1;
        assertNotVisible(validation(index));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -220);
        typeInDate(datePickerTextBoxId(index), calendar);
        selenium.click(statusId(index));
        assertVisible(validation(index));
    }

    private void validateIncorrectFormat() {
        int index = 1;
        assertNotVisible(validation(index));
        selenium.setValue(datePickerTextBoxId(index), "not a date");
        selenium.click(statusId(index));
        assertVisible(validation(index));
        clearDateValidation(index);
        selenium.setValue(datePickerTextBoxId(index), "not a date 2");
        saveAndContinue(false);
        assertMessages(UserMessageType.WARN);
        assertVisible(validation(index));
        clearDateValidation(index);
    }

    private void validateMissingDate() {
        int index = 1;
        assertNotVisible(validation(index));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -20);
        //On lost focus
        selenium.setValue(datePickerTextBoxId(index), "");
        selenium.click(statusId(index));
        assertVisible(validation(index));
        clearDateValidation(index);
        //on save
        selenium.setValue(datePickerTextBoxId(index), "");
        saveAndContinue(false);
        assertMessages(UserMessageType.WARN);
        assertVisible(validation(index));
        clearDateValidation(index);
    }

    private void validateDependant() {
        //Dependant < 18
        int index = 1;
        //Make sure status is editable
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -20);
        navigateToDateAndClick(datePickerId(index), calendar);
        setStatus(index, TenantInLease.Status.Dependent);
        calendar.add(Calendar.YEAR, 10);
        navigateToDateAndClick(datePickerId(index), calendar);
        assertNotEditable(statusId(index));
        assertNotEditable(ownershipId(index));
        assertEquals("on", selenium.getValue(ownershipId(index)));
        //Dependant > 18
        calendar.add(Calendar.YEAR, -10);
        navigateToDateAndClick(datePickerId(index), calendar);
        assertEditable(statusId(index));
        assertEditable(ownershipId(index));
        setStatus(index, TenantInLease.Status.Dependent);
        //TODO 
        //ask anya about this test
        //assertEquals("off", selenium.getValue(ownershipId(index)));
    }

    private void validateCoApplicant() {
        //Change birthdate to now-20 to ensure change of status
        int index = 1;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -20);
        navigateToDate(datePickerId(index), calendar);
        setStatus(index, TenantInLease.Status.CoApplicant);
        validateBirthDateLessThen18(index);
    }

    private void validateBirthDateLessThen18(int index) {
        //Validate less then 18 on lost focus
        Calendar calendar = Calendar.getInstance();
        navigateToDateAndClick(datePickerId(index), calendar);
        assertVisible(validation(index));
        clearDateValidation(index);
        //Validate less then 18 on save
        calendar.add(Calendar.YEAR, 5);
        typeInDate(datePickerTextBoxId(index), calendar);
        saveAndContinue(false);
        assertMessages(UserMessageType.WARN);
        assertVisible(validation(index));
        clearDateValidation(index);
    }

    private void validateBirthDateInFuture(int index) {
        //Validate with lost focus
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        navigateToDateAndClick(datePickerId(index), calendar);
        assertVisible(validation(index));
        clearDateValidation(index);
        //Validate with click on save
        calendar.add(Calendar.YEAR, 22);
        typeInDate(datePickerTextBoxId(index), calendar);
        saveAndContinue(false);
        assertMessages(UserMessageType.WARN);
        assertVisible(validation(index));
        clearDateValidation(index);
    }

    private void clearDateValidation(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -22);
        typeInDate(datePickerTextBoxId(index), calendar);
        selenium.click(lastNameId(index));
    }

    private void setStatus(int index, TenantInLease.Status status) {
        selenium.setValue(statusId(index), status.toString());
    }

    private IDebugId ownershipId(int index) {
        return D.id(baseID(index), D.id(proto(TenantInLease.class).takeOwnership()));
    }

    private IDebugId statusId(int index) {
        return D.id(baseID(index), D.id(proto(TenantInLease.class).status()));
    }

    private IDebugId lastNameId(int index) {
        return D.id(baseID(index), D.id(proto(TenantInLease.class).tenant().person().name().lastName()));
    }

    private IDebugId datePickerTextBoxId(int index) {
        return D.id(baseID(index), D.id(proto(TenantInLease.class).tenant().person().birthDate()));
    }

    private IDebugId datePickerId(int index) {
        return D.id(datePickerTextBoxId(index), CCompDebugId.trigger);
    }

    private IDebugId validation(int index) {
        return new CompositeDebugId(baseID(index), IFolderDecorator.DecoratorsIds.Label);
    }

    private IDebugId baseID(int index) {
        return D.id(proto(TenantInLeaseListDTO.class).tenants(), index);
    }
}
