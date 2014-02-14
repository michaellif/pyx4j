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
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import java.util.EnumSet;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeSource;

public class PersonalIncomeFolder extends VistaBoxFolder<CustomerScreeningIncome> {

    private static final I18n i18n = I18n.get(PersonalIncomeFolder.class);

    private ApplicationDocumentationPolicy documentationPolicy;

    public PersonalIncomeFolder(boolean modifyable) {
        super(CustomerScreeningIncome.class, modifyable);
    }

    public void setParentEntity(IEntity parentEntity) {
        ClientPolicyManager.obtainHierarchicalEffectivePolicy(parentEntity, ApplicationDocumentationPolicy.class,
                new DefaultAsyncCallback<ApplicationDocumentationPolicy>() {
                    @Override
                    public void onSuccess(ApplicationDocumentationPolicy result) {
                        documentationPolicy = result;

                        for (CComponent<?> item : getComponents()) {
                            ((PersonalIncomeEditor) ((CEntityFolderItem<?>) item).getComponents().iterator().next()).setDocumentsPolicy(documentationPolicy);
                        }
                    }
                });
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

        this.addValueValidator(new EditableValueValidator<IList<CustomerScreeningIncome>>() {

            @SuppressWarnings("incomplete-switch")
            @Override
            public FieldValidationError isValid(CComponent<IList<CustomerScreeningIncome>> component, IList<CustomerScreeningIncome> value) {
                if (value != null && value.size() == 1) {
                    CustomerScreeningIncome income = value.get(0);
                    if (!income.details().isEmpty()) {
                        switch (income.incomeSource().getValue()) {
                        case fulltime:
                        case parttime:
                            IncomeInfoEmployer employer = income.details().cast();
                            if (employer.ends().isNull() || employer.starts().isNull()) {
                                return null;
                            }
                            return (employer.ends().getValue().getTime() - employer.starts().getValue().getTime()) > 365 * 24 * 60 * 60 * 1000l ? null
                                    : new FieldValidationError(component, i18n.tr("You need to enter previous employment information"));
                            // valid, if more than 1 year, otherwise - previous employment needed! 
                        }
                    }
                }
                return null;
            }

        });
    }
}