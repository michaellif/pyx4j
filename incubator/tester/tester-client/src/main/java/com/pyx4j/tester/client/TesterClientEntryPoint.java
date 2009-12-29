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
import com.pyx4j.unit.client.ui.TestRunnerDialog;

public class TesterClientEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {
        final Button startButton = new Button("Start Tests");
        RootPanel.get().add(startButton, 0, 0);
        startButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TestRunnerDialog d = new TestRunnerDialog();
                d.center();
            }
        });
    }

}
