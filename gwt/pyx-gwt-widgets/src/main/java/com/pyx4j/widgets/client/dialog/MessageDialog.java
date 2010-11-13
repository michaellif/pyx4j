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

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.widgets.client.dialog.Dialog.Type;

public class MessageDialog {

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

    /*
     * Move all the Dialog and PopupPanel JS code to "Left over code"
     */
    public static void show(final String title, final String text, final Type type, final DialogOptions options) {
        GWT.runAsync(MessageDialog.class, new RunAsyncCallback() {

            @Override
            public void onFailure(Throwable reason) {
                throw new UnrecoverableClientError(reason);
            }

            @Override
            public void onSuccess() {
                new Dialog(title, text, type, options).show();
            }
        });
    }

    public static void confirm(String title, String text, final Runnable onConfirmed) {
        show(title, text, Dialog.Type.Confirm, new YesNoOption() {
            @Override
            public boolean onClickYes() {
                onConfirmed.run();
                return true;
            }

            @Override
            public boolean onClickNo() {
                return true;
            }
        });
    }

}
