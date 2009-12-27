/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.dialog.Custom1Option;
import com.pyx4j.widgets.client.dialog.Custom2Option;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.GlueOption;
import com.pyx4j.widgets.client.dialog.YesNoCancelOption;
import com.pyx4j.widgets.client.dialog.Dialog.Type;
import com.pyx4j.widgets.client.richtext.RichTextEditorDecorator;
import com.pyx4j.widgets.client.style.StyleManger;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetsDemo implements EntryPoint {

    public void onModuleLoad() {

        StyleManger.installDefaultTheme();

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
            Dialog dialog = new Dialog("TestTestTestTestTe");
            dialog.show();
        }
    }

    interface Options2 extends YesNoCancelOption, Custom1Option, Custom2Option, GlueOption {

    }

    class DialogButtonHandler2 implements ClickHandler {

        public void onClick(ClickEvent event) {
            Dialog dialog = new Dialog("Caption2", "Test2Test2", Type.Error, new Options2() {

                @Override
                public boolean onClickCancel() {
                    // TODO Auto-generated method stub
                    return true;
                }

                @Override
                public boolean onClickNo() {
                    // TODO Auto-generated method stub
                    return true;
                }

                @Override
                public boolean onClickYes() {
                    // TODO Auto-generated method stub
                    return true;
                }

                @Override
                public String custom1Text() {
                    return "Custom1";
                }

                @Override
                public boolean onClickCustom1() {
                    return false;
                }

                @Override
                public String custom2Text() {
                    return "Custom2";
                }

                @Override
                public boolean onClickCustom2() {
                    return false;
                }
            });
            dialog.show();
        }
    }

}
