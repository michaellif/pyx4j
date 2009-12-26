/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.ria.client.app.HeaderPanel;
import com.pyx4j.ria.client.app.IApplication;
import com.pyx4j.ria.client.app.IView;
import com.pyx4j.ria.client.app.Perspective;
import com.pyx4j.ria.client.app.StatusBar;
import com.pyx4j.ria.client.app.ThreeFoldersMainPanel;

public class DemoApplication implements IApplication {

    private Perspective perspective;

    private StatusBar statusBar;

    private Timer progressBarTimer;

    private final Command openCommand;

    private final Command saveCommand;

    private ThreeFoldersMainPanel mainPanel;

    public DemoApplication() {
        openCommand = new Command() {
            private int counter;

            @Override
            public void execute() {
                TestView view = new TestView("Tab" + counter++);
                mainPanel.getTopFolder().addView(view);
                mainPanel.getTopFolder().showView(view);
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

    @Override
    public void openView(IView view) {
        mainPanel.getTopFolder().addView(view);
        mainPanel.getTopFolder().showView(view);
    }

    public void onLoad() {
        perspective = new Perspective();

        statusBar = new StatusBar();

        perspective.setHeaderPanel(new HeaderPanel("Demo"));
        perspective.setMenuBar(new MainMenu(this));

        perspective.setToolbarActions(new Toolbar(this));

        HorizontalPanel links = new HorizontalPanel();
        Hyperlink logoutHyperlink = new Hyperlink("Sign out", "Sign-out");
        DOM.setStyleAttribute(logoutHyperlink.getElement(), "marginRight", "10px");
        DOM.setStyleAttribute(logoutHyperlink.getElement(), "whiteSpace", "nowrap");
        logoutHyperlink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //TODO Logger.info("TODO Logout");
            }
        });
        links.add(logoutHyperlink);
        perspective.setToolbarLinks(links);

        mainPanel = new ThreeFoldersMainPanel();

        mainPanel.getLeftFolder().addView(new TestView("Long Tab1"));
        mainPanel.getLeftFolder().addView(new TestView("Tab2"));
        mainPanel.getLeftFolder().addView(new TestView("Tab3"));

        //TODO mainPanel.getBottomFolder().addView(new LogView("Log", mainPanel.getBottomFolder()));
        mainPanel.getBottomFolder().addView(new TabPanelView("Tab Pane", null));

        perspective.setMainPanel(mainPanel);

        perspective.setStatusPanel(statusBar);

        perspective.attachToParent(RootPanel.get());
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
