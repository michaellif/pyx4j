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
 * Created on Oct 3, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.old;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.tester.client.images.Images;
import com.pyx4j.tester.client.theme.TesterPalette;
import com.pyx4j.tester.client.theme.TesterTheme;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.GlassPanel.GlassStyle;
import com.pyx4j.widgets.client.dashboard.Dashboard;
import com.pyx4j.widgets.client.dashboard.Reportboard;
import com.pyx4j.widgets.client.datepicker.DatePickerComposite;
import com.pyx4j.widgets.client.dialog.Custom1Option;
import com.pyx4j.widgets.client.dialog.Custom2Option;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.GlueOption;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.UnrecoverableErrorHandlerDialog;
import com.pyx4j.widgets.client.dialog.YesNoCancelOption;
import com.pyx4j.widgets.client.richtext.RichTextEditorDecorator;

public class TesterEntryPoint implements EntryPoint {

    private static final Logger log = LoggerFactory.getLogger(TesterEntryPoint.class);

    public TesterEntryPoint() {
        UnrecoverableErrorHandlerDialog.register();
        ClientLogger.setDebugOn(true);
    }

    @Override
    public void onModuleLoad() {

        // Hack :)
        new AppSite("tester", null, null) {
            @Override
            public void onSiteLoad() {
            }
        }.hideLoadingIndicator();

        StyleManager.installTheme(new TesterTheme(), new TesterPalette());

        ClientLogger.setDebugOn(true);
        ClientLogger.setTraceOn(true);
        UnrecoverableErrorHandlerDialog.register();

        VerticalPanel contentPanel = new VerticalPanel();

        RootPanel.get().add(GlassPanel.instance());
        RootPanel.get().add(contentPanel);
        contentPanel.setWidth("100%");

        //========== Report ==========//
        contentPanel.add(new HTML("<b>Report Test:</b>"));
        Reportboard report = new Reportboard();
        fillReport(report);
        contentPanel.add(report);

        //========== Dashboard ==========//
        contentPanel.add(new HTML("&nbsp"));
        contentPanel.add(new HTML("<b>Dashboard Test:</b>"));
        Dashboard dashboard = new Dashboard();
        fillDashboard(dashboard);
        contentPanel.add(dashboard);

        //=================================//

        final Button sendButton = new Button("Send");
        final TextBox pageNameTextBox = new TextBox();
        pageNameTextBox.setText("page1");

        RichTextArea pageEditor = new RichTextArea();

        RichTextEditorDecorator editorDecorator = new RichTextEditorDecorator(pageEditor);

        TextArea htmlViewer = new TextArea();

        sendButton.addClickHandler(new MyHandler(htmlViewer, pageEditor));

        {
            final TextBox textBox = new TextBox();
            LogicalDate minDate = new LogicalDate(0, 1, 15);
            LogicalDate maxDate = new LogicalDate(111, 3, 3);
            LogicalDate starting = new LogicalDate(110, 2, 10);
            ArrayList<LogicalDate> disabledDates = new ArrayList<LogicalDate>();
            disabledDates.add(new LogicalDate(103, 1, 1));
            disabledDates.add(new LogicalDate(103, 1, 2));
            disabledDates.add(new LogicalDate(103, 1, 3));
            disabledDates.add(new LogicalDate(103, 1, 4));
            disabledDates.add(new LogicalDate(103, 1, 5));

            DatePickerComposite datePicker = new DatePickerComposite(1, starting, minDate, maxDate, disabledDates);
            datePicker.addDateChosenEventHandler(new DatePickerComposite.DateChosenEventHandler() {

                @Override
                public void onDateChosen(DatePickerComposite.DateChosenEvent event) {
                    String date = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(event.getChosenDate());
                    textBox.setText(date);
                }

            });
            contentPanel.add(datePicker);
            contentPanel.add(textBox);

        }

        {

            com.pyx4j.widgets.client.Button buttonTest2 = new com.pyx4j.widgets.client.Button(Images.INSTANCE.collapse(), "Test");
            contentPanel.add(buttonTest2);
        }

        contentPanel.add(pageNameTextBox);
        contentPanel.add(editorDecorator);
        contentPanel.add(sendButton);
        contentPanel.add(htmlViewer);

        contentPanel.add(new CheckBox());

        {
            final Button dialogButton = new Button("Show Dialog1");
            dialogButton.addClickHandler(new DialogButtonHandler1());
            contentPanel.add(dialogButton);
        }

        {
            final Button dialogButton = new Button("Show Dialog2");
            dialogButton.addClickHandler(new DialogButtonHandler2());
            contentPanel.add(dialogButton);
        }

        {
            final Button button = new Button("Key Test Dialog");
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    KeysTestDialog.show();
                }
            });
            contentPanel.add(button);
        }

        {
            final Button button = new Button("Image from text");
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new Base64ImageView().show();
                }
            });
            contentPanel.add(button);
        }

        {
            final Button button = new Button("Throw Unhandled Error");
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    throw new Error("Unhandled problem");
                }
            });
            contentPanel.add(button);
        }

        {
            final Button button = new Button("Throw NPE");
            button.addClickHandler(new ClickHandler() {

                @SuppressWarnings("null")
                @Override
                public void onClick(ClickEvent event) {
                    ClickHandler ch = null;
                    ch.onClick(null);
                }
            });
            contentPanel.add(button);
        }

        {
            Button buttonGlass = new Button("Glass  SemiTransparent ON (15 sec)");
            contentPanel.add(buttonGlass);
            buttonGlass.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    showGlassPanel(GlassStyle.SemiTransparent, 15);
                };
            });
        }

        {
            Button buttonGlass = new Button("Glass Transparent ON (15 sec)");
            contentPanel.add(buttonGlass);
            buttonGlass.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    showGlassPanel(GlassStyle.Transparent, 15);
                };
            });
        }
        {
            com.pyx4j.widgets.client.Button buttonTest1 = new com.pyx4j.widgets.client.Button("Test");
            contentPanel.add(buttonTest1);

            com.pyx4j.widgets.client.Button buttonTest2 = new com.pyx4j.widgets.client.Button(Images.INSTANCE.collapse(), "Test");
            contentPanel.add(buttonTest2);
        }

    }

    private static void showGlassPanel(GlassStyle glassStyle, int sec) {
        log.info("glassPanel.show");
        GlassPanel.show(glassStyle, "testing...");
        Timer timer = new Timer() {
            @Override
            public void run() {
                GlassPanel.hide();
                log.info("glassPanel.hide");
            }
        };
        timer.schedule(1000 * sec);
    }

    class MyHandler implements ClickHandler {

        private final TextArea htmlViewer;

        private final RichTextArea pageEditor;

        MyHandler(TextArea htmlViewer, RichTextArea pageEditor) {
            this.htmlViewer = htmlViewer;
            this.pageEditor = pageEditor;
        }

        @Override
        public void onClick(ClickEvent event) {
            htmlViewer.setText(pageEditor.getHTML());
        }
    }

    class DialogButtonHandler1 implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            MessageDialog.info("TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest");
        }
    }

    interface Options2 extends YesNoCancelOption, Custom1Option, Custom2Option, GlueOption {

    }

    class DialogButtonHandler2 implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            Dialog dialog = new Dialog("Caption2", new Options2() {

                @Override
                public boolean onClickCancel() {
                    log.info("onClickCancel");
                    return true;
                }

                @Override
                public boolean onClickNo() {
                    log.info("onClickNo");
                    return true;
                }

                @Override
                public boolean onClickYes() {
                    log.info("onClickYes");
                    MessageDialog.info("Yes");
                    return false;
                }

                @Override
                public String custom1Text() {
                    return "Glass ON (15 sec)";
                }

                @Override
                public boolean onClickCustom1() {
                    log.info("onClickCustom1");
                    showGlassPanel(GlassStyle.SemiTransparent, 15);
                    return false;
                }

                @Override
                public String custom2Text() {
                    return "Custom2";
                }

                @Override
                public boolean onClickCustom2() {
                    log.info("onClickCustom2");
                    return false;
                }

                @Override
                public IDebugId getCustom2DebugID() {
                    return null;
                }

                @Override
                public IDebugId getCustom1DebugID() {
                    return null;
                }
            }, null);

            VerticalPanel inputPanel = new VerticalPanel();

            inputPanel.add(createLabled("Text Box :", new TextBox()));
            inputPanel.add(createLabled("Check Box:", new CheckBox()));
            inputPanel.add(createLabled("Text Area:", new TextArea()));

            inputPanel.setSize("400px", "200px");

            dialog.setBody(inputPanel);
            dialog.show();
        }

    }

    private static Widget createLabled(String lable, Widget widget) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new Label(lable));
        panel.add(widget);
        return panel;
    }

    private void fillReport(Reportboard report) {

        int count = 0;
        for (int row = 0; row < 7; ++row) {
            DemoGadget widget = new DemoGadget("&nbsp;Gadget&nbsp;#" + ++count);
            widget.setHeight(Random.nextInt(10) + 1 + "em");
            report.addGadget(widget, (row % 2 > 0 ? Reportboard.Location.Full : Reportboard.Location.Left));
        }
    }

    private void fillDashboard(Dashboard dashboard) {

        int count = 0;
        for (int col = 0; col < dashboard.getLayout().columns(); ++col)
            for (int row = 0; row < 3; ++row) {
                // initialize a widget
                DemoGadget widget = new DemoGadget("&nbsp;Gadget&nbsp;#" + ++count);
                widget.setHeight(Random.nextInt(8) + 2 + "em");
                dashboard.addGadget(widget, col);
            }
    }
}
