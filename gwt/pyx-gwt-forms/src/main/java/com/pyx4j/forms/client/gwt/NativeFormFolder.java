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
 * @version $Id$
 */
package com.pyx4j.forms.client.gwt;

import java.util.LinkedHashMap;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.gwt.NativeForm.ToolbarMode;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CFormFolder;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.widgets.client.util.BrowserType;

public class NativeFormFolder<E> extends DockPanel implements INativeEditableComponent<List<E>> {

    private static final I18n i18n = I18nFactory.getI18n(NativeFormFolder.class);

    private final CFormFolder<?> folder;

    private final Image addCommand;

    private final Label label;

    private final VerticalPanel container;

    public NativeFormFolder(final CFormFolder<?> folder) {
        setWidth("100%");

        this.folder = folder;

        container = new VerticalPanel();
        container.setWidth("100%");

        label = new Label(folder.getTitle() == null ? "" : folder.getTitle());
        label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        label.getElement().getStyle().setPaddingLeft(10, Unit.PX);
        label.getElement().getStyle().setPaddingRight(25, Unit.PX);

        addCommand = new Image();
        addCommand.setResource(ImageFactory.getImages().addItem());
        addCommand.getElement().getStyle().setCursor(Cursor.POINTER);
        addCommand.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                folder.addItem();
            }
        });

        label.setVisible(folder.isVisible());
        setVisible(folder.isVisible());

        folder.addPropertyChangeHandler(new PropertyChangeHandler() {
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VISIBILITY_PROPERTY) {
                    label.setVisible(folder.isVisible());
                    setVisible(folder.isVisible());
                } else if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.TITLE_PROPERTY) {
                    label.setText(folder.getTitle() + ":");
                }
            }
        });

        add(container, CENTER);

        HorizontalPanel labelHolder = new HorizontalPanel();

        labelHolder.add(label);

        labelHolder.add(addCommand);

        add(labelHolder, NORTH);

        label.setWordWrap(false);

        addCommand.getElement().getStyle().setProperty("left", (NativeForm.LEFT_LABEL_WIDTH + 25) + "px");
        addCommand.getElement().getStyle().setProperty("top", "5px");
        addCommand.setTitle(i18n.tr("Add Item"));

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
    public void setNativeValue(List<E> value) {
        container.clear();
        if (value != null) {
            LinkedHashMap map = folder.getFormsMap();
            container.getElement().getStyle().setPadding(10, Unit.PX);
            for (int i = 0; i < value.size(); i++) {
                E item = value.get(i);
                NativeForm nativeForm = (NativeForm) ((CForm) map.get(item)).initNativeComponent();
                nativeForm.getElement().getStyle().setMarginBottom(5, Unit.PX);
                nativeForm.setWidth("100%");
                container.add(nativeForm);
                container.setCellWidth(nativeForm, "100%");
                if (i == 0 && value.size() == 1) {
                    nativeForm.setToolbarMode(ToolbarMode.Only);
                } else if (i == 0) {
                    nativeForm.setToolbarMode(ToolbarMode.First);
                } else if (i == value.size() - 1) {
                    nativeForm.setToolbarMode(ToolbarMode.Last);
                } else {
                    nativeForm.setToolbarMode(ToolbarMode.Inner);
                }

            }
        }
    }

    @Override
    public void setFocus(boolean focused) {
    }

    @Override
    public void setTabIndex(int tabIndex) {
    }

    @Override
    public CComponent<?> getCComponent() {
        return folder;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

}