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

import com.google.gwt.user.datepicker.client.CalendarUtil;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
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

    public void setPolicyEntity(IEntity parentEntity) {
        ClientPolicyManager.obtainHierarchicalEffectivePolicy(parentEntity, ApplicationDocumentationPolicy.class,
                new DefaultAsyncCallback<ApplicationDocumentationPolicy>() {
                    @Override
                    public void onSuccess(ApplicationDocumentationPolicy result) {
                        documentationPolicy = result;

                        for (CComponent<?, ?, ?> item : getComponents()) {
                            ((PersonalIncomeEditor) ((CFolderItem<?>) item).getComponents().iterator().next()).setDocumentsPolicy(documentationPolicy);
                        }
                    }
                });
    }

    @Override
    protected CForm<CustomerScreeningIncome> createItemForm(IObject<?> member) {
        return new PersonalIncomeEditor(documentationPolicy);
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
                if (getComponent().getValue() != null) {
                    if (getComponent().getValue().size() == 1) {
                        CustomerScreeningIncome income = getComponent().getValue().get(0);
                        if (!income.details().isEmpty()) {
                            switch (income.incomeSource().getValue()) {
                            case fulltime:
                            case parttime:
                                IncomeInfoEmployer employer = income.details().cast();
                                if (!employer.ends().isNull() && !employer.starts().isNull()) {
                                    // valid, if more than 1 year, otherwise - more employment needed! 
                                    if (CalendarUtil.getDaysBetween(employer.starts().getValue(), employer.ends().getValue()) < 366) {
                                        return new BasicValidationError(getComponent(), i18n.tr("You need to enter more employment information"));
                                    }
                                }
                            }
                        }
                    } else if (getComponent().getValue().size() > 3) {
                        return new BasicValidationError(getComponent(), i18n.tr("No need to supply more than 3 items"));
                    }
                }
                return null;
            }
        });
    }
}