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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.resources.SiteImages;

public class DevConsoleTab implements IsWidget {

    private final FlowPanel devConsole;

    private final SimplePanel devConsoleContent;

    public DevConsoleTab(IsWidget consolePanel) {

        devConsole = new FlowPanel();
        devConsole.setStylePrimaryName(DevConsoleTheme.StyleName.DevConsole.name());

        FlowPanel devConsoleHandler = new FlowPanel();
        devConsoleHandler.sinkEvents(Event.ONCLICK);
        devConsoleHandler.setStylePrimaryName(DevConsoleTheme.StyleName.DevConsoleHandler.name());
        devConsole.add(devConsoleHandler);

        HTML devConsoleHandlerLabel = new HTML("Dev. Console");
        devConsoleHandlerLabel.setStylePrimaryName(DevConsoleTheme.StyleName.DevConsoleHandlerLabel.name());
        devConsoleHandler.add(devConsoleHandlerLabel);
        final Image devConsoleHandlerImage = new Image(SiteImages.INSTANCE.openDevConsoleButton());
        devConsoleHandlerImage.setStylePrimaryName(DevConsoleTheme.StyleName.DevConsoleHandlerImage.name());
        devConsoleHandler.add(devConsoleHandlerImage);

        devConsoleContent = new SimplePanel();
        devConsoleContent.setVisible(false);
        devConsoleContent.setStylePrimaryName(DevConsoleTheme.StyleName.DevConsoleContent.name());
        devConsole.add(devConsoleContent);

        devConsoleContent.setWidget(consolePanel);

        devConsoleHandler.addHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (devConsoleContent.isVisible()) {
                    devConsoleHandlerImage.setResource(SiteImages.INSTANCE.openDevConsoleButton());
                } else {
                    devConsoleHandlerImage.setResource(SiteImages.INSTANCE.closeDevConsoleButton());
                }
                devConsoleContent.setVisible(!devConsoleContent.isVisible());
            }
        }, ClickEvent.getType());

    }

    @Override
    public Widget asWidget() {
        return devConsole;
    }

    public void setDevConsole(IsWidget widget) {
        devConsoleContent.setWidget(widget);
    }
}
