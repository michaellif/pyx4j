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
package com.propertyvista.common.client.ui.decorations;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.Cursor;
import com.pyx4j.forms.client.ui.NativeCheckBox;
import com.pyx4j.forms.client.ui.decorators.SpaceHolder;
import com.pyx4j.widgets.client.Tooltip;
import com.pyx4j.widgets.client.style.IStyleName;

import com.propertyvista.common.client.ui.decorations.DecorationData.ShowMandatory;

/**
 * Widget decorator helpful for representation of Label : [ edit field ] widgets pair in
 * various view forms with uniform project style.
 */
public class VistaWidgetDecorator extends VerticalPanel {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_VistaWidgetDecorator";

    public static enum StyleSuffix implements IStyleName {
        Label, Component
    }

    private final CComponent<?> component;

    private final Widget nativeComponent;

    private final Label label;

    private final Label mandatoryLabel;

    private final Label validationLabel;

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

        label = new Label(decorData.componentCaption != null ? decorData.componentCaption : CommonsStringUtils.nvl(component.getTitle()));
        label.setHorizontalAlignment(decorData.labelAlignment);
        label.ensureDebugId(new CompositeDebugId(component.getDebugId(), VistaDecoratorsIds.Label.name()).debugId());

        if (decorData.labelStyleName == null) {
            label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Label);
        } else {
            label.addStyleName(decorData.labelStyleName);
        }

        Cursor.setDefault(label.getElement());

        if (decorData.hideInfoHolder) {
            infoImageHolder = new SpaceHolder("1px");

        } else {
            infoImageHolder = new SpaceHolder("16px");
            infoImageHolder.getElement().getStyle().setPaddingRight(5, Unit.PX);
        }
        infoImageHolder.getElement().getStyle().setPaddingTop(2, Unit.PX);
        infoImageHolder.getElement().getStyle().setPaddingLeft(5, Unit.PX);

        if (component.getTooltip() != null && component.getTooltip().trim().length() > 0) {
            Image infoImage = new Image(ImageFactory.getImages().formTooltipInfo());
            Tooltip.tooltip(infoImage, component.getTooltip());
            infoImageHolder.setWidget(infoImage);
        }

        mandatoryLabel = new Label();
        mandatoryLabel.getElement().getStyle().setFloat(Float.LEFT);
        mandatoryLabel.getElement().getStyle().setPaddingLeft(15, Unit.PX);
        mandatoryLabel.getElement().getStyle().setColor("#aaa");

        validationLabel = new Label();
        validationLabel.getElement().getStyle().setFloat(Float.LEFT);
        validationLabel.getElement().getStyle().setPaddingLeft(decorData.labelWidth, decorData.labelUnit);
        validationLabel.getElement().getStyle().setMarginLeft(30, Unit.PX);
        validationLabel.getElement().getStyle().setColor("red");
        validationLabel.ensureDebugId(new CompositeDebugId(component.getDebugId(), VistaDecoratorsIds.Validation.name()).debugId());
        nativeComponent = component.asWidget();

        if (nativeComponent == null) {
            throw new RuntimeException("initNativeComponent() method call on [" + component.getClass() + "] returns null.");
        }
        if (nativeComponent instanceof NativeCheckBox) {
            ((NativeCheckBox) nativeComponent).setText(null);
            nativeComponent.getElement().getStyle().setMargin(0, Unit.PX);
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

        HorizontalPanel leftSide = new HorizontalPanel();
        leftSide.getElement().getStyle().setFloat(Float.LEFT);
        if (decorData.labelWidth != 0) {
            leftSide.getElement().getStyle().setWidth(decorData.labelWidth, decorData.labelUnit);
        }

        leftSide.add(label);
        leftSide.add(infoImageHolder);

        if (decorData.hideInfoHolder) {
            leftSide.setCellWidth(infoImageHolder, "6px");
        } else {
            leftSide.setCellWidth(infoImageHolder, "26px");
        }

        FlowPanel firstLine = new FlowPanel();
        firstLine.add(leftSide);
        firstLine.add(nativeComponentHolder);

        if (!ShowMandatory.None.equals(decorData.showMandatory)) {
            firstLine.add(mandatoryLabel);
        }

        add(firstLine);

        setVisible(component.isVisible());

        if (!decorData.readOnlyMode) {
            FlowPanel secondLine = new FlowPanel();
            secondLine.add(validationLabel);
            add(secondLine);

            renderMandatoryMessage();

            component.addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                    if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.visible) {
                        setVisible(component.isVisible());
                    }
                    if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.valid) {
                        renderValidationMessage();
                    }
                    renderMandatoryMessage();
                }
            });

            getElement().getStyle().setPaddingTop(2, Unit.PX);
            getElement().getStyle().setPaddingBottom(13, Unit.PX);

        } else {

            component.addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                    if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.visible) {
                        setVisible(component.isVisible());
                    }
                }
            });
        }
    }

    private void renderMandatoryMessage() {
        if (component instanceof CEditableComponent<?, ?>) {
            CEditableComponent<?, ?> editableComponent = (CEditableComponent<?, ?>) component;
            if (editableComponent.isVisible() && editableComponent.isEnabled() && editableComponent.isEditable()) {
                if (editableComponent.isMandatory() && DecorationData.ShowMandatory.Mandatory.equals(decorData.showMandatory)) {
                    mandatoryLabel.setText(DecorationData.ShowMandatory.Mandatory.toString());
                } else if (!editableComponent.isMandatory() && DecorationData.ShowMandatory.Optional.equals(decorData.showMandatory)) {
                    mandatoryLabel.setText(DecorationData.ShowMandatory.Optional.toString());
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
}