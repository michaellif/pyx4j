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
package com.propertyvista.common.client.ui.components;

import java.text.ParseException;
import java.util.Arrays;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.combobox.ListBox;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.Selector;

import com.propertyvista.domain.contact.Email;

public class NativeEmail extends HorizontalPanel implements INativeEditableComponent<Email> {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_Email";

    public static enum StyleDependent implements IStyleDependent {
        disabled, readOnly, invalid
    }

    private final TextBox address;

    private final ListBox<Email.Type> type;

    private final CEmail cComponent;

    private final HandlerManager focusHandlerManager;

    private final FocusWidget focusWidget;

    public NativeEmail(CEmail cComponent) {
        super();
        this.cComponent = cComponent;

        setWidth("100%");

        if (cComponent.isShowType()) {
            add(type = new ListBox<Email.Type>(false, true));
            setCellWidth(type, "30%");
            type.setOptions(Arrays.asList(Email.Type.values()));
            type.setWidth("100%");
            type.getElement().getStyle().setMarginLeft(5, Unit.PX);
        } else {
            type = null;
        }

        add(address = new TextBox());
        address.setWidth("100%");

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

        focusWidget = address;
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
        address.setEnabled(enabled);
        String dependentSuffix = Selector.getDependentName(StyleDependent.disabled);
        if (enabled) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public boolean isEnabled() {
        return address.isEnabled();
    }

    @Override
    public void setEditable(boolean editable) {
        address.setReadOnly(!editable);
        String dependentSuffix = Selector.getDependentName(StyleDependent.readOnly);
        if (editable) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public boolean isEditable() {
        return !address.isReadOnly();
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
        address.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        address.setTabIndex(index);
    }

    @Override
    public void setNativeValue(Email value) {
        if (value != null && !value.address().isNull()) {
            address.setText(cComponent.getFormat().format(value));
            if (type != null && !value.type().isNull()) {
                type.setSelection(value.type().getValue());
            }
        }
    }

    @Override
    public Email getNativeValue() throws ParseException {
        Email value;
        value = cComponent.getFormat().parse(address.getText());
        if (value != null && type != null && !type.getSelection().isEmpty()) {
            value.type().setValue(type.getSelection().iterator().next());
        }
        return value;
    }

    @Override
    public void installStyles(String stylePrefix) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        address.ensureDebugId(baseID);
    }
}
