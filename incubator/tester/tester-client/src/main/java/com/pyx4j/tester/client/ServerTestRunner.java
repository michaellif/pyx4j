/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 3, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.unit.shared.UnitTestInfo;
import com.pyx4j.unit.shared.UnitTestsServices;
import com.pyx4j.widgets.client.dialog.Dialog;

public class ServerTestRunner extends VerticalPanel {

    private static final Logger log = LoggerFactory.getLogger(ServerTestRunner.class);

    public static void createAsync() {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onFailure(Throwable reason) {
                log.error("Tests are not available", reason);
            }

            @Override
            public void onSuccess() {
                Dialog dialogBox = new Dialog("Server tests", new ServerTestRunner(), null);
                dialogBox.show();
            }

        });
    }

    public ServerTestRunner() {
        final HorizontalPanel buttonsPanel = new HorizontalPanel();

        this.add(buttonsPanel);

        final AsyncCallback<Vector<UnitTestInfo>> callback = new AsyncCallback<Vector<UnitTestInfo>>() {

            public void onFailure(Throwable t) {
                log.error("Got error", t);
            }

            public void onSuccess(Vector<UnitTestInfo> result) {
                log.info("got tests {}", result.size());
                for (UnitTestInfo ti : result) {
                    log.info("{} tests {}", ti.getClassName(), ti.getTestNames());
                }
            }
        };

        buttonsPanel.add(new Button("Call #1", new ClickHandler() {
            public void onClick(ClickEvent event) {
                RPCManager.execute(UnitTestsServices.GetTestsList.class, null, callback);
            }
        }));
    }
}
