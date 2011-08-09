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

import com.propertyvista.domain.contact.Phone;

public class NativePhone extends HorizontalPanel implements INativeEditableComponent<Phone> {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_Phone";

    public static enum StyleDependent implements IStyleDependent {
        disabled, readOnly, invalid
    }

    private final TextBox number;

    private final TextBox extention;

    private final ListBox<Phone.Type> type;

    private final CPhone cComponent;

    private final HandlerManager focusHandlerManager;

    private final FocusWidget focusWidget;

    public NativePhone(CPhone cComponent) {
        super();
        this.cComponent = cComponent;

        setWidth("100%");

        if (cComponent.isShowType()) {
            add(type = new ListBox<Phone.Type>(false, true));
            setCellWidth(type, "30%");
            type.setOptions(Arrays.asList(Phone.Type.values()));
            type.setWidth("100%");
            type.getElement().getStyle().setMarginRight(5, Unit.PX);
        } else {
            type = null;
        }

        add(number = new TextBox());
        number.setWidth("100%");

        if (cComponent.isShowExtention()) {
            add(extention = new TextBox());
            setCellWidth(extention, "20%");
            extention.setWidth("100%");
            extention.getElement().getStyle().setMarginLeft(5, Unit.PX);
        } else {
            extention = null;
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

        focusWidget = number;
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
        number.setEnabled(enabled);
        String dependentSuffix = Selector.getDependentName(StyleDependent.disabled);
        if (enabled) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public boolean isEnabled() {
        return number.isEnabled();
    }

    @Override
    public void setEditable(boolean editable) {
        number.setReadOnly(!editable);
        String dependentSuffix = Selector.getDependentName(StyleDependent.readOnly);
        if (editable) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public boolean isEditable() {
        return !number.isReadOnly();
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
        number.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        number.setTabIndex(index);
    }

    @Override
    public void setNativeValue(Phone value) {
        if (value != null && !value.number().isNull()) {
            number.setText(cComponent.getFormat().format(value));
            if (type != null && !value.type().isNull()) {
                type.setSelection(value.type().getValue());
            }
            if (extention != null && !value.extension().isNull()) {
                extention.setText(value.extension().getStringView());
            }
        }
    }

    @Override
    public Phone getNativeValue() throws ParseException {
        Phone value;
        value = cComponent.getFormat().parse(number.getText());
        if (value != null) {
            if (type != null && !type.getSelection().isEmpty()) {
                value.type().setValue(type.getSelection().iterator().next());
            }
            if (extention != null && !extention.getValue().isEmpty()) {
                value.extension().setValue(Integer.decode(extention.getValue()));
            }
        }
        return value;
    }

    @Override
    public void installStyles(String stylePrefix) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        number.ensureDebugId(baseID);
    }
}
