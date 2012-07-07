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
package com.propertyvista.common.client.ui.components.c;

import java.text.ParseException;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.INativeTextComponent;
import com.pyx4j.forms.client.ui.NativeTextBox;

import com.propertyvista.domain.financial.Money;

public class NativeMoney extends SimplePanel implements Focusable, INativeTextComponent<Money> {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_Money";

    public static enum StyleDependent implements IStyleDependent {
        disabled, readOnly, invalid
    }

    private final NativeTextBox<Money> amount;

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
        contentPanel.add(amount = new NativeTextBox<Money>(cComponent));
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
    }

    @Override
    public boolean isEnabled() {
        return amount.isEnabled();
    }

    @Override
    public void setEditable(boolean editable) {
        amount.setReadOnly(!editable);
    }

    @Override
    public boolean isEditable() {
        return !amount.isReadOnly();
    }

    @Override
    public void setViewable(boolean editable) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isViewable() {
        // TODO Auto-generated method stub
        return false;
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
        if (value != null) {
            amount.setText(cComponent.getFormat().format(value));
            if (currency != null) {
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
    public Money getNativeValue() throws ParseException {
        Money value = null;
        value = amount.getNativeValue();
        if (value != null && currency != null) {
            value.currency().name().setValue(currency.getText());
        }
        return value;
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        amount.ensureDebugId(baseID);
    }

    @Override
    public void setNativeText(String newValue) {
        amount.setText(newValue);
    }

    @Override
    public String getNativeText() {
        return amount.getNativeText();
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return amount.addChangeHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return amount.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return amount.addKeyUpHandler(handler);
    }

    @Override
    public int getTabIndex() {
        return amount.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        amount.setAccessKey(key);
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        // TODO Auto-generated method stub

    }

}
