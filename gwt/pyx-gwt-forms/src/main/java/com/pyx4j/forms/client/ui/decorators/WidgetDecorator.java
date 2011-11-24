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

import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecorator;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorComponent;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorComponentHolder;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorContentPanel;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorInfoImage;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabelHolder;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorMandatoryImage;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorValidationLabel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.Cursor;
import com.pyx4j.forms.client.ui.NativeCheckBox;

public class WidgetDecorator extends FlexTable {

    private final CComponent<?, ?> component;

    private final Label label;

    private final SpaceHolder mandatoryImageHolder;

    private final SpaceHolder infoImageHolder;

    private Image mandatoryImage;

    private final Label validationLabel;

    private final FlowPanel labelHolder;

    private final HorizontalPanel contentPanel;

    public WidgetDecorator(CComponent<?, ?> component) {
        this(new Builder(component));
    }

    protected WidgetDecorator(Builder builder) {

        setStyleName(WidgetDecorator.name());

        this.component = builder.component;
        final Widget nativeComponent = component.asWidget();
        nativeComponent.addStyleName(WidgetDecoratorComponent.name());

        String caption = builder.customLabel;

        if (caption == null) {
            caption = component.getTitle();
        }

        if (caption == null) {
            caption = "";
        } else {
            caption += builder.useLabelSemicolon ? ":" : "";
        }

        label = new Label(caption);
        label.setStyleName(WidgetDecoratorLabel.name());

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
        infoImageHolder.setStyleName(WidgetDecoratorInfoImage.name());

        if (component.getTooltip() != null && component.getTooltip().trim().length() > 0) {
            Image infoImage = new Image(ImageFactory.getImages().formTooltipInfo());
            infoImage.setTitle(component.getTooltip());
            infoImageHolder.setWidget(infoImage);
        }

        mandatoryImageHolder = new SpaceHolder();
        mandatoryImageHolder.setStyleName(WidgetDecoratorMandatoryImage.name());

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

        labelHolder = new FlowPanel();
        labelHolder.setStyleName(WidgetDecoratorLabelHolder.name());
        labelHolder.add(mandatoryImageHolder);
        labelHolder.add(label);
        labelHolder.getElement().getStyle().setProperty("textAlign", builder.labelAlignment.name());
        labelHolder.getElement().getStyle().setWidth(builder.labelWidth, Unit.EM);
        getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);

        SimplePanel componentHolder = new SimplePanel();
        componentHolder.setStyleName(WidgetDecoratorComponentHolder.name());
        componentHolder.getElement().getStyle().setWidth(builder.componentWidth, Unit.EM);
        componentHolder.add(nativeComponent);

        validationLabel = new Label();
        validationLabel.setStyleName(WidgetDecoratorValidationLabel.name());

        contentPanel = new HorizontalPanel();
        contentPanel.setStyleName(WidgetDecoratorContentPanel.name());
        contentPanel.add(componentHolder);
        contentPanel.add(infoImageHolder);
        if (builder.readOnlyMode) {
            addStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.readOnly.name());
        }

        layout();
    }

    public Label getLabel() {
        return label;
    }

    public CComponent<?, ?> getComnponent() {
        return component;
    }

    protected void layout() {
        setWidget(0, 0, getLabelHolder());
        setWidget(0, 1, getContentPanel());
        setWidget(1, 1, getValidationLabel());
    }

    protected FlowPanel getLabelHolder() {
        return labelHolder;
    }

    protected HorizontalPanel getContentPanel() {
        return contentPanel;
    }

    protected Label getValidationLabel() {
        return validationLabel;
    }

    protected void renderMandatoryStar() {
        if (component instanceof CComponent<?, ?>) {
            if (!((CComponent<?, ?>) component).isMandatoryConditionMet()) {
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

    protected void renderValidationMessage() {
        if (component instanceof CComponent<?, ?>) {
            CComponent<?, ?> editableComponent = component;
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

        public enum Alignment {
            left, right
        }

        private final CComponent<?, ?> component;

        private double labelWidth = 15;

        private double componentWidth = 25;

        private String customLabel;

        private boolean useLabelSemicolon = true;

        private boolean readOnlyMode = false;

        private Alignment labelAlignment = Alignment.right;

        public Builder(final CComponent<?, ?> component) {
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

        public Builder customLabel(String customLabel) {
            this.customLabel = customLabel;
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

        public Builder labelAlignment(Alignment labelAlignment) {
            this.labelAlignment = labelAlignment;
            return this;
        }
    }

    @Deprecated
    public static WidgetDecorator build(CComponent<?, ?> component, double componentWidth) {
        return new WidgetDecorator.Builder(component).componentWidth(componentWidth).build();
    }

    @Deprecated
    public static WidgetDecorator build(CComponent<?, ?> component, double labelWidth, double componentWidth) {
        return new WidgetDecorator.Builder(component).labelWidth(labelWidth).componentWidth(componentWidth).build();
    }
}