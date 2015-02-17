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
 */
package com.propertyvista.portal.prospect.ui.application.components;

import java.util.EnumSet;

import com.google.gwt.user.datepicker.client.CalendarUtil;

import com.pyx4j.commons.LogicalDate;
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
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.IEmploymentInfo;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class PersonalIncomeFolder extends PortalBoxFolder<CustomerScreeningIncome> {

    private static final I18n i18n = I18n.get(PersonalIncomeFolder.class);

    private RestrictionsPolicy restrictionsPolicy;

    private ApplicationDocumentationPolicy documentationPolicy;

    public PersonalIncomeFolder() {
        this(true);
    }

    public PersonalIncomeFolder(boolean editable) {
        super(CustomerScreeningIncome.class, i18n.tr("Personal Income"), editable);

        if (editable) {
            setNoDataLabel(i18n.tr("Please enter your source(s) of income if present"));
        }
    }

    public void setRestrictionsPolicy(RestrictionsPolicy policy) {
        restrictionsPolicy = policy;
        revalidate();
    }

    public void setDocumentationPolicy(ApplicationDocumentationPolicy policy) {
        documentationPolicy = policy;
        for (CComponent<?, ?, ?, ?> item : getComponents()) {
            ((PersonalIncomeEditor) ((CFolderItem<?>) item).getComponents().iterator().next()).onSetDocumentationPolicy();
        }
    }

    public ApplicationDocumentationPolicy getDocumentationPolicy() {
        return documentationPolicy;
    }

    @Override
    protected CForm<CustomerScreeningIncome> createItemForm(IObject<?> member) {
        return new PersonalIncomeEditor(this);
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
                if (!getCComponent().getValue().isEmpty() && restrictionsPolicy != null) {
                    EmploymentsInfo info = getEmploymentsInfo();
                    if (info.employmentCount == 1) {
                        if (info.firstEmployment != null && !info.firstEmployment.isEmpty()) {
                            if (!info.firstEmployment.starts().isNull()) {
                                LogicalDate today = new LogicalDate(ClientContext.getServerDate());
                                LogicalDate date = new LogicalDate(info.firstEmployment.ends().isNull() ? today : info.firstEmployment.ends().getValue());
                                CalendarUtil.addMonthsToDate(date, -restrictionsPolicy.minEmploymentDuration().getValue(0));
                                CalendarUtil.addDaysToDate(date, 1); // compensate for 'including end date' logic
                                if (info.firstEmployment.starts().getValue().after(date) //
                                        || (!info.firstEmployment.ends().isNull() && !info.firstEmployment.ends().getValue().after(today))) {
                                    return new BasicValidationError(
                                            getCComponent(),
                                            i18n.tr("More employment information is necessary (entered employment either is not current or its duration is less than {0} months)",
                                                    restrictionsPolicy.minEmploymentDuration().getValue(0)));
                                }
                            }
                        }
                    } else if (info.employmentCount > restrictionsPolicy.maxNumberOfEmployments().getValue(Integer.MAX_VALUE)) {
                        return new BasicValidationError(getCComponent(), i18n.tr("No need to supply more than {0} employment items", restrictionsPolicy
                                .maxNumberOfEmployments().getValue(Integer.MAX_VALUE)));
                    } else if (getCComponent().getValue().size() - info.employmentCount > 3) {
                        return new BasicValidationError(getCComponent(), i18n.tr("No need to supply more than 3 other income items"));
                    }
                }
                return null;
            }
        });
    }

    private class EmploymentsInfo {

        int employmentCount;

        IEmploymentInfo firstEmployment;
    }

    private EmploymentsInfo getEmploymentsInfo() {
        EmploymentsInfo info = new EmploymentsInfo();
        info.firstEmployment = null;
        info.employmentCount = 0;

        for (CustomerScreeningIncome income : getValue()) {
            if (IncomeSource.employment().contains(income.incomeSource().getValue())) {
                if (++info.employmentCount == 1) {
                    info.firstEmployment = income.details().cast();
                }
            }
        }

        return info;
    }
}