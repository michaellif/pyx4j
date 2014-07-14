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
package com.propertyvista.portal.shared.ui.util.decorators;

import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorContent;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.decorators.IFieldDecorator;
import com.pyx4j.widgets.client.Label;

public class CheckBoxDecorator extends FlowPanel implements IFieldDecorator {

    private final SimplePanel componentHolder;

    private CCheckBox component;

    private final Label label;

    public CheckBoxDecorator() {

        componentHolder = new SimplePanel();
        componentHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        componentHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        componentHolder.getElement().getStyle().setWidth(1.5, Unit.EM);
        add(componentHolder);

        label = new Label();
        label.setStyleName(WidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        label.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        label.getElement().getStyle().setMarginLeft(0.5, Unit.EM);
        add(label);

    }

    @Override
    public void init(CField<?, ?> component) {
        this.component = (CCheckBox) component;
        component.asWidget().addStyleName(WidgetDecoratorContent.name());
        label.setText(component.getTitle());
        label.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBoxDecorator.this.component.getNativeComponent().setFocus(true);
            }
        });
    }

    @Override
    public void setContent(IsWidget content) {
        componentHolder.setWidget(content);
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {

    }
}
