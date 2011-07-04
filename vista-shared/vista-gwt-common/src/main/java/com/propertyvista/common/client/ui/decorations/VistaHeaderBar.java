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

public class VistaHeaderBar extends HorizontalPanel {

    public static String DEFAULT_STYLE_PREFIX = "vista_HeaderBar";

    public static enum StyleSuffix implements IStyleSuffix {
        Caption
    }

    private final HTML captionHolder;

    public VistaHeaderBar(String caption, Widget widget, String width) {
        captionHolder = new HTML(caption);
        setStyleName(getStylePrefix());
        captionHolder.setStyleName(getStylePrefix() + StyleSuffix.Caption.name());
        add(captionHolder);
        setCellVerticalAlignment(captionHolder, HorizontalPanel.ALIGN_MIDDLE);

        if (widget != null) {
            add(widget);
            widget.getElement().getStyle().setMarginRight(1, Unit.EM);
            setCellVerticalAlignment(widget, HorizontalPanel.ALIGN_MIDDLE);
            setCellHorizontalAlignment(widget, ALIGN_RIGHT);
        }

        if (width != null)
            setWidth(width);
    }

    public VistaHeaderBar(String caption, Widget widget) {
        this(caption, widget, null);
    }

    public VistaHeaderBar(String caption, String width) {
        this(caption, null, width);
    }

    public VistaHeaderBar(String caption) {
        this(caption, (String) null);
    }

    public VistaHeaderBar(IObject<?> member, String width) {
        this(member.getMeta().getCaption(), null, width);
    }

    public VistaHeaderBar(IObject<?> member) {
        this(member, null);
    }

    protected String getStylePrefix() {
        return DEFAULT_STYLE_PREFIX;
    }

    public void setCaption(String caption) {
        captionHolder.setText(caption);
    }
}
