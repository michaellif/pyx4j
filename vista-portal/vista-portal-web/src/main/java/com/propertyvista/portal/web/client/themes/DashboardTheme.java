/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class DashboardTheme extends Theme {

    public static enum StyleName implements IStyleName {
        Gadget, GadgetContent, GadgetBlock, GadgetBlockSeparator, GadgetDecorator,

        PersonPhoto, PersonName
    }

    public DashboardTheme() {
        Style style = new Style(".", StyleName.Gadget);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.GadgetContent);
        style.addProperty("background", ThemeColor.foreground, 0.01);
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("border-radius", "5px");
        style.addProperty("padding", "10px");
        addStyle(style);

        style = new Style(".", StyleName.GadgetDecorator);
        style.addProperty("padding", "10px");
        addStyle(style);

        style = new Style(".", StyleName.GadgetBlock);
        addStyle(style);

        style = new Style(".", StyleName.GadgetBlock, " .", DefaultWidgetsTheme.StyleName.Anchor);
        style.addProperty("display", "block");
        style.addProperty("white-space", "nowrap");
        style.addProperty("color", ThemeColor.contrast2, 1);
        style.addProperty("font-size", "0.8em");
        style.addProperty("padding", "0 5px");
        addStyle(style);

        style = new Style(".", StyleName.PersonPhoto);
        style.addProperty("border-color", ThemeColor.contrast2, 1);
        style.addProperty("border-width", "6px");
        style.addProperty("margin-left", "10px");
        addStyle(style);

        style = new Style(".", StyleName.PersonName);
        style.addProperty("font-size", "1.5em");
        style.addProperty("margin-left", "10px");
        addStyle(style);

        style = new Style(".", StyleName.GadgetBlockSeparator);
        style.addProperty("border-left-color", ThemeColor.foreground, 0.3);
        style.addProperty("border-left-width", "1px");
        style.addProperty("border-left-style", "solid");
        addStyle(style);

        style = new Style(".", StyleName.Gadget, " .", DefaultWidgetsTheme.StyleName.Toolbar);
        style.addProperty("white-space", "normal");
        style.addProperty("text-align", "center");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.Gadget, " .", DefaultWidgetsTheme.StyleName.ToolbarItem);
        style.addProperty("max-width", "250px");
        style.addProperty("width", "100%");
        style.addProperty("margin", "10px 10px 0 10px");
        style.addProperty("text-align", "center");
        style.addProperty("font-size", "0.9em");
        addStyle(style);

        style = new Style(".", StyleName.Gadget, " .", DefaultWidgetsTheme.StyleName.ToolbarItem, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("width", "100%");
        style.addProperty("line-height", "40px");
        style.addProperty("background", "none");
        style.addProperty("background-color", ThemeColor.contrast2);
        style.addProperty("border-radius", "5px");
        style.addProperty("border", "none");
        style.addProperty("color", ThemeColor.foreground, 0.1);
        addStyle(style);

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
