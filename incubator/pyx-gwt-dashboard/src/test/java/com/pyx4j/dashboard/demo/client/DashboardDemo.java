package com.pyx4j.dashboard.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.dashboard.client.DashboardPanel;

/**
 * EntryPoint class for demonstrating and testing gwt-dnd.
 */
public final class DashboardDemo implements EntryPoint {

    private static final String MAIN_PANEL = "main-panel";

    @Override
    public void onModuleLoad() {
        // set uncaught exception handler
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {

            @Override
            public void onUncaughtException(Throwable throwable) {
                String text = "Uncaught exception: ";
                while (throwable != null) {
                    StackTraceElement[] stackTraceElements = throwable.getStackTrace();
                    text += throwable.toString() + "\n";
                    for (StackTraceElement element : stackTraceElements) {
                        text += "    at " + element + "\n";
                    }
                    throwable = throwable.getCause();
                    if (throwable != null) {
                        text += "Caused by: ";
                    }
                }

                DialogBox dialogBox = new DialogBox(true, false);
                dialogBox.getElement().getStyle().setProperty("backgroundColor", "#ABCDEF");
                System.err.print(text);
                text = text.replaceAll(" ", "&nbsp;");
                dialogBox.setHTML("<pre>" + text + "</pre>");
                dialogBox.center();
            }
        });

        // use a deferred command so that the handler catches onModuleLoad2() exceptions
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                onModuleLoad2();
            }
        });
    }

    private void onModuleLoad2() {
        RootPanel mainPanel = RootPanel.get(MAIN_PANEL);
        DOM.setInnerHTML(mainPanel.getElement(), "");

        DashboardPanel.Layout layout = new DashboardPanel.Layout(3, 8, 4);

        // uncomment for captioned columns:     
        byte colWidths[] = { 20, 30, 50 };
        layout.setColumnWidths(colWidths);

        //        // uncomment for captioned columns: layout.useColumnNames = true; String
        //        String colNames[] = { "one", "two", "three" };
        //        layout.setColumnNames(colNames);

        DashboardPanel dashboardPanel = new DashboardPanel(layout);
        dashboardPanel.setWidth("100%");
        mainPanel.add(dashboardPanel);

        // fill the dashboard with demo widgets: 
        class MyHTML extends HTML implements DashboardPanel.IWidget {
            MyHTML(String s) {
                super(s);
            }

            // info:

            @Override
            public Widget getWidget() {
                return this;
            }

            @Override
            public String getName() {
                return "MyHTML Title";
            }

            // verbs:

            @Override
            public void showSetup() {
                Window.alert("IWidget::ShowSetup() has beed called!..");
            }

            // flags:   

            @Override
            public boolean isMaximizable() {
                return true;
            }

            @Override
            public boolean isMinimizable() {
                return true;
            }

            @Override
            public boolean isSetupable() {
                return true;
            }

            // notifications:

            @Override
            public void onMaximize(boolean maximized_restored) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMinimize(boolean minimized_restored) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDelete() {
                // TODO Auto-generated method stub

            }
        }

        int count = 0;
        for (int col = 0; col < dashboardPanel.getLayout().getColumns(); ++col)
            for (int row = 0; row < 3; ++row) {
                // initialize a widget
                MyHTML widget = new MyHTML("Widget&nbsp;#" + ++count);
                widget.setHeight(Random.nextInt(8) + 2 + "em");
                //			dashboardPanel.insertWidget(widget, col, row);
                dashboardPanel.addWidget(widget, col);
            }
    }
}
