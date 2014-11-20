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

import com.pyx4j.commons.LogicalDate;
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
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.IEmploymentInfo;
import com.propertyvista.domain.tenant.income.IncomeSource;

public class PersonalIncomeFolder extends VistaBoxFolder<CustomerScreeningIncome> {

    private static final I18n i18n = I18n.get(PersonalIncomeFolder.class);

    private RestrictionsPolicy restrictionsPolicy = EntityFactory.create(RestrictionsPolicy.class);

    public PersonalIncomeFolder(boolean modifyable) {
        super(CustomerScreeningIncome.class, modifyable);
    }

    public void setPolicyEntity(IEntity parentEntity) {
        ClientPolicyManager.obtainHierarchicalEffectivePolicy(parentEntity, ApplicationDocumentationPolicy.class,
                new DefaultAsyncCallback<ApplicationDocumentationPolicy>() {
                    @Override
                    public void onSuccess(ApplicationDocumentationPolicy result) {
                        for (CComponent<?, ?, ?, ?> item : getComponents()) {
                            ((PersonalIncomeEditor) ((CFolderItem<?>) item).getComponents().iterator().next()).setDocumentsPolicy(result);
                        }
                    }
                });

        ClientPolicyManager.obtainHierarchicalEffectivePolicy(parentEntity, RestrictionsPolicy.class, new DefaultAsyncCallback<RestrictionsPolicy>() {
            @Override
            public void onSuccess(RestrictionsPolicy result) {
                restrictionsPolicy = result;
                revalidate();
            }
        });
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
                    int employmentCount = countEmployments();
                    if (employmentCount == 1) {
                        IEmploymentInfo employment = getFirstEmployment();
                        if (!employment.isEmpty()) {
                            if (!employment.ends().isNull() && !employment.starts().isNull()) {
                                LogicalDate date = new LogicalDate(employment.starts().getValue());
                                CalendarUtil.addMonthsToDate(date, restrictionsPolicy.minEmploymentDuration().getValue(0));
                                if (employment.ends().getValue().before(date)) {
                                    return new BasicValidationError(getCComponent(), i18n.tr("You need to enter more employment information"));
                                }
                            }
                        }
                    } else if (employmentCount > restrictionsPolicy.maxNumberOfEmployments().getValue(Integer.MAX_VALUE)) {
                        return new BasicValidationError(getCComponent(), i18n.tr("No need to supply more than {0} employment items", restrictionsPolicy
                                .maxNumberOfEmployments().getValue(Integer.MAX_VALUE)));
                    } else if (getCComponent().getValue().size() - employmentCount > 3) {
                        return new BasicValidationError(getCComponent(), i18n.tr("No need to supply more than 3 general income items"));
                    }
                }
                return null;
            }
        });
    }

    private int countEmployments() {
        int counter = 0;

        for (CustomerScreeningIncome income : getValue()) {
            if (IncomeSource.employment().contains(income.incomeSource().getValue())) {
                ++counter;
            }
        }

        return counter;
    }

    private IEmploymentInfo getFirstEmployment() {
        IEmploymentInfo employment = null;

        for (CustomerScreeningIncome income : getValue()) {
            if (IncomeSource.employment().contains(income.incomeSource().getValue())) {
                employment = income.details().cast();
            }
        }

        return employment;
    }
}