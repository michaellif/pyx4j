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
package com.propertyvista.portal.client.ptapp.ui.decorations;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class ViewHeaderDecorator extends HorizontalPanel {

    public static String DEFAULT_STYLE_PREFIX = "vista_ViewHeaderDecorator";

    public static enum StyleSuffix implements IStyleSuffix {
        Caption
    }

    public ViewHeaderDecorator(String caption, Widget widget) {
        HTML captionHolder = new HTML(caption);
        setStyleName(DEFAULT_STYLE_PREFIX);
        captionHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Caption.name());
        add(captionHolder);
        setCellVerticalAlignment(captionHolder, HorizontalPanel.ALIGN_MIDDLE);
        if (widget != null) {
            add(widget);
            setCellVerticalAlignment(widget, HorizontalPanel.ALIGN_MIDDLE);
        }
    }

    public ViewHeaderDecorator(String caption) {
        this(caption, null);
    }

    public ViewHeaderDecorator(IObject<?> member) {
        this(member.getMeta().getCaption(), null);
    }
}
