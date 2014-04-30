/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.dto.TenantFinancialDTO;

public class FinancialViewForm extends CForm<TenantFinancialDTO> {

    static I18n i18n = I18n.get(FinancialViewForm.class);

    public FinancialViewForm() {
        super(TenantFinancialDTO.class, new VistaEditorsComponentFactory());
    }

    public FinancialViewForm(boolean viewMode) {
        this();

        if (viewMode) {
            setEditable(false);
            setViewable(true);
        }
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Dual, proto().person().name(), new NameEditor(i18n.tr("Person")));

        formPanel.h1(proto().incomes().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().incomes(), new PersonalIncomeFolder(isEditable()));

        formPanel.h1(proto().assets().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().assets(), new PersonalAssetFolder(isEditable()));

        return formPanel;
    }

    @Override
    public void addValidations() {
        this.addComponentValidator(new AbstractComponentValidator<TenantFinancialDTO>() {
            @Override
            public FieldValidationError isValid() {
                return (getComponent().getValue().assets().size() > 0) || (getComponent().getValue().incomes().size() > 0) ? null : new FieldValidationError(
                        getComponent(), i18n.tr("At least one source of income or one asset is required"));
            }
        });
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable()) {
            ((PersonalIncomeFolder) (CComponent<?, ?, ?>) get(proto().incomes())).setPolicyEntity(getValue());
        }
    }
}
