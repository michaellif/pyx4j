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
package com.propertyvista.common.client.ui.components.folders;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.editors.PersonalIncomeEditor;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.PersonalIncome;

public class PersonalIncomeFolder extends VistaBoxFolder<PersonalIncome> {

    private static final I18n i18n = I18n.get(PersonalIncomeFolder.class);

    public PersonalIncomeFolder(boolean modifyable) {
        super(PersonalIncome.class, modifyable);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PersonalIncome) {
            return new PersonalIncomeEditor();
        }
        return super.create(member);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        this.addValueValidator(new EditableValueValidator<IList<PersonalIncome>>() {

            @SuppressWarnings("incomplete-switch")
            @Override
            public ValidationError isValid(CComponent<IList<PersonalIncome>, ?> component, IList<PersonalIncome> value) {
                if (value != null && value.size() == 1) {
                    PersonalIncome income = value.get(0);
                    if (!income.details().isEmpty()) {
                        switch (income.incomeSource().getValue()) {
                        case fulltime:
                        case parttime:
                            IncomeInfoEmployer employer = income.details().cast();
                            return (employer.ends().getValue().getTime() - employer.starts().getValue().getTime()) > 365 * 24 * 60 * 60 * 1000l ? null
                                    : new ValidationError(component, i18n.tr("You need to enter previous employment information"));
                            // valid, if more than 1 year, otherwise - previous employment needed! 
                        }
                    }
                }
                return null;
            }

        });
    }
}