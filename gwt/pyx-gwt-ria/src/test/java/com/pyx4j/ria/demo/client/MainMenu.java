/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.demo.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.ria.demo.client.proving.ProvingManager;
import com.pyx4j.widgets.client.dialog.DialogPanel;
import com.pyx4j.widgets.client.menu.ActionMenuItem;
import com.pyx4j.widgets.client.menu.Menu;
import com.pyx4j.widgets.client.menu.SubMenuItem;

//TODO import com.pyx4j.client.log.Logger;

public class MainMenu extends Menu {

    public MainMenu(final DemoApplication app) {

        Menu fileMenuBar = new Menu(true);
        addItem(new SubMenuItem("File", fileMenuBar));

        fileMenuBar.addItem(new ActionMenuItem("Open", app.getOpenCommand()));

        fileMenuBar.addItem(new ActionMenuItem("Save", app.getSaveCommand()));

        fileMenuBar.addItem(new ActionMenuItem("Print", new Command() {
            @Override
            public void execute() {
                //TODO Logger.warn("Save");
            }
        }));

        Menu provingMenuBar = new Menu(true);
        addItem(new SubMenuItem("Proving", provingMenuBar));
        ProvingManager.createMenu(provingMenuBar, app);

        Menu helpMenuBar = new Menu(true);
        addItem(new SubMenuItem("Help", helpMenuBar));

        helpMenuBar.addItem(new ActionMenuItem("Welcome", new Command() {
            @Override
            public void execute() {
                //TODO Logger.error("Welcome");
            }
        }));
        helpMenuBar.addSeparator();
        helpMenuBar.addItem(new ActionMenuItem("About", new Command() {
            @Override
            public void execute() {
                DialogPanel aboutPanel = new DialogPanel();
                aboutPanel.setPixelSize(400, 400);
                aboutPanel
                        .setWidget(new HTML(
                                "Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text "));
                aboutPanel.center();
                aboutPanel.show();
            }
        }));

    }

}