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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class PersonalIncomeFolder extends PortalBoxFolder<CustomerScreeningIncome> {

    private static final I18n i18n = I18n.get(PersonalIncomeFolder.class);

    private ApplicationDocumentationPolicy documentationPolicy;

    public PersonalIncomeFolder() {
        this(true);
    }

    public PersonalIncomeFolder(boolean modifiable) {
        super(CustomerScreeningIncome.class, i18n.tr("Personal Income"), modifiable);
    }

    public void setDocumentsPolicy(ApplicationDocumentationPolicy policy) {
        this.documentationPolicy = policy;

        for (CComponent<?> item : getComponents()) {
            ((PersonalIncomeEditor) ((CEntityFolderItem<?>) item).getComponents().iterator().next()).setDocumentsPolicy(documentationPolicy);
        }
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof CustomerScreeningIncome) {
            return new PersonalIncomeEditor(documentationPolicy);
        }
        return super.create(member);
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
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && getComponent().getValue().size() == 1) {
                    CustomerScreeningIncome income = getComponent().getValue().get(0);
                    if (!income.details().isEmpty()) {
                        switch (income.incomeSource().getValue()) {
                        case fulltime:
                        case parttime:
                            IncomeInfoEmployer employer = income.details().cast();
                            if (employer.ends().isNull() || employer.starts().isNull()) {
                                return null;
                            }
                            return (employer.ends().getValue().getTime() - employer.starts().getValue().getTime()) > 365 * 24 * 60 * 60 * 1000l ? null
                                    : new FieldValidationError(getComponent(), i18n.tr("You need to enter previous employment information"));
                            // valid, if more than 1 year, otherwise - previous employment needed! 
                        }
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