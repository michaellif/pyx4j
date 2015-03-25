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
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CTextComponent;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.ui.components.editors.payments.CreditCardNumberTypeValidator.CreditCardTypeProvider;
import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.CreditCardNumberIdentity;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.portal.rpc.shared.services.CreditCardValidationService;
import com.propertyvista.shared.rpc.CreditCardValidationResponce;
import com.propertyvista.shared.util.CreditCardFormatter;

public class CreditCardInfoEditor extends CForm<CreditCardInfo> {

    private static final I18n i18n = I18n.get(CreditCardInfoEditor.class);

    // a hack for async creditCardNumber Validation
    private boolean isCreditCardNumberCheckSent;

    private boolean isCreditCardNumberCheckRecieved;

    private BasicValidationError isCreditCardNumberValid;

    protected final CMonthYearPicker monthYearPicker = new CMonthYearPicker(false);

    protected final CComboBox<CreditCardType> typeSelector = new CComboBox<CreditCardType>();

    protected final CPersonalIdentityField<CreditCardNumberIdentity> cardEditor = new CPersonalIdentityField<CreditCardNumberIdentity>(
            CreditCardNumberIdentity.class, new CreditCardFormatter());

    public CreditCardInfoEditor() {
        super(CreditCardInfo.class);

        monthYearPicker.setYearRange(new Range(1900 + new Date().getYear(), 10));
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().nameOn()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().cardType(), typeSelector).decorate().componentWidth(200);

        formPanel.append(Location.Left, proto().card(), cardEditor).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().expiryDate(), monthYearPicker).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().securityCode()).decorate().componentWidth(50);

        contentTweaks();
        return formPanel;
    }

    protected void contentTweaks() {
        get(proto().securityCode()).setVisible(isEditable());
        get(proto().securityCode()).setMandatory(false);
        // manage security code mandatory state:
        get(proto().nameOn()).addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                get(proto().securityCode()).setMandatory(true);
            }
        });
        get(proto().cardType()).addValueChangeHandler(new ValueChangeHandler<CreditCardType>() {
            @Override
            public void onValueChange(ValueChangeEvent<CreditCardType> event) {
                get(proto().securityCode()).setMandatory(true);
                cardEditor.revalidate();
            }
        });
        get(proto().card()).addValueChangeHandler(new ValueChangeHandler<CreditCardNumberIdentity>() {
            @Override
            public void onValueChange(ValueChangeEvent<CreditCardNumberIdentity> event) {
                get(proto().securityCode()).setMandatory(true);
            }

        });
        get(proto().expiryDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                get(proto().securityCode()).setMandatory(true);
                cardEditor.revalidate();
            }
        });
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

        get(proto().securityCode()).setMandatory(false);
        ((CTextComponent<?, ?>) get(proto().securityCode())).setWatermark("XXX");
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

        // set up validation for credit card number:
        // sync validation:
        cardEditor.addComponentValidator(new CreditCardNumberTypeValidator(new CreditCardTypeProvider() {
            @Override
            public CreditCardType getCreditCardType() {
                return get(proto().cardType()).getValue();
            }
        }));
        // async validation:
        cardEditor.addComponentValidator(new AbstractComponentValidator<CreditCardNumberIdentity>() {
            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() != null) {
                    if (isCreditCardNumberCheckRecieved) {
                        isCreditCardNumberCheckRecieved = false;
                    } else {
                        validateCreditCardNumberAsync(getCComponent(), getCComponent().getValue());
                    }
                    return isCreditCardNumberValid;
                }
                return null;
            }
        });

        get(proto().expiryDate()).addComponentValidator(new FutureDateValidator());
        get(proto().expiryDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public AbstractValidationError isValid() {
                // TODO Auto-generated method stub
                return null;
            }
        });

        get(proto().securityCode()).addComponentValidator(new AbstractComponentValidator<String>() {
            @Override
            public BasicValidationError isValid() {
                if (CommonsStringUtils.isStringSet(getCComponent().getValue())) {
                    return ValidationUtils.isCreditCardCodeValid(getCComponent().getValue()) ? null : new BasicValidationError(getCComponent(), i18n
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

    private void validateCreditCardNumberAsync(final CComponent<?, ?, ?, ?> component, CreditCardNumberIdentity value) {
        // check preconditions:
        if (value == null || !isCardTypeReady() || !isExpiryDateReady()) {
            return;
        }

        if (!isCreditCardNumberCheckSent) {
            resetCreditCardNumberValidationResult();

            if (ValidationUtils.isCreditCardNumberIinValid(retrieveCreditCardTypePatterns(), value.newNumber().getValue())) {
                CreditCardInfo ccInfo = getValue().<CreditCardInfo> duplicate();
                ccInfo.card().newNumber().set(value.newNumber());
                GWT.<CreditCardValidationService> create(CreditCardValidationService.class).validate(new DefaultAsyncCallback<CreditCardValidationResponce>() {
                    @Override
                    public void onSuccess(CreditCardValidationResponce result) {
                        //TODO use valid type validCardType
                        setCreditCardNumberValidationResult(result.validWithTypeProvided().getValue(false) ? null : new BasicValidationError(component,
                                i18n.tr("Invalid Card Number or Expiry Date")));
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        setCreditCardNumberValidationResult(null);
                        super.onFailure(caught);
                    }
                }, ccInfo);

                isCreditCardNumberCheckSent = true;
            }
        }
    }

    boolean isCardTypeReady() {
        return (get(proto().cardType()).getValue() != null);
    }

    boolean isExpiryDateReady() {
        return (get(proto().expiryDate()).getValue() != null && get(proto().expiryDate()).isValid());
    }

    private String[] retrieveCreditCardTypePatterns() {
        return (get(proto().cardType()).isValueEmpty() ? null : get(proto().cardType()).getValue().iinsPatterns);
    }

    private void setCreditCardNumberValidationResult(BasicValidationError error) {
        isCreditCardNumberCheckRecieved = true;
        isCreditCardNumberCheckSent = false;
        isCreditCardNumberValid = error;

        cardEditor.revalidate();
    }

    private void resetCreditCardNumberValidationResult() {
        isCreditCardNumberCheckRecieved = false;
        isCreditCardNumberCheckSent = false;
        isCreditCardNumberValid = null;
    }

    @Override
    public void generateMockData() {
        get(proto().nameOn()).setMockValue("Dev");
        get(proto().cardType()).setMockValue(CreditCardType.Visa);
        cardEditor.setMockValueByString(CreditCardNumberGenerator.generateCardNumber(get(proto().cardType()).getValue()));

        LogicalDate nextMonth = new LogicalDate();
        TimeUtils.addDays(nextMonth, 31);
        get(proto().expiryDate()).setMockValue(nextMonth);
        get(proto().securityCode()).setMockValue("423");
    }
}
