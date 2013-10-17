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
import com.google.gwt.user.client.Command;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;

public class MessageDialog_v2 extends Dialog_v2 {

    private static final I18n i18n = I18n.get(MessageDialog_v2.class);

    public MessageDialog_v2(String caption, String message, Type type, DialogOptions options) {
        super(caption, options, new MessagePanel(message, type));
        setDialogPixelWidth(500);
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
        show(title, text, Dialog_v2.Type.Confirm, new YesNoOption() {
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

    public static void confirm(String title, String text, final ConfirmDecline confirmDecline) {
        show(title, text, Dialog_v2.Type.Confirm, new YesNoOption() {
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
        GWT.runAsync(MessageDialog_v2.class, new RunAsyncCallback() {

            @Override
            public void onFailure(Throwable reason) {
                if (!prefetch) {
                    throw new UnrecoverableClientError(reason);
                }
            }

            @Override
            public void onSuccess() {
                if (!prefetch) {
                    new MessageDialog_v2(title, text, type, options).show();
                }
            }
        });
    }

}
