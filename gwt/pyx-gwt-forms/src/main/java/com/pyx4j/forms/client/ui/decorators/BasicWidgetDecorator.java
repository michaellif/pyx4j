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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.Cursor;
import com.pyx4j.forms.client.ui.NativeCheckBox;
import com.pyx4j.widgets.client.Tooltip;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class BasicWidgetDecorator extends FlowPanel {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_BasicWidgetDecorator";

    public static enum StyleSuffix implements IStyleSuffix {
        Label, Component, Gap
    }

    private final CComponent<?> component;

    private final Widget nativeComponent;

    private final Label label;

    private final SpaceHolder imageMandatoryHolder;

    private Image imageInfoWarn;

    private final SpaceHolder imageInfoWarnHolder;

    private Image imageMandatory;

    private Tooltip tooltip;

    public BasicWidgetDecorator(final CComponent<?> component) {
        this(component, new DecorationData());
    }

    public BasicWidgetDecorator(final CComponent<?> component, int labelWidth, int componentWidth) {
        this(component, new DecorationData(labelWidth, componentWidth));
    }

    static public class DecorationData {
        public double labelWidth = 140;

        public Unit labelUnit = Unit.PX;

        public HorizontalAlignmentConstant labelAlignment = HasHorizontalAlignment.ALIGN_RIGHT;

        public double componentWidth = 140;

        public Unit componentUnit = Unit.PX;

        public double gapWidth = 20;

        public Unit gapUnit = Unit.PX;

        // various construction:
        public DecorationData() {
        }

        public DecorationData(int labelWidth, int componentWidth) {
            this.labelWidth = labelWidth;
            this.labelUnit = Unit.PX;
            this.componentWidth = componentWidth;
            this.componentUnit = Unit.PX;
        }

        public DecorationData(int labelWidth, int componentWidth, int gapWidth) {
            this(labelWidth, componentWidth);
            this.gapWidth = gapWidth;
            this.gapUnit = Unit.PX;
        }

        public DecorationData(double labelWidth, Unit labelUnit, double componentWidth, Unit componentUnit) {
            this.labelWidth = labelWidth;
            this.labelUnit = labelUnit;
            this.componentWidth = componentWidth;
            this.componentUnit = componentUnit;
        }

        public DecorationData(double labelWidth, Unit labelUnit, double componentWidth, Unit componentUnit, double gapWidth, Unit gapUnit) {
            this(labelWidth, labelUnit, componentWidth, componentUnit);
            this.gapWidth = gapWidth;
            this.gapUnit = gapUnit;
        }
    }

    public BasicWidgetDecorator(final CComponent<?> component, DecorationData decorData) {

        label = new Label(component.getTitle() == null ? "" : component.getTitle());
        label.getElement().getStyle().setFloat(Float.LEFT);
        label.getElement().getStyle().setWidth(decorData.labelWidth, decorData.labelUnit);
        label.setHorizontalAlignment(decorData.labelAlignment);
        label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Label);

        Cursor.setDefault(label.getElement());

        this.component = component;
        nativeComponent = component.asWidget();
        nativeComponent.getElement().getStyle().setWidth(decorData.componentWidth, decorData.componentUnit);
        nativeComponent.getElement().getStyle().setFloat(Float.LEFT);
        nativeComponent.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Component);

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

        imageInfoWarnHolder = new SpaceHolder("18px");
        imageInfoWarnHolder.getElement().getStyle().setFloat(Float.LEFT);
        imageInfoWarnHolder.getElement().getStyle().setPaddingTop(2, Unit.PX);
        imageInfoWarnHolder.getElement().getStyle().setPaddingLeft(10, Unit.PX);

        imageMandatoryHolder = new SpaceHolder(decorData.gapWidth + decorData.gapUnit.getType());
        imageMandatoryHolder.getElement().getStyle().setFloat(Float.LEFT);
        imageMandatoryHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Gap);

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

        add(label);
        add(imageMandatoryHolder);
        add(nativeComponent);
        add(imageInfoWarnHolder);

        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        getElement().getStyle().setPadding(2, Unit.PX);
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