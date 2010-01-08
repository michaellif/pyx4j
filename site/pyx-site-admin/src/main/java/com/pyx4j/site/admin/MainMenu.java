/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.admin;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkOption;
import com.pyx4j.widgets.client.dialog.Dialog.Type;

//TODO import com.pyx4j.client.log.Logger;

public class MainMenu extends MenuBar {

    public MainMenu(final AdminApplication app) {

        MenuBar fileMenuBar = new MenuBar(true);
        addItem(new MenuItem("File", fileMenuBar));

        fileMenuBar.addItem(new MenuItem("Save", app.getSaveCommand()));

        MenuBar helpMenuBar = new MenuBar(true);
        addItem(new MenuItem("Help", helpMenuBar));

        helpMenuBar.addItem(new MenuItem("About", new Command() {
            @Override
            public void execute() {
                Dialog aboutDialog = new Dialog("About", "TODO About", Type.Info, new OkOption() {

                    @Override
                    public boolean onClickOk() {
                        return true;
                    }
                });
                aboutDialog.show();
            }
        }));

    }

}