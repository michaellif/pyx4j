/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-01-31
 * @author VladLL
 * @version $Id$
 */
package com.pyx4j.dashboard.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.dashboard.client.DashboardPanel;
import com.pyx4j.dashboard.client.Layout;
import com.pyx4j.widgets.client.style.StyleManger;

/**
 * EntryPoint class for demonstrating and testing gwt-dnd.
 */
public final class DashboardDemo implements EntryPoint {

    private static final String MAIN_PANEL = "main-panel";

    private static final String CSS_DASHBOARD_WRAPPER = "Dashboard-wrapper";

    protected static final String CSS_DASHBOARD_MENU = "Dashboard-menu";

    private static final String CSS_DASHBOARD_CAPTION = "Dashboard-caption";

    private DashboardPanel dashboardPanel;

    @Override
    public void onModuleLoad() {
        StyleManger.installTheme(new DashboardTheme(), new DashboardPalette());

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

        FlowPanel dashboardWrapper = new FlowPanel();
        dashboardWrapper.addStyleName(CSS_DASHBOARD_WRAPPER);
        dashboardWrapper.setWidth("100%");
        mainPanel.add(dashboardWrapper);

        Layout layout = new Layout(3, 1, 12);

        // uncomment for captioned columns:     
        byte colWidths[] = { 30, 50, 20 };
        layout.setColumnWidths(colWidths);

//        // uncomment for captioned columns: layout.useColumnNames = true; String
//        String colNames[] = { "one", "two", "three" };
//        layout.setColumnNames(colNames);

        dashboardPanel = new DashboardPanel(layout);

        HorizontalPanel dashboardCaption = new HorizontalPanel();
        dashboardCaption.add(new Label());
        dashboardCaption.setCellWidth(dashboardCaption.getWidget(dashboardCaption.getWidgetCount() - 1), "90%");
        dashboardCaption.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        dashboardCaption.add(createDashboardMenu(dashboardPanel));
        dashboardCaption.setCellWidth(dashboardCaption.getWidget(dashboardCaption.getWidgetCount() - 1), "10%");
        dashboardCaption.addStyleName(CSS_DASHBOARD_CAPTION);
        dashboardCaption.setWidth("100%");
        dashboardWrapper.add(dashboardCaption);

        dashboardPanel.setWidth("100%");
        dashboardWrapper.add(dashboardPanel);

        fillDashboard(false);
    }

    private void fillDashboard(boolean useSubRows) {

        // fill the dashboard with demo widgets:
        dashboardPanel.removeAllGadgets();

        int count = 0;
        for (int col = 0; col < dashboardPanel.getLayout().getColumns(); ++col)
            for (int row = 0; row < (useSubRows ? 7 : 4); ++row) {
                // initialize a widget
                DemoGadget widget = new DemoGadget("&nbsp;Gadget&nbsp;#" + ++count);
                widget.setHeight(Random.nextInt(8) + 2 + "em");
                if (useSubRows) {
                    widget.setFullWidth(row % 2 > 0);
                }
                //          dashboardPanel.insertWidget(widget, col, row);
                dashboardPanel.addGadget(widget, col);
            }
    }

    private Widget createDashboardMenu(final DashboardPanel dashboardPanel) {
        final Button btn = new Button("Layout...");
        btn.addClickHandler(new ClickHandler() {
            private final PopupPanel pp = new PopupPanel(true);

            @Override
            public void onClick(ClickEvent event) {
                // menu items command processors go here:
                Command cmdL1 = new Command() {
                    @Override
                    public void execute() {
                        pp.hide();

                        Layout layout = new Layout(1, 2, 12);
                        dashboardPanel.setLayout(layout);
                    }
                };

                Command cmdL21 = new Command() {
                    @Override
                    public void execute() {
                        pp.hide();

                        Layout layout = new Layout(2, 1, 12);
                        byte colWidths[] = { 33, 67 };
                        layout.setColumnWidths(colWidths);
                        dashboardPanel.setLayout(layout);
                    }
                };

                Command cmdL22 = new Command() {
                    @Override
                    public void execute() {
                        pp.hide();

                        Layout layout = new Layout(2, 1, 12);
                        byte colWidths[] = { 67, 33 };
                        layout.setColumnWidths(colWidths);
                        dashboardPanel.setLayout(layout);
                    }
                };

                Command cmdL23 = new Command() {
                    @Override
                    public void execute() {
                        pp.hide();

                        Layout layout = new Layout(2, 1, 12);
                        byte colWidths[] = { 50, 50 };
                        layout.setColumnWidths(colWidths);
                        dashboardPanel.setLayout(layout);
                    }
                };

                Command cmdL3 = new Command() {
                    @Override
                    public void execute() {
                        pp.hide();

                        Layout layout = new Layout(3, 1, 12);
                        dashboardPanel.setLayout(layout);
                    }
                };

                Command cmdLr = new Command() {
                    @Override
                    public void execute() {
                        pp.hide();

                        dashboardPanel.clear();

                        Layout layout = new Layout(1, 2, 12);
                        dashboardPanel.setLayout(layout);

                        fillDashboard(true);
                        btn.removeFromParent();
                    }
                };

                // create the menu:
                MenuBar menu = new MenuBar(true);
                menu.addItem("One columns", cmdL1);
                menu.addItem("Two columns(1/3)", cmdL21);
                menu.addItem("Two columns (3/1)", cmdL22);
                menu.addItem("Two equal columns", cmdL23);
                menu.addItem("Three equal columns", cmdL3);
                menu.addSeparator();
                menu.addItem("<b>One-two gadgets per row...</b>", true, cmdLr);

                menu.addStyleName(CSS_DASHBOARD_MENU);

                pp.setWidget(menu);
                pp.setPopupPosition(btn.getAbsoluteLeft() - 30, btn.getAbsoluteTop() + btn.getOffsetHeight());
                pp.show();
            } // onClick button event handler...
        }); // ClickHandler class...

        //        btn.addStyleName(CSS_DASHBOARD_PANEL_HOLDER_MENU_BUTTON);
        return btn;
    }
}
