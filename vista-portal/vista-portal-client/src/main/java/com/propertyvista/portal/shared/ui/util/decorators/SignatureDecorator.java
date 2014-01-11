/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.decorators;

import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorComponent;

import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponentTheme;
import com.pyx4j.forms.client.ui.CSignature;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.DebugIds;

public class SignatureDecorator extends FlowPanel implements IDecorator<CSignature> {

    private final SimplePanel componentHolder;

    private final Label validationLabel;

    private CSignature component;

    public SignatureDecorator(CSignature component) {

        componentHolder = new SimplePanel();
        componentHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        add(componentHolder);

        validationLabel = new Label();
        validationLabel.setVisible(false);
        validationLabel.setStyleName(CComponentTheme.StyleName.ValidationLabel.name());
        add(validationLabel);

        setComponent(component);
    }

    @Override
    public void setComponent(CSignature component) {
        this.component = component;
        component.asWidget().addStyleName(WidgetDecoratorComponent.name());
        componentHolder.setWidget(component);

        component.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.showErrorsUnconditional, PropertyName.repopulated,
                        PropertyName.enabled, PropertyName.editable)) {
                    renderValidationMessage();
                }
            }
        });
    }

    protected void renderValidationMessage() {
        if ((this.component.isUnconditionalValidationErrorRendering() || component.isVisited()) && !component.isValid()) {
            validationLabel.setText(component.getValidationResults().getValidationMessage(false, false, false));
            component.asWidget().addStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.invalid.name());
            validationLabel.setVisible(true);
        } else {
            validationLabel.setText(null);
            component.asWidget().removeStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.invalid.name());
            validationLabel.setVisible(false);
        }

        if (component.getDebugId() != null) {
            validationLabel.ensureDebugId(new CompositeDebugId(component.getDebugId(), DebugIds.ValidationLabel).debugId());
        }

    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {

    }
}
