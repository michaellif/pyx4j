/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style.window;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.style.CSSClass;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.Theme;
import com.pyx4j.widgets.client.style.ThemeColor;
import com.pyx4j.widgets.client.tabpanel.TabPanel;

public class WindowsTheme extends Theme {

    public static String pyx4j_TabBottom = "pyx4j_TabBottom";

    public WindowsTheme() {
        initThemeColors();
        initStyles();
    }

    protected void initStyles() {
        initGeneralStyles();
        initBodyStyles();
        initSectionStyles();
        initToolbarStyle();
        initBarSeparatorStyle();
        initStatusBarStyle();
        initProgressBarStyles();
        initMenuBarStyles();
        initTabPanelStyles();
        initDialogBoxStyles();
        initDialogPanelStyles();
        initGwtButtonStyles();
        initComboBoxStyles();
        initButtonStyles();
        initTooltipStyle();
        initTreeStyle();
        initGlassPanelStyle();
        initTextBoxStyle();
        initCheckBoxStyle();
        initListBoxStyle();
        initDatePickerStyle();
        initHyperlinkStyle();
        initGroupBoxStyle();
        initPhotoalbomStyle();
        initSlideshowActionStyle();
        initSuggestBoxStyle();
        initBannerStyle();
    }

    protected void initThemeColors() {
        putThemeColor(ThemeColor.OBJECT_TONE1, 0xece9d8);
        putThemeColor(ThemeColor.OBJECT_TONE2, 0xfdfae9);
        putThemeColor(ThemeColor.OBJECT_TONE3, 0xdbd8c7);
        putThemeColor(ThemeColor.BORDER, 0x666666);
        putThemeColor(ThemeColor.GROUP_BORDER, 0x387CBB);
        putThemeColor(ThemeColor.SELECTION, 0x86adc4);
        putThemeColor(ThemeColor.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemeColor.TEXT, 0x000000);
        putThemeColor(ThemeColor.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, 0xfcba84);
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemeColor.SEPARATOR, 0xeeeeee);
    }

    protected void initGeneralStyles() {
        Style style = new Style("td");
        style.addProperty("padding", "0px");
        addStyle(style);

    }

    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("color", ThemeColor.TEXT);
        addStyle(style);
    }

    protected void initSectionStyles() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_Border));
        style.addProperty("background-color", ThemeColor.BORDER);
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_SelectionBorder));
        style.addProperty("background-color", ThemeColor.SELECTION);
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_Background));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_Content));
        style.addProperty("background-color", ThemeColor.TEXT_BACKGROUND);
        addStyle(style);
    }

    protected void initToolbarStyle() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_Toolbar));
        // style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("padding", "2 2 2 8");

        style.addProperty("filter", "progid:DXImageTransform.Microsoft.gradient(startColorstr='#cccccc', endColorstr='#000000')");

        style.addProperty("background", "-webkit-gradient(linear, left top, left bottom, from(#ccc), to(#000))");

        style.addProperty("background", "-moz-linear-gradient(top,  #ccc,  #000)");

        addStyle(style);
    }

    protected void initStatusBarStyle() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_StatusBar));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("padding", "2 2 2 8");
        addStyle(style);
    }

    protected void initBarSeparatorStyle() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_BarSeparator));
        style.addProperty("border-left-width", "2px");
        style.addProperty("border-left-style", "ridge");
        style.addProperty("border-color", ThemeColor.OBJECT_TONE1);

        style.addProperty("margin-left", "3px");
        addStyle(style);
    }

    protected void initProgressBarStyles() {
        Style style = new Style(".gwt-ProgressBar-shell");
        style.addProperty("background-color", ThemeColor.BORDER);
        addStyle(style);

        style = new Style(".gwt-ProgressBar-bar");
        style.addProperty("background-color", ThemeColor.SELECTION);
        addStyle(style);

        style = new Style(".gwt-ProgressBar-text");
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        addStyle(style);
    }

    protected void initMenuBarStyles() {
        Style style = new Style(".gwt-MenuBar");
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(".gwt-MenuBar .gwt-MenuItem");
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(".gwt-MenuBar .gwt-MenuItem-selected");
        style.addProperty("background", ThemeColor.SELECTION);
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical");
        style.addProperty("margin-top", "0px");
        style.addProperty("margin-left", "0px");
        style.addProperty("background", ThemeColor.TEXT_BACKGROUND);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.SELECTION);
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical .gwt-MenuItem");
        style.addProperty("padding", "4px 14px 4px 1px");
        addStyle(style);

        style = new Style(".gwt-MenuBar-horizontal");
        addStyle(style);

        style = new Style(".gwt-MenuBar-horizontal .gwt-MenuItem");
        style.addProperty("padding", "0px 10px");
        style.addProperty("vertical-align", "bottom");
        addStyle(style);
    }

    protected void initDialogBoxStyles() {

        Style style = new Style(".gwt-DialogBox");
        style.addProperty("border-width", "2px");
        style.addProperty("border-style", "outset");
        style.addProperty("border-color", ThemeColor.BORDER);
        style.addProperty("background-color", ThemeColor.OBJECT_TONE3);
        addStyle(style);

        style = new Style(".gwt-DialogBox .Caption");
        style.addProperty("background-color", ThemeColor.SELECTION);
        addStyle(style);

        style = new Style(".gwt-PopupPanelGlass");
        style.addProperty("background-color", "#000");
        style.addProperty("opacity", "0.1");
        style.addProperty("filter", "alpha(opacity=10)");
        style.addProperty("z-index", "20");
        addStyle(style);
    }

    protected void initDialogPanelStyles() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_Dialog));
        style.addProperty("background-color", ThemeColor.SELECTION);
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Dialog_Caption));
        style.addProperty("background", ThemeColor.SELECTION);
        style.addProperty("filter", "alpha(opacity=95)");
        style.addProperty("opacity", "0.95");
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Dialog_Resizer));
        style.addProperty("background", ThemeColor.SELECTION);
        style.addProperty("filter", "alpha(opacity=95)");
        style.addProperty("opacity", "0.95");
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Dialog_Content));
        style.addProperty("background-color", ThemeColor.TEXT_BACKGROUND);
        addStyle(style);
    }

    protected void initTabPanelStyles() {
        initTopTabPanelStyles(TabPanel.pyx4j_Tab);
        initBottomTabPanelStyles(pyx4j_TabBottom);

    }

    private void initTopTabPanelStyles(String prefix) {
        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.PanelBottom));
        style.addProperty("padding", "2px");
        style.addProperty("margin", "0px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarMoveLeft));
        style.addProperty("margin", "3px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarMoveRight));
        style.addProperty("margin", "3px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarItem));
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("text-align", "center");
        style.addProperty("border-right-width", "1px");
        style.addProperty("border-right-style", "solid");
        style.addProperty("border-right-color", ThemeColor.BORDER);
        style.addProperty("background-color", ThemeColor.OBJECT_TONE3);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarItem, TabPanel.StyleDependent.first));
        style.addProperty("border-left-width", "1px");
        style.addProperty("border-left-style", "solid");
        style.addProperty("border-left-color", ThemeColor.BORDER);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarItem, TabPanel.StyleDependent.selected));
        style.addProperty("cursor", "default");
        style.addProperty("background-color", ThemeColor.SELECTION);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarItem, TabPanel.StyleDependent.selected), Selector.valueOf(prefix,
                TabPanel.StyleSuffix.BarItemLabel));
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarItemLabel));
        style.addProperty("margin", "3px");
        addStyle(style);

    }

    private void initBottomTabPanelStyles(String prefix) {
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("background-color", "#ECE9D8");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.PanelBottom));
        style.addProperty("padding", "2px");
        style.addProperty("margin", "0px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarMoveLeft));
        style.addProperty("margin", "3px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarMoveRight));
        style.addProperty("margin", "3px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarItem));
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("text-align", "center");
        style.addProperty("border-right-width", "1px");
        style.addProperty("border-right-style", "solid");
        style.addProperty("border-right-color", ThemeColor.BORDER);
        style.addProperty("background-color", ThemeColor.OBJECT_TONE3);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarItem, TabPanel.StyleDependent.first));
        style.addProperty("border-left-width", "1px");
        style.addProperty("border-left-style", "solid");
        style.addProperty("border-left-color", ThemeColor.BORDER);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarItem, TabPanel.StyleDependent.selected));
        style.addProperty("cursor", "default");
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarItem, TabPanel.StyleDependent.selected), Selector.valueOf(prefix,
                TabPanel.StyleSuffix.BarItemLabel));
        style.addProperty("color", "black");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TabPanel.StyleSuffix.BarItemLabel));
        style.addProperty("margin-left", "2px");
        style.addProperty("margin-right", "2px");
        addStyle(style);

    }

    protected void initComboBoxStyles() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_Picker));
        style.addProperty("border", "1px solid transparent");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("outline", "none");
        style.addProperty("width", "18px");
        style.addProperty("height", "22px");
        style.addProperty("background", "url('" + ImageFactory.getImages().comboBoxPicker().getURL() + "') no-repeat 100%");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_Picker, "-hover");
        style.addProperty("background", "url('" + ImageFactory.getImages().comboBoxPickerHover().getURL() + "') no-repeat 100%");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_Picker, "-pushed");
        style.addProperty("background", "url('" + ImageFactory.getImages().comboBoxPickerPushed().getURL() + "') no-repeat 100%");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_PickerPanel);
        style.addProperty("border-color", ThemeColor.BORDER);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("background-color", "#fff");
        addStyle(style);

        addStyle(style);
        style = new Style(CSSClass.pyx4j_PickerPanel, " td");
        style.addProperty("padding", "0px");

        style = new Style(CSSClass.pyx4j_PickerPanel, " table");
        style.addProperty("border-collapse", "collapse");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_PickerLine, " .gwt-TreeItem-selected");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_PickerLine_Selected);
        style.addProperty("background", "lightGray");
        addStyle(style);

    }

    protected void initButtonStyles() {
        Style style = new Style(CSSClass.pyx4j_ButtonContainer);
        style.addProperty("height", "22px");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_ButtonContent);
        style.addProperty("padding-left", "2px");
        style.addProperty("padding-right", "2px");
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "outset");
        style.addProperty("border-color", ThemeColor.OBJECT_TONE3);
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("outline", "none");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_ButtonImage);
        style.addProperty("padding-right", "4px");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button + "-hover" + " ." + CSSClass.pyx4j_ButtonContent);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "outset");
        style.addProperty("border-color", ThemeColor.OBJECT_TONE2);
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button + "-pushed" + " ." + CSSClass.pyx4j_ButtonContent);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "ridge");
        style.addProperty("border-color", ThemeColor.OBJECT_TONE3);
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button + "-checked" + " ." + CSSClass.pyx4j_ButtonContent);
        style.addProperty("background", ThemeColor.OBJECT_TONE2);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "inset");
        style.addProperty("border-color", ThemeColor.OBJECT_TONE3);
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
        style.addProperty("border", "2px outset #ccc");
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
        style.addProperty("background", ThemeColor.SELECTION);
        addStyle(style);
    }

    protected void initGlassPanelStyle() {
        Style style = new Style(CSSClass.pyx4j_GlassPanel_SemiTransparent);
        style.addProperty("background-color", "#000");
        style.addProperty("opacity", "0.2");
        style.addProperty("filter", "alpha(opacity=20)");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_GlassPanel_Transparent);
        addStyle(style);

        style = new Style(CSSClass.pyx4j_GlassPanel_SemiTransparent_Label);
        style.addProperty("background-color", "#FFFBD3");
        style.addProperty("opacity", "0.8");
        style.addProperty("filter", "alpha(opacity=80)");
        style.addProperty("padding", "3px");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_GlassPanel_Transparent_Label);
        style.addProperty("background-color", "#FFFBD3");
        style.addProperty("opacity", "0.8");
        style.addProperty("filter", "alpha(opacity=80)");
        style.addProperty("padding", "3px");
        addStyle(style);

    }

    protected void initCheckBoxStyle() {
        Style style = new Style(CSSClass.pyx4j_CheckBox);
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }

    protected void initTextBoxStyle() {
        Style style = new Style(CSSClass.pyx4j_TextBox);
        style.addProperty("border-color", ThemeColor.BORDER);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("background-color", "#fff");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_TextBox, " td");
        style.addProperty("padding", "0px");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_TextBox, "-watermark");
        style.addProperty("color", "gray");
        addStyle(style);

    }

    protected void initListBoxStyle() {
        Style style = new Style(CSSClass.pyx4j_ListBox);
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }

    protected void initDatePickerStyle() {

        Style style = new Style(".gwt-DatePicker");
        style.addProperty("margin", "2px 4px");
        style.addProperty("border", "1px solid #A2BBDD");
        style.addProperty("background-color", "white");
        style.addProperty("color", ThemeColor.TEXT);
        addStyle(style);

        style = new Style(".gwt-DatePicker td, .datePickerMonthSelector td:focus");
        style.addProperty("outline-style", "none");
        style.addProperty("outline-width", "medium");
        addStyle(style);

        style = new Style(".datePickerDays");
        style.addProperty("background", "white none repeat scroll 0 0");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".datePickerDay, .datePickerWeekdayLabel, .datePickerWeekendLabel");
        style.addProperty("font-size", "75%");
        style.addProperty("outline-color", ThemeColor.TEXT);
        style.addProperty("outline-style", "none");
        style.addProperty("outline-width", "medium");
        style.addProperty("padding", "4px");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".datePickerWeekdayLabel, .datePickerWeekendLabel");
        style.addProperty("background", "#C3D9FF none repeat scroll 0 0");
        style.addProperty("cursor", "default");
        style.addProperty("padding", "0 4px 2px");
        addStyle(style);

        style = new Style(".datePickerDay");
        style.addProperty("cursor", "pointer");
        style.addProperty("padding", "4px");
        addStyle(style);

        style = new Style(".datePickerDayIsToday");
        style.addProperty("border", "1px solid black");
        style.addProperty("padding", "3px");
        addStyle(style);

        style = new Style(".datePickerDayIsWeekend");
        style.addProperty("background", "#EEEEEE none repeat scroll 0 0");
        addStyle(style);

        style = new Style(".datePickerDayIsFiller");
        style.addProperty("color", "#888888");
        addStyle(style);

        style = new Style(".datePickerDayIsValue");
        style.addProperty("background", "#AACCEE none repeat scroll 0 0");
        addStyle(style);

        style = new Style(".datePickerDayIsDisabled");
        style.addProperty("color", "#AAAAAA");
        style.addProperty("font-style", "italic");
        addStyle(style);

        style = new Style(".datePickerDayIsHighlighted");
        style.addProperty("background", "#F0E68C none repeat scroll 0 0");
        addStyle(style);

        style = new Style(".datePickerDayIsValueAndHighlighted");
        style.addProperty("background", "#BBDDD9 none repeat scroll 0 0");
        addStyle(style);

        style = new Style(".datePickerMonthSelector");
        style.addProperty("background", "#C3D9FF none repeat scroll 0 0");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style("td.datePickerMonth");
        style.addProperty("color", "blue");
        style.addProperty("font-size", "70%");
        style.addProperty("font-weight", "bold");
        style.addProperty("text-align", "center");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".datePickerPreviousButton, .datePickerNextButton");
        style.addProperty("color", "blue");
        style.addProperty("cursor", "pointer");
        style.addProperty("font-size", "120%");
        style.addProperty("line-height", "1em");
        style.addProperty("padding", "0 4px");
        addStyle(style);

    }

    protected void initHyperlinkStyle() {
    }

    protected void initGroupBoxStyle() {

        Style style = new Style(CSSClass.pyx4j_GroupBox);
        style.addProperty("padding", "5px");
        style.addProperty("margin", "3px");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_GroupBox, "-expanded");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.GROUP_BORDER);
        addStyle(style);

        style = new Style(CSSClass.pyx4j_GroupBox, "-collapsed");
        style.addProperty("border", "none");
        style.addProperty("border-top", "1px solid");
        style.addProperty("border-color", ThemeColor.GROUP_BORDER);
        addStyle(style);

        style = new Style(CSSClass.pyx4j_GroupBox_Caption);
        style.addProperty("padding", "5px 2px 2px 2px");
        style.addProperty("verticalAlign", "top");
        style.addProperty("color", ThemeColor.GROUP_BORDER);
        addStyle(style);
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

    protected void initSlideshowActionStyle() {

        Style style = new Style(CSSClass.pyx4j_SlideshowAction);
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowItem().getURL() + "') no-repeat");
        style.addProperty("width", "17px");
        style.addProperty("height", "16px");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-disabled");
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-left");
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowLeft().getURL() + "') no-repeat");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-right");
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowRight().getURL() + "') no-repeat");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-selected");
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowSelectedItem().getURL() + "') no-repeat");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-playing");
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowPause().getURL() + "') no-repeat");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-paused");
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowPlay().getURL() + "') no-repeat");
        addStyle(style);

    }

    protected void initSuggestBoxStyle() {
        Style style = new Style(".gwt-SuggestBoxPopup");
        style.addProperty("background-color", "white");
        style.addProperty("padding", "2px");
        style.addProperty("border-color", ThemeColor.BORDER);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        addStyle(style);

        style = new Style(".gwt-SuggestBoxPopup .item");
        addStyle(style);

        style = new Style(".gwt-SuggestBoxPopup .item-selected");
        style.addProperty("background-color", "#ffc");
        addStyle(style);

    }

    protected void initBannerStyle() {
        Style style = new Style(CSSClass.pyx4j_Banner);
        addStyle(style);

    }

}
