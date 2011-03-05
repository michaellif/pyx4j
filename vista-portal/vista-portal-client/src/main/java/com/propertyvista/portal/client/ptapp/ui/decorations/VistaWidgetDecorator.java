/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author VladLL
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.decorations;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.Cursor;
import com.pyx4j.forms.client.ui.INativeTextComponent;
import com.pyx4j.forms.client.ui.NativeCheckBox;
import com.pyx4j.forms.client.ui.decorators.SpaceHolder;
import com.pyx4j.widgets.client.Tooltip;
import com.pyx4j.widgets.client.style.IStyleSuffix;

/**
 * Widget decorator helpful for representation of Label : [ edit field ] widgets pair in
 * various view forms with uniform project style.
 */
public class VistaWidgetDecorator extends VerticalPanel {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_VistaWidgetDecorator";

    public static enum StyleSuffix implements IStyleSuffix {
        Label, Component
    }

    private final CComponent<?> component;

    private final Widget nativeComponent;

    private final Label label;

    private final Label mandatoryLabel;

    private final Label validationLabel;

    private Tooltip tooltip;

    private final DecorationData decorData;

    private final SpaceHolder infoImageHolder;

    public VistaWidgetDecorator(final CComponent<?> component) {
        this(component, new DecorationData());
    }

    public VistaWidgetDecorator(final CComponent<?> component, int labelWidth, int componentWidth) {
        this(component, new DecorationData(labelWidth, componentWidth));
    }

    public VistaWidgetDecorator(final CComponent<?> component, double labelWidth, double componentWidth) {
        this(component, new DecorationData(labelWidth, componentWidth));
    }

    public VistaWidgetDecorator(final CComponent<?> component, DecorationData decorData) {
        this.component = component;
        this.decorData = decorData;

        label = new Label(CommonsStringUtils.nvl(component.getTitle()));
        label.getElement().getStyle().setFloat(Float.LEFT);

        label.setHorizontalAlignment(decorData.labelAlignment);
        if (decorData.labelWidth != 0) {
            label.getElement().getStyle().setWidth(decorData.labelWidth, decorData.labelUnit);
        }

        if (decorData.labelStyle == null) {
            label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Label);
        } else {
            label.addStyleName(decorData.labelStyle);
        }

        Cursor.setDefault(label.getElement());

        if (decorData.hideInfoHolder) {
            infoImageHolder = new SpaceHolder("1px");

        } else {
            infoImageHolder = new SpaceHolder("18px");
            infoImageHolder.getElement().getStyle().setPaddingRight(5, Unit.PX);
        }
        infoImageHolder.getElement().getStyle().setFloat(Float.LEFT);
        infoImageHolder.getElement().getStyle().setPaddingTop(2, Unit.PX);
        infoImageHolder.getElement().getStyle().setPaddingLeft(5, Unit.PX);

        if (component.getToolTip() != null && component.getToolTip().trim().length() > 0) {
            Image infoImage = new Image(ImageFactory.getImages().formTooltipInfo());
            tooltip = Tooltip.tooltip(infoImage, component.getToolTip());
            infoImageHolder.setWidget(infoImage);
        }

        mandatoryLabel = new Label();
        mandatoryLabel.getElement().getStyle().setFloat(Float.LEFT);
        mandatoryLabel.getElement().getStyle().setPaddingLeft(10, Unit.PX);
        mandatoryLabel.getElement().getStyle().setColor("#aaa");

        validationLabel = new Label();
        validationLabel.getElement().getStyle().setFloat(Float.LEFT);
        validationLabel.getElement().getStyle().setPaddingLeft(decorData.labelWidth, decorData.labelUnit);
        validationLabel.getElement().getStyle().setMarginLeft(30, Unit.PX);
        validationLabel.getElement().getStyle().setColor("red");

        nativeComponent = component.asWidget();

        if (component instanceof CEditableComponent) {
            ((CEditableComponent<?, ?>) component).setEditable(decorData.editable);
        }

        if (nativeComponent == null) {
            throw new RuntimeException("initNativeComponent() method call on [" + component.getClass() + "] returns null.");
        }
        if (nativeComponent instanceof NativeCheckBox) {
            ((NativeCheckBox) nativeComponent).setText(null);
            nativeComponent.getElement().getStyle().setMargin(0, Unit.PX);
        } else if (nativeComponent instanceof INativeTextComponent<?>) {
            nativeComponent.getElement().getStyle().setProperty("padding", "2px 5px");
            mandatoryLabel.getElement().getStyle().setPaddingLeft(14, Unit.PX);
        }

