/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.unit.client.ui.TestRunnerDialog;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

public class SerializationDemo implements EntryPoint {

    @Override
    public void onModuleLoad() {
        StyleManger.installTheme(new WindowsTheme());
        ClientLogger.setDebugOn(true);

        VerticalPanel panel = new VerticalPanel();
        RootPanel.get().add(panel, 30, 30);
        panel.add(new HTML("This version compiled with GWT <b>" + GWT.getVersion() + "</b>"));

        final Button startButton = new Button("Start Tests");
        panel.add(startButton);
        startButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TestRunnerDialog.createAsync();
            }
        });

        panel.add(new HTML5StorageDemoPanel());
    }

}
