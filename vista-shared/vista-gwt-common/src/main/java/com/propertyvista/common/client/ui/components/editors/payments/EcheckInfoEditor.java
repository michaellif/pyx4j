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
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.EcheckAccountNumberValidator;
import com.propertyvista.domain.payment.AccountNumberIdentity;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.util.ValidationUtils;

public class EcheckInfoEditor extends CEntityDecoratableForm<EcheckInfo> {

    private static final I18n i18n = I18n.get(EcheckInfoEditor.class);

    private final CPersonalIdentityField<AccountNumberIdentity> accountEditor = new CPersonalIdentityField<AccountNumberIdentity>(AccountNumberIdentity.class,
            "X xxxx;XX xxxx;XXX xxxx;XXXX xxxx;X XXXX xxxx;XX XXXX xxxx;XXX XXXX xxxx;XXXX XXXX xxxx", null);

    private final double maxCompWidth;

    public EcheckInfoEditor() {
        this(20);
    }

    public EcheckInfoEditor(double maxCompWidth) {
        super(EcheckInfo.class);
        this.maxCompWidth = maxCompWidth;
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();

        int row = -1;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().nameOn()), maxCompWidth).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().accountNo(), accountEditor), maxCompWidth).build());

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().branchTransitNumber()), 5).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().bankId()), 3).build());

        if (!isViewable()) {
            Image image = new Image(VistaImages.INSTANCE.eChequeGuide().getSafeUri());
            image.getElement().getStyle().setMarginTop(1, Unit.EM);
            panel.setWidget(++row, 0, image);
            panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        }

        return panel;
    }

    @Override
    public void addValidations() {
        get(proto().accountNo()).addValueValidator(new EcheckAccountNumberValidator());
        get(proto().branchTransitNumber()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String> component, String value) {
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
            public ValidationError isValid(CComponent<String> component, String value) {
                if (CommonsStringUtils.isStringSet(value)) {
                    return ValidationUtils.isBankIdNumberValid(value) ? null : new ValidationError(component, i18n.tr("Number should consist of 3 digits"));
                } else {
                    return null;
                }
            }
        });

        if (ApplicationMode.isDevelopment()) {
            this.addDevShortcutHandler(new DevShortcutHandler() {
                @Override
                public void onDevShortcut(DevShortcutEvent event) {
                    if (event.getKeyCode() == 'Q') {
                        event.consume();
                        devGenerateEcheck();
                    }
                }
            });
        }
    }

    private void devGenerateEcheck() {
        if (get(proto().nameOn()).isValueEmpty()) {
            get(proto().nameOn()).setValue("Dev");
        }
        get(proto().bankId()).setValue("123");
        get(proto().branchTransitNumber()).setValue("12345");

        CTextFieldBase<?, ?> id = (CTextFieldBase<?, ?>) get(proto().accountNo());
        id.onEditingStop(); // assume new user input; will obfuscate the value if focused
        id.setValueByString(String.valueOf(System.currentTimeMillis() % 10000000));
    }
}
