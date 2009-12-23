/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author vlads
 * @version $Id: TestRunnerDialog.java 4443 2009-12-22 19:32:06Z vlads $
 */
package com.pyx4j.unit.client.ui;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.unit.client.GCaseMeta;
import com.pyx4j.unit.client.GCaseResultAsyncCallback;
import com.pyx4j.unit.client.GResult;
import com.pyx4j.unit.client.GUnitTester;
import com.pyx4j.unit.client.TestAwareExceptionHandler;

/**
 * Initial GUI for tests, require Additional GWT module 'com.pyx4j.Unit'.
 */
public class TestRunnerDialog extends DialogBox {

    private final FlexTable testsPanel;

    private final List<TestInfo> testInfo = new Vector<TestInfo>();

    private final CheckBox checkAll;

    private final Label failedList;

    private final Label statusRun;

    private final Label statusFailed;

    private final Label statusSuccess;

    private final Label statusDuration;

    public TestRunnerDialog() {
        super.setText("Unit Tests");

        VerticalPanel dialogVPanel = new VerticalPanel();

        testsPanel = new FlexTable();
        testsPanel.setCellSpacing(5);
        testsPanel.setCellPadding(3);
        testsPanel.setWidget(0, 0, new Label());
        testsPanel.setWidget(0, 1, new Label("Class"));
        testsPanel.setWidget(0, 2, new Label("Test"));
        testsPanel.setWidget(0, 3, new Label("Result"));
        testsPanel.setWidget(0, 4, new Label("Duration (Millis)"));

        buildTestList();
        dialogVPanel.add(testsPanel);

        //TODO
        //scrollPanel.setSize("700px", "400px");

        HorizontalPanel buttonsPanel = new HorizontalPanel();

        checkAll = new CheckBox("All");
        checkAll.ensureDebugId("gUnitAll");
        checkAll.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                checkAll();
            }
        });
        buttonsPanel.insert(checkAll, 0);
        HorizontalPanel statusPanel = new HorizontalPanel();
        statusPanel.setWidth("400px");
        statusPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        DOM.setStyleAttribute(statusPanel.getElement(), "paddingRight", "40px");

        statusRun = new Label();
        statusRun.ensureDebugId("gUnitRunning");
        statusPanel.add(statusRun);

        statusDuration = new Label();
        statusPanel.add(new Label("Duration:"));
        statusDuration.ensureDebugId("gUnitDuration");
        statusPanel.add(statusDuration);

        statusSuccess = new Label();
        statusSuccess.ensureDebugId("gUnitSuccess");
        setColor(statusSuccess, colorOk);
        statusPanel.add(new Label("Success:"));
        statusPanel.add(statusSuccess);

        statusFailed = new Label();
        statusFailed.ensureDebugId("gUnitFailed");
        setColor(statusFailed, colorError);
        statusPanel.add(new Label("Failed:"));
        statusPanel.add(statusFailed);

        buttonsPanel.insert(statusPanel, 0);

        failedList = new Label();
        failedList.ensureDebugId("gUnitFailedList");
        failedList.setVisible(false);
        ///TODO
        ///failedList.setVisible(ClientState.isSeleniumMode());
        buttonsPanel.insert(failedList, 0);

        final Button runButton = new Button("Run");
        buttonsPanel.add(runButton);
        runButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                runSelectedTests();
            }
        });

        final Button closeButton = new Button("Close");
        buttonsPanel.add(closeButton);
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });

        dialogVPanel.add(buttonsPanel);

        // Indirect reference so main UncaughtHandler can compile without 'Unit'
        GUnitTester.setTestAwareExceptionHandler(new TestAwareExceptionHandler() {

            @Override
            public void delegateExceptionHandler(UncaughtExceptionHandler testHandler) {
                //TODO
                //UncaughtHandler.delegateExceptionHandler(testHandler);
            }
        });

        statusRun.setText("Ready");

        super.setWidget(dialogVPanel);
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
                dialogBox.center();
                dialogBox.show();
            }

        });
    }

    private void checkAll() {
        for (TestInfo t : testInfo) {
            t.checkBox.setValue(checkAll.getValue());
        }
    }

    static String colorRunning = "#3300FF";

    static String colorOk = "#009900";

    static String colorError = "#FF3300";

    private long runStart;

    private int runningCount;

    private int totalTestsSelected;

    private int totalTestsSuccess;

    private int totalTestsError;

    public void runSelectedTests() {
        //TODO
        //Message.info("Starting tests...");
        List<TestInfo> testQueue = new Vector<TestInfo>();
        for (final TestInfo t : testInfo) {
            if (!t.checkBox.getValue()) {
                continue;
            }
            t.reset();
            testQueue.add(t);
        }
        totalTestsSelected = testQueue.size();
        runStart = System.currentTimeMillis();
        runningCount = 0;
        totalTestsSuccess = 0;
        totalTestsError = 0;
        setColor(statusRun, colorRunning);
        statusFailed.setText("0");
        setColor(statusFailed, colorOk);
        statusSuccess.setText("0");
        failedList.setText("");

        runNextTest(testQueue);
    }

    private void setColor(Label result, String color) {
        DOM.setStyleAttribute(result.getElement(), "color", color);
    }

    private void onCompleted() {
        statusRun.setText("Completed");
        if (totalTestsError == 0) {
            if (totalTestsSuccess > 0) {
                setColor(statusRun, colorOk);
            }
        } else {
            setColor(statusRun, colorError);
        }
    }

    private void runNextTest(final List<TestInfo> testQueue) {
        statusDuration.setText(String.valueOf(System.currentTimeMillis() - runStart));
        if (testQueue.size() == 0) {
            //TODO
            //Logger.info("Tests completed");
            onCompleted();
            return;
        }
        runningCount++;
        statusRun.setText("Running " + runningCount + "of " + totalTestsSelected);

        final TestInfo t = testQueue.remove(0);
        t.result.setText("Running ...");
        setColor(t.result, colorRunning);
        t.time.setText("");
        t.meta.execute(new GCaseResultAsyncCallback() {

            public void onComplete(GResult result) {
                if (result.isSuccess()) {
                    t.result.setText("Ok");
                    setColor(t.result, colorOk);
                    totalTestsSuccess++;
                    statusSuccess.setText(String.valueOf(totalTestsSuccess));
                } else {
                    t.message.setText(result.getMessage());
                    t.result.setText("Failed");
                    setColor(t.result, colorError);
                    totalTestsError++;
                    statusFailed.setText(String.valueOf(totalTestsError));
                    setColor(statusFailed, colorError);
                    failedList.setText(failedList.getText() + "; " + t.getFullName() + " [" + result.getMessage() + "]");
                }
                t.time.setText(String.valueOf(result.getDuration()));
                runNextTest(testQueue);
            }
        });
    }

    private class TestInfo {

        Class<? extends TestCase> gCase;

        GCaseMeta meta;

        CheckBox checkBox;

        Label result;

        Label time;

        Label message;

        TestInfo(Class<? extends TestCase> c, GCaseMeta meta) {
            this.gCase = c;
            this.meta = meta;
            int numRows = testsPanel.getRowCount();
            checkBox = new CheckBox();
            testsPanel.setWidget(numRows, 0, checkBox);
            testsPanel.setWidget(numRows, 1, new Label(GWTJava5Helper.getSimpleName(c)));
            testsPanel.setWidget(numRows, 2, new Label(meta.getName()));
            testsPanel.setWidget(numRows, 3, result = new Label(""));
            testsPanel.setWidget(numRows, 4, time = new Label(""));
            DOM.setStyleAttribute(result.getElement(), "cursor", "hand");

            testsPanel.setWidget(numRows + 1, 1, message = new Label(""));
            message.setVisible(false);
            setColor(message, colorError);
            testsPanel.getFlexCellFormatter().setColSpan(numRows + 1, 1, 4);

            result.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    message.setVisible(!message.isVisible());
                }
            });

        }

        String getFullName() {
            return gCase.getName() + "#" + meta.getName();
        }

        void reset() {
            result.setText("");
            time.setText("");
            message.setText("");
            setColor(result, "");
            message.setVisible(false);
        }

    }

    public void buildTestList() {
        for (Class<? extends TestCase> c : GUnitTester.getAllCases()) {
            for (GCaseMeta meta : GUnitTester.getClassMeta(c)) {
                testInfo.add(new TestInfo(c, meta));
            }
        }
    }

}
