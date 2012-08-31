/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.payments;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.c.CTokinazedNumberEditor;
import com.propertyvista.common.client.ui.validators.EcheckAccountNumberValidator;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.TokenizedAccountNumber;
import com.propertyvista.domain.util.ValidationUtils;

public class EcheckInfoEditor extends CEntityDecoratableForm<EcheckInfo> {

    private static final I18n i18n = I18n.get(EcheckInfoEditor.class);

    public EcheckInfoEditor() {
        super(EcheckInfo.class);
    }

    public EcheckInfoEditor(IEditableComponentFactory factory) {
        super(EcheckInfo.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();

        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nameOn()), 20).build());
        panel.getFlexCellFormatter().setColSpan(row, 0, 2);

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().branchTransitNumber()), 5).build());
        panel.setWidget(row, 1, new DecoratorBuilder(inject(proto().bankId()), 3).labelWidth(7).build());

        panel.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().accountNo(), new CTokinazedNumberEditor<TokenizedAccountNumber>(TokenizedAccountNumber.class)), 10).build());
        panel.getFlexCellFormatter().setColSpan(row, 0, 2);

        if (isEditable()) {
            panel.setWidget(++row, 0, new Image(VistaImages.INSTANCE.eChequeGuide().getSafeUri()));
            panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
            panel.getFlexCellFormatter().setColSpan(row, 0, 2);
        }

        panel.getColumnFormatter().setWidth(0, "70%");
        panel.getColumnFormatter().setWidth(1, "30%");

        panel.setWidth("36em");
        return panel;
    }

    @Override
    public void addValidations() {
        get(proto().accountNo()).addValueValidator(new EcheckAccountNumberValidator());
        get(proto().branchTransitNumber()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String, ?> component, String value) {
                if (CommonsStringUtils.isStringSet(value)) {
                    return ValidationUtils.isBranchTransitNumberValid(value) ? null : new ValidationError(component, i18n
                            .tr("Number should consist of 5 digits"));
                } else {
                    return null;
                }
            }
        });
        get(proto().bankId()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String, ?> component, String value) {
                if (CommonsStringUtils.isStringSet(value)) {
                    return ValidationUtils.isBankIdNumberValid(value) ? null : new ValidationError(component, i18n.tr("Number should consist of 3 digits"));
                } else {
                    return null;
                }
            }
        });
    }
}
