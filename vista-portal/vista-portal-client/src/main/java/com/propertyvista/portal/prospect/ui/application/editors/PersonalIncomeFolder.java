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
package com.propertyvista.portal.prospect.ui.application.editors;

import java.util.EnumSet;

import com.google.gwt.user.datepicker.client.CalendarUtil;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class PersonalIncomeFolder extends PortalBoxFolder<CustomerScreeningIncome> {

    private static final I18n i18n = I18n.get(PersonalIncomeFolder.class);

    public PersonalIncomeFolder() {
        this(true);
    }

    public PersonalIncomeFolder(boolean editable) {
        super(CustomerScreeningIncome.class, i18n.tr("Personal Income"), editable);

        if (editable) {
            setNoDataLabel(i18n.tr("Please enter your source(s) of income if present"));
        }
    }

    public void setDocumentsPolicy(ApplicationDocumentationPolicy policy) {
        for (CComponent<?, ?, ?, ?> item : getComponents()) {
            ((PersonalIncomeEditor) ((CFolderItem<?>) item).getComponents().iterator().next()).setDocumentsPolicy(policy);
        }
    }

    @Override
    protected CForm<CustomerScreeningIncome> createItemForm(IObject<?> member) {
        return new PersonalIncomeEditor();
    }

    @Override
    protected void addItem() {
        new SelectEnumDialog<IncomeSource>(i18n.tr("Select Income Source"), EnumSet.allOf(IncomeSource.class)) {
            @Override
            public boolean onClickOk() {
                CustomerScreeningIncome item = EntityFactory.create(CustomerScreeningIncome.class);
                item.incomeSource().setValue(getSelectedType());
                addItem(item);
                return true;
            }
        }.show();
    }

    @Override
    public void addValidations() {
        super.addValidations();

        this.addComponentValidator(new AbstractComponentValidator<IList<CustomerScreeningIncome>>() {
            @Override
            public AbstractValidationError isValid() {
                if (getCComponent().getValue() != null) {
                    if (getCComponent().getValue().size() == 1) {
                        CustomerScreeningIncome income = getCComponent().getValue().get(0);
                        if (!income.details().isEmpty()) {
                            switch (income.incomeSource().getValue()) {
                            case fulltime:
                            case parttime:
                                IncomeInfoEmployer employer = income.details().cast();
                                if (!employer.ends().isNull() && !employer.starts().isNull()) {
                                    // valid, if more than 1 year, otherwise - more employment needed!
                                    if (CalendarUtil.getDaysBetween(employer.starts().getValue(), employer.ends().getValue()) < 366) {
                                        return new BasicValidationError(getCComponent(), i18n.tr("You need to enter more employment information"));
                                    }
                                }
                            }
                        }
                    } else if (getCComponent().getValue().size() > 3) {
                        return new BasicValidationError(getCComponent(), i18n.tr("No need to supply more than 3 items"));
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void generateMockData() {
        if (getItemCount() == 0) {
            CustomerScreeningIncome income = EntityFactory.create(CustomerScreeningIncome.class);
            income.incomeSource().setValue(IncomeSource.fulltime);
            addItem(income);
        }
    }
}