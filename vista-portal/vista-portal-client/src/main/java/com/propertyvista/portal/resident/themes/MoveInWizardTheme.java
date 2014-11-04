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
 * @version $Id$
 */
package com.propertyvista.portal.resident.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class MoveInWizardTheme extends Theme {

    public enum StyleName implements IStyleName {

        DoItLaterButton

    }

    public MoveInWizardTheme() {
        initTheme();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    public void initTheme() {
        Style style = new Style(".", WidgetsTheme.StyleName.Button, ".", MoveInWizardTheme.StyleName.DoItLaterButton.name());
        style.addProperty("color", ThemeColor.foreground, 0.7);
        style.addProperty("width", "auto");
        style.addProperty("font-size", "0.9em");
        style.addProperty("font-style", "italic");
        addStyle(style);

        style = new Style(".", WidgetsTheme.StyleName.Button, "-hover", ".", MoveInWizardTheme.StyleName.DoItLaterButton.name());
        style.addProperty("color", ThemeColor.foreground, 0.9);
        style.addProperty("text-decoration", "underline");
        addStyle(style);
    }
}
