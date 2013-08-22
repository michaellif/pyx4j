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
import java.util.EnumSet;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
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
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CTextComponent;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.payments.CreditCardNumberTypeValidator.CreditCardTypeProvider;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.CreditCardNumberIdentity;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.portal.rpc.shared.services.CreditCardValidationService;

public class CreditCardInfoEditor extends CEntityDecoratableForm<CreditCardInfo> {

    private static final I18n i18n = I18n.get(CreditCardInfoEditor.class);

    // a hack for async creditCardNumber Validation
    private boolean isCreditCardNumberCheckSent;

    private boolean isCreditCardNumberCheckRecieved;

    private ValidationError isCreditCardNumberValid;

    private final CPersonalIdentityField<CreditCardNumberIdentity> cardEditor;

    private final CComboBox<CreditCardType> typeSelector;

    public CreditCardInfoEditor() {
        super(CreditCardInfo.class);
        typeSelector = new CComboBox<CreditCardType>();
        cardEditor = new CPersonalIdentityField<CreditCardNumberIdentity>(CreditCardNumberIdentity.class, "X XXXX XXXX xxxx;XXXX XXXX XXXX xxxx", null);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();

        int row = -1;
        CMonthYearPicker monthYearPicker = new CMonthYearPicker(false);
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().nameOn()), 20, true).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().cardType(), typeSelector), 15, true).build());

        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().card(), cardEditor), 15, true).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().expiryDate(), monthYearPicker), 15, true).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().securityCode()), 3, true).build());

        // tweak:
        monthYearPicker.setYearRange(new Range(1900 + new Date().getYear(), 10));
        get(proto().securityCode()).setVisible(isEditable());

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        typeSelector.setOptions(getAllowedCardTypes());

        updateVisibility(getValue());
    }

    private void updateVisibility(CreditCardInfo value) {
        if (isEditable()) {
            cardEditor.setMandatory(false);
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

        cardEditor.addValueChangeHandler(new ValueChangeHandler<CreditCardNumberIdentity>() {
            @Override
            public void onValueChange(ValueChangeEvent<CreditCardNumberIdentity> event) {
                cardEditor.setMandatory(true);
                get(proto().securityCode()).setMandatory(true);
            }
        });

        // set up async validation for credit card number:
        cardEditor.addValueValidator(new EditableValueValidator<CreditCardNumberIdentity>() {
            @Override
            public ValidationError isValid(CComponent<CreditCardNumberIdentity> component, CreditCardNumberIdentity value) {
                if (value != null) {
                    if (isCreditCardNumberCheckRecieved) {
                        isCreditCardNumberCheckRecieved = false;
                        isCreditCardNumberCheckSent = false;
                        return isCreditCardNumberValid;
                    } else if (!isCreditCardNumberCheckSent) {
                        return CreditCardInfoEditor.this.validateCreditCardNumberAsync(component, value);
                    } else {
                        return new ValidationError(component, i18n.tr("Validation in progress"));
                    }
                } else {
                    return null;
                }
            }
        });

        cardEditor.addValueValidator(new CreditCardNumberTypeValidator(new CreditCardTypeProvider() {
            @Override
            public CreditCardType getCreditCardType() {
                return (get(proto().cardType()).getValue() == null ? null : get(proto().cardType()).getValue());
            }
        }));
        get(proto().cardType()).addValueChangeHandler(new ValueChangeHandler<CreditCardType>() {
            @Override
            public void onValueChange(ValueChangeEvent<CreditCardType> event) {
                // imitate user input and revalidate
                cardEditor.clear(true);
//                cardEditor.onEditingStop();
//                cardEditor.revalidate();
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

    protected Set<CreditCardType> getAllowedCardTypes() {
        return EnumSet.allOf(CreditCardType.class);
    }

    private ValidationError validateCreditCardNumberAsync(final CComponent<?> component, CreditCardNumberIdentity value) {
        if ((value != null) && CommonsStringUtils.isStringSet(value.newNumber().getValue())) {
            if (ValidationUtils.isCreditCardNumberValid(value.newNumber().getValue())) {
                if (getValue().cardType().getValue() != CreditCardType.VisaDebit) {
                    return null;
                } else {
                    CreditCardInfo ccInfo = getValue().<CreditCardInfo> duplicate();
                    ccInfo.card().newNumber().set(value.newNumber());
                    GWT.<CreditCardValidationService> create(CreditCardValidationService.class).validate(new DefaultAsyncCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            setCreditCardNumberValidationResult(result ? null : new ValidationError(component, i18n.tr("Invalid Card Number")));
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            isCreditCardNumberCheckRecieved = true;
                            super.onFailure(caught);
                        }
                    }, ccInfo);
                    isCreditCardNumberCheckSent = true;
                    return new ValidationError(component, i18n.tr("Validation in progress"));
                }
            }
        }
        return new ValidationError(component, i18n.tr("Invalid Card Number"));
    }

    private void setCreditCardNumberValidationResult(ValidationError error) {
        isCreditCardNumberCheckRecieved = true;
        isCreditCardNumberValid = error;
        cardEditor.revalidate();
    }

    private void devGenerateCreditCard() {
        if (get(proto().nameOn()).isValueEmpty()) {
            get(proto().nameOn()).setValue("Dev");
        }
        if (get(proto().cardType()).getValue() == null) {
            get(proto().cardType()).setValue(CreditCardType.Visa);
        }
        cardEditor.setValueByString(CreditCardNumberGenerator.generateCardNumber(get(proto().cardType()).getValue()));

        LogicalDate nextMonth = new LogicalDate();
        TimeUtils.addDays(nextMonth, 31);
        get(proto().expiryDate()).setValue(nextMonth);
        get(proto().securityCode()).setValue("123");
    }
}
