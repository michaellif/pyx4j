/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.financial;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.domain.tenant.income.PersonalIncome;

public class PersonalIncomeFolder extends VistaBoxFolder<PersonalIncome> {

    public PersonalIncomeFolder(boolean modifyable) {
        super(PersonalIncome.class, modifyable);
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PersonalIncome) {
            return new PersonalIncomeEditor();
        }
        return super.create(member);
    }
}