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
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class CommunicationTheme extends Theme {

    public enum StyleName implements IStyleName {

        CommunicationFolderView, CommunicationThreadName, Button, ButtonText

    }

    public CommunicationTheme() {
        initTheme();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    public void initTheme() {
        {
            Style style = new Style(".", CommunicationTheme.StyleName.CommunicationFolderView.name(), " .",
                    WidgetDecoratorTheme.StyleName.EntityContainerDecoratorCollapsedCaption);
            style.addProperty("font-size", "0.9em");
            addStyle(style);
        }
        {
            Style style = new Style(".", CommunicationTheme.StyleName.CommunicationThreadName.name());
            style.addProperty("font-size", "1.2em");
            style.addProperty("font-weight", "bold");
            addStyle(style);
        }
    }

    protected void initButtonStyle() {
        Style style = new Style(".", StyleName.Button);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.4);
        style.addProperty("padding", "6px 3px");
        style.addProperty("display", "inline-block");
        style.addGradient(ThemeColor.foreground, 0, ThemeColor.foreground, 0.2);
        style.addProperty("cursor", "pointer");
        style.addProperty("-webkit-touch-callout", "none");
        style.addProperty("-webkit-user-select", "none");
        style.addProperty("-khtml-user-select", "none");
        style.addProperty("-moz-user-select", "none");
        style.addProperty("-ms-user-select", "none");
        style.addProperty("user-select", "none");

        addStyle(style);

        style = new Style(".", StyleName.ButtonText);
        style.addProperty("display", "inline");
        style.addProperty("whiteSpace", "nowrap");
        style.addProperty("text-indent", "0");
        style.addProperty("height", "100%");
        style.addProperty("text-align", "center");
        style.addProperty("padding", "0 3px");
        addStyle(style);

        style = new Style(".", StyleName.Button, "-", WidgetsTheme.StyleDependent.hover);
        style.addGradient(ThemeColor.foreground, 0.2, ThemeColor.foreground, 0);
        addStyle(style);

        style = new Style(".", StyleName.Button, "-", WidgetsTheme.StyleDependent.disabled);
        style.addGradient(ThemeColor.foreground, 0.1, ThemeColor.foreground, 0.1);
        style.addProperty("cursor", "default");
        style.addProperty("opacity", "0.4");
        addStyle(style);

    }
}
