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
 * Created on Feb 11, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.devconsole;

import java.util.Iterator;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel;
import com.pyx4j.widgets.client.Button;

public class ResponsiveLayoutDevConsole extends FlowPanel {

    public ResponsiveLayoutDevConsole(final ResponsiveLayoutPanel responsiveLayoutPanel) {
        getElement().getStyle().setPadding(20, Unit.PX);
        Button setMocksButton = new Button("Set Mock Values", new Command() {

            @Override
            public void execute() {
                setWidgetDecoratorsMockValues(responsiveLayoutPanel.getContentDisplay());
            }
        });
        add(setMocksButton);
    }

    private void setWidgetDecoratorsMockValues(Widget widget) {
        if (widget instanceof WidgetDecorator) {
            WidgetDecorator decorator = (WidgetDecorator) widget;
            decorator.setMockValue();
        } else if (widget instanceof HasWidgets) {
            for (Iterator<Widget> iterator = ((HasWidgets) widget).iterator(); iterator.hasNext();) {
                setWidgetDecoratorsMockValues(iterator.next());
            }
        }
    }
}
