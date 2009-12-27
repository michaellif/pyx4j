/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on May 16, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.ria.demo.client.proving;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.ria.client.IApplication;

public class ProvingManager {

    public static void createMenu(MenuBar provingMenuBar, final IApplication app) {

        provingMenuBar.addItem(new MenuItem("Logger", new Command() {
            @Override
            public void execute() {
                app.openView(new LogRangeView());
            }
        }));

        provingMenuBar.addItem(new MenuItem("StatusBar", new Command() {
            @Override
            public void execute() {
                app.openView(new StatusBarProvingView());
            }
        }));

        provingMenuBar.addItem(new MenuItem("ui - Button Range", new Command() {
            @Override
            public void execute() {
                app.openView(new ButtonRangeView());
            }
        }));

    }
}
