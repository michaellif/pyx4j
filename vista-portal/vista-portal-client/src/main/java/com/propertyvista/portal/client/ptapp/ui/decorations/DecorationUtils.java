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
        Widget wg = w.asWidget();
        wg.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        if (textAlign != null) {
            wg.getElement().getStyle().setProperty("textAlign", textAlign);
        }
        wg.setWidth(width);
        return w;
    }

}
