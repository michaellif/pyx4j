/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-29
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.tester.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;

import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.tester.client.TesterSite;
import com.pyx4j.tester.client.ui.event.CComponentBrowserEvent;

public class FormDecoratorBuilder extends FieldDecorator.Builder<FormDecoratorBuilder> {

    public FormDecoratorBuilder(String labelWidth, String componentWidth) {
        super();
        labelWidth(labelWidth);
        componentWidth(componentWidth);
        labelAlignment(Alignment.left);
        useLabelSemicolon(false);

    }

    public FormDecoratorBuilder(String componentWidth) {
        this("150px", componentWidth);
    }

    public FormDecoratorBuilder() {
        this("200px");
    }

    @Override
    public FieldDecorator build() {
        final FieldDecorator decorator = super.build();
        decorator.sinkEvents(Event.ONCLICK);

        decorator.addHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                TesterSite.getEventBus().fireEvent(new CComponentBrowserEvent(decorator.getComponent()));
            }
        }, ClickEvent.getType());

        return decorator;
    }

}
