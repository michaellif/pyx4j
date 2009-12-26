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
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.pyx4j.widgets.client.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.style.Theme.CSSClass;
import com.pyx4j.widgets.client.ImageBundle;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.ResizibleScrollPanel;

public class Dialog extends DialogPanel {

    public static enum Type {
        Error, Warning, Info, Confirm
    }

    protected Button focusButton;

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

        Panel buttonPanel = createButtonsPanel();
        content.add(buttonPanel, DockPanel.SOUTH);
        content.setCellWidth(buttonPanel, "100%");

        setWidget(content);
        setPixelSize(400, 300);
        center();

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

    private Button createButton(String text, ClickHandler buttonListener, boolean canHaveFocus) {
        Button button = new Button(text);
        button.ensureDebugId("Dialog." + text);
        button.addClickHandler(buttonListener);
        DOM.setStyleAttribute(button.getElement(), "margin", "3px");
        if (canHaveFocus && focusButton == null) {
            focusButton = button;
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

            ImageBundle images = ImageFactory.getImages();

            Image image = new Image(images.warning());
            DOM.setStyleAttribute(image.getElement(), "padding", "10px");

            contentPanel.add(image, DockPanel.WEST);
            setCellVerticalAlignment(image, DockPanel.ALIGN_MIDDLE);

            ResizibleScrollPanel scrollPanel = new ResizibleScrollPanel();
            contentPanel.add(scrollPanel, DockPanel.CENTER);
            setCellHeight(scrollPanel, "100%");
            setCellWidth(scrollPanel, "100%");

            scrollPanel.setViewport(new HTML(message));

        }
    }

    @Override
    public void show() {
        super.show();
        if (this.focusButton != null) {
            requestFocus(this.focusButton);
        }
    }

    public void requestFocus(final FocusWidget focusWidget) {
        DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
            public void execute() {
                focusWidget.setFocus(true);
            }
        });
    }

}