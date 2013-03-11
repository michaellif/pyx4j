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
        SiteView, SiteViewContent, SiteViewHeader, SiteViewFooter, SiteViewDisplay, LoginInputField, LoginViewPanel, LoginViewSectionHeader, LoginViewSectionContent, LoginViewSectionFooter, LoginOrLineSeparator, LoginCaption, LoginCaptionText, LoginCaptionTextEmph, LoginButton, LoginButtonHolder;
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

        //Login fields:
        style = new Style(".", StyleName.LoginInputField.name());
        style.addProperty("width", "20em");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        addStyle(style);

        style = new Style(".", StyleName.LoginViewPanel);
        style.addProperty("position", "relative");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.LoginViewSectionHeader.name());
        style.addProperty("width", "100%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "bottom");
        style.addProperty("text-align", "center");
        style.addProperty("margin-top", "10%");
        addStyle(style);

        style = new Style(".", StyleName.LoginViewSectionContent.name());
        style.addProperty("width", "100%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.LoginViewSectionFooter.name());
        style.addProperty("width", "100%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.LoginOrLineSeparator);
        style.addProperty("position", "absolute");
        style.addProperty("top", "0%");
        style.addProperty("bottom", "0%");
        style.addProperty("left", "100%");
        style.addProperty("right", "100%");
        style.addProperty("border-color", "gray");

        style = new Style(".", StyleName.LoginCaption);
        style.addProperty("margin-top", "25px");
        style.addProperty("margin-bottom", "25px");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.LoginCaptionText);
        addStyle(style);

        style = new Style(".", StyleName.LoginCaptionTextEmph);
        addStyle(style);

        style = new Style(".", StyleName.LoginButtonHolder.name());
        style.addProperty("width", "20em");
        style.addProperty("text-align", "center");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("margin-top", "20px");
        addStyle(style);
    }

}