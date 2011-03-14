/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-18
 * @author VladLL
 * @version $Id$
 */
package com.propertyvista.common.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;

public class ViewLineSeparator extends HTML {

    public static String DEFAULT_STYLE_PREFIX = "vista_ViewLineSeparator";

    public ViewLineSeparator() {
        setStyleName(DEFAULT_STYLE_PREFIX);
    }

    public ViewLineSeparator(double width, Unit unit) {
        this();
        if (width != 0)
            getElement().getStyle().setWidth(width, unit);
    }

    public ViewLineSeparator(double width, Unit widthUnit, double topMargin, Unit topMarginUnit) {
        this(width, widthUnit);
        getElement().getStyle().setMarginTop(topMargin, topMarginUnit);
    }

    public ViewLineSeparator(double width, Unit widthUnit, double topMargin, Unit topMarginUnit, double bottomMargin, Unit bottomMarginUnit) {
        this(width, widthUnit, topMargin, topMarginUnit);
        getElement().getStyle().setMarginBottom(bottomMargin, bottomMarginUnit);
    }
}
