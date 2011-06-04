/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 3, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.decorators;

import java.util.Collection;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.DecorationData;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class PortalMultiLineDecorator extends FlowPanel {
    public static String DEFAULT_STYLE_PREFIX = "PortalMultiLineDecorator";

    public static final String DEFAULT_SEAPATOR = ",";

    public static enum StyleSuffix implements IStyleSuffix {
        Label, Value
    }

    public PortalMultiLineDecorator(IEntity entity, DecorationData decorator, String delimiter) {
        setStyleName(DEFAULT_STYLE_PREFIX);
        if (entity.isNull())
            return;

        String delim = (delimiter == null) ? DEFAULT_SEAPATOR : delimiter;
        String caption = entity.getMeta().getCaption();
        if (caption == null)
            caption = "";
        Label lbl = new Label(caption);
        lbl.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Label);
        lbl.getElement().getStyle().setVerticalAlign(decorator.labelVerticalAlignment);
        lbl.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        lbl.setHorizontalAlignment(decorator.labelAlignment);
        if (decorator.labelWidth != 0)
            lbl.getElement().getStyle().setWidth(decorator.labelWidth, decorator.labelUnit);
        add(lbl);

        String value = "";
        Collection<Object> values = entity.getValue().values();
        if (values != null) {
            for (Object v : values)
                value += String.valueOf(v) + delim;
            value = value.substring(0, value.lastIndexOf(delim));
        }
        lbl = new Label(value);
        lbl.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Value);
        lbl.getElement().getStyle().setVerticalAlign(decorator.labelVerticalAlignment);
        lbl.setHorizontalAlignment(decorator.labelAlignment);
        if (decorator.componentWidth != 0)
            lbl.getElement().getStyle().setWidth(decorator.componentWidth, decorator.componentUnit);
        add(lbl);
    }
}
