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

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.common.client.ui.decorations.DecorationData;

public class PortalListDecorator extends FlowPanel {
    public static String DEFAULT_STYLE_PREFIX = "PortalListDecorator";

    public static enum StyleSuffix implements IStyleName {
        List, ListElement
    }

    public PortalListDecorator(IList<?> list, String memberName, DecorationData decorator) {
        setStyleName(DEFAULT_STYLE_PREFIX);

        if (list.isNull() || memberName == null || memberName.isEmpty())
            return;

        VerticalPanel listHolder = new VerticalPanel();
        listHolder.setWidth("100%");
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
        add(listHolder);

    }
}
