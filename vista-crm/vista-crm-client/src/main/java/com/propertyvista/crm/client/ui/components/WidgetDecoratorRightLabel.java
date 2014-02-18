/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CComponentTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.DebugIds;
import com.pyx4j.widgets.client.Label;

/**
 * [Artyom]: This is not fully functional (I was made to in order to decorate "I Agree" style checkboxes in wizards so that label could be placed on the left
 * side.
 * 
 */
public class WidgetDecoratorRightLabel extends Composite implements IDecorator<CComponent<?>> {

    private final FlowPanel panel;

    private final SimplePanel componentHolder;

    private final Label label;

    private CComponent<?> component;

    private final Label validationLabel;

    private final double componentWidth;

    /**
     * 
     * @param component
     *            the component that will be wrapped
     * @param componentWidth
     *            component width in "EM"s
     * @param labelWidth
     *            label width in "EM"s
     */
    public WidgetDecoratorRightLabel(CComponent<?> component, double componentWidth, double labelWidth) {
        this.componentWidth = componentWidth;
        panel = new FlowPanel();

        FlowPanel labelAndComponentHolder = new FlowPanel();
        labelAndComponentHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        componentHolder = new SimplePanel();
        componentHolder.getElement().getStyle().setFloat(Float.LEFT);
        labelAndComponentHolder.add(componentHolder);

        label = new Label();
        label.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        label.getElement().getStyle().setFloat(Float.LEFT);
        label.getElement().getStyle().setWidth(labelWidth, Unit.EM);
        label.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        labelAndComponentHolder.add(label);

        panel.add(labelAndComponentHolder);

        SimplePanel validationLabelHolder = new SimplePanel();
        validationLabel = new Label();
        validationLabel.setStyleName(CComponentTheme.StyleName.ValidationLabel.name());
        validationLabel.getElement().getStyle().setWidth(labelWidth + componentWidth, Unit.EM);
        validationLabelHolder.add(validationLabel);
        panel.add(validationLabelHolder);

        initWidget(panel);
        setComponent(component);
    }

    @Override
    public void setComponent(CComponent<?> component) {
        this.component = component;
        this.component.asWidget().setWidth(componentWidth + "em");
        this.component.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyChangeEvent.PropertyName.visible) {
                    label.setVisible(WidgetDecoratorRightLabel.this.component.isVisible());
                    WidgetDecoratorRightLabel.this.setVisible(WidgetDecoratorRightLabel.this.component.isVisible());
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.title) {
                    label.setText(WidgetDecoratorRightLabel.this.component.getTitle());
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.tooltip) {
                    // TODO                     
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.note) {
                    // TODO
                } else if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.repopulated, PropertyName.enabled, PropertyName.editable)) {
                    renderValidationMessage();
                }
            }

        });
        this.componentHolder.setWidget(component);

        if (this.component.asWidget() instanceof Focusable) {
            label.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ((Focusable) WidgetDecoratorRightLabel.this.component.asWidget()).setFocus(true);
                }
            });
        }
        label.ensureDebugId(CompositeDebugId.debugId(component.getDebugId(), DebugIds.Label));
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        // TODO Auto-generated method stub
    }

    private void renderValidationMessage() {
        if (!component.isValid()) {
            validationLabel.setText(component.getValidationResults().getValidationMessage(false));
            component.asWidget().addStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.invalid.name());
        } else {
            validationLabel.setText(null);
            component.asWidget().removeStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.invalid.name());
        }

        if (component.getDebugId() != null) {
            validationLabel.ensureDebugId(new CompositeDebugId(component.getDebugId(), DebugIds.ValidationLabel).debugId());
        }
    }
}
