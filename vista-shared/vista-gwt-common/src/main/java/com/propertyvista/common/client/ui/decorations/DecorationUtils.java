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
package com.propertyvista.common.client.ui.decorations;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;

import com.propertyvista.common.domain.Person;

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

    // forms full person's name from our IPerson domain: 
    public static FlowPanel formFullName(CEntityEditableComponent<?> entityComp, final Person person) {

        FlowPanel fullname = new FlowPanel();
        fullname.add(inline(entityComp.inject(person.name().firstName()), "auto"));
        fullname.add(inline(new HTML("&nbsp;")));
        fullname.add(inline(entityComp.inject(person.name().middleName()), "auto"));
        fullname.add(inline(new HTML("&nbsp;")));
        fullname.add(inline(entityComp.inject(person.name().lastName()), "auto"));
        return fullname;
    }
}
