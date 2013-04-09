/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2013-04-09
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.theme;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class VistaWizardPaneTheme extends Theme {

    public static enum StyleName implements IStyleName {
        //@formatter:off
        
        Header, 
        HeaderCaption, 
        HeaderContainer, 
        HeaderToolbar, 
        
        FooterToolbar, 
        
        HighlightedButton, 
        HighlightedAction, 
        
        WizardPanel, 
        WizardStep

        //@formatter:on
    }

    public VistaWizardPaneTheme() {
        initGeneralStyles();
        initWizardPanelStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initGeneralStyles() {
//        initButtonStyles("." + StyleName.HeaderToolbar);
//        initButtonStyles("." + StyleName.FooterToolbar);
//        initHighlightedButtonStyles("." + StyleName.HeaderToolbar);

        Style style = new Style(".", StyleName.Header);
//        style.addProperty("background-color", ThemeColor.object1, 1);
//        style.addProperty("color", ThemeColor.object1, 0.1);
        style.addProperty("width", "100%");
        style.addProperty("white-space", "nowrap");
        style.addProperty("font-size", "1.3em");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.HeaderContainer);
//        style.addProperty("background-color", ThemeColor.object1, 0.3);
        style.addProperty("padding-top", "6px");
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.HeaderCaption);
        style.addProperty("float", "left");
        style.addProperty("padding", "0 1em");
        addStyle(style);

        style = new Style(".", StyleName.HeaderToolbar);
        style.addProperty("height", "100%");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-right", "6px");
        addStyle(style);

        style = new Style(".", StyleName.HeaderContainer, " .", StyleName.HeaderToolbar);
//        style.addProperty("background-color", ThemeColor.object1, 0.3);
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.FooterToolbar);
        style.addProperty("margin", "8px 0");
        style.addProperty("padding", "2px 0");
        style.addProperty("float", "right");
        style.addProperty("border-top", "4px solid");
//        style.addProperty("border-top-color", ThemeColor.foreground, 0.3);
        addStyle(style);

        style = new Style(".", StyleName.FooterToolbar, " .", DefaultWidgetsTheme.StyleName.Toolbar);
        style.addProperty("padding", "2px");
        style.addProperty("float", "right");
        addStyle(style);
    }

    private void initButtonStyles(String selector) {
        Style style = new Style(selector, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.05);
        style.addProperty("color", ThemeColor.foreground, 0);
        style.addProperty("padding", "2px 12px");
        style.addGradient(ThemeColor.foreground, 1, ThemeColor.foreground, 2);
        style.addProperty("font-size", "11px");
        style.addProperty("font-weight", "bold");
        style.addProperty("border-radius", "5px");
        style.addProperty("-moz-border-radius", "5px");
        addStyle(style);

        style = new Style(selector, " .", DefaultWidgetsTheme.StyleName.Button, "-", DefaultWidgetsTheme.StyleDependent.hover);
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        addStyle(style);

        style = new Style(selector, " .", DefaultWidgetsTheme.StyleName.Button, "-", DefaultWidgetsTheme.StyleDependent.disabled);
        style.addProperty("color", ThemeColor.foreground, 0);
        style.addGradient(ThemeColor.foreground, 0.4, ThemeColor.foreground, 0.4);
        addStyle(style);

    }

    private void initHighlightedButtonStyles(String selector) {
        Style style = new Style(selector, " .", StyleName.HighlightedButton);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.05);
        style.addProperty("color", ThemeColor.foreground, 0);
        style.addProperty("padding", "2px 12px");
        style.addGradient(ThemeColor.object1, 1, ThemeColor.object1, 1.6);
        style.addProperty("font-size", "11px");
        style.addProperty("font-weight", "bold");
        style.addProperty("border-radius", "5px");
        style.addProperty("-moz-border-radius", "5px");
        addStyle(style);

        style = new Style(selector, " .", StyleName.HighlightedButton, "-", DefaultWidgetsTheme.StyleDependent.hover);
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        addStyle(style);

        style = new Style(selector, " .", StyleName.HighlightedButton, "-", DefaultWidgetsTheme.StyleDependent.disabled);
        style.addProperty("color", ThemeColor.foreground, 0);
        style.addGradient(ThemeColor.foreground, 0.4, ThemeColor.foreground, 0.4);
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical", " .", StyleName.HighlightedAction);
        style.addProperty("color", ThemeColor.foreground, 0);
//        style.addProperty("background-color", ThemeColor.object1, 0.2);
        style.addGradient(ThemeColor.object1, 0.1, ThemeColor.object1, 0.5);
        style.addProperty("font-weight", "bold");
        addStyle(style);
    }

    protected void initWizardPanelStyles() {
        Style style = new Style(".", StyleName.WizardPanel, " .", StyleName.WizardStep);
        style.addProperty("height", "auto");
//        style.addProperty("border", "1px solid");
//        style.addProperty("border-color", ThemeColor.foreground, 0.6);
//        style.addProperty("margin-top", "5px");
//        style.addProperty("margin-left", "4px");
//        style.addProperty("margin-right", "4px");
        addStyle(style);

    }
}
