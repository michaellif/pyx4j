/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.admin;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.ria.client.HeaderPanel;
import com.pyx4j.ria.client.IApplication;
import com.pyx4j.ria.client.IView;
import com.pyx4j.ria.client.Perspective;
import com.pyx4j.ria.client.StatusBar;
import com.pyx4j.ria.client.ThreeFoldersMainPanel;

public class AdminApplication implements IApplication {

    private Perspective perspective;

    private StatusBar statusBar;

    private final Command saveCommand;

    private ThreeFoldersMainPanel mainPanel;

    public AdminApplication() {
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
        mainPanel.getTopFolder().addView(view, false);
        mainPanel.getTopFolder().showView(view);
    }

    public void onLoad() {
        perspective = new Perspective();

        statusBar = new StatusBar();

        perspective.setHeaderPanel(new HeaderPanel("Site Admin"));
        perspective.setMenuBar(new MainMenu(this));

        perspective.setActionsToolbar(new Toolbar(this));

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
        perspective.setLinksToolbar(links);

        mainPanel = new ThreeFoldersMainPanel();

        mainPanel.getLeftFolder().addView(new SiteMapView(), false);

        //TODO mainPanel.getBottomFolder().addView(new LogView("Log", mainPanel.getBottomFolder()));

        perspective.setMainPanel(mainPanel);

        perspective.setStatusPanel(statusBar);

        perspective.attachToParent(RootPanel.get());
    }

    public void onDiscard() {
    }

    public Command getSaveCommand() {
        return saveCommand;
    }

}
