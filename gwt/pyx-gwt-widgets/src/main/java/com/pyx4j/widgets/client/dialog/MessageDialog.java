/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 12, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;

public class MessageDialog extends Dialog {

    private static final I18n i18n = I18n.get(MessageDialog.class);

    public static enum Type {
        Error, Warning, Info, Confirm
    }

    private final MessagePanel messagePanel;

    public MessageDialog(String caption, String message, Type type, DialogOptions options) {
        super(caption);
        setDialogOptions(options);
        messagePanel = new MessagePanel(message, type);
        setBody(messagePanel);
    }

    public static void error(String title, String text) {
        show(title, text, Type.Error);
    }

    public static void error(String title, Throwable caught) {
        String text = caught.getMessage();
        if (text == null) {
            text = caught.getClass().getName();
        }
        show(title, text, Type.Error);
    }

    public static void warn(String title, String text) {
        show(title, text, Type.Warning);
    }

    public static void info(String text) {
        show(i18n.tr("Information"), text, Type.Info);
    }

    public static void info(String title, String text) {
        show(title, text, Type.Info);
    }

    public static void show(String title, String text, Type type) {
        show(title, text, type, new OkOption() {
            @Override
            public boolean onClickOk() {
                return true;
            }
        });
    }

    public static void confirm(String title, String text, final Command onConfirmed) {
        confirm(title, text, onConfirmed, null);
    }

    /**
     * Better use variation of this function with ConfirmDecline
     */
    public static void confirm(String title, String text, final Command onConfirmed, final Command onDeclined) {
        show(title, text, Type.Confirm, new YesNoOption() {
            @Override
            public boolean onClickYes() {
                if (onConfirmed != null) {
                    onConfirmed.execute();
                }
                return true;
            }

            @Override
            public boolean onClickNo() {
                if (onDeclined != null) {
                    onDeclined.execute();
                }
                return true;
            }
        });
    }

    public static void confirm(String title, String text, final Command onConfirmed, final Command onDeclined, final Command onCanceled) {
        show(title, text, Type.Confirm, new YesNoCancelOption() {
            @Override
            public boolean onClickYes() {
                if (onConfirmed != null) {
                    onConfirmed.execute();
                }
                return true;
            }

            @Override
            public boolean onClickNo() {
                if (onDeclined != null) {
                    onDeclined.execute();
                }
                return true;
            }

            @Override
            public boolean onClickCancel() {
                if (onCanceled != null) {
                    onCanceled.execute();
                }
                return true;
            }
        });
    }

    public static void confirm(String title, String text, final ConfirmDecline confirmDecline) {
        show(title, text, Type.Confirm, new YesNoOption() {
            @Override
            public boolean onClickYes() {
                confirmDecline.onConfirmed();
                return true;
            }

            @Override
            public boolean onClickNo() {
                confirmDecline.onDeclined();
                return true;
            }
        });
    }

    public static void show(final String title, final String text, final Type type, final DialogOptions options) {
        show(title, text, type, options, false);
    }

    public static void prefetch() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                show(null, null, null, null, true);
            }
        });
    }

    /*
     * Move all the Dialog and PopupPanel JS code to "Left over code"
     */
    private static void show(final String title, final String text, final Type type, final DialogOptions options, final boolean prefetch) {
        GWT.runAsync(MessageDialog.class, new RunAsyncCallback() {

            @Override
            public void onFailure(Throwable reason) {
                if (!prefetch) {
                    throw new UnrecoverableClientError(reason);
                }
            }

            @Override
            public void onSuccess() {
                if (!prefetch) {
                    new MessageDialog(title, text, type, options).show();
                }
            }
        });
    }

    @Override
    public void layout() {
        if (messagePanel != null) {
            messagePanel.layout();
        }
        super.layout();
    }

    class MessagePanel extends FlowPanel {

        private final ScrollPanel htmlScroll;

        private final HTML htmlMessage;

        MessagePanel(final String message, Type type) {
            super();
            getElement().getStyle().setProperty("display", "table");

            WidgetsImageBundle imageBundle = ImageFactory.getImages();
            ImageResource imageResource = null;

            switch (type) {
            case Info:
                imageResource = imageBundle.info();
                break;
            case Confirm:
                imageResource = imageBundle.confirm();
                break;
            case Warning:
                imageResource = imageBundle.warning();
                break;
            case Error:
                imageResource = imageBundle.error();
                break;
            default:
                break;
            }

            SimplePanel imageHolder = new SimplePanel(new Image(imageResource));
            imageHolder.getElement().getStyle().setProperty("display", "table-cell");
            imageHolder.getElement().getStyle().setPadding(10, Unit.PX);
            imageHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            add(imageHolder);

            htmlMessage = new HTML((message == null) ? "" : message.replace("\n", "<br/>"));
            htmlMessage.getElement().getStyle().setPadding(10, Unit.PX);
            htmlMessage.getElement().getStyle().setProperty("wordWrap", "break-word");
            htmlMessage.getElement().getStyle().setWhiteSpace(WhiteSpace.NORMAL);

            htmlScroll = new ScrollPanel();
            htmlScroll.setWidget(htmlMessage);

            SimplePanel htmlHolder = new SimplePanel(htmlScroll);
            htmlHolder.getElement().getStyle().setProperty("display", "table-cell");
            htmlHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

            add(htmlHolder);

        }

        public void layout() {
            htmlScroll.getElement().getStyle().setPropertyPx("maxHeight", Math.max(100, (int) (Window.getClientHeight() * 0.9) - 100));
        }

    }

}
