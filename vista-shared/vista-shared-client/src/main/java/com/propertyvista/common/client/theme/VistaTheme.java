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
package com.propertyvista.common.client.theme;

import com.pyx4j.commons.css.CSSClass;
import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Selector;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;

public class VistaTheme extends Theme {

    public static enum StyleName implements IStyleName {
        InfoMessage, WarningMessage, ErrorMessage
    }

    public VistaTheme() {

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initGeneralStyles() {
        Style style = new Style("html");
        style.addProperty("-webkit-tap-highlight-color", "rgba(255, 255, 255, 0)");
        addStyle(style);

        style = new Style("input, select, textarea");
        style.addProperty("font-size", "100%");
        addStyle(style);

        style = new Style("table");
        style.addProperty("border-collapse", "collapse");
        style.addProperty("border-spacing", "0");
        addStyle(style);

        style = new Style("td");
        style.addProperty("padding", "0px");
        addStyle(style);

        style = new Style("p");
        style.addProperty("margin", "0.3em");
        addStyle(style);

        style = new Style("h1");
        style.addProperty("font-size", "2em");
        style.addProperty("line-height", "2.5em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h2");
        style.addProperty("font-size", "1.5em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h3");
        style.addProperty("font-size", "1.17em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h4, blockquote");
        style.addProperty("font-size", "1.12em");
        style.addProperty("padding-bottom", "0.3px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h5");
        style.addProperty("font-size", "1.08em");
        style.addProperty("padding-bottom", "0.2px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h6");
        style.addProperty("font-size", ".75em");
        style.addProperty("padding-bottom", "0.2px");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style("h1, h2, h3, h4, h5, h6, b, strong");
        style.addProperty("font-weight", "bolder");
        addStyle(style);

        style = new Style("blockquote, ul, fieldset, form, ol, dl, dir, menu");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style("blockquote");
        style.addProperty("margin-left", "40px");
        style.addProperty("margin-right", "40px");
        addStyle(style);

        style = new Style("select:-moz-focusring");
        style.addProperty("color", "transparent");
        style.addProperty("text-shadow", "0 0 0 #000");
        addStyle(style);

    }

    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("background-color", ThemeColor.formBackground);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("margin", "0");
        style.addProperty("border", "none");
        style.addProperty("font", "12px/14px Arial, Helvetica, sans-serif");
        addStyle(style);

    }

    protected void initToolbarStyle() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_Toolbar));
        // style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("padding", "2 2 2 8");

        //style.addProperty("background-color", ThemeColor.OBJECT_TONE1);

        style.addGradient(ThemeColor.object1, 0.2, ThemeColor.object1, 0.5);

        addStyle(style);
    }

    protected void initStatusBarStyle() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_StatusBar));
        style.addProperty("background-color", ThemeColor.object1, 0.8);
        style.addProperty("padding", "2 2 2 8");
        addStyle(style);
    }

    protected void initProgressBarStyles() {
        Style style = new Style(".gwt-ProgressBar-shell");
        style.addProperty("margin", "8px 0");
        addStyle(style);

        style = new Style(".gwt-ProgressBar-bar");
        style.addProperty("background-color", "green");
        style.addProperty("display", "inline-block");
        style.addProperty("height", "8px");
        style.addProperty("border", "1px solid black");
        addStyle(style);

        style = new Style(".gwt-ProgressBar-text");
        style.addProperty("display", "inline-block");
        addStyle(style);
    }

    protected void initMenuBarStyles() {
        Style style = new Style(".gwt-MenuBar");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("color", ThemeColor.foreground, 0.2);
        addStyle(style);

        style = new Style(".gwt-MenuItem");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("background-color", ThemeColor.foreground, 0.2);
        style.addProperty("background", "transparent");
        style.addProperty("color", "#E5F0E1");
        style.addProperty("border", "0");
        addStyle(style);

        style = new Style(".gwt-MenuItem-selected");
        style.addProperty("background", ThemeColor.foreground, 0.8);
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("text-decoration", "underline");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical");
        style.addProperty("margin-top", "0px");
        style.addProperty("margin-left", "0px");
        style.addProperty("font-size", "1.2em");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.object1, 0.8);
        style.addProperty("background", ThemeColor.foreground, 0.2);
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical .gwt-MenuItem");
        style.addProperty("padding", "4px 14px 4px 1px");
        style.addProperty("color", "#666666");
        addStyle(style);

        style = new Style(".gwt-MenuBar-horizontal");
        addStyle(style);

        style = new Style(".gwt-MenuBar-horizontal .gwt-MenuItem");
        style.addProperty("vertical-align", "bottom");
        style.addProperty("background", "transparent");
        addStyle(style);
    }

    protected void initDialogBoxStyles() {

        Style style = new Style(".gwt-DialogBox");
        style.addProperty("border", "2px outset");
        style.addProperty("border-color", ThemeColor.object1, 0.6);
        style.addProperty("background-color", ThemeColor.object1, 0.6);
        addStyle(style);

        style = new Style(".gwt-DialogBox .Caption");
        style.addProperty("background-color", ThemeColor.object1, 0.8);
        addStyle(style);

        style = new Style(".gwt-PopupPanelGlass");
        style.addProperty("background-color", "#000");
        style.addProperty("opacity", "0.1");
        style.addProperty("filter", "alpha(opacity=10)");
        style.addProperty("z-index", "20");
        addStyle(style);
    }

    protected void initTabPanelStyles() {
        Style style = new Style(".gwt-TabLayoutPanel");
        addStyle(style);

        style = new Style(".gwt-TabLayoutPanelTabs");
        style.addProperty("padding-top", "0.5em");
        style.addProperty("padding-left", "0.5em");
        style.addProperty("border-bottom", "4px solid");
        style.addProperty("border-bottom-color", ThemeColor.foreground, 0.3);
        addStyle(style);

        style = new Style(".gwt-TabLayoutPanelTab");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("height", "2em");
        style.addProperty("line-height", "2em");
        style.addProperty("text-align", "center");
        style.addProperty("margin-right", "1px");
        style.addProperty("margin-left", "1px");
        style.addProperty("padding-right", "10px");
        style.addProperty("padding-left", "10px");
        style.addProperty("border-right", "1px solid");
        style.addProperty("border-right-color", ThemeColor.foreground, 0.6);
        style.addProperty("border-left", "1px solid");
        style.addProperty("border-left-color", ThemeColor.foreground, 0.6);
        style.addProperty("border-top", "1px solid");
        style.addProperty("border-top-color", ThemeColor.foreground, 0.6);
        style.addProperty("background", ThemeColor.object1, 1);
        style.addProperty("color", ThemeColor.foreground, 0);
        style.addProperty("display", "inline-block");
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(".gwt-TabLayoutPanelTab-selected");
        style.addProperty("cursor", "default");
        style.addGradient(ThemeColor.foreground, 0.1, ThemeColor.foreground, 0.30);
        style.addProperty("color", "#333");
        addStyle(style);

        String prefix = VistaTabLayoutPanel.TAB_DIASBLED_STYLE;
        style = new Style(Selector.valueOf(prefix));
        style.addProperty("background", ThemeColor.object1, 0.6);
        style.addProperty("color", ThemeColor.foreground, 0.2);
        style.addProperty("cursor", "default");
        addStyle(style);
    }

    protected void initGwtButtonStyles() {
        // GWT Button, Code is a copy from com.google.gwt.user.theme.standard.Standard
        String gwtButton = ".gwt-Button";
        Style style = new Style(gwtButton);
        style.addProperty("margin", "0");
        style.addProperty("padding", "3px 5px");
        style.addProperty("text-decoration", "none");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("background", "url(images/button-bkg.png) repeat-x 0px -27px");
        style.addProperty("border", "1px outset #ccc");
        addStyle(style);

        style = new Style(CSSClass.gwtButtonDefault);
        style.addProperty("border", "1px outset #3090C7");
        addStyle(style);

        // The next is added and is not in default GWT code.
        style = new Style(gwtButton + ":focus");
        style.addProperty("border", "1px outset #3090C7");
        addStyle(style);

        style = new Style(gwtButton + ":active");
        style.addProperty("border", "1px inset #ccc");
        addStyle(style);

        style = new Style(gwtButton + ":hover");
        style.addProperty("border-color", "#9cf #69e #69e #7af");
        addStyle(style);

        style = new Style(gwtButton + "[disabled]");
        style.addProperty("cursor", "default");
        style.addProperty("color", "#888");
        style.addProperty("background", "url(images/button-bkg.png) repeat-x 0px -35px");
        style.addProperty("border", "1px ridged #cccccc");
        addStyle(style);

        style = new Style(gwtButton + "[disabled]:hover");
        style.addProperty("border", "1px outset #555");
        addStyle(style);
    }

    protected void initTooltipStyle() {
        Style style = new Style(CSSClass.pyx4j_Tooltip);
        style.addProperty("border", "1px solid #000000");
        style.addProperty("background-color", "#FCFFDB");
        style.addProperty("padding", "1px 3px 1px 3px");
        style.addProperty("color", "#000000");
        addStyle(style);
        style = new Style(CSSClass.pyx4j_Tooltip_Shadow);
        style.addProperty("background-color", "gray");
        style.addProperty("opacity", "0.2");
        style.addProperty("filter", "alpha(opacity=20)");

        addStyle(style);
    }

    protected void initTreeStyle() {
        Style style = new Style(".gwt-TreeItem");
        style.addProperty("padding", "1px 0px");
        style.addProperty("margin", "0px");
        style.addProperty("white-space", "nowrap");
        style.addProperty("cursor", "hand");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".gwt-TreeItem-selected");
        style.addProperty("background", ThemeColor.object1, 0.8);
        addStyle(style);
    }

    protected void initHyperlinkStyle() {
    }

    protected void initPhotoalbomStyle() {
        Style style = new Style(CSSClass.pyx4j_Photoalbom_Thumbnail);
        style.addProperty("background-color", "#F6F9FF");
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", "#E5ECF9");
        style.addProperty("-webkit-box-shadow", "4px 4px 2px #aaa");
        style.addProperty("-moz-box-shadow", "4px 4px 2px #aaa");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowPopup);
        style.addProperty("background-color", "#F6F9FF");
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", "#E5ECF9");
        style.addProperty("-webkit-box-shadow", "10px 10px 5px #aaa");
        style.addProperty("-moz-box-shadow", "10px 10px 5px #aaa");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_Photoalbom_Caption);
        style.addProperty("color", "#333");
        style.addProperty("font-weight", "bold");
        addStyle(style);

    }

    protected void initCellListStyle() {
        // Available Selectors
        // cellListWidget
        // cellListEvenItem
        // cellListOddItem
        // cellListKeyboardSelectedItem
        // cellListSelectedItem

        Style style = new Style(".cellListEvenItem");
        style.addProperty("background-color", ThemeColor.foreground, 0.05);
        style.addProperty("color", ThemeColor.foreground, 0.9);
        addStyle(style);

        style = new Style(".cellListOddItem");
        style.addProperty("background-color", "white");
        style.addProperty("color", ThemeColor.foreground, 0.9);
        addStyle(style);

        style = new Style(".cellListSelectedItem");
        style.addProperty("background-color", ThemeColor.object1, 0.8);
        style.addProperty("color", ThemeColor.foreground, 0);
        addStyle(style);

    }

    protected void initMessageStyles() {
        Style style = new Style(".", StyleName.ErrorMessage);
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
        style.addProperty("color", "#EF231B");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.WarningMessage);
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
        style.addProperty("color", "#F68308");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.InfoMessage);
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
        style.addProperty("color", "#60A11B");
        style.addProperty("text-align", "left");
        addStyle(style);
    }
}