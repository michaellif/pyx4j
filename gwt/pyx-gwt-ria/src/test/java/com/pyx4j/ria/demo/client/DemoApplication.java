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
 * Created on Apr 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.demo.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.pyx4j.ria.client.HeaderPanel;
import com.pyx4j.ria.client.IApplication;
import com.pyx4j.ria.client.Perspective;
import com.pyx4j.ria.client.StatusBar;
import com.pyx4j.ria.client.view.AbstractView;

public class DemoApplication implements IApplication {

    private Perspective perspective;

    private StatusBar statusBar;

    private Timer progressBarTimer;

    private final Command openCommand;

    private final Command saveCommand;

    private MainPanel mainPanel;

    public DemoApplication() {
        openCommand = new Command() {
            private int counter;

            @Override
            public void execute() {
                TestView view = new TestView("Tab" + counter++);
                openView(view);
            }
        };
        saveCommand = new Command() {
            @Override
            public void execute() {
                //TODO
                //                Logger.info("Save");
                //                Logger.debug("Save");
            }
        };

    }

    public void openView(AbstractView view) {
        mainPanel.openView(view, true);
    }

    public void onLoad() {
        perspective = new Perspective();

        statusBar = new StatusBar();

        perspective.setHeaderPanel(new HeaderPanel("Demo"));
        perspective.setMenuBar(new MainMenu(this));

        HorizontalPanel toolbarPanel = new HorizontalPanel();
        toolbarPanel.setWidth("100%");

        Toolbar toolbar = new Toolbar(this);
        toolbarPanel.add(toolbar);

        HorizontalPanel links = new HorizontalPanel();
        Anchor logoutHyperlink = new Anchor("Sign out", "Sign-out");
        DOM.setStyleAttribute(logoutHyperlink.getElement(), "marginRight", "10px");
        DOM.setStyleAttribute(logoutHyperlink.getElement(), "whiteSpace", "nowrap");
        logoutHyperlink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //TODO Logger.info("TODO Logout");
            }
        });
        links.add(logoutHyperlink);
        toolbarPanel.add(links);
        toolbarPanel.setCellHorizontalAlignment(links, HorizontalPanel.ALIGN_RIGHT);
        toolbarPanel.setCellVerticalAlignment(links, HorizontalPanel.ALIGN_MIDDLE);

        perspective.setToolbar(toolbarPanel);

        mainPanel = new MainPanel();

        mainPanel.openView(new TestView("Long Tab1"), true);
        mainPanel.openView(new TestView("Tab2"), true);
        mainPanel.openView(new TestView("Tab3"), true);

        //TODO mainPanel.getBottomFolder().addView(new LogView("Log", mainPanel.getBottomFolder()));
        mainPanel.openView(new TabPanelView("Tab Pane", null), true);

        perspective.setMainPanel(mainPanel);

        perspective.setStatusPanel(statusBar);

        perspective.attachToParent(RootLayoutPanel.get());
    }

    void runProgressBar() {
        if (progressBarTimer != null) {
            return;
        }
        progressBarTimer = new Timer() {
            int counetr = 0;

            @Override
            public void run() {
                statusBar.setProgress(counetr++);
                if (counetr > 100) {
                    this.cancel();
                    progressBarTimer = null;
                    statusBar.setProgressBarVisible(false);
                    statusBar.setProgress(0);
                }
            }
        };
        statusBar.setProgressBarVisible(true);
        progressBarTimer.scheduleRepeating(100);
    }

    public void onDiscard() {
        // TODO Auto-generated method stub

    }

    public Command getOpenCommand() {
        return openCommand;
    }

    public Command getSaveCommand() {
        return saveCommand;
    }

}
