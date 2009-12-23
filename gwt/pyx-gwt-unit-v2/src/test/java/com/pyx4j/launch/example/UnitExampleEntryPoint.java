/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id: UnitExampleEntryPoint.java 4436 2009-12-22 08:45:29Z vlads $
 */
package com.pyx4j.launch.example;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.pyx4j.unit.ui.TestRunnerDialog;

public class UnitExampleEntryPoint implements EntryPoint {

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
