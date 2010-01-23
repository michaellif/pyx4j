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
 * Created on 26-Sep-06
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.client.dialog;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.pyx4j.widgets.client.DecoratorPanel;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.ResizibleScrollPanel;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;
import com.pyx4j.widgets.client.style.CSSClass;

/**
 * Shared implementation for Modal Dialogs
 */
public class Dialog extends DialogPanel {

    Logger log = LoggerFactory.getLogger(Dialog.class);

    public static enum Type {
        Error, Warning, Info, Confirm
    }

    private FocusWidget firstFocusWidget;

    private Button defaultButton;

    boolean allowEnterKeyForDefaultButton;

    protected final HorizontalPanel buttonsPanel;

    private Button yesButton;

    private Button noButton;

    private Button okButton;

    private Button cancelButton;

    private Button closeButton;

    private Button custom1Button;

    private Button custom2Button;

    private Button custom3Button;

    private Button custom4Button;

    private final DialogOptions options;

    private final DockPanel content;

    private FocusHandler focusHandler;

    private BlurHandler blurHandler;

    // Handle focus for Stack of Dialogs, e.g. make proper focus on dialog bellow once the one above closed. 
    private FocusWidget currentFocusWidget;

    private static final List<Dialog> openDialogs = new Vector<Dialog>();

    public Dialog(String message) {
        this("Information", message, Type.Info, new OkOption() {

            @Override
            public boolean onClickOk() {
                return true;
            }
        });

    }

    public Dialog(String caption, String message, Type type, DialogOptions options) {
        this(caption, options);
        setBody(new MessagePanel(message, type));
    }

    public Dialog(String caption, DialogOptions options) {
        super(false, true);
        setCaption(caption);

        this.options = options;

        content = new DockPanel();
        content.setHeight("100%");
        content.setWidth("100%");

        buttonsPanel = createButtonsPanel();
        content.add(buttonsPanel, DockPanel.SOUTH);
        content.setCellWidth(buttonsPanel, "100%");

        setWidget(content);
        setPixelSize(400, 300);
        center();

        this.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                if ((allowEnterKeyForDefaultButton) && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    // This does help for in Safari on Windows, 
                    // or else other dialog shown after this would catch the keyCode be automatically closed.
                    event.preventDefault();
                    defaultButton.click();
                } else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                    event.preventDefault();
                    if (cancelButton != null) {
                        cancelButton.click();
                    } else if (closeButton != null) {
                        closeButton.click();
                    }
                }
            }
        });

        setGlassEnabled(true);
    }

    public void setBody(Widget message) {
        content.add(message, DockPanel.CENTER);
        message.setSize("100%", "100%");
        content.setCellHeight(message, "100%");
    }

    private HorizontalPanel createButtonsPanel() {
        HorizontalPanel buttonsPanel = new HorizontalPanel();
        buttonsPanel.setWidth("100%");
        DOM.setStyleAttribute(buttonsPanel.getElement(), "padding", "3px");

        ClickHandler buttonListener = new ButtonListener();

        if (options instanceof Custom1Option) {
            custom1Button = createButton(((Custom1Option) options).custom1Text(), buttonListener, false);
            buttonsPanel.add(custom1Button);
        }
        if (options instanceof Custom2Option) {
            custom2Button = createButton(((Custom2Option) options).custom2Text(), buttonListener, false);
            buttonsPanel.add(custom2Button);
        }
        if (options instanceof Custom3Option) {
            custom3Button = createButton(((Custom3Option) options).custom3Text(), buttonListener, false);
            buttonsPanel.add(custom3Button);
        }
        if (options instanceof Custom4Option) {
            custom4Button = createButton(((Custom4Option) options).custom4Text(), buttonListener, false);
            buttonsPanel.add(custom4Button);
        }

        {
            Panel glue = new SimplePanel();
            glue.setWidth("100%");
            if (options instanceof GlueOption) {
                buttonsPanel.add(glue);
                Panel rigid = new SimplePanel();
                rigid.setWidth("30px");
                buttonsPanel.insert(rigid, 0);
            } else {
                buttonsPanel.insert(glue, 0);
            }
            buttonsPanel.setCellWidth(glue, "100%");
        }

        if (options instanceof YesOption) {
            yesButton = createButton("Yes", buttonListener, true);
            buttonsPanel.add(yesButton);
        }
        if (options instanceof NoOption) {
            noButton = createButton("No", buttonListener, true);
            buttonsPanel.add(noButton);
        }
        if (options instanceof OkOption) {
            okButton = createButton(optionTextOk(), buttonListener, true);
            buttonsPanel.add(okButton);
        }
        if (options instanceof CancelOption) {
            cancelButton = createButton(optionTextCancel(), buttonListener, true);
            buttonsPanel.add(cancelButton);
        }
        if (options instanceof CloseOption) {
            closeButton = createButton(optionTextClose(), buttonListener, true);
            buttonsPanel.add(closeButton);
        }

        return buttonsPanel;
    }

    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return addDomHandler(handler, KeyDownEvent.getType());
    }

    /**
     * This is alternative to using CustomOption. Override to change the text in dialog.
     * 
     * @return text for 'Ok' button
     */
    protected String optionTextOk() {
        if (options instanceof OkOptionText) {
            return ((OkOptionText) options).optionTextOk();
        } else {
            return "Ok";
        }
    }

    /**
     * This is alternative to using CustomOption. Override to change the text in dialog.
     * 
     * @return text for 'Cancel' button
     */
    protected String optionTextCancel() {
        if (options instanceof CancelOptionText) {
            return ((CancelOptionText) options).optionTextCancel();
        } else {
            return "Cancel";
        }
    }

    /**
     * This is alternative to using CustomOption. Override to change the text in dialog.
     * 
     * @return text for 'Close' button
     */
    protected String optionTextClose() {
        return "Close";
    }

    private Button createButton(String text, ClickHandler buttonListener, boolean canHaveFocus) {
        Button button = new Button(text);
        button.ensureDebugId("Dialog." + text);
        button.addClickHandler(buttonListener);
        DOM.setStyleAttribute(button.getElement(), "margin", "3px");
        DOM.setStyleAttribute(button.getElement(), "whiteSpace", "nowrap");
        if (canHaveFocus && defaultButton == null) {
            defaultButton = button;
        }
        return button;
    }

    private class ButtonListener implements ClickHandler {
        public void onClick(ClickEvent event) {
            Object sender = event.getSource();
            if (triggerOption(sender)) {
                hide();
            }
        }

    }

    private boolean triggerOption(Object sender) {
        if (sender == yesButton) {
            return ((YesOption) options).onClickYes();
        } else if (sender == noButton) {
            return ((NoOption) options).onClickNo();
        } else if (sender == okButton) {
            return ((OkOption) options).onClickOk();
        } else if (sender == cancelButton) {
            return ((CancelOption) options).onClickCancel();
        } else if (sender == closeButton) {
            return ((CloseOption) options).onClickClose();
        } else if (sender == custom1Button) {
            return ((Custom1Option) options).onClickCustom1();
        } else if (sender == custom2Button) {
            return ((Custom2Option) options).onClickCustom2();
        } else if (sender == custom3Button) {
            return ((Custom3Option) options).onClickCustom3();
        } else if (sender == custom4Button) {
            return ((Custom4Option) options).onClickCustom4();
        } else {
            return true;
        }
    }

    static class MessagePanel extends DecoratorPanel {

        MessagePanel(final String message, Type type) {

            super(false, false, true, false, 1, CSSClass.pyx4j_Section_Border.name());
            setSize("100%", "100%");

            DOM.setStyleAttribute(getElement(), "padding", "10px");

            DockPanel contentPanel = new DockPanel();
            contentPanel.setSize("100%", "100%");
            DOM.setStyleAttribute(contentPanel.getElement(), "paddingBottom", "10px");

            setWidget(contentPanel);

            WidgetsImageBundle images = ImageFactory.getImages();
            ImageResource imageResource = null;

            switch (type) {
            case Info:
                imageResource = images.info();
                break;
            case Confirm:
                imageResource = images.confirm();
                break;
            case Warning:
                imageResource = images.warning();
                break;
            case Error:
                imageResource = images.error();
                break;
            default:
                break;
            }

            Image image = new Image(imageResource);
            DOM.setStyleAttribute(image.getElement(), "margin", "10px");

            contentPanel.add(image, DockPanel.WEST);
            setCellVerticalAlignment(image, DockPanel.ALIGN_MIDDLE);

            ResizibleScrollPanel scrollPanel = new ResizibleScrollPanel();
            contentPanel.add(scrollPanel, DockPanel.CENTER);
            setCellHeight(scrollPanel, "100%");
            setCellWidth(scrollPanel, "100%");

            HTML htmlMessage = new HTML(message);

            HorizontalPanel htmlHolder = new HorizontalPanel();
            htmlHolder.setSize("100%", "100%");
            htmlHolder.add(htmlMessage);
            htmlHolder.setCellHorizontalAlignment(htmlMessage, HasHorizontalAlignment.ALIGN_CENTER);
            htmlHolder.setCellVerticalAlignment(htmlMessage, HasVerticalAlignment.ALIGN_MIDDLE);

            scrollPanel.setContentWidget(htmlHolder);

        }
    }

    @Override
    public boolean equals(Object other) {
        return (this == other);
    }

    @Override
    public void show() {
        if (!openDialogs.contains(this)) {
            openDialogs.add(this);
        }
        super.show();
        // The insides of Dialog may be CForm that is only initialized on show.
        DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
            public void execute() {
                setupFocusManager();
                if (firstFocusWidget != null) {
                    firstFocusWidget.setFocus(true);
                    currentFocusWidget = firstFocusWidget;
                } else {
                    if (defaultButton != null) {
                        defaultButton.setFocus(true);
                    }
                }
            }
        });
    }

    @Override
    public void hide() {
        openDialogs.remove(this);
        super.hide();

        // Set proper focus in the Dialog blow just closed one.
        if (openDialogs.size() > 0) {
            final Dialog d = openDialogs.get(openDialogs.size() - 1);
            if (d.currentFocusWidget != null) {
                DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
                    public void execute() {
                        if (d.currentFocusWidget != null) {
                            d.currentFocusWidget.setFocus(true);
                            log.trace("Focus requestd to {}", d.currentFocusWidget);
                        }
                    }
                });
            }
        } else {
            log.trace("Last dialog Closed");
        }
    }

    public static void closeOpenDialogs() {
        for (int i = 0; i < openDialogs.size(); i++) {
            Dialog d = openDialogs.get(i);
            d.hide();
        }
        openDialogs.clear();
    }

    public void requestFocus(final FocusWidget focusWidget) {
        firstFocusWidget = focusWidget;
    }

    private void setupFocusManager() {
        if (focusHandler == null) {
            focusHandler = new FocusHandler() {
                @Override
                public void onFocus(FocusEvent event) {
                    if (defaultButton != null) {
                        log.trace("Focus on {}", event.getSource().getClass());
                        boolean allowDefaultButton = ((event.getSource() instanceof TextBox) || (event.getSource() instanceof CheckBox))
                                || (event.getSource() instanceof ListBox);
                        allowEnterKeyForDefaultButton = allowDefaultButton;
                        if (allowDefaultButton) {
                            defaultButton.addStyleName(CSSClass.gwtButtonDefault.name());
                        } else {
                            defaultButton.removeStyleName(CSSClass.gwtButtonDefault.name());
                        }
                    }
                    if (event.getSource() instanceof FocusWidget) {
                        currentFocusWidget = (FocusWidget) event.getSource();
                    }
                }
            };
        }
        if (blurHandler == null) {
            blurHandler = new BlurHandler() {
                @Override
                public void onBlur(BlurEvent event) {
                    if (defaultButton != null) {
                        allowEnterKeyForDefaultButton = false;
                        defaultButton.removeStyleName(CSSClass.gwtButtonDefault.name());
                    }
                }

            };
        }
        attachFocusHandler(this.content.iterator());
    }

    private void attachFocusHandler(Iterator<Widget> iterator) {
        while (iterator.hasNext()) {
            Widget w = iterator.next();
            if (w instanceof FocusWidget) {
                ((FocusWidget) w).addFocusHandler(focusHandler);
                ((FocusWidget) w).addBlurHandler(blurHandler);
                if ((firstFocusWidget == null) && (!(w instanceof Button))) {
                    firstFocusWidget = (FocusWidget) w;
                }
            }
            if (w instanceof HasWidgets) {
                attachFocusHandler(((HasWidgets) w).iterator());
            }
        }
    }
}