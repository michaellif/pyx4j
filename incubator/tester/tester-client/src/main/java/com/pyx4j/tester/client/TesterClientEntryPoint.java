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
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.log4gwt.rpcappender.RPCAppender;
import com.pyx4j.unit.client.ui.TestRunnerDialog;
import com.pyx4j.unit.runner.ServerTestRunner;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

public class TesterClientEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {
        ClientEntityFactory.ensureIEntityImplementations();
        StyleManger.installTheme(new WindowsTheme());

        ClientLogger.addAppender(new RPCAppender());
        ClientLogger.setDebugOn(true);

        VerticalPanel menu = new VerticalPanel();
        RootPanel.get().add(menu, 0, 0);

        final Button startButton = new Button("Start Client Side (GWT) Tests");
        menu.add(startButton);
        startButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TestRunnerDialog.createAsync();
            }
        });

        final Button serverStartButton = new Button("Start Server Side Tests");
        menu.add(serverStartButton);
        serverStartButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ServerTestRunner.createAsync();
            }
        });
    }

}
