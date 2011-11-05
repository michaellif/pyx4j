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
package com.propertyvista.common.client.ui.components.editors;

import java.text.ParseException;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.Selector;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.INativeFocusComponent;
import com.pyx4j.widgets.client.TextBox;

import com.propertyvista.domain.financial.Money;

public class NativeMoney extends SimplePanel implements INativeFocusComponent<Money> {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_Money";

    public static enum StyleDependent implements IStyleDependent {
        disabled, readOnly, invalid
    }

    private final TextBox amount;

    private final Label currency;

    private final CMoney cComponent;

    private final HandlerManager focusHandlerManager;

    public NativeMoney(CMoney cComponent) {
        super();
        this.cComponent = cComponent;

        // ----------------------------------------------

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

        // ----------------------------------------------

        HorizontalPanel contentPanel = new HorizontalPanel();
        contentPanel.add(amount = new TextBox());
        amount.setWidth("100%");
        amount.addFocusHandler(groupFocusHandler);
        amount.addBlurHandler(groupBlurHandler);

        if (cComponent.isShowCurrency()) {
            contentPanel.add(currency = new Label());
            currency.setWidth("100%");
            currency.getElement().getStyle().setMarginLeft(5, Unit.PX);
        } else {
            currency = null;
            contentPanel.setCellWidth(amount, "100%");
        }

        // ----------------------------------------------

        setWidget(contentPanel);
        contentPanel.setWidth("100%");
        setStyleName(DEFAULT_STYLE_PREFIX);
    }

    @Override
    public CComponent<?, ?> getCComponent() {
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
        clearUI();
        if (value != null && !value.amount().isNull()) {
            amount.setText(cComponent.getFormat().format(value));
            if (currency != null && !value.currency().isNull()) {
                currency.setText(value.currency().getStringView());
            }
        }
    }

    private void clearUI() {
        amount.setText("");
        if (currency != null) {
            currency.setText("");
        }
    }

    @Override
    public Money getNativeValue() {
        Money value;
        try {
            value = cComponent.getFormat().parse(amount.getText());
        } catch (ParseException e) {
            value = null;
        }
        if (value != null && currency != null) {
            value.currency().name().setValue(currency.getText());
        }
        return value;
    }

    @Override
    public void installStyles(String stylePrefix) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        amount.ensureDebugId(baseID);
    }
}
