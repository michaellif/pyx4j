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
 * Created on Nov 13, 2011
 * @author michaellif
 */
package com.pyx4j.site.client.backoffice.ui;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class PaneTheme extends Theme {

    public static enum StyleName implements IStyleName {

        HeaderCaption,

        HeaderCaptionLabel,

        HeaderContainer,

        HeaderToolbar,

        HeaderToolbarLeft,

        HeaderBreadcrumbs,

        FooterToolbar,

        BreadcrumbsBar,

        BreadcrumbAnchor,

        HighlightedButton,

        HighlightedAction,

        PrimePaneContentHolder,

        Lister,

        ListerFiltersPanel,

        ListerListPanel,

        Visor,

        VisorCloseButton,

        ReportView, ReportControlPanel, ReportControlPanelActionBar, ReportProgressErrorPanel

    }

    public PaneTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        initGeneralStyles();
        initListerStyles();
        initReportStyles();
    }

    private void initGeneralStyles() {

        initButtonStyles("." + StyleName.HeaderToolbar);

        initButtonStyles("." + StyleName.FooterToolbar);

        initHighlightedButtonStyles("." + StyleName.HeaderToolbar);

        Style style = new Style(".", StyleName.HeaderCaption);
        style.addProperty("background-color", ThemeColor.object1, 1);
        style.addProperty("color", ThemeColor.object1, 0.1);
        style.addProperty("width", "100%");
        style.addProperty("white-space", "nowrap");
        style.addProperty("font-size", "1.3em");
        style.addProperty("line-height", "35px");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.HeaderContainer);
        style.addProperty("background-color", ThemeColor.object1, 0.3);
        style.addProperty("padding-top", "6px");
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.HeaderCaptionLabel);
        style.addProperty("float", "left");
        style.addProperty("padding", "0 1em");
        addStyle(style);

        style = new Style(".", StyleName.HeaderToolbar);
        style.addProperty("height", "100%");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-right", "6px");
        addStyle(style);

        style = new Style(".", StyleName.HeaderContainer, " .", StyleName.HeaderToolbar);
        style.addProperty("background-color", ThemeColor.object1, 0.3);
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.HeaderContainer, " .", StyleName.HeaderToolbarLeft);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.HeaderBreadcrumbs);
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(".", StyleName.HeaderContainer, " .", StyleName.HeaderBreadcrumbs);
        style.addProperty("background-color", ThemeColor.object1, 0.3);
        addStyle(style);

        style = new Style(".", StyleName.FooterToolbar);
        style.addProperty("padding", "2px 0px");
        style.addProperty("border-top", "4px solid");
        style.addProperty("border-top-color", ThemeColor.foreground, 0.3);
        addStyle(style);

        style = new Style(".", StyleName.FooterToolbar, " .", WidgetsTheme.StyleName.Toolbar);
        style.addProperty("padding", "2px");
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.BreadcrumbsBar);
        style.addProperty("height", "29px");
        addStyle(style);

        style = new Style(".", StyleName.BreadcrumbAnchor);
        style.addProperty("cursor", "pointer");
        style.addProperty("height", "100%");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".", StyleName.BreadcrumbAnchor, ":hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style(".", StyleName.Visor);
        style.addGradient(ThemeColor.object1, 0, ThemeColor.object1, 0.15);
        style.addProperty("border", "4px solid");
        style.addProperty("border-color", ThemeColor.object1, 1);
        style.addProperty("padding", "2px");
        style.addProperty("margin", "10px");
        style.addProperty("box-shadow", "10px 10px 5px rgba(0, 0, 0, 0.3)");
        addStyle(style);

        style = new Style(".", StyleName.Visor, " .", StyleName.HeaderCaption);
        style.addProperty("background-color", ThemeColor.object1, 0.8);
        addStyle(style);

        style = new Style(".", StyleName.Visor, " .", StyleName.FooterToolbar);
        style.addProperty("border-top-color", ThemeColor.object1, 0.6);
        addStyle(style);

        style = new Style(".", StyleName.VisorCloseButton);
        style.addProperty("float", "right");
        style.addProperty("margin", "11px");
        style.addProperty("border-color", ThemeColor.object1, 1);
        style.addProperty("cursor", "pointer");
        addStyle(style);

    }

    private void initHighlightedButtonStyles(String selector) {
        Style style = new Style(selector, " .", StyleName.HighlightedButton);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.05);
        style.addProperty("color", ThemeColor.foreground, 0);
        style.addGradient(ThemeColor.object1, 1, ThemeColor.object1, 1.6);
        style.addProperty("padding", "2px 12px");
        style.addProperty("font-size", "11px");
        style.addProperty("font-weight", "bold");
        style.addProperty("border-radius", "5px");
        style.addProperty("-moz-border-radius", "5px");
        addStyle(style);

        style = new Style(selector, " .", StyleName.HighlightedButton, "-", WidgetsTheme.StyleDependent.hover);
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        addStyle(style);

        style = new Style(selector, " .", StyleName.HighlightedButton, "-", WidgetsTheme.StyleDependent.disabled);
        style.addProperty("color", ThemeColor.foreground, 0);
        style.addGradient(ThemeColor.foreground, 0.4, ThemeColor.foreground, 0.4);
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical", " .", StyleName.HighlightedAction);
        style.addGradient(ThemeColor.object1, 0.05, ThemeColor.object1, 0.1);
        style.addProperty("font-weight", "bold");
        addStyle(style);
    }

    private void initButtonStyles(String selector) {
        Style style = new Style(selector, " .", WidgetsTheme.StyleName.Button);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.05);
        style.addProperty("color", ThemeColor.foreground, 0);
        style.addGradient(ThemeColor.foreground, 1, ThemeColor.foreground, 2);
        style.addProperty("padding", "2px 12px");
        style.addProperty("margin", "0 4px");
        style.addProperty("font-size", "11px");
        style.addProperty("font-weight", "bold");
        style.addProperty("border-radius", "5px");
        style.addProperty("-moz-border-radius", "5px");
        addStyle(style);

        style = new Style(selector, " .", WidgetsTheme.StyleName.Button, "-", WidgetsTheme.StyleDependent.hover);
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        addStyle(style);

        style = new Style(selector, " .", WidgetsTheme.StyleName.Button, "-", WidgetsTheme.StyleDependent.disabled);
        style.addProperty("color", ThemeColor.foreground, 0);
        style.addGradient(ThemeColor.foreground, 0.4, ThemeColor.foreground, 0.4);
        addStyle(style);

    }

    protected void initListerStyles() {

        Style style = new Style(".", StyleName.Lister);
        addStyle(style);

        style = new Style(".", StyleName.ListerFiltersPanel);
        style.addProperty("background-color", ThemeColor.foreground, 0.05);
        style.addProperty("padding-top", "0.5em");
        addStyle(style);

        style = new Style(".", StyleName.ListerListPanel);
        addStyle(style);

    }

    private void initReportStyles() {
        Style style = new Style(".", PaneTheme.StyleName.ReportControlPanel);
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", PaneTheme.StyleName.ReportControlPanelActionBar);
        style.addProperty("text-align", "center");
        style.addProperty("padding-bottom", "10px");
        addStyle(style);

        style = new Style(".", PaneTheme.StyleName.ReportControlPanel, " .EntityFolderTableDecorator");
        style.addProperty("width", "30em");
        addStyle(style);

        style = new Style(".", PaneTheme.StyleName.ReportView.name(), " .FooterToolbar");
        style.addProperty("height", "2em");
        addStyle(style);

        style = new Style(".", PaneTheme.StyleName.ReportProgressErrorPanel.name());
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "red");
        style.addProperty("width", "100%");
        addStyle(style);

    }
}
