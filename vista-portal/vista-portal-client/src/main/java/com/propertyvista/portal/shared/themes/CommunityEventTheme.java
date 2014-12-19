/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-08
 * @author ArtyomB
 */
package com.propertyvista.portal.shared.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;

public class CommunityEventTheme extends Theme {

    public enum StyleName implements IStyleName {

        CommunityEventCaption, CommunityEventDate, CommunityEventDescription

    }

    public CommunityEventTheme() {
        initTheme();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    public void initTheme() {
        {
            Style style = new Style(".", CommunityEventTheme.StyleName.CommunityEventCaption.name());
            style.addProperty("font-weight", "bolder");
            style.addProperty("padding-top", "5px");
            style.addProperty("text-align", "left");
            addStyle(style);
        }
        {
            Style style = new Style(".", CommunityEventTheme.StyleName.CommunityEventDate.name());
            style.addProperty("font-style", "italic");
            style.addProperty("font-size", "0.8em");
            style.addProperty("text-align", "right");
            style.addProperty("padding", "5px");
            addStyle(style);
        }

        {
            Style style = new Style(".", CommunityEventTheme.StyleName.CommunityEventDescription.name());
            style.addProperty("text-align", "left");
            addStyle(style);
        }

    }

}
