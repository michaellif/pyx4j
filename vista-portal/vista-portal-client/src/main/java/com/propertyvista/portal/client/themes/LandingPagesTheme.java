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
package com.propertyvista.portal.client.themes;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;

/**
 * Defines style names and CSS for the public portal pages as login, sign-up, password reset etc...
 */
public class LandingPagesTheme extends Theme {

    public static enum StyleName implements IStyleName {

        LandingPage, LandingPageContent, PortalLandingButton, LandingCaption, LandingCaptionText, LandingCaptionTextEmph, LandingGreeting, LandingGreetingText, LandingOrLineSeparator, LandingButtonHolder, LandingPageHeader, LandingPageFooter

    }

    public LandingPagesTheme() {
        overrideCommonStyles();

        Style style;

        style = new Style(".", StyleName.PortalLandingButton);
        style.addProperty("display", "inline-block");
        style.addProperty("cursor", "pointer");
        style.addProperty("background-color", "#0099FF");
        style.addProperty("border-color", "#003399");
        style.addProperty("border-style", "outset");
        style.addProperty("border-width", "1px");
        style.addProperty("border-radius", "10px");
        style.addProperty("border-shadow", "2px 2px 2px #0099FF");
        style.addProperty("color", "white");
        style.addProperty("font-size", "15px");
        style.addProperty("font-weight", "bold");
        style.addProperty("font-family", "Arial");
        style.addProperty("text-align", "center");
        style.addProperty("text-shadow", "1px 0 1px #003366");
        style.addProperty("width", "150px");
        style.addProperty("padding", "7px");
        addStyle(style);

        style = new Style(".", StyleName.PortalLandingButton, ":hover");
        style.addProperty("background-color", "#B8DBFF");
        style.addProperty("color", "#003366");
        addStyle(style);

        style = new Style(".", StyleName.LandingPage, " .TextBox");
        style.addProperty("border-radius", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("box-shadow", "1px 1px 3px grey inset");
        addStyle(style);

        style = new Style(".", StyleName.LandingCaption);
        style.addProperty("margin-top", "25px");
        style.addProperty("margin-bottom", "25px");
        style.addProperty("text-align", "center");
        style.addProperty("text-shadow", "1px 0 1px gray");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.LandingCaptionText);
        style.addProperty("font-size", "20px");
        addStyle(style);

        style = new Style(".", StyleName.LandingCaptionTextEmph);
        style.addProperty("font-size", "20px");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.LandingOrLineSeparator);
        style.addProperty("border-color", "gray");
        style.addProperty("height", "300px");
        addStyle(style);

        style = new Style(".", StyleName.LandingGreeting.name());
        style.addProperty("height", "100%");
        style.addProperty("width", "100%");
        style.addProperty("display", "block");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.LandingGreetingText.name());
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("font-size", "16px");
        style.addProperty("font-style", "italic");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.LandingButtonHolder.name());
        style.addProperty("width", "100%");
        style.addProperty("text-align", "center");
        style.addProperty("margin-top", "20px");
        addStyle(style);

        style = new Style(".", StyleName.LandingPageHeader.name());
        style.addProperty("width", "50%");
        style.addProperty("height", "100%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "bottom");
        addStyle(style);

        style = new Style(".", StyleName.LandingPageContent.name());
        style.addProperty("width", "50%");
        style.addProperty("height", "100%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.LandingPageFooter.name());
        style.addProperty("width", "50%");
        style.addProperty("height", "100%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "top");
        addStyle(style);

    }

    private void overrideCommonStyles() {
        Style style;

        style = new Style(".", StyleName.LandingPage, " .WidgetDecoratorLabelHolder");
        style.addProperty("padding", "0px");
        style.addProperty("margin", "0px");
        addStyle(style);
    }
}
