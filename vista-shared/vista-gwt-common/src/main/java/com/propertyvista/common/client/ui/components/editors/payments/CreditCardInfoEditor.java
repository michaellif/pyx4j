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

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CTextComponent;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.payments.CreditCardNumberTypeValidator.CreditCardTypeProvider;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.CreditCardNumberValidator;
import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.CreditCardNumberIdentity;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.misc.CreditCardNumberGenerator;

public class CreditCardInfoEditor extends CEntityDecoratableForm<CreditCardInfo> {

    private static final I18n i18n = I18n.get(CreditCardInfoEditor.class);

    public CreditCardInfoEditor() {
        super(CreditCardInfo.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();

        int row = -1;
        CMonthYearPicker monthYearPicker = new CMonthYearPicker(false);
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().nameOn()), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().cardType()), 15).build());
        panel.setWidget(
                ++row,
                0,
                new FormDecoratorBuilder(inject(proto().card(), new CPersonalIdentityField<CreditCardNumberIdentity>(CreditCardNumberIdentity.class,
                        "X XXXX XXXX xxxx;XXXX XXXX XXXX xxxx", null)), 15).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().expiryDate(), monthYearPicker), 15).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().securityCode()), 3).build());

        // tweak:
        monthYearPicker.setYearRange(new Range(1900 + new Date().getYear(), 10));
        get(proto().securityCode()).setVisible(isEditable());

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        updateVisibility(getValue());
    }

    private void updateVisibility(CreditCardInfo value) {
        if (isEditable()) {
            get(proto().card()).setMandatory(false);
            get(proto().securityCode()).setMandatory(false);
            ((CTextComponent<?, ?>) get(proto().securityCode())).setWatermark("XXX");
        }
    }

    @Override
    public void addValidations() {
        this.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyName.editable) {
                    get(proto().securityCode()).setVisible(isEditable());
                }
            }
        });

        get(proto().card()).addValueChangeHandler(new ValueChangeHandler<CreditCardNumberIdentity>() {
            @Override
            public void onValueChange(ValueChangeEvent<CreditCardNumberIdentity> event) {
                get(proto().card()).setMandatory(true);
                get(proto().securityCode()).setMandatory(true);
            }
        });

        get(proto().card()).addValueValidator(new CreditCardNumberValidator());
        get(proto().card()).addValueValidator(new CreditCardNumberTypeValidator(new CreditCardTypeProvider() {
            @Override
            public CreditCardType getCreditCardType() {
                return (get(proto().cardType()).getValue() == null ? null : get(proto().cardType()).getValue());
            }
        }));
        get(proto().cardType()).addValueChangeHandler(new ValueChangeHandler<CreditCardType>() {
            @Override
            public void onValueChange(ValueChangeEvent<CreditCardType> event) {
                // imitate user input and revalidate
                get(proto().card()).reset();
                get(proto().card()).onEditingStop();
                get(proto().card()).revalidate();
            }
        });

        get(proto().expiryDate()).addValueValidator(new FutureDateValidator());

        get(proto().securityCode()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String> component, String value) {
                if (CommonsStringUtils.isStringSet(value)) {
                    return ValidationUtils.isCreditCardCodeValid(value) ? null : new ValidationError(component, i18n
                            .tr("Security Code should consist of 3 to 4 digits"));
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
                        devGenerateCreditCard();
                    }
                }

            });
        }

    }

    private void devGenerateCreditCard() {
        get(proto().nameOn()).setValue("Dev");
        if (get(proto().cardType()).getValue() == null) {
            get(proto().cardType()).setValue(CreditCardType.Visa);
        }
        ((CTextFieldBase<?, ?>) get(proto().card())).setValueByString(CreditCardNumberGenerator.generateCardNumber(get(proto().cardType()).getValue()));

        LogicalDate nextMonth = new LogicalDate();
        TimeUtils.addDays(nextMonth, 31);
        get(proto().expiryDate()).setValue(nextMonth);
        get(proto().securityCode()).setValue("123");
    }
}
