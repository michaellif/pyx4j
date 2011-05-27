/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-17
 * @author VladLL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.decorations;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class VistaHeaderDecorator extends HorizontalPanel {

    public static String DEFAULT_STYLE_PREFIX = "vista_HeaderDecorator";

    public static enum StyleSuffix implements IStyleSuffix {
        Caption
    }

    public VistaHeaderDecorator(String caption, Widget widget, String width) {
        HTML captionHolder = new HTML(caption);
        setStyleName(DEFAULT_STYLE_PREFIX);
        captionHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Caption.name());
        add(captionHolder);
        setCellVerticalAlignment(captionHolder, HorizontalPanel.ALIGN_MIDDLE);
        setCellWidth(captionHolder, "200px");

        if (widget != null) {
            add(widget);
            widget.getElement().getStyle().setMarginRight(1, Unit.EM);
            setCellVerticalAlignment(widget, HorizontalPanel.ALIGN_MIDDLE);
            setCellHorizontalAlignment(widget, ALIGN_RIGHT);
        }

        if (width != null)
            setWidth(width);
    }

    public VistaHeaderDecorator(String caption, Widget widget) {
        this(caption, widget, null);
    }

    public VistaHeaderDecorator(String caption, String width) {
        this(caption, null, width);
    }

    public VistaHeaderDecorator(String caption) {
        this(caption, (String) null);
    }

    public VistaHeaderDecorator(IObject<?> member, String width) {
        this(member.getMeta().getCaption(), null, width);
    }

    public VistaHeaderDecorator(IObject<?> member) {
        this(member, null);
    }
}
