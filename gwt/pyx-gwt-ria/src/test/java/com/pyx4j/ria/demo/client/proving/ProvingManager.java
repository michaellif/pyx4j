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

import com.pyx4j.ria.client.app.IApplication;
import com.pyx4j.widgets.client.menu.ActionMenuItem;
import com.pyx4j.widgets.client.menu.Menu;

public class ProvingManager {

    public static void createMenu(Menu provingMenuBar, final IApplication app) {

        provingMenuBar.addItem(new ActionMenuItem("Logger", new Command() {
            @Override
            public void execute() {
                app.openView(new LogRangeView());
            }
        }));

        provingMenuBar.addItem(new ActionMenuItem("StatusBar", new Command() {
            @Override
            public void execute() {
                app.openView(new StatusBarProvingView());
            }
        }));

        provingMenuBar.addItem(new ActionMenuItem("ui - Button Range", new Command() {
            @Override
            public void execute() {
                app.openView(new ButtonRangeView());
            }
        }));

    }
}
