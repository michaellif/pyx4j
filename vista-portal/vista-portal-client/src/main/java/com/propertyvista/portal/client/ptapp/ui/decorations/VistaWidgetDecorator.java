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
import com.pyx4j.forms.client.gwt.Cursor;
import com.pyx4j.forms.client.gwt.NativeCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.decorators.ImageHolder;
import com.pyx4j.widgets.client.Tooltip;
import com.pyx4j.widgets.client.style.IStyleSuffix;

/**
 * Widget decorator helpful for representation of Label : [ edit field ] widgets pair in
 * various view forms with uniform project style.
 */
public class VistaWidgetDecorator extends FlowPanel {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_VistaWidgetDecorator";

    public static enum StyleSuffix implements IStyleSuffix {
        Label, Component, Gap
    }

    private final CComponent<?> component;

    private final Widget nativeComponent;

    private final Label label;

    private final ImageHolder imageMandatoryHolder;

    private Image imageInfoWarn;

    private final ImageHolder imageInfoWarnHolder;

    private Image imageMandatory;

    private Tooltip tooltip;

    public VistaWidgetDecorator(final CComponent<?> component) {
        this(component, new DecorationData());
    }

    public VistaWidgetDecorator(final CComponent<?> component, int labelWidth, int componentWidth) {
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

    public VistaWidgetDecorator(final CComponent<?> component, DecorationData decorData) {

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

        imageInfoWarnHolder = new ImageHolder("18px");
        imageInfoWarnHolder.getElement().getStyle().setFloat(Float.LEFT);
        imageInfoWarnHolder.getElement().getStyle().setPaddingTop(2, Unit.PX);
        imageInfoWarnHolder.getElement().getStyle().setPaddingLeft(10, Unit.PX);

        imageMandatoryHolder = new ImageHolder(decorData.gapWidth + decorData.gapUnit.getType());
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