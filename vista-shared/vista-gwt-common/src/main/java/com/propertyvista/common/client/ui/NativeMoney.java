/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.propertyvista.portal.domain.Money;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.Selector;

public class NativeMoney extends HorizontalPanel implements INativeEditableComponent<Money> {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_Money";

    public static enum StyleDependent implements IStyleDependent {
        disabled, readOnly, invalid
    }

    private TextBox amount;

    private Label currency;

    private final CMoney cComponent;

    private final HandlerManager focusHandlerManager;

    private final FocusWidget focusWidget;

    public NativeMoney(CMoney cComponent) {
        super();
        this.cComponent = cComponent;

        add(amount = new TextBox());
        add(currency = new Label());
        currency.getElement().getStyle().setMarginLeft(5, Unit.PX);
        if (!cComponent.isShowCurrency()) {
            remove(currency);
        }

        setStyleName(DEFAULT_STYLE_PREFIX);

        focusHandlerManager = new HandlerManager(this);

        FocusHandler groupFocusHandler = new FocusHandler() {
            @Override
            public void onFocus(FocusEvent e) {
                focusHandlerManager.fireEvent(e);
            }
        };

        BlurHandler groupBlurHandler = new BlurHandler() {
            @Override
            public void onBlur(final BlurEvent e) {
                focusHandlerManager.fireEvent(e);
            }
        };

        focusWidget = amount;
        focusWidget.addFocusHandler(groupFocusHandler);
        focusWidget.addBlurHandler(groupBlurHandler);
    }

    @Override
    public CComponent<?> getCComponent() {
        return cComponent;
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return focusHandlerManager.addHandler(FocusEvent.getType(), focusHandler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return focusHandlerManager.addHandler(BlurEvent.getType(), blurHandler);
    }

    @Override
    public void setEnabled(boolean enabled) {
        amount.setEnabled(enabled);
        String dependentSuffix = Selector.getDependentName(StyleDependent.disabled);
        if (enabled) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public boolean isEnabled() {
        return amount.isEnabled();
    }

    @Override
    public void setEditable(boolean editable) {
        amount.setReadOnly(!editable);
        String dependentSuffix = Selector.getDependentName(StyleDependent.readOnly);
        if (editable) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public boolean isEditable() {
        return !amount.isReadOnly();
    }

    @Override
    public void setValid(boolean valid) {
        String dependentSuffix = Selector.getDependentName(StyleDependent.invalid);
        if (valid) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public void setFocus(boolean focused) {
        amount.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        amount.setTabIndex(index);
    }

    @Override
    public void setNativeValue(Money value) {
        if (value != null && !value.amount().isNull()) {
            amount.setText(cComponent.getFormat().format(value));
            if (!value.currency().isNull()) {
                currency.setText(value.currency().getStringView());
            }
        }
    }

    @Override
    public Money getNativeValue() {
        Money value = cComponent.getFormat().parse(amount.getText());
        if (value != null && !value.currency().isNull()) {
            value.currency().name().setValue(currency.getText());
        }
        return value;
    }
}
