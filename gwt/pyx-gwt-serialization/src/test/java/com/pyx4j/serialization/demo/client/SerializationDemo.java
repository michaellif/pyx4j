/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.unit.client.ui.TestRunnerDialog;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

public class SerializationDemo implements EntryPoint {

    @Override
    public void onModuleLoad() {
        StyleManger.installTheme(new WindowsTheme());
        ClientLogger.setDebugOn(true);

        final Button startButton = new Button("Start Tests");
        RootPanel.get().add(startButton, 0, 0);
        startButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TestRunnerDialog.createAsync();
            }
        });
    }

}
