/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.decorators;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.events.NValueChangeEvent;
import com.pyx4j.forms.client.events.NValueChangeHandler;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.INativeTextComponent;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.DebugIds;

public final class WatermarkDecorator<Component extends CTextFieldBase<?, INativeTextComponent<?>>> extends Composite implements IDecorator<Component> {

    private Component component;

    private final SimplePanel componentHolder;

    private final Label watermarklabel;

    public WatermarkDecorator(Component component) {
        FlowPanel containerPanel = new FlowPanel();
        containerPanel.getElement().getStyle().setPosition(Position.STATIC);
        containerPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
        containerPanel.getElement().getStyle().setMarginBottom(10, Unit.PX);

        FlowPanel decoratorPanel = new FlowPanel();
        decoratorPanel.getElement().getStyle().setPosition(Position.RELATIVE);
        decoratorPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        containerPanel.add(decoratorPanel);

        watermarklabel = new Label();
        watermarklabel.getElement().getStyle().setPosition(Position.ABSOLUTE);
        watermarklabel.getElement().getStyle().setZIndex(255);
        // TODO move ALL THESE SETTINGS TO A THEME
        watermarklabel.getElement().getStyle().setPaddingLeft(10, Unit.PX);
        watermarklabel.getElement().getStyle().setPaddingRight(10, Unit.PX);
        watermarklabel.getElement().getStyle().setPaddingTop(2, Unit.PX);
        watermarklabel.getElement().getStyle().setPaddingBottom(2, Unit.PX);
        watermarklabel.getElement().getStyle().setColor("#BBBBBB");

        decoratorPanel.add(watermarklabel);

        componentHolder = new SimplePanel();
        componentHolder.getElement().getStyle().setPosition(Position.RELATIVE);
        decoratorPanel.add(componentHolder);

        initWidget(containerPanel);
        setComponent(component);
    }

    @Override
    public void setComponent(Component component) {
        this.component = component;
        this.setVisible(component.isVisible());
        this.watermarklabel.setText(this.component.getTitle());
        this.componentHolder.setWidget(component);

        this.component.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyChangeEvent.PropertyName.visible) {
                    watermarklabel.setVisible(WatermarkDecorator.this.component.isVisible());
                    WatermarkDecorator.this.setVisible(WatermarkDecorator.this.component.isVisible());
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.title) {
                    watermarklabel.setText(WatermarkDecorator.this.component.getTitle());
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.tooltip) {
                    // TODO                     
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.note) {
                    // TODO
                } else if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.showErrorsUnconditional, PropertyName.repopulated,
                        PropertyName.enabled, PropertyName.editable)) {
                    //renderValidationMessage();
                }
            }
        });

        this.watermarklabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WatermarkDecorator.this.component.getWidget().setFocus(true);
            }
        });
        this.component.addNValueChangeHandler(new NValueChangeHandler<String>() {
            @Override
            public void onNValueChange(NValueChangeEvent<String> event) {
                WatermarkDecorator.this.watermarklabel.setVisible(CommonsStringUtils.isEmpty(event.getValue()));
            }
        });

        this.component.getWidget().asWidget().addHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                watermarklabel.setVisible(false);
            }
        }, FocusEvent.getType());

        watermarklabel.ensureDebugId(CompositeDebugId.debugId(component.getDebugId(), DebugIds.Label));
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        // TODO Auto-generated method stub

    }

}