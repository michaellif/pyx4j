/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-24
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.decorations;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.decorations.DecorationData;

public class VistaReadOnlyDecorator extends FlowPanel {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_VistatextPairDecorator";

    public static enum StyleSuffix implements IStyleName {
        Left, Right
    }

    public VistaReadOnlyDecorator(final CComponent<?> component, DecorationData decorData) {

        setStyleName(DEFAULT_STYLE_PREFIX);

        Label lw = new Label(component.getTitle() == null ? "" : component.getTitle());
        lw.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Left);
        lw.getElement().getStyle().setVerticalAlign(decorData.labelVerticalAlignment);
        lw.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        lw.setHorizontalAlignment(decorData.labelAlignment);
        if (decorData.labelWidth != 0)
            lw.getElement().getStyle().setWidth(decorData.labelWidth, decorData.labelUnit);

        add(lw);

        Widget right = component.asWidget();
        right.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Right);
        right.getElement().getStyle().setVerticalAlign(decorData.componentVerticalAlignment);
        right.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        //        right.asWidget().setHorizontalAlignment(decorData.componentAlignment);
        if (decorData.componentWidth != 0) {
            right.getElement().getStyle().setWidth(decorData.componentWidth, decorData.componentUnit);
        }

        add(right);
    }

}
