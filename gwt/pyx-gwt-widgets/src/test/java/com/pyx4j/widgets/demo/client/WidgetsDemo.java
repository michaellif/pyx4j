/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.widgets.client.dialog.Custom1Option;
import com.pyx4j.widgets.client.dialog.Custom2Option;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.GlueOption;
import com.pyx4j.widgets.client.dialog.YesNoCancelOption;
import com.pyx4j.widgets.client.richtext.RichTextEditorDecorator;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetsDemo implements EntryPoint {

    final Logger log = LoggerFactory.getLogger(WidgetsDemo.class);

    public void onModuleLoad() {

        StyleManger.installTheme(new WindowsTheme());
        ClientLogger.setDebugOn(true);

        VerticalPanel contentPanel = new VerticalPanel();
        RootPanel.get().add(contentPanel);

        final Button sendButton = new Button("Send");
        final TextBox pageNameTextBox = new TextBox();
        pageNameTextBox.setText("page1");

        RichTextArea pageEditor = new RichTextArea();

        RichTextEditorDecorator editorDecorator = new RichTextEditorDecorator(pageEditor);

        TextArea htmlViewer = new TextArea();

        sendButton.addClickHandler(new MyHandler(htmlViewer, pageEditor));

        contentPanel.add(pageNameTextBox);
        contentPanel.add(editorDecorator);
        contentPanel.add(sendButton);
        contentPanel.add(htmlViewer);

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

    }

    class MyHandler implements ClickHandler {

        private final TextArea htmlViewer;

        private final RichTextArea pageEditor;

        MyHandler(TextArea htmlViewer, RichTextArea pageEditor) {
            this.htmlViewer = htmlViewer;
            this.pageEditor = pageEditor;
        }

        public void onClick(ClickEvent event) {
            htmlViewer.setText(pageEditor.getHTML());
        }
    }

    class DialogButtonHandler1 implements ClickHandler {

        public void onClick(ClickEvent event) {
            Dialog dialog = new Dialog("TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest");
            dialog.show();
        }
    }

    interface Options2 extends YesNoCancelOption, Custom1Option, Custom2Option, GlueOption {

    }

    class DialogButtonHandler2 implements ClickHandler {

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
                    return true;
                }

                @Override
                public String custom1Text() {
                    return "Custom1";
                }

                @Override
                public boolean onClickCustom1() {
                    log.info("onClickCustom1");
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
            });

            VerticalPanel inputPanel = new VerticalPanel();

            inputPanel.add(createLabled("Text Box :", new TextBox()));
            inputPanel.add(createLabled("Check Box:", new CheckBox()));
            inputPanel.add(createLabled("Text Area:", new TextArea()));

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

}
