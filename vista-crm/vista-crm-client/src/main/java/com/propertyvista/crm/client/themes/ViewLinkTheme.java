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
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class ViewLinkTheme extends Theme {
    public static enum StyleName implements IStyleName {
        ViewLink,
    }

    public ViewLinkTheme() {

        Style style = new Style(".", StyleName.ViewLink, ".", WidgetTheme.StyleName.Anchor);
        style.addProperty("padding-left", "4px");
        style.addProperty("color", ThemeColor.object1, 0.8);
        style.addProperty("font-size", "0.8em");
        style.addProperty("font-style", "italic");
        addStyle(style);

    }

    @Override
    public ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }
}