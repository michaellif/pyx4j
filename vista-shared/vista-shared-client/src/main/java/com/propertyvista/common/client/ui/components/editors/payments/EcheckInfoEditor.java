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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.EcheckAccountNumberValidator;
import com.propertyvista.domain.payment.AccountNumberIdentity;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.shared.util.EcheckFormatter;

public class EcheckInfoEditor extends CEntityForm<EcheckInfo> {

    private static final I18n i18n = I18n.get(EcheckInfoEditor.class);

    protected final CPersonalIdentityField<AccountNumberIdentity> accountEditor = new CPersonalIdentityField<AccountNumberIdentity>(
            AccountNumberIdentity.class, new EcheckFormatter());

    public EcheckInfoEditor() {
        super(EcheckInfo.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();

        int row = -1;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().nameOn()), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().accountNo(), accountEditor), 20).build());

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().branchTransitNumber()), 5).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().bankId()), 3).build());

        if (!isViewable() && isEditable()) {
            Image image = new Image(VistaImages.INSTANCE.eChequeGuide().getSafeUri());
            image.getElement().getStyle().setMarginTop(1, Unit.EM);
            panel.setWidget(++row, 0, image);
            panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        }

        return panel;
    }

    @Override
    public void addValidations() {
        get(proto().accountNo()).addComponentValidator(new EcheckAccountNumberValidator());
        get(proto().branchTransitNumber()).addComponentValidator(new AbstractComponentValidator<String>() {
            @Override
            public FieldValidationError isValid() {
                if (CommonsStringUtils.isStringSet(getComponent().getValue())) {
                    return ValidationUtils.isBranchTransitNumberValid(getComponent().getValue()) ? null : new FieldValidationError(getComponent(), i18n
                            .tr("Number should consist of 5 digits"));
                } else {
                    return null;
                }
            }
        });
        get(proto().bankId()).addComponentValidator(new AbstractComponentValidator<String>() {
            @Override
            public FieldValidationError isValid() {
                if (CommonsStringUtils.isStringSet(getComponent().getValue())) {
                    return ValidationUtils.isBankIdNumberValid(getComponent().getValue()) ? null : new FieldValidationError(getComponent(), i18n
                            .tr("Number should consist of 3 digits"));
                } else {
                    return null;
                }
            }
        });

    }

    @Override
    public void generateMockData() {
        get(proto().nameOn()).setMockValue("Dev");
        get(proto().bankId()).setMockValue("123");
        get(proto().branchTransitNumber()).setMockValue("12345");

        CTextFieldBase<?, ?> id = (CTextFieldBase<?, ?>) get(proto().accountNo());
        id.setMockValueByString(String.valueOf(System.currentTimeMillis() % 10000000));
    }
}
