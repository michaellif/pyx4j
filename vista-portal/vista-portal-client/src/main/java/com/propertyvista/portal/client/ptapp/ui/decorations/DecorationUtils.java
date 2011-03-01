/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.decorations;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class DecorationUtils {

    public static IsWidget inline(IsWidget w, String width, String textAlign) {
        return insert(Display.INLINE_BLOCK, w, width, textAlign);
    }

    public static IsWidget inline(IsWidget w, String width) {
        return inline(w, width, null);
    }

    public static IsWidget inline(IsWidget w) {
        return inline(w, null, null);
    }

    public static IsWidget block(IsWidget w, String width, String textAlign) {
        return insert(Display.BLOCK, w, width, textAlign);
    }

    public static IsWidget block(IsWidget w, String width) {
        return block(w, width, null);
    }

    public static IsWidget block(IsWidget w) {
        return block(w, null, null);
    }

    private static IsWidget insert(Display disp, IsWidget w, String width, String textAlign) {
        Widget wg = w.asWidget();
        wg.getElement().getStyle().setDisplay(disp);
        if (textAlign != null) {
            wg.getElement().getStyle().setProperty("textAlign", textAlign);
        }
        if (width != null) {
            wg.setWidth(width);
        }
        return w;
    }
}
