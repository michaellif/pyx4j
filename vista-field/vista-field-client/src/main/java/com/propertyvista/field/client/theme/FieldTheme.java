/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.field.client.theme;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.common.client.theme.VistaTheme;

public class FieldTheme extends VistaTheme {

    public static enum StyleName implements IStyleName {
        SiteView, SiteViewContent, SiteViewHeader, SiteViewFooter, SiteViewDisplay;
    }

    public FieldTheme() {
        initStyles();
    }

    protected void initStyles() {
        // All viewable area:
        Style style = new Style(".", StyleName.SiteView.name());
        style.addProperty("color", ThemeColor.foreground);
        addStyle(style);

        // DockLayoutPanel:
        style = new Style(".", StyleName.SiteViewContent.name());
        addStyle(style);

        // Header:
        style = new Style(".", StyleName.SiteViewHeader.name());
        style.addGradient(ThemeColor.object1, 1, ThemeColor.object1, 0.7);
        addStyle(style);

        // Footer:
        style = new Style(".", StyleName.SiteViewFooter.name());
        style.addProperty("background-color", "yellow");
        addStyle(style);

    }

}