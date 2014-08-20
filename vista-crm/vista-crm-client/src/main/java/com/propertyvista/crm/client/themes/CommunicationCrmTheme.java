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
package com.propertyvista.crm.client.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class CommunicationCrmTheme extends Theme {
    public static enum StyleName implements IStyleName {
        Comm, CommContent, CommCallout, CommHeader, CommHeaderTitle, CommMessage, AllertButton
    }

    public static enum StyleDependent implements IStyleDependent {
        sideComm, alertOn
    }

    public CommunicationCrmTheme() {

        Style style = new Style(".", StyleName.Comm);
        style.addProperty("width", "400px");
        style.addBoxShadow(ThemeColor.foreground, "5px 5px 5px");
        style.addProperty("background", ThemeColor.foreground, 0.01);
        style.addProperty("border-width", "1px");
        style.addProperty("border-color", ThemeColor.foreground, 0.8);
        style.addProperty("border-style", "solid");
        style.addProperty("margin-top", "15px");
        addStyle(style);

        style = new Style(".", StyleName.CommContent);
        style.addProperty("max-height", "400px");
        style.addProperty("height", "400px");
        addStyle(style);

        style = new Style(".", StyleName.CommCallout);
        style.addProperty("fill", ThemeColor.foreground, 0.8);
        addStyle(style);

        style = new Style(".", StyleName.CommHeader);
        style.addProperty("line-height", "60px");
        style.addProperty("background", ThemeColor.foreground, 0.8);
        addStyle(style);

        style = new Style(".", StyleName.CommHeaderTitle);
        style.addProperty("margin-left", "5px");
        style.addProperty("vertical-align", "top");
        style.addProperty("color", ThemeColor.foreground, 0.1);
        style.addProperty("font-weight", "bold");
        style.addProperty("color", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.CommMessage);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("padding", "5px");
        style.addProperty("width", "100%");
        style.addProperty("border-bottom-width", "1px");
        style.addProperty("border-bottom-color", ThemeColor.foreground, 0.8);
        style.addProperty("border-bottom-style", "solid");
        addStyle(style);

        style = new Style(".", StyleName.Comm, "-", StyleDependent.sideComm);
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        style.addProperty("border-radius", "0");
        addStyle(style);

        style = new Style(".", StyleName.Comm, "-", StyleDependent.sideComm, " .", StyleName.CommContent);
        style.addProperty("max-height", "none");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.Comm, "-", StyleDependent.sideComm, " .", StyleName.CommHeaderTitle);
        style.addProperty("line-height", "60px");
        addStyle(style);

        style = new Style(".", StyleName.AllertButton);
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(".", StyleName.AllertButton, " .", WidgetTheme.StyleName.ButtonText);
        style.addProperty("background", "none repeat scroll 0 0 #FF0000");
        style.addProperty("border-radius", "5px");
        style.addProperty("font-size", "9px");
        style.addProperty("height", "auto");
        style.addProperty("line-height", "9px");
        style.addProperty("margin", "0 9px");
        style.addProperty("position", "absolute");
        style.addProperty("right", "0px");
        style.addProperty("top", "4px");
        style.addProperty("padding", "2px 4px");
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.AllertButton, "-", StyleDependent.alertOn, " .", WidgetTheme.StyleName.ButtonText);
        style.addProperty("display", "inline");
        addStyle(style);

    }

    @Override
    public ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }
}