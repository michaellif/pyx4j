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

public class ExtraGadgetsTheme extends Theme {

    public static enum StyleName implements IStyleName {
        CommunityEventCaption, CommunityEventTimeAndLocation,

        WeatherIcon, WeatherText, WeatherTemperature, WeatherType

    }

    public ExtraGadgetsTheme() {
        Style style = new Style(".", StyleName.CommunityEventCaption);
        style.addProperty("font-weight", "bolder");
        style.addProperty("text-decoration", "underline");
        style.addProperty("padding-top", "5px");
        addStyle(style);

        style = new Style(".", StyleName.CommunityEventTimeAndLocation);
        style.addProperty("font-style", "italic");
        style.addProperty("font-size", "0.9em");
        addStyle(style);

        style = new Style(".", StyleName.WeatherIcon);
        style.addProperty("vertical-align", "middle");
        style.addProperty("margin", "10px 0");
        addStyle(style);

        style = new Style(".", StyleName.WeatherText);
        style.addProperty("vertical-align", "middle");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.WeatherTemperature);
        style.addProperty("font-size", "2em");
        addStyle(style);

        style = new Style(".", StyleName.WeatherType);
        addStyle(style);

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
