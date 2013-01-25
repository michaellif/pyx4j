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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.widgets.client.Label;

public class CheckBoxDecorator extends Composite implements IDecorator<CCheckBox> {

    private final SimplePanel componentHolder;

    private CCheckBox component;

    private final Label label;

    public CheckBoxDecorator(CCheckBox component) {
        FlowPanel decoratorPanel = new FlowPanel();
        componentHolder = new SimplePanel();
        componentHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        decoratorPanel.add(componentHolder);

        label = new Label();
        label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        decoratorPanel.add(label);

        initWidget(decoratorPanel);

        setComponent(component);
    }

    @Override
    public void setComponent(CCheckBox component) {
        this.component = component;
        componentHolder.setWidget(component);
        label.setText(component.getTitle());
        label.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBoxDecorator.this.component.getWidget().setFocus(true);
            }
        });

        // TODO add property change handler

    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        // TODO Auto-generated method stub

    }

}
