/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.tester.unit;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.propertyvista.portal.tester.TesterDebugId;

public class TestDeferredCommands extends FlowPanel {

    private final Label messageCheck;

    private final Label messageStatus;

    private String runningStatus = "Not started";

    private StringBuilder executionSeq = new StringBuilder();

    private Timer timer;

    public TestDeferredCommands() {

        Button start = new Button("Start Deferred Process");
        start.ensureDebugId(TesterDebugId.DeferredStartProcess.name());
        start.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                executionSeq = new StringBuilder();
                messageCheck.setText("");
                messageStatus.setText("Started");
                runningStatus = "started";
                executionSeq.append("started,");
                long start = System.currentTimeMillis();

                for (int i = 0; i < 5000000; i++) {
                    runningStatus = "running " + i;
                    Random.nextInt(1000);
                }

                messageStatus.setText("Ended");
                runningStatus = "ended";
                executionSeq.append("ended,");

                Scheduler.get().scheduleEntry(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        executionSeq.append("scheduleEntry,");
                    }
                });

                Scheduler.get().scheduleFinally(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        executionSeq.append("scheduleFinally,");

                    }
                });

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        executionSeq.append("scheduleDeferred,");
                    }
                });
            }
        });
        this.add(start);

        messageStatus = new Label();
        messageStatus.ensureDebugId(TesterDebugId.DeferredMessageStatus.debugId());
        this.add(messageStatus);

        Button checkStatus = new Button("Check Status");
        checkStatus.ensureDebugId(TesterDebugId.DeferredCheckStatus.name());
        checkStatus.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                executionSeq.append("checked,");
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        executionSeq.append("scheduleChecked,");
                    }
                });
            }
        });
        this.add(checkStatus);

        messageCheck = new Label();
        messageCheck.ensureDebugId(TesterDebugId.DeferredMessageCheck.debugId());
        this.add(messageCheck);

    }

    @Override
    protected void onLoad() {
        timer = new Timer() {

            @Override
            public void run() {
                messageCheck.setText(executionSeq.toString());
            }
        };
        timer.scheduleRepeating(100);
    }

    @Override
    protected void onUnload() {
        timer.cancel();
    }

}
