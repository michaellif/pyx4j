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
package com.propertyvista.portal.client.ui.decorations;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.common.client.ui.decorations.DecorationData;

public class PortalListDecorator extends FlowPanel {
    public static String DEFAULT_STYLE_PREFIX = "PortalListDecorator";

    public static enum StyleSuffix implements IStyleSuffix {
        Label, List, ListElement
    }

    public PortalListDecorator(IList<?> list, String memberName, DecorationData decorator) {
        setStyleName(DEFAULT_STYLE_PREFIX);

        if (list.isNull() || memberName == null || memberName.isEmpty())
            return;

        String caption = list.getMeta().getCaption();
        SimplePanel captionHolder = null;

        if (caption != null && !caption.trim().isEmpty()) {
            captionHolder = new SimplePanel();
            captionHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Label);
            Style captionHolderStyle = captionHolder.getElement().getStyle();
            captionHolder.setHeight("100%");
            if (decorator.labelWidth != 0) {
                captionHolderStyle.setWidth(decorator.labelWidth, decorator.labelUnit);
            }
            captionHolderStyle.setFloat(Float.LEFT);
            Label lbl = new Label(caption);
            lbl.getElement().getStyle().setVerticalAlign(decorator.labelVerticalAlignment);
            captionHolder.setWidget(lbl);
        }

        VerticalPanel listHolder = new VerticalPanel();
        listHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.List);
        listHolder.getElement().getStyle().setFloat(Float.RIGHT);
        if (decorator.componentWidth != 0)
            listHolder.getElement().getStyle().setWidth(decorator.componentWidth, decorator.componentUnit);
        for (IEntity element : list) {
            if (!element.isNull()) {
                Object value = element.getMemberValue(memberName);
                if (value != null) {
                    Label item = new Label((String) value);
                    item.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.ListElement);
                    listHolder.add(item);
                }
            }
        }
        if (captionHolder != null)
            add(captionHolder);
        add(listHolder);

    }
}
