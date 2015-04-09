/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Oct 29, 2014
 * @author michaellif
 */
package com.pyx4j.tester.client.view.form;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.FormFieldDecoratorOptions;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.tester.client.TesterSite;
import com.pyx4j.tester.client.ui.event.CComponentBrowserEvent;

public class TesterFormPanel extends FormPanel {

    public TesterFormPanel(CForm<?> parent) {
        super(parent);
    }

    @Override
    protected FormFieldDecoratorOptions createFieldDecoratorOptions() {
        FormFieldDecoratorOptions options = super.createFieldDecoratorOptions();
        options.labelWidth("150px");
        options.componentWidth("200px");
        options.labelAlignment(Alignment.left);
        options.useLabelSemicolon(false);
        return options;
    }

    @Override
    protected FieldDecorator createFieldDecorator(FormFieldDecoratorOptions options) {
        final FieldDecorator decorator = super.createFieldDecorator(options);
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
