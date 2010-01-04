/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pyx4j.unit.client.ui.TestRunnerDialog;
import com.pyx4j.widgets.client.style.StyleManger;

public class TesterClientEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {

        StyleManger.installDefaultTheme();

        VerticalPanel menu = new VerticalPanel();
        RootPanel.get().add(menu, 0, 0);

        final Button startButton = new Button("Start Client Side (GWT) Tests");
        menu.add(startButton);
        startButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TestRunnerDialog.createAsync();
            }
        });

        final Button serverStartButton = new Button("Start Server Side Tests");
        menu.add(serverStartButton);
        serverStartButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ServerTestRunner.createAsync();
            }
        });
    }

}
