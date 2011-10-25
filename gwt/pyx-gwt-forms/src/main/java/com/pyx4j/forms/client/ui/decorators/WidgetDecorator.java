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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.decorators;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.Cursor;
import com.pyx4j.forms.client.ui.NativeCheckBox;

public class WidgetDecorator extends FlowPanel {

    public static enum StyleName implements IStyleName {
        WidgetDecorator, WidgetDecoratorLabelHolder, WidgetDecoratorLabel, WidgetDecoratorMandatoryImage, WidgetDecoratorInfoImage,

        WidgetDecoratorValidationLabel, WidgetDecoratorContentPanel, WidgetDecoratorComponent, WidgetDecoratorComponentHolder
    }

    public static enum StyleDependent implements IStyleDependent {
        readOnly, noMandatoryStar
    }

    private final CComponent<?> component;

    private final Label label;

    private final SpaceHolder mandatoryImageHolder;

    private final SpaceHolder infoImageHolder;

    private Image mandatoryImage;

    private final Label validationLabel;

    public WidgetDecorator(CComponent<?> component) {
        this(new Builder(component));
    }

    protected WidgetDecorator(Builder builder) {

        setStyleName(StyleName.WidgetDecorator.name());

        this.component = builder.component;
        final Widget nativeComponent = component.asWidget();
        nativeComponent.addStyleName(StyleName.WidgetDecoratorComponent.name());

        String caption = builder.componentCaption;

        if (caption == null) {
            caption = component.getTitle();
        }

        if (caption == null) {
            caption = "";
        } else {
            caption += builder.useLabelSemicolon ? ":" : "";
        }

        label = new Label(caption);
        label.setStyleName(StyleName.WidgetDecoratorLabel.name());

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

        infoImageHolder = new SpaceHolder();
        infoImageHolder.setStyleName(StyleName.WidgetDecoratorInfoImage.name());

        if (component.getTooltip() != null && component.getTooltip().trim().length() > 0) {
            Image infoImage = new Image(ImageFactory.getImages().formTooltipInfo());
            infoImage.setTitle(component.getTooltip());
            infoImageHolder.setWidget(infoImage);
        }

        mandatoryImageHolder = new SpaceHolder();
        mandatoryImageHolder.setStyleName(StyleName.WidgetDecoratorMandatoryImage.name());

        renderMandatoryStar();

        label.setVisible(component.isVisible());
        setVisible(component.isVisible());

        component.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.visible) {
                    label.setVisible(component.isVisible());
                    setVisible(component.isVisible());
                } else if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.title) {
                    label.setText(component.getTitle() + ":");
                } else if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.valid) {
                    renderValidationMessage();
                }
                renderMandatoryStar();
            }
        });

        FlowPanel labelHolder = new FlowPanel();
        labelHolder.getElement().getStyle().setWidth(builder.labelWidth, Unit.EM);
        labelHolder.setStyleName(StyleName.WidgetDecoratorLabelHolder.name());
        labelHolder.add(label);
        labelHolder.add(mandatoryImageHolder);
        add(labelHolder);

        SimplePanel componentHolder = new SimplePanel();
        componentHolder.getElement().getStyle().setWidth(builder.componentWidth, Unit.EM);
        componentHolder.setStyleName(StyleName.WidgetDecoratorComponentHolder.name());
        componentHolder.add(nativeComponent);

        validationLabel = new Label();
        validationLabel.setStyleName(StyleName.WidgetDecoratorValidationLabel.name());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.setStyleName(StyleName.WidgetDecoratorContentPanel.name());
        contentPanel.add(componentHolder);
        contentPanel.add(infoImageHolder);
        contentPanel.add(validationLabel);
        add(contentPanel);

        if (builder.readOnlyMode) {
            addStyleDependentName(WidgetDecorator.StyleDependent.readOnly.name());
        }

    }

    private void renderMandatoryStar() {
        if (component instanceof CEditableComponent<?, ?>) {
            if (!((CEditableComponent<?, ?>) component).isMandatoryConditionMet()) {
                if (mandatoryImage == null) {
                    mandatoryImage = new Image();
                    mandatoryImage.setResource(ImageFactory.getImages().mandatory());
                    mandatoryImage.setTitle("This field is mandatory");
                }
                mandatoryImageHolder.add(mandatoryImage);
            } else {
                mandatoryImageHolder.clear();
            }
        } else {
            mandatoryImageHolder.clear();
        }
    }

    private void renderValidationMessage() {
        if (component instanceof CEditableComponent<?, ?>) {
            CEditableComponent<?, ?> editableComponent = (CEditableComponent<?, ?>) component;
            if (!editableComponent.isValid()) {
                if (!editableComponent.isMandatoryConditionMet()) {
                    validationLabel.setText(editableComponent.getMandatoryValidationMessage());
                } else {
                    validationLabel.setText(editableComponent.getValidationMessage());
                }
            } else {
                validationLabel.setText(null);
            }
        }
    }

    public static class Builder {
        private final CComponent<?> component;

        private double labelWidth = 15;

        private double componentWidth = 25;

        private String componentCaption;

        private boolean useLabelSemicolon = true;

        private boolean readOnlyMode = false;

        public Builder(final CComponent<?> component) {
            this.component = component;
        }

        public WidgetDecorator build() {
            return new WidgetDecorator(this);
        }

        public Builder labelWidth(double labelWidth) {
            this.labelWidth = labelWidth;
            return this;
        }

        public Builder componentWidth(double componentWidth) {
            this.componentWidth = componentWidth;
            return this;
        }

        public Builder componentCaption(String componentCaption) {
            this.componentCaption = componentCaption;
            return this;
        }

        public Builder useLabelSemicolon(boolean useLabelSemicolon) {
            this.useLabelSemicolon = useLabelSemicolon;
            return this;
        }

        public Builder readOnlyMode(boolean readOnlyMode) {
            this.readOnlyMode = readOnlyMode;
            return this;
        }

    }

    public static WidgetDecorator build(CComponent<?> component, double componentWidth) {
        return new WidgetDecorator(new Builder(component).componentWidth(componentWidth));
    }

    public static WidgetDecorator build(CComponent<?> component, double labelWidth, double componentWidth) {
        return new WidgetDecorator(new Builder(component).labelWidth(labelWidth).componentWidth(componentWidth));
    }
}