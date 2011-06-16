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

import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.ui.CCompDebugId;
import com.pyx4j.selenium.D;

import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenantList;

public class DatePickerValidationTest extends DatePickerBaseTest {

    private final IDebugId datePickerTextBoxId = D.id(D.id(proto(PotentialTenantList.class).tenants(), 0), D.id(proto(PotentialTenantInfo.class).birthDate()));

    private final IDebugId datePickerId = D.id(datePickerTextBoxId, CCompDebugId.trigger);

    public void testDatePickerValidation() {
        login();
        validateBirthDateOfApplicant();
    }

    private void validateBirthDateOfApplicant() {
        validateBirthDateLessThen18();
        validateBirthDateAt18();
        validateBirthDateMoreThen18();
    }

    private void validateBirthDateMoreThen18() {
        // TODO Auto-generated method stub

    }

    private void validateBirthDateAt18() {
        // TODO Auto-generated method stub

    }

    private void validateBirthDateLessThen18() {
        // TODO Auto-generated method stub

    }
}