        if (nativeComponent instanceof Focusable) {
            label.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ((Focusable) nativeComponent).setFocus(true);
                }
            });
        }

        SimplePanel nativeComponentHolder = new SimplePanel();

        nativeComponentHolder.getElement().getStyle().setFloat(Float.LEFT);
        nativeComponentHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        if (decorData.componentWidth != 0)
            nativeComponentHolder.getElement().getStyle().setWidth(decorData.componentWidth, decorData.componentUnit);

        nativeComponentHolder.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Component);
        nativeComponentHolder.setWidget(nativeComponent);

        // put it together:

        FlowPanel firstLine = new FlowPanel();
        firstLine.add(label);
        firstLine.add(infoImageHolder);
        firstLine.add(nativeComponentHolder);
        firstLine.add(mandatoryLabel);

        add(firstLine);

        if (!decorData.readOnlyMode) {
            FlowPanel secondLine = new FlowPanel();
            secondLine.add(validationLabel);
            add(secondLine);

            renderMandatoryMessage();

            setVisible(component.isVisible());

            component.addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                    if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VISIBILITY_PROPERTY) {
                        setVisible(component.isVisible());
                    }
                    if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VALIDITY) {
                        renderValidationMessage();
                    }
                    renderMandatoryMessage();
                }
            });

            getElement().getStyle().setPadding(2, Unit.PX);
            getElement().getStyle().setPaddingBottom(13, Unit.PX);
        }

    }

    private void renderMandatoryMessage() {
        if (component instanceof CEditableComponent<?, ?>) {
            CEditableComponent<?, ?> editableComponent = (CEditableComponent<?, ?>) component;
            if (editableComponent.isVisible() && editableComponent.isEnabled() && editableComponent.isEditable()) {
                if (editableComponent.isMandatory() && DecorationData.ShowMandatory.Mandatory.equals(decorData.showMandatory)) {
                    mandatoryLabel.setText(DecorationData.ShowMandatory.Mandatory.name());
                } else if (!editableComponent.isMandatory() && DecorationData.ShowMandatory.Optional.equals(decorData.showMandatory)) {
                    mandatoryLabel.setText(DecorationData.ShowMandatory.Optional.name());
                } else {
                    mandatoryLabel.setText(null);
                }
            }

        } else {
            mandatoryLabel.setText(null);
        }
    }

    private void renderValidationMessage() {
        if (component instanceof CEditableComponent<?, ?>) {
            CEditableComponent<?, ?> editableComponent = (CEditableComponent<?, ?>) component;
            if (!editableComponent.isValid()) {
                validationLabel.setText(editableComponent.getValidationMessage());
            } else {
                validationLabel.setText(null);
            }
        } else {
            mandatoryLabel.setText(null);
        }
    }

    public static class DecorationData {

        public static enum ShowMandatory {
            Mandatory, Optional, None
        }

        public boolean editable = true;

        public boolean readOnlyMode = false;

        public double labelWidth = 10;

        public Unit labelUnit = Unit.EM;

        public String labelStyle;

        public HorizontalAlignmentConstant labelAlignment = HasHorizontalAlignment.ALIGN_RIGHT;

        public VerticalAlign labelVerticalAlignment = VerticalAlign.BASELINE;

        public double componentWidth = 10;

        public Unit componentUnit = Unit.EM;

        public HorizontalAlignmentConstant componentAlignment = HasHorizontalAlignment.ALIGN_RIGHT;

        public VerticalAlign componentVerticalAlignment = VerticalAlign.BASELINE;

        public ShowMandatory showMandatory = ShowMandatory.Optional;

        public boolean hideInfoHolder = false;

        // various construction:
        public DecorationData() {
        }

        // first set of construction:
        public DecorationData(int labelWidth, int componentWidth) {
            this.labelWidth = labelWidth;
            this.labelUnit = Unit.PX;
            this.componentWidth = componentWidth;
            this.componentUnit = Unit.PX;
        }

        public DecorationData(double labelWidth, double componentWidth) {
            this.labelWidth = labelWidth;
            this.labelUnit = Unit.EM;
            this.componentWidth = componentWidth;
            this.componentUnit = Unit.EM;
        }

        public DecorationData(double labelWidth, Unit labelUnit, double componentWidth, Unit componentUnit) {
            this.labelWidth = labelWidth;
            this.labelUnit = labelUnit;
            this.componentWidth = componentWidth;
            this.componentUnit = componentUnit;
        }

        // second one - with alignment:
        //
        public DecorationData(HorizontalAlignmentConstant labelAlignment, HorizontalAlignmentConstant componentAlignment) {
            this.labelAlignment = labelAlignment;
            this.componentAlignment = componentAlignment;
        }

        public DecorationData(int labelWidth, HorizontalAlignmentConstant labelAlignment, int componentWidth, HorizontalAlignmentConstant componentAlignment) {
            this.labelWidth = labelWidth;
            this.labelUnit = Unit.PX;
            this.labelAlignment = labelAlignment;
            this.componentWidth = componentWidth;
            this.componentUnit = Unit.PX;
            this.componentAlignment = componentAlignment;
        }

        public DecorationData(double labelWidth, HorizontalAlignmentConstant labelAlignment, double componentWidth,
                HorizontalAlignmentConstant componentAlignment) {
            this.labelWidth = labelWidth;
            this.labelUnit = Unit.EM;
            this.labelAlignment = labelAlignment;
            this.componentWidth = componentWidth;
            this.componentUnit = Unit.EM;
            this.componentAlignment = componentAlignment;
        }

        public DecorationData(double labelWidth, Unit labelUnit, HorizontalAlignmentConstant labelAlignment, double componentWidth, Unit componentUnit,
                HorizontalAlignmentConstant componentAlignment) {
            this.labelWidth = labelWidth;
            this.labelUnit = labelUnit;
            this.labelAlignment = labelAlignment;
            this.componentWidth = componentWidth;
            this.componentUnit = componentUnit;
            this.componentAlignment = componentAlignment;
        }

    }
}