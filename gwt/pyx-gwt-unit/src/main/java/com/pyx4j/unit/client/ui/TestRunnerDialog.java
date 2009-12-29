/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.unit.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

import com.pyx4j.widgets.client.dialog.Dialog;

public class TestRunnerDialog extends Dialog {

    public TestRunnerDialog() {
        this(new TestRunner());
    }

    public TestRunnerDialog(TestRunner runner) {
        super("JUnit", runner, runner);
    }

    public static void createAsync() {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onFailure(Throwable reason) {
                //TODO
                //MessageDialog.error("Error", "Tests are not available", reason);
            }

            @Override
            public void onSuccess() {
                TestRunnerDialog dialogBox = new TestRunnerDialog();
                dialogBox.show();
            }

        });
    }

}
