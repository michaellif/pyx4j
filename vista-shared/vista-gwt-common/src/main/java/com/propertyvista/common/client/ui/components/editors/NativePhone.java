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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.Selector;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.INativeFocusComponent;
import com.pyx4j.widgets.client.TextBox;

import com.propertyvista.domain.contact.Phone;

public class NativePhone extends SimplePanel implements INativeFocusComponent<Phone> {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_Phone";

    public static enum StyleDependent implements IStyleDependent {
        disabled, readOnly, invalid
    }

    private final TextBox number;

    private final TextBox extention;

    private final ListBox type;

    private final CPhone cComponent;

    private final HandlerManager focusHandlerManager;

    public NativePhone(CPhone cComponent) {
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
        if (cComponent.isShowType()) {
            contentPanel.add(type = new ListBox());
            contentPanel.setCellWidth(type, "90px");
            DOM.getParent(type.getElement()).getStyle().setPaddingRight(5, Unit.PX);

            for (Phone.Type item : Phone.Type.values()) {
                type.addItem(item.toString(), item.name());
            }
            type.setSelectedIndex(-1);
            type.setWidth("100%");
            type.addFocusHandler(groupFocusHandler);
            type.addBlurHandler(groupBlurHandler);
        } else {
            type = null;
        }

        contentPanel.add(number = new TextBox());
        number.setWidth("100%");
        number.addFocusHandler(groupFocusHandler);
        number.addBlurHandler(groupBlurHandler);

        if (cComponent.isShowExtention()) {
            contentPanel.add(extention = new TextBox());
            contentPanel.setCellWidth(extention, "50px");
            DOM.getParent(extention.getElement()).getStyle().setPaddingLeft(5, Unit.PX);

            extention.setWidth("100%");
            extention.addFocusHandler(groupFocusHandler);
            extention.addBlurHandler(groupBlurHandler);
        } else {
            extention = null;
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
        clearUI();
        if (value != null && !value.number().isNull()) {
            number.setText(cComponent.getFormat().format(value));
            if (type != null && !value.type().isNull()) {
                for (int i = 0; i < type.getItemCount(); ++i) {
                    if (value.type().getValue().name().compareTo(type.getValue(i)) == 0) {
                        type.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (extention != null && !value.extension().isNull()) {
                extention.setText(value.extension().getStringView());
            }
        }
    }

    private void clearUI() {
        number.setText("");
        if (type != null) {
            type.setSelectedIndex(-1);
        }
        if (extention != null) {
            extention.setText("");
        }
    }

    @Override
    public Phone getNativeValue() throws ParseException {
        Phone value;
        value = cComponent.getFormat().parse(number.getText());
        if (value != null) {
            if (type != null && type.getSelectedIndex() >= 0) {
                value.type().setValue(Phone.Type.valueOf(type.getValue(type.getSelectedIndex())));
            }
            if (extention != null) {
                value.extension().setValue(!CommonsStringUtils.isEmpty(extention.getValue()) ? Integer.decode(extention.getValue()) : null);
            }
        }
        return value;
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        number.ensureDebugId(baseID);
    }
}
