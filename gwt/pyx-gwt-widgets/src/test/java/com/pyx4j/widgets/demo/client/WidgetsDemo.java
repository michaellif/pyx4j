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
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.GlassPanel.GlassStyle;
import com.pyx4j.widgets.client.dialog.Custom1Option;
import com.pyx4j.widgets.client.dialog.Custom2Option;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.GlueOption;
import com.pyx4j.widgets.client.dialog.UnrecoverableErrorHandlerDialog;
import com.pyx4j.widgets.client.dialog.YesNoCancelOption;
import com.pyx4j.widgets.client.photoalbum.BasicPhotoAlbumModel;
import com.pyx4j.widgets.client.photoalbum.Photo;
import com.pyx4j.widgets.client.photoalbum.PhotoAlbum;
import com.pyx4j.widgets.client.richtext.RichTextEditorDecorator;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.window.WindowsTheme;
import com.pyx4j.widgets.demo.client.images.TestImages;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetsDemo implements EntryPoint {

    private static final Logger log = LoggerFactory.getLogger(WidgetsDemo.class);

    private static TestImages bundle = GWT.create(TestImages.class);

    public void onModuleLoad() {

        StyleManger.installTheme(new WindowsTheme());
        ClientLogger.setDebugOn(true);
        ClientLogger.setTraceOn(true);
        UnrecoverableErrorHandlerDialog.register();

        VerticalPanel contentPanel = new VerticalPanel();

        RootPanel.get().add(GlassPanel.instance());
        RootPanel.get().add(contentPanel);

        final Button sendButton = new Button("Send");
        final TextBox pageNameTextBox = new TextBox();
        pageNameTextBox.setText("page1");

        RichTextArea pageEditor = new RichTextArea();

        RichTextEditorDecorator editorDecorator = new RichTextEditorDecorator(pageEditor);

        TextArea htmlViewer = new TextArea();

        sendButton.addClickHandler(new MyHandler(htmlViewer, pageEditor));

        {
            PhotoAlbum photoAlbum = new PhotoAlbum() {

                @Override
                public void addPhotoCommand() {
                    getPhotoAlbumModel().addPhoto(
                            new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo#"));
                }

                @Override
                public void updateCaptionCommand(int index) {
                    getPhotoAlbumModel().updateCaption(index, "EDITED " + System.currentTimeMillis());
                }

            };

            photoAlbum.setWidth("700px");
            photoAlbum.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            photoAlbum.getElement().getStyle().setBorderColor("black");
            photoAlbum.getElement().getStyle().setBorderWidth(2, Unit.PX);
            final BasicPhotoAlbumModel model = new BasicPhotoAlbumModel();
            photoAlbum.setPhotoAlbumModel(model);

            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo1"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh4.ggpht.com/_FD9tLNw_5yE/Szyrc_7QzfI/AAAAAAAAC_k/IHpmCERsqko/s576/IMG_4118.JPG", "Photo2"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo3"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh4.ggpht.com/_FD9tLNw_5yE/Szyrc_7QzfI/AAAAAAAAC_k/IHpmCERsqko/s576/IMG_4118.JPG", "Photo4"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo5"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh4.ggpht.com/_FD9tLNw_5yE/Szyrc_7QzfI/AAAAAAAAC_k/IHpmCERsqko/s576/IMG_4118.JPG", "Photo6"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo7"));

            contentPanel.add(photoAlbum);
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

            com.pyx4j.widgets.client.Button buttonTest2 = new com.pyx4j.widgets.client.Button(new Image(bundle.groupBoxClose()), "Test");
            contentPanel.add(buttonTest2);
        }

    }

    private static void showGlassPanel(GlassStyle glassStyle, int sec) {
        log.info("glassPanel.show");
        GlassPanel.show(glassStyle);
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
                    Dialog dialog = new Dialog("Yes");
                    dialog.show();
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
            });

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

}
