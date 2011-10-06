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
package com.propertyvista.portal.client.ui.decorations;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
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
import com.pyx4j.forms.client.ui.NativeCheckBox;
import com.pyx4j.forms.client.ui.decorators.SpaceHolder;
import com.pyx4j.widgets.client.Tooltip;
import com.pyx4j.widgets.client.style.IStyleName;

public class CriteriaWidgetDecorator extends VerticalPanel {

    public static String DEFAULT_STYLE_PREFIX = "CriteriaWidgetDecorator";

    public static enum StyleSuffix implements IStyleName {
        Label, Component
    }

    private final CComponent<?> component;

    private final Widget nativeComponent;

    private final Label label;

    private final Label validationLabel;

    private final SpaceHolder infoImageHolder;

    //  private final SimplePanel nativeComponentHolder;

    public CriteriaWidgetDecorator(final CComponent<?> component) {
        this(component, "160px");
    }

    public CriteriaWidgetDecorator(final CComponent<?> component, String componentWidth) {
        this.component = component;
        setStyleName(DEFAULT_STYLE_PREFIX);

        label = new Label(CommonsStringUtils.nvl(component.getTitle()));
        label.getElement().getStyle().setFloat(Float.LEFT);
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Label);
        Cursor.setDefault(label.getElement());

        infoImageHolder = new SpaceHolder("16px");
        infoImageHolder.getElement().getStyle().setPaddingRight(5, Unit.PX);

        infoImageHolder.getElement().getStyle().setPaddingTop(2, Unit.PX);
        infoImageHolder.getElement().getStyle().setPaddingLeft(5, Unit.PX);
        infoImageHolder.getElement().getStyle().setFloat(Float.RIGHT);

        if (component.getToolTip() != null && component.getToolTip().trim().length() > 0) {
            Image infoImage = new Image(ImageFactory.getImages().formTooltipInfo());
            Tooltip.tooltip(infoImage, component.getToolTip());
            infoImageHolder.setWidget(infoImage);
        }

        FlowPanel labelLine = new FlowPanel();
        labelLine.getElement().getStyle().setWidth(100, Unit.PCT);
        labelLine.add(label);
        labelLine.add(infoImageHolder);

        validationLabel = new Label();
        validationLabel.getElement().getStyle().setFloat(Float.LEFT);
        validationLabel.getElement().getStyle().setMarginLeft(30, Unit.PX);
        validationLabel.getElement().getStyle().setColor("red");

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

        //  nativeComponentHolder.getElement().getStyle().setWidth(componentWidth, Unit.PX);
        nativeComponentHolder.setWidth(componentWidth);

        nativeComponentHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Component);
        nativeComponentHolder.setWidget(nativeComponent);

        // put it together:

        add(labelLine);
        add(nativeComponentHolder);
        add(validationLabel);

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

            }
        });

        getElement().getStyle().setPaddingTop(2, Unit.PX);
        getElement().getStyle().setPaddingBottom(13, Unit.PX);

    }

    private void renderValidationMessage() {
        if (component instanceof CEditableComponent<?, ?>) {
            CEditableComponent<?, ?> editableComponent = (CEditableComponent<?, ?>) component;
            if (!editableComponent.isValid()) {
                validationLabel.setText(editableComponent.getValidationMessage());
            } else {
                validationLabel.setText(null);
            }
        }
    }

}