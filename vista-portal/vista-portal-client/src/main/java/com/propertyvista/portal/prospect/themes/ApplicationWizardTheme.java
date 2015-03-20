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
 */
package com.propertyvista.portal.prospect.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class ApplicationWizardTheme extends Theme {

    public static enum StyleName implements IStyleName {
        UnitCard, UnitCardFirstLine, UnitCardSecondLine,

        UnitCardNumber, UnitCardPrice, UnitCardAvailable,

        UnitCardBeds, UnitCardBaths, UnitCardFloor, UnitCardDens,

        UnitCardInfo, UnitCardInfoLeftColumn, UnitCardInfoRightColumn,

        UnitCardSelectButton
    }

    public ApplicationWizardTheme() {
        Style style = new Style(".", StyleName.UnitCard);
        style.addProperty("padding", "10px 0");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.UnitCard, ":hover");
        style.addProperty("background", ThemeColor.contrast2, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.UnitCardFirstLine);
        style.addProperty("display", "inline-block");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardSecondLine);
        style.addProperty("display", "inline-block");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardNumber);
        style.addProperty("font-size", "1.2em");
        style.addProperty("color", ThemeColor.contrast2);
        style.addProperty("padding", "0 10px");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardPrice);
        style.addProperty("font-size", "1.2em");
        style.addProperty("font-weight", "bolder");
        style.addProperty("padding", "0 10px");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardBeds);
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
        style.addProperty("padding", "2px 10px");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardBaths);
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding", "2px 10px");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardFloor);
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding", "2px 10px");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardDens);
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding", "2px 10px");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardAvailable);
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-type", "italic");
        style.addProperty("padding", "0 10px");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardInfo);
        style.addProperty("margin", "10px 0");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardInfoLeftColumn);
        style.addProperty("display", "inline-block");
        style.addProperty("text-align", "left");
        style.addProperty("width", "40%");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardInfoRightColumn);
        style.addProperty("display", "inline-block");
        style.addProperty("text-align", "left");
        style.addProperty("width", "40%");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.UnitCardSelectButton);
        style.addProperty("background", ThemeColor.contrast2);
        style.addProperty("padding", "0 10px");
        style.addProperty("margin", "0 10px");
        addStyle(style);

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
