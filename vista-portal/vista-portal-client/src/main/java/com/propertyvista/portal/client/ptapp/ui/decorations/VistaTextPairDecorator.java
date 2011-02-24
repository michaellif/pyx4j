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
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.decorations;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;

import com.pyx4j.widgets.client.style.IStyleSuffix;

public class VistaTextPairDecorator extends FlowPanel {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_VistatextPairDecorator";

    public static enum StyleSuffix implements IStyleSuffix {
        Left, Right, Gap
    }

    public VistaTextPairDecorator(String left, String right) {
        this(left, right, new DecorationData(HasHorizontalAlignment.ALIGN_LEFT, HasHorizontalAlignment.ALIGN_RIGHT));
    }

    public VistaTextPairDecorator(String left, String right, int labelWidth, int componentWidth) {
        this(left, right, new DecorationData(labelWidth, HasHorizontalAlignment.ALIGN_LEFT, componentWidth, HasHorizontalAlignment.ALIGN_RIGHT));
    }

    public VistaTextPairDecorator(String left, String right, double labelWidth, double componentWidth) {
        this(left, right, new DecorationData(labelWidth, HasHorizontalAlignment.ALIGN_LEFT, componentWidth, HasHorizontalAlignment.ALIGN_RIGHT));
    }

    public VistaTextPairDecorator(String left, String right, double labelWidth, double componentWidth, double gapWidth) {
        this(left, right, new DecorationData(labelWidth, HasHorizontalAlignment.ALIGN_LEFT, componentWidth, HasHorizontalAlignment.ALIGN_RIGHT, gapWidth));
    }

    public VistaTextPairDecorator(String left, String right, DecorationData decorData) {

        setStyleName(DEFAULT_STYLE_PREFIX);

        Label lw = new Label(left);
        lw.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Left);
        lw.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        lw.setHorizontalAlignment(decorData.labelAlignment);
        if (decorData.labelWidth != 0)
            lw.getElement().getStyle().setWidth(decorData.labelWidth, decorData.labelUnit);

        add(lw);

        Label rw = new Label(right);
        rw.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Right);
        rw.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        rw.setHorizontalAlignment(decorData.componentAlignment);
        if (decorData.componentWidth != 0)
            rw.getElement().getStyle().setWidth(decorData.componentWidth, decorData.componentUnit);

        add(rw);

    }
}
