/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 12, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.dialog;

import com.pyx4j.widgets.client.dialog.Dialog.Type;

public class MessageDialog {

    public static Dialog error(String title, String text) {
        return show(title, text, Type.Error);
    }

    public static Dialog warn(String title, String text) {
        return show(title, text, Type.Warning);
    }

    public static Dialog info(String title, String text) {
        return show(title, text, Type.Info);
    }

    private static Dialog show(String title, String text, Type type) {
        Dialog d = new Dialog(title, text, type, new OkOption() {
            public boolean onClickOk() {
                return true;
            }
        });
        d.show();
        return d;
    }
}
