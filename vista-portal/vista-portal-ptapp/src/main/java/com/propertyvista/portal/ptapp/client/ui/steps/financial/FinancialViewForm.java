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
package com.propertyvista.portal.ptapp.client.ui.steps.financial;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.CMoney;
import com.propertyvista.common.client.ui.components.folders.PersonalAssetFolder;
import com.propertyvista.common.client.ui.components.folders.PersonalIncomeFolder;
import com.propertyvista.common.client.ui.components.folders.TenantGuarantorFolder;
import com.propertyvista.domain.financial.Money;
import com.propertyvista.portal.rpc.ptapp.dto.TenantFinancialDTO;

public class FinancialViewForm extends CEntityEditor<TenantFinancialDTO> {

    static I18n i18n = I18n.get(FinancialViewForm.class);

    public FinancialViewForm() {
        this(new VistaEditorsComponentFactory());
    }

    public FinancialViewForm(IEditableComponentFactory factory) {
        super(TenantFinancialDTO.class, factory);
        setEditable(factory instanceof VistaEditorsComponentFactory);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(Money.class)) {
            return new CMoney();
        } else {
            return super.create(member);
        }
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, proto().incomes().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().incomes(), new PersonalIncomeFolder(isEditable())));
        main.setWidget(++row, 0, new HTML());

        main.setH1(++row, 0, 1, proto().assets().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().assets(), new PersonalAssetFolder(isEditable())));
        main.setWidget(++row, 0, new HTML());

        main.setH1(++row, 0, 1, proto().guarantors().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().guarantors(), new TenantGuarantorFolder(isEditable())));
        main.setWidget(++row, 0, new HTML());

        return main;
    }

    @Override
    public void populate(TenantFinancialDTO value) {
        super.populate(value);
    }

    @Override
    public void addValidations() {
        this.addValueValidator(new EditableValueValidator<TenantFinancialDTO>() {

            @Override
            public boolean isValid(CComponent<TenantFinancialDTO, ?> component, TenantFinancialDTO value) {
                return (value.assets().size() > 0) || (value.incomes().size() > 0);
            }

            @Override
            public String getValidationMessage(CComponent<TenantFinancialDTO, ?> component, TenantFinancialDTO value) {
                return i18n.tr("At least one source of income or one asset is required");
            }
        });
    }
}
