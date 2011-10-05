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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.log4gwt.rpcappender.RPCAppender;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.unit.client.ui.TestRunnerDialog;
import com.pyx4j.unit.runner.ServerTestRunner;
import com.pyx4j.unit.shared.UnitDebugId;
import com.pyx4j.widgets.client.dialog.UnrecoverableErrorHandlerDialog;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;
import com.pyx4j.widgets.client.style.ThemePalette;
import com.pyx4j.widgets.client.style.theme.WindowsTheme;

public class TesterClientEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {

        //NotifyClientStarted.notifyServer();

        ClientEntityFactory.ensureIEntityImplementations();
        UnrecoverableErrorHandlerDialog.register();
        Theme theme = new WindowsTheme();
        theme.putThemeColor(ThemePalette.OBJECT_TONE1, 0xFFFFFF);
        StyleManger.installTheme(theme);

        ClientLogger.addAppender(new RPCAppender());
        ClientLogger.setDebugOn(true);
        RPCManager.enableAppEngineUsageStats();

        VerticalPanel menu = new VerticalPanel();
        RootPanel.get().add(menu, 30, 30);
        menu.add(new HTML("This version compiled with GWT <b>" + GWT.getVersion() + "</b>"));

        final Button startButton = new Button("Start Client Side (GWT) Tests");
        startButton.ensureDebugId(UnitDebugId.JUnit_StartClientTests.name());
        menu.add(startButton);
        startButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TestRunnerDialog.createAsync();
            }
        });

        final Button serverStartButton = new Button("Start Server Side Tests");
        serverStartButton.ensureDebugId(UnitDebugId.JUnit_StartServerTests.name());
        menu.add(serverStartButton);
        serverStartButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ServerTestRunner.createAsync();
            }
        });

        menu.add(new SessionControlPanel());

        HorizontalPanel logos = new HorizontalPanel();
        menu.add(logos);

        logos.add(new Image("pyx_logo-32x32.png"));
        logos.add(new Anchor("code.pyx4j.com", "http://code.pyx4j.com"));
        logos.add(new HTML(CommonsStringUtils.NO_BREAK_SPACE_HTML));
        logos.add(new Image("gwt-logo-30x30.png"));
        logos.add(new Image("http://code.google.com/appengine/images/appengine-noborder-120x30.gif"));
    }

}
