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
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.demo.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.ria.demo.client.proving.ProvingManager;
import com.pyx4j.widgets.client.dialog.DialogPanelNew;

//TODO import com.pyx4j.client.log.Logger;

public class MainMenu extends MenuBar {

    public MainMenu(final DemoApplication app) {

        MenuBar fileMenuBar = new MenuBar(true);
        addItem(new MenuItem("File", fileMenuBar));

        fileMenuBar.addItem(new MenuItem("Open", app.getOpenCommand()));

        fileMenuBar.addItem(new MenuItem("Save", app.getSaveCommand()));

        fileMenuBar.addItem(new MenuItem("Print", new Command() {
            @Override
            public void execute() {
                //TODO Logger.warn("Save");
            }
        }));

        MenuBar provingMenuBar = new MenuBar(true);
        addItem(new MenuItem("Proving", provingMenuBar));
        ProvingManager.createMenu(provingMenuBar, app);

        MenuBar helpMenuBar = new MenuBar(true);
        addItem(new MenuItem("Help", helpMenuBar));

        helpMenuBar.addItem(new MenuItem("Welcome", new Command() {
            @Override
            public void execute() {
                //TODO Logger.error("Welcome");
            }
        }));
        helpMenuBar.addSeparator();
        helpMenuBar.addItem(new MenuItem("About", new Command() {
            @Override
            public void execute() {
                DialogPanelNew aboutPanel = new DialogPanelNew(true, false);
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