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

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.VerticalAlign;
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
import com.pyx4j.forms.client.ui.DefaultCComponentsTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.DebugIds;
import com.pyx4j.widgets.client.Label;

/**
 * [Artyom]: This is not fully functional (I was made to in order to decorate "I Agree" style checkboxes in wizards so that label could be placed on the left
 * side.
 * 
 */
public class WidgetDecoratorRightLabel extends Composite implements IDecorator<CComponent<?, ?>> {

    private final FlowPanel panel;

    private final SimplePanel componentHolder;

    private final Label label;

    private CComponent<?, ?> component;

    private final Label validationLabel;

    public WidgetDecoratorRightLabel(CComponent<?, ?> component) {
        panel = new FlowPanel();

        label = new Label();
        label.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        componentHolder = new SimplePanel();

        SimplePanel labelComponentHolder = new SimplePanel();
        FlowPanel pTop = new FlowPanel();
        componentHolder.getElement().getStyle().setFloat(Float.LEFT);
        pTop.add(componentHolder);
        label.getElement().getStyle().setFloat(Float.LEFT);
        pTop.add(label);
        labelComponentHolder.setWidget(pTop);
        labelComponentHolder.getElement().getStyle().setProperty("display", "table-cell");
        labelComponentHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        panel.add(labelComponentHolder);

        validationLabel = new Label();
        validationLabel.setStyleName(DefaultCComponentsTheme.StyleName.ValidationLabel.name());
        panel.add(validationLabel);

        initWidget(panel);
        setComponent(component);
    }

    @Override
    public void setComponent(CComponent<?, ?> component) {
        this.component = component;
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
                } else if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.showErrorsUnconditional, PropertyName.repopulated,
                        PropertyName.enabled, PropertyName.editable)) {
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
        if ((this.component.isUnconditionalValidationErrorRendering() || component.isVisited()) && !component.isValid()) {
            validationLabel.setText(component.getValidationResults().getValidationMessage(false, false));
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
