/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on May 28, 2010
 * @author michaellif
 * @version $Id: NativeFormFolder.java 6714 2010-08-09 18:41:29Z michaellif $
 */
package com.pyx4j.forms.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.widgets.client.util.BrowserType;

public class NativeFormGroup<E> extends DockPanel implements INativeEditableComponent<E> {

    private static final I18n i18n = I18nFactory.getI18n(NativeFormGroup.class);

    private final CFormGroup<?> group;

    private final Label label;

    private final VerticalPanel container;

    public NativeFormGroup(final CFormGroup<?> group) {
        setWidth("100%");

        this.group = group;

        container = new VerticalPanel();
        container.setWidth("100%");

        getElement().getStyle().setPaddingBottom(10, Unit.PX);

        label = new Label(group.getTitle() == null ? "" : group.getTitle());
        label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        label.getElement().getStyle().setPaddingLeft(10, Unit.PX);

        label.setVisible(group.isVisible());
        setVisible(group.isVisible());

        group.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VISIBILITY_PROPERTY) {
                    label.setVisible(group.isVisible());
                    setVisible(group.isVisible());
                } else if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.TITLE_PROPERTY) {
                    label.setText(group.getTitle() + ":");
                }
            }
        });

        container.getElement().getStyle().setPadding(10, Unit.PX);
        container.getElement().getStyle().setPaddingBottom(4, Unit.PX);

        NativeForm nativeForm = group.getForm().asWidget();
        nativeForm.getElement().getStyle().setMarginBottom(5, Unit.PX);
        nativeForm.setWidth("100%");
        container.add(nativeForm);
        container.setCellWidth(nativeForm, "100%");

        add(container, CENTER);

        add(label, NORTH);

        label.setWordWrap(false);

        if (BrowserType.isIE7()) {
            getElement().getStyle().setMarginLeft(5, Unit.PX);
        }

    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public void setEditable(boolean editable) {
    }

    @Override
    public void setNativeValue(E value) {
    }

    @Override
    public void setFocus(boolean focused) {
    }

    @Override
    public void setTabIndex(int tabIndex) {
    }

    @Override
    public CComponent<?> getCComponent() {
        return group;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public void setValid(boolean valid) {
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        // TODO Auto-generated method stub
        return null;
    }
}