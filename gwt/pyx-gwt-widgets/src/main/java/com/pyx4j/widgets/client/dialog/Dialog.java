/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.dialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.ImageBundle;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.ResizibleScrollPanel;

public class Dialog extends DialogPanel {

    public static enum Type {
        Error, Warning, Info, Confirm
    }

    protected Button firstButton;

    protected Button yesButton;

    protected Button noButton;

    protected Button okButton;

    protected Button cancelButton;

    protected Button closeButton;

    protected Button custom1Button;

    protected Button custom2Button;

    protected Button custom3Button;

    protected Button custom4Button;

    protected DialogOptions options;

    public Dialog(String message) {
        this("Information", message, Type.Info, new OkOption() {

            @Override
            public boolean onClickOk() {
                return true;
            }
        });

    }

    public Dialog(String caption, String message, Type type, DialogOptions options) {
        this(caption, new MessagePanel(message, type), options);
    }

    public Dialog(String caption, Widget message, DialogOptions options) {
        setCaption(caption);

        this.options = options;

        DockPanel content = new DockPanel();
        content.setHeight("100%");
        content.setWidth("100%");

        content.add(message, DockPanel.CENTER);
        message.setSize("100%", "100%");
        content.setCellHeight(message, "100%");

        content.add(createButtonsPanel(), DockPanel.SOUTH);

        setWidget(content);
        setPixelSize(400, 300);
        center();

    }

    private HorizontalPanel createButtonsPanel() {
        HorizontalPanel buttonsPanel = new HorizontalPanel();
        ClickHandler buttonListener = new ButtonListener();

        int index = 0;

        if (options instanceof Custom1Option) {
            custom1Button = createButton(((Custom1Option) options).custom1Text(), buttonListener);
            buttonsPanel.insert(custom1Button, index);
        }
        if (options instanceof Custom2Option) {
            custom2Button = createButton(((Custom2Option) options).custom2Text(), buttonListener);
            buttonsPanel.add(custom2Button);
        }
        if (options instanceof Custom3Option) {
            custom3Button = createButton(((Custom3Option) options).custom3Text(), buttonListener);
            buttonsPanel.add(custom3Button);
        }
        if (options instanceof Custom4Option) {
            custom4Button = createButton(((Custom4Option) options).custom4Text(), buttonListener);
            buttonsPanel.add(custom4Button);
        }
        if (options instanceof YesOption) {
            yesButton = createButton("Yes", buttonListener);
            buttonsPanel.add(yesButton);
        }
        if (options instanceof NoOption) {
            noButton = createButton("No", buttonListener);
            buttonsPanel.add(noButton);
        }
        if (options instanceof OkOption) {
            okButton = createButton(optionTextOk(), buttonListener);
            buttonsPanel.add(okButton);
        }
        if (options instanceof CancelOption) {
            cancelButton = createButton(optionTextCancel(), buttonListener);
            buttonsPanel.add(cancelButton);
        }
        if (options instanceof CloseOption) {
            closeButton = createButton(optionTextClose(), buttonListener);
            buttonsPanel.add(closeButton);
        }

        return buttonsPanel;
    }

    /**
     * This is alternative to using CustomOption. Override to change the text in dialog.
     * 
     * @return text for 'Ok' button
     */
    protected String optionTextOk() {
        return "Ok";
    }

    /**
     * This is alternative to using CustomOption. Override to change the text in dialog.
     * 
     * @return text for 'Cancel' button
     */
    protected String optionTextCancel() {
        return "Cancel";
    }

    /**
     * This is alternative to using CustomOption. Override to change the text in dialog.
     * 
     * @return text for 'Close' button
     */
    protected String optionTextClose() {
        return "Close";
    }

    private Button createButton(String text, ClickHandler buttonListener) {
        Button button = new Button(text);
        button.ensureDebugId("Dialog." + text);
        button.addClickHandler(buttonListener);
        DOM.setStyleAttribute(button.getElement(), "margin", "3px");
        DOM.setStyleAttribute(button.getElement(), "padding", "0,5,0,5");
        if (firstButton == null) {
            firstButton = button;
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

    static class MessagePanel extends DockPanel {

        MessagePanel(final String message, Type type) {

            ImageBundle images = ImageFactory.getImages();

            setSize("100%", "100%");

            add(new Image(images.warning()), DockPanel.WEST);

            ResizibleScrollPanel scrollPanel = new ResizibleScrollPanel();
            add(scrollPanel, DockPanel.CENTER);
            setCellHeight(scrollPanel, "100%");
            setCellWidth(scrollPanel, "100%");

            scrollPanel.setViewport(new HTML(message));

        }

    }

}