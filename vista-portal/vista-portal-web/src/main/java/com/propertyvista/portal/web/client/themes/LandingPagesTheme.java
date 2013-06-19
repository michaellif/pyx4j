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
import com.pyx4j.commons.css.ThemeId;

/**
 * Defines style names and CSS for the public portal pages as login, sign-up, password reset etc...
 */
public class LandingPagesTheme extends Theme {

    public static enum StyleName implements IStyleName {
        //@formatter:off
        LandingViewPanel,
        LandingViewSectionContent,
        LandingButton,
        LandingCaption,
        LandingCaptionText,
        LandingCaptionTextEmph,
        LandingGreetingPanel,
        LandingGreetingText,
        LandingOrLineSeparator,
        LandingButtonHolder,
        LandingViewSectionHeader,
        LandingViewSectionFooter,
        LandingInputField, 
        LandingTermsAndConditionsBox,
        
        LoginImageDecoration,
        LoginImageDecorationImage,
        LoginImageDecorationLabel,
        
        SignUpGreetingRow1,
        SignUpGreetingRow2,
        SignUpGreetingImageHolder
        //@formatter:on

    }

    public LandingPagesTheme() {
        overrideCommonStyles();

        Style style;
        style = new Style(".", StyleName.LandingViewPanel);
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(".", StyleName.LandingCaption);
        style.addProperty("margin-top", "25px");
        style.addProperty("margin-bottom", "25px");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.LandingCaptionText);
        addStyle(style);

        style = new Style(".", StyleName.LandingCaptionTextEmph);
        addStyle(style);

        style = new Style(".", StyleName.LandingOrLineSeparator);
        style.addProperty("position", "absolute");
        style.addProperty("top", "0%");
        style.addProperty("bottom", "0%");
        style.addProperty("left", "50%");
        style.addProperty("right", "50%");
        style.addProperty("border-color", "gray");

        addStyle(style);

        style = new Style(".", StyleName.LandingGreetingPanel.name());
        style.addProperty("width", "300px");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("display", "block");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.LandingGreetingText.name());
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        addStyle(style);

        style = new Style(".", StyleName.LandingButtonHolder.name());
        style.addProperty("width", "20em"); // TODO should be same as width of component set by WatermarkDecoratorBuilder
        style.addProperty("text-align", "center");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("margin-top", "20px");
        addStyle(style);

        style = new Style(".", StyleName.LandingViewSectionHeader.name());
        style.addProperty("width", "50%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "bottom");
        addStyle(style);

        style = new Style(".", StyleName.LandingViewSectionContent.name());
        style.addProperty("width", "50%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.LandingViewSectionFooter.name());
        style.addProperty("width", "50%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.LandingInputField.name()); // TODO should be same as width of component set by WatermarkDecoratorBuilder
        style.addProperty("width", "20em");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        addStyle(style);

        style = new Style(".", StyleName.LandingTermsAndConditionsBox.name()); // TODO should be same as width of component set by WatermarkDecoratorBuilder        
        style.addProperty("margin-top", "20px");
        addStyle(style);

        //  login image decorator styles
        style = new Style(".", StyleName.LoginImageDecoration.name());
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.LoginImageDecorationImage.name());
        style.addProperty("vertical-align", "middle");
        style.addProperty("display", "inline");
        addStyle(style);

        style = new Style(".", StyleName.LoginImageDecorationLabel.name());
        style.addProperty("vertical-align", "middle");
        style.addProperty("display", "inline");
        style.addProperty("margin-left", "20px");
        addStyle(style);

        // sign up greeting styles:
        style = new Style(".", StyleName.SignUpGreetingRow1.name());
        style.addProperty("text-align", "left");
        style.addProperty("font-size", "20px");
        style.addProperty("font-weight", "bold");
        style.addProperty("display", "block");
        addStyle(style);

        style = new Style(".", StyleName.SignUpGreetingRow2.name());
        style.addProperty("text-align", "right");
        style.addProperty("font-size", "20px");
        style.addProperty("font-weight", "bold");
        style.addProperty("display", "block");
        addStyle(style);

        style = new Style(".", StyleName.SignUpGreetingImageHolder.name());
        style.addProperty("text-align", "right");
        addStyle(style);

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    private void overrideCommonStyles() {
        Style style;

        style = new Style(".", StyleName.LandingViewPanel, " .WidgetDecoratorLabelHolder");
        style.addProperty("padding", "0px");
        style.addProperty("margin", "0px");
        addStyle(style);

    }
}
