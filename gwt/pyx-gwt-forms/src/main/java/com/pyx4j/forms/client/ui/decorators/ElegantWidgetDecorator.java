/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Feb 10, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.forms.client.ui.decorators;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.gwt.Cursor;
import com.pyx4j.forms.client.gwt.NativeCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.widgets.client.Tooltip;

public class ElegantWidgetDecorator extends DockPanel {

    private final CComponent<?> component;

    private final Widget nativeComponent;

    private final Label label;

    private final ImageHolder imageMandatoryHolder;

    private Image imageInfoWarn;

    private final ImageHolder imageInfoWarnHolder;

    private Image imageMandatory;

    private Tooltip tooltip;

    public ElegantWidgetDecorator(final CComponent<?> component) {
        this(component, 140);
    }

    public ElegantWidgetDecorator(final CComponent<?> component, int labelWidth) {

        getElement().getStyle().setPadding(2, Unit.PX);

        this.component = component;
        nativeComponent = component.asWidget();

        label = new Label(component.getTitle() == null ? "" : component.getTitle());

        label.getElement().getStyle().setProperty("textAlign", "right");

        Cursor.setDefault(label.getElement());

        if (nativeComponent == null) {
            throw new RuntimeException("initNativeComponent() method call on [" + component.getClass() + "] returns null.");
        }
        if (nativeComponent instanceof NativeCheckBox) {
            ((NativeCheckBox) nativeComponent).setText(null);
        }

        if (nativeComponent instanceof Focusable) {
            label.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ((Focusable) nativeComponent).setFocus(true);
                }
            });
        }

        imageInfoWarnHolder = new ImageHolder("18px");
        imageInfoWarnHolder.getElement().getStyle().setPaddingTop(2, Unit.PX);
        imageInfoWarnHolder.getElement().getStyle().setPaddingLeft(10, Unit.PX);

        imageMandatoryHolder = new ImageHolder("7px");

        renderToolTip();
        renderMandatoryStar();

        label.setVisible(component.isVisible());
        setVisible(component.isVisible());

        component.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VISIBILITY_PROPERTY) {
                    label.setVisible(component.isVisible());
                    setVisible(component.isVisible());
                } else if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.TITLE_PROPERTY) {
                    label.setText(component.getTitle() + ":");
                }
                renderToolTip();
                renderMandatoryStar();
            }
        });

        HorizontalPanel labelHolder = new HorizontalPanel();
        labelHolder.getElement().getStyle().setPaddingRight(10, Unit.PX);

        labelHolder.add(label);
        labelHolder.add(imageMandatoryHolder);
        add(labelHolder, WEST);
        setCellVerticalAlignment(labelHolder, ALIGN_MIDDLE);

        HorizontalPanel nativeComponentHolder = new HorizontalPanel();
        nativeComponentHolder.setWidth("100%");
        nativeComponentHolder.add(nativeComponent);
        nativeComponentHolder.setCellWidth(nativeComponent, "100%");
        nativeComponentHolder.add(imageInfoWarnHolder);

        add(nativeComponentHolder, CENTER);
        setCellWidth(nativeComponentHolder, "100%");

        setWidth("100%");
        label.getElement().getStyle().setWidth(labelWidth, Unit.PX);
        label.getElement().getStyle().setFontSize(0.8, Unit.EM);
        label.getElement().getStyle().setColor("#888888");
        label.getElement().getStyle().setFontWeight(FontWeight.BOLD);

    }

    private void renderToolTip() {
        if (component.getToolTip() == null || component.getToolTip().trim().length() == 0) {
            imageInfoWarnHolder.clear();
        } else {
            if (imageInfoWarn == null) {
                imageInfoWarn = new Image();
                tooltip = Tooltip.tooltip(imageInfoWarn, "");
            }
            if (component instanceof CEditableComponent<?, ?> && ((CEditableComponent<?, ?>) component).isMandatoryConditionMet()
                    && !((CEditableComponent<?, ?>) component).isValid()) {
                imageInfoWarn.setResource(ImageFactory.getImages().formTooltipWarn());
            } else {
                imageInfoWarn.setResource(ImageFactory.getImages().formTooltipInfo());
            }
            imageInfoWarnHolder.setWidget(imageInfoWarn);
            tooltip.setTooltipText(component.getToolTip());

        }
    }

    private void renderMandatoryStar() {
        if (component instanceof CEditableComponent<?, ?>) {
            if (!((CEditableComponent<?, ?>) component).isMandatoryConditionMet()) {
                if (imageMandatory == null) {
                    imageMandatory = new Image();
                    imageMandatory.setResource(ImageFactory.getImages().mandatory());
                    imageMandatory.setTitle("This field is mandatory");
                }
                imageMandatoryHolder.setWidget(imageMandatory);
            } else {
                imageMandatoryHolder.clear();
            }
        } else {
            imageMandatoryHolder.clear();
        }
    }

}