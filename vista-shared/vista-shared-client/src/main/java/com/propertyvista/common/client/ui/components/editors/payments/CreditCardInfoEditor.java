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
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CTextComponent;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.ui.components.editors.payments.CreditCardNumberTypeValidator.CreditCardTypeProvider;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.CreditCardNumberIdentity;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.portal.rpc.shared.services.CreditCardValidationService;
import com.propertyvista.shared.util.CreditCardFormatter;

public class CreditCardInfoEditor extends CEntityForm<CreditCardInfo> {

    private static final I18n i18n = I18n.get(CreditCardInfoEditor.class);

    // a hack for async creditCardNumber Validation
    private boolean isCreditCardNumberCheckSent;

    private boolean isCreditCardNumberCheckRecieved;

    private FieldValidationError isCreditCardNumberValid;

    protected final CComboBox<CreditCardType> typeSelector = new CComboBox<CreditCardType>();

    protected final CPersonalIdentityField<CreditCardNumberIdentity> cardEditor = new CPersonalIdentityField<CreditCardNumberIdentity>(
            CreditCardNumberIdentity.class, new CreditCardFormatter());

    public CreditCardInfoEditor() {
        super(CreditCardInfo.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();

        int row = -1;
        CMonthYearPicker monthYearPicker = new CMonthYearPicker(false);
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().nameOn()), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().cardType(), typeSelector), 20).build());

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().card(), cardEditor), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().expiryDate(), monthYearPicker), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().securityCode()), 3).build());

        // tweak:
        monthYearPicker.setYearRange(new Range(1900 + new Date().getYear(), 10));
        get(proto().securityCode()).setVisible(isEditable());

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (typeSelector.isEditable()) {
            typeSelector.setOptions(getAllowedCardTypes());
            if (getValue().id().isNull() && typeSelector.getOptions().size() == 1) {
                typeSelector.setValue(typeSelector.getOptions().get(0));
            }
        }

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
            public FieldValidationError isValid(CComponent<CreditCardNumberIdentity> component, CreditCardNumberIdentity value) {
                if (value != null) {
                    if (isCreditCardNumberCheckRecieved) {
                        isCreditCardNumberCheckRecieved = false;
                        isCreditCardNumberCheckSent = false;
                        return isCreditCardNumberValid;
                    } else if (!isCreditCardNumberCheckSent) {
                        return CreditCardInfoEditor.this.validateCreditCardNumberAsync(component, value);
                    } else {
                        return new FieldValidationError(component, i18n.tr("Validation in progress"));
                    }
                } else {
                    return null;
                }
            }
        });

        cardEditor.addComponentValidator(new CreditCardNumberTypeValidator(new CreditCardTypeProvider() {
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
            public FieldValidationError isValid(CComponent<String> component, String value) {
                if (CommonsStringUtils.isStringSet(value)) {
                    return ValidationUtils.isCreditCardCodeValid(value) ? null : new FieldValidationError(component, i18n
                            .tr("Security Code should consist of 3 to 4 digits"));
                } else {
                    return null;
                }
            }
        });

    }

    protected Set<CreditCardType> getAllowedCardTypes() {
        return EnumSet.allOf(CreditCardType.class);
    }

    private FieldValidationError validateCreditCardNumberAsync(final CComponent<?> component, CreditCardNumberIdentity value) {
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
                            setCreditCardNumberValidationResult(result ? null : new FieldValidationError(component, i18n.tr("Invalid Card Number")));
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            isCreditCardNumberCheckRecieved = true;
                            super.onFailure(caught);
                        }
                    }, ccInfo);
                    isCreditCardNumberCheckSent = true;
                    return new FieldValidationError(component, i18n.tr("Validation in progress"));
                }
            } else {
                return new FieldValidationError(component, i18n.tr("Invalid Card Number"));
            }
        } else if (value != null && value.obfuscatedNumber().isNull()) {
            return new FieldValidationError(component, i18n.tr("Invalid Card Number"));
        } else {
            return null;
        }
    }

    private void setCreditCardNumberValidationResult(FieldValidationError error) {
        isCreditCardNumberCheckRecieved = true;
        isCreditCardNumberValid = error;
        cardEditor.revalidate();
    }

    @Override
    public void generateMockData() {
        get(proto().nameOn()).setMockValue("Dev");
        get(proto().cardType()).setMockValue(CreditCardType.Visa);
        cardEditor.setMockValueByString(CreditCardNumberGenerator.generateCardNumber(get(proto().cardType()).getValue()));

        LogicalDate nextMonth = new LogicalDate();
        TimeUtils.addDays(nextMonth, 31);
        get(proto().expiryDate()).setMockValue(nextMonth);
        get(proto().securityCode()).setMockValue("123");
    }
}
