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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.ResizableWidget;
import com.google.gwt.widgetideas.client.ResizableWidgetCollection;

import com.pyx4j.widgets.client.ImageBundle;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.dialog.images.DialogImages;

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
            public void onClickOk() {
                //do nothing
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

        if (options instanceof Custom1Option) {
            custom1Button = createButton(((Custom1Option) options).customButtonText(), buttonListener);
            DOM.setStyleAttribute(custom1Button.getElement(), "padding", "0,5,0,5");
            buttonsPanel.add(custom1Button);
        }
        if (options instanceof Custom2Option) {
            custom2Button = createButton(((Custom2Option) options).customButtonText(), buttonListener);
            DOM.setStyleAttribute(custom2Button.getElement(), "padding", "0,5,0,5");
            buttonsPanel.add(custom2Button);
        }
        if (options instanceof Custom3Option) {
            custom3Button = createButton(((Custom3Option) options).customButtonText(), buttonListener);
            DOM.setStyleAttribute(custom3Button.getElement(), "padding", "0,5,0,5");
            buttonsPanel.add(custom3Button);
        }
        if (options instanceof Custom4Option) {
            custom4Button = createButton(((Custom4Option) options).customButtonText(), buttonListener);
            DOM.setStyleAttribute(custom4Button.getElement(), "padding", "0,5,0,5");
            buttonsPanel.add(custom4Button);
        }
        if (options instanceof YesOption) {
            yesButton = createButton("Yes", buttonListener);
            DOM.setStyleAttribute(yesButton.getElement(), "padding", "0,5,0,5");
            buttonsPanel.add(yesButton);
        }
        if (options instanceof NoOption) {
            noButton = createButton("No", buttonListener);
            DOM.setStyleAttribute(noButton.getElement(), "padding", "0,5,0,5");
            buttonsPanel.add(noButton);
        }
        if (options instanceof OkOption) {
            okButton = createButton(optionTextOk(), buttonListener);
            DOM.setStyleAttribute(okButton.getElement(), "padding", "0,5,0,5");
            buttonsPanel.add(okButton);
        }
        if (options instanceof CancelOption) {
            cancelButton = createButton(optionTextCancel(), buttonListener);
            DOM.setStyleAttribute(cancelButton.getElement(), "padding", "0,5,0,5");
            buttonsPanel.add(cancelButton);
        }
        if (options instanceof CloseOption) {
            closeButton = createButton(optionTextClose(), buttonListener);
            DOM.setStyleAttribute(closeButton.getElement(), "padding", "0,5,0,5");
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
        Button retVal = new Button(text);
        retVal.ensureDebugId("Dialog." + text);
        retVal.addClickHandler(buttonListener);
        DOM.setStyleAttribute(retVal.getElement(), "margin", "3px");
        if (firstButton == null) {
            firstButton = retVal;
        }
        return retVal;
    }

    private class ButtonListener implements ClickHandler {
        public void onClick(ClickEvent event) {
            Object sender = event.getSource();
            if (sender == yesButton) {
                ((YesOption) options).onClickYes();
            } else if (sender == noButton) {
                ((NoOption) options).onClickNo();
            } else if (sender == okButton) {
                ((OkOption) options).onClickOk();
            } else if (sender == cancelButton) {
                ((CancelOption) options).onClickCancel();
            } else if (sender == closeButton) {
                ((CloseOption) options).onClickClose();
            } else if (sender == custom1Button) {
                ((Custom1Option) options).onClickCustom1();
            } else if (sender == custom2Button) {
                ((Custom2Option) options).onClickCustom2();
            } else if (sender == custom3Button) {
                ((Custom3Option) options).onClickCustom3();
            } else if (sender == custom4Button) {
                ((Custom4Option) options).onClickCustom4();
            }
        }
    }

    static class MessagePanel extends DockPanel implements ResizableWidget {

        private static ResizableWidgetCollection resizableWidgetCollection = new ResizableWidgetCollection(50);

        private final SimplePanel scrollPanel;

        private final SimplePanel viewportPanel;

        MessagePanel(final String message, Type type) {

            ImageBundle images = ImageFactory.getImages();

            setSize("100%", "100%");

            add(new Image(images.warning()), DockPanel.WEST);

            scrollPanel = new SimplePanel();
            add(scrollPanel, DockPanel.CENTER);
            setCellHeight(scrollPanel, "100%");
            setCellWidth(scrollPanel, "100%");

            DOM.setStyleAttribute(scrollPanel.getElement(), "position", "relative");
            scrollPanel.setSize("100%", "100%");

            viewportPanel = new SimplePanel();
            viewportPanel.add(new HTML(message));

            scrollPanel.add(viewportPanel);

            viewportPanel.setSize("100%", "100%");

            DOM.setStyleAttribute(viewportPanel.getElement(), "overflow", "auto");
            DOM.setStyleAttribute(viewportPanel.getElement(), "position", "absolute");
            DOM.setStyleAttribute(viewportPanel.getElement(), "top", "0px");
            DOM.setStyleAttribute(viewportPanel.getElement(), "left", "0px");

        }

        @Override
        protected void onAttach() {
            super.onAttach();
            resizableWidgetCollection.add(this);
        }

        @Override
        protected void onDetach() {
            super.onDetach();
            resizableWidgetCollection.remove(this);
        }

        @Override
        public void onResize(int width, int height) {
            onResize();
        }

        public void onResize() {
            viewportPanel.setWidth(scrollPanel.getOffsetWidth() + "px");
            viewportPanel.setHeight(scrollPanel.getOffsetHeight() + "px");
        }
    }

}