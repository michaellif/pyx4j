/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Nov 3, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.tester.client.TesterSite;
import com.pyx4j.tester.client.ui.event.CComponentBrowserEvent;

public class TesterWidgetDecorator extends WidgetDecorator {

    public TesterWidgetDecorator(final CComponent<?> component) {
        super(component);
        getLabel().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                TesterSite.getEventBus().fireEvent(new CComponentBrowserEvent(component));
            }
        });
    }
}
