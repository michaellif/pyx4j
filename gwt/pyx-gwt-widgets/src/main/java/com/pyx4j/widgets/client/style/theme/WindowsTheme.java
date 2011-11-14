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
package com.pyx4j.widgets.client.style.theme;

import static com.pyx4j.widgets.client.tabpanel.TabPanel.StyleName.Tab;
import static com.pyx4j.widgets.client.tabpanel.TabPanel.StyleName.TabBarItem;
import static com.pyx4j.widgets.client.tabpanel.TabPanel.StyleName.TabBarItemLabel;
import static com.pyx4j.widgets.client.tabpanel.TabPanel.StyleName.TabList;
import static com.pyx4j.widgets.client.tabpanel.TabPanel.StyleName.TabListItem;
import static com.pyx4j.widgets.client.tabpanel.TabPanel.StyleName.TabPanelBottom;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.css.CSSClass;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Selector;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.dashboard.CSSNames;
import com.pyx4j.widgets.client.datepicker.images.DatePickerImages;
import com.pyx4j.widgets.client.tabpanel.TabPanel;

public class WindowsTheme extends Theme {

    public static String pyx4j_TabBottom = "pyx4j_TabBottom";

    public WindowsTheme() {
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
        initTopTabPanelStyles();
        initBottomTabPanelStyles();
        initDialogBoxStyles();
        initGwtButtonStyles();
        initComboBoxStyles();
        initButtonStyles();
        initTooltipStyle();
        initTreeStyle();
        initGlassPanelStyle();
        initCheckBoxStyle();
        initDatePickerStyle();
        initHyperlinkStyle();
        initGroupBoxStyle();
        initPhotoalbomStyle();
        initSlideshowActionStyle();
        initSuggestBoxStyle();
        initBannerStyle();
        initMultipleDatePicker();
        initDashboard_Report();
    }

    protected void initGeneralStyles() {
        Style style = new Style("td");
        style.addProperty("padding", "0px");
        addStyle(style);

    }

    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE2);
        style.addProperty("color", ThemeColors.TEXT);
        addStyle(style);
    }

    protected void initSectionStyles() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_Border));
        style.addProperty("background-color", ThemeColors.BORDER);
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_SelectionBorder));
        style.addProperty("background-color", ThemeColors.SELECTION);
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_Background));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE2);
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_Content));
        style.addProperty("background-color", ThemeColors.TEXT_BACKGROUND);
        addStyle(style);
    }

    protected void initToolbarStyle() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_Toolbar));
        // style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("padding", "2 2 2 8");

        //style.addProperty("background-color", ThemeColor.OBJECT_TONE1);

        style.addGradient(ThemeColors.OBJECT_TONE10, ThemeColors.OBJECT_TONE30);

        addStyle(style);
    }

    protected void initStatusBarStyle() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_StatusBar));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE2);
        style.addProperty("padding", "2 2 2 8");
        addStyle(style);
    }

    protected void initBarSeparatorStyle() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_BarSeparator));
        style.addProperty("border-left", "2px ridge");
        style.addProperty("border-left-color", ThemeColors.OBJECT_TONE2);

        style.addProperty("margin-left", "3px");
        addStyle(style);
    }

    protected void initProgressBarStyles() {
        Style style = new Style(".gwt-ProgressBar-shell");
        style.addProperty("background-color", ThemeColors.BORDER);
        addStyle(style);

        style = new Style(".gwt-ProgressBar-bar");
        style.addProperty("background-color", ThemeColors.SELECTION);
        addStyle(style);

        style = new Style(".gwt-ProgressBar-text");
        style.addProperty("color", ThemeColors.SELECTION_TEXT);
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
        style.addProperty("background", ThemeColors.SELECTION);
        style.addProperty("color", ThemeColors.SELECTION_TEXT);
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical");
        style.addProperty("margin-top", "0px");
        style.addProperty("margin-left", "0px");
        style.addProperty("background", ThemeColors.TEXT_BACKGROUND);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.SELECTION);
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
        style.addProperty("border", "2px outset");
        style.addProperty("border-color", ThemeColors.BORDER);
        style.addProperty("background-color", ThemeColors.OBJECT_TONE4);
        addStyle(style);

        style = new Style(".gwt-DialogBox .Caption");
        style.addProperty("background-color", ThemeColors.SELECTION);
        addStyle(style);

        style = new Style(".gwt-PopupPanelGlass");
        style.addProperty("background-color", "#000");
        style.addProperty("opacity", "0.1");
        style.addProperty("filter", "alpha(opacity=10)");
        style.addProperty("z-index", "20");
        addStyle(style);
    }

    private void initTopTabPanelStyles() {
        Style style = new Style((IStyleName) Tab);
        style.addProperty("margin-top", "2px");
        style.addProperty("margin-left", "6px");
        addStyle(style);

        style = new Style((IStyleName) TabPanelBottom);
        style.addProperty("padding", "2px");
        style.addProperty("margin", "0px");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE1);
        addStyle(style);

        style = new Style((IStyleName) TabBarItem);
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("height", "2em");
        style.addProperty("text-align", "center");
        style.addProperty("margin-right", "1px");
        style.addProperty("margin-left", "1px");
        style.addProperty("border-right", "1px solid");
        style.addProperty("border-right-color", ThemeColors.SELECTION);
        style.addProperty("border-left", "1px solid");
        style.addProperty("border-left-color", ThemeColors.SELECTION);
        style.addProperty("border-top", "1px solid");
        style.addProperty("border-top-color", ThemeColors.SELECTION);
        style.addGradient(ThemeColors.OBJECT_TONE10, ThemeColors.OBJECT_TONE30);
        addStyle(style);

        style = new Style(TabBarItem, "-", TabPanel.StyleDependent.selected);
        style.addProperty("cursor", "default");
        style.addProperty("background", ThemeColors.SELECTION);
        style.addProperty("color", "white");
        addStyle(style);

        style = new Style(TabBarItem, TabPanel.StyleDependent.hover);
        style.addProperty("background", ThemeColors.OBJECT_TONE3);
        addStyle(style);

        style = new Style(".", TabBarItem, "-", TabPanel.StyleDependent.selected, " .", TabBarItemLabel);
        //style.addProperty("color", ThemeColor.SELECTION_TEXT);
        addStyle(style);

        style = new Style((IStyleName) TabBarItemLabel);
        style.addProperty("margin", "3px");
        addStyle(style);

        style = new Style((IStyleName) TabList);
        style.addProperty("background-color", "white");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.SELECTION);
        addStyle(style);

        style = new Style((IStyleName) TabListItem);
        style.addProperty("color", "black");
        style.addProperty("padding", "4px 14px 4px 1px");
        addStyle(style);

        style = new Style(TabListItem, TabPanel.StyleDependent.hover);
        style.addProperty("background", ThemeColors.SELECTION);
        style.addProperty("color", ThemeColors.SELECTION_TEXT);
        addStyle(style);

    }

    private void initBottomTabPanelStyles() {
        String discriminator = ".BottomTab";
        Style style = new Style(discriminator, " .", Tab);
        style.addProperty("background-color", ThemeColors.SELECTION);
        addStyle(style);

        style = new Style(discriminator, " .", TabPanelBottom);
        style.addProperty("padding", "2px");
        style.addProperty("margin", "0px");
        addStyle(style);

        style = new Style(discriminator, " .", TabBarItem);
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("text-align", "center");
        style.addProperty("border-top", "1px solid");
        style.addProperty("border-top-color", ThemeColors.SELECTION);
        style.addProperty("background-color", ThemeColors.OBJECT_TONE3);
        addStyle(style);

        style = new Style(discriminator, TabBarItem, TabPanel.StyleDependent.selected);
        style.addProperty("border-top", "1px solid");
        style.addProperty("border-top-color", ThemeColors.OBJECT_TONE1);
        style.addProperty("cursor", "default");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE1);
        addStyle(style);

        style = new Style("." + TabBarItem, "-", TabPanel.StyleDependent.selected, " .", TabBarItemLabel);
        style.addProperty("color", "black");
        addStyle(style);

        style = new Style(discriminator, " .", TabBarItemLabel);
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
        style.addProperty("border-color", ThemeColors.BORDER);
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
        style.addProperty("border-color", ThemeColors.OBJECT_TONE5);
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("outline", "none");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE1);
        addStyle(style);

        style = new Style(CSSClass.pyx4j_ButtonImage);
        style.addProperty("padding-right", "4px");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button + "-hover" + " ." + CSSClass.pyx4j_ButtonContent);
        style.addProperty("background-color", ThemeColors.OBJECT_TONE4);
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button + "-pushed" + " ." + CSSClass.pyx4j_ButtonContent);
        style.addProperty("border-style", "ridge");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button + "-checked" + " ." + CSSClass.pyx4j_ButtonContent);
        style.addProperty("background", ThemeColors.OBJECT_TONE3);
        style.addProperty("border-style", "inset");
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
        style.addProperty("background", ThemeColors.SELECTION);
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
        style.addProperty("border-color", ThemeColors.BORDER);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");

        addStyle(style);
    }

    protected void initDatePickerStyle() {

        Style style = new Style(".gwt-DatePicker");
        style.addProperty("margin", "2px 4px");
        style.addProperty("border", "1px solid #A2BBDD");
        style.addProperty("background-color", "white");
        style.addProperty("color", ThemeColors.TEXT);
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
        style.addProperty("outline-color", ThemeColors.TEXT);
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
        style.addProperty("border-color", ThemeColors.OBJECT_TONE5);
        addStyle(style);

        style = new Style(CSSClass.pyx4j_GroupBox, "-collapsed");
        style.addProperty("border", "none");
        style.addProperty("border-top", "1px solid");
        style.addProperty("border-color", ThemeColors.OBJECT_TONE5);
        addStyle(style);

        style = new Style(CSSClass.pyx4j_GroupBox_Caption);
        style.addProperty("padding", "5px 2px 2px 2px");
        style.addProperty("verticalAlign", "top");
        style.addProperty("color", ThemeColors.OBJECT_TONE5);
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
        style.addProperty("border-color", ThemeColors.BORDER);
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

    private void initMultipleDatePicker() {
        DatePickerImages resource = (DatePickerImages) GWT.create(DatePickerImages.class);

        Style style = new Style("table.datePickerMonthSelector");
        style.addProperty("background-image", "url('" + resource.MonthSelectorBackground().getURL() + "')");
        style.addProperty("background-repeat", "repeat-x");
        style.addProperty("line-height", "12px");
        style.addProperty("border-bottom", "1px solid #A8A8A8");
        addStyle(style);

        style = new Style("table.gwt-DatePicker");
        style.addProperty("width", "250px");
        style.addProperty("border", "1px solid #A8A8A8");
        addStyle(style);

        style = new Style("table.gwt-DatePicker.multiple");
        style.addProperty("border-left", "0");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style("table.gwt-DatePicker.multiple.first");
        style.addProperty("border-left", "1px solid #A8A8A8");
        addStyle(style);

        style = new Style(".gwt-DatePicker td");
        style.addProperty("text-align", "center");
        style.addProperty("padding", "0");
        style.addProperty("font-size", "11px");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".datePickerGrid .gwt-Label");
        style.addProperty("border", "1px solid #E4E4EC");
        addStyle(style);

        style = new Style(".datePickerGrid .gwt-Label.disabled");
        style.addProperty("color", "#B0B0B0");
        addStyle(style);

        style = new Style(".datePickerGrid .gwt-Label.heighlighted");
        style.addProperty("border", "1px solid #D0D0F0");
        style.addProperty("background-color", "#C8D8D8");
        addStyle(style);

        style = new Style(".datePickerGrid .gwt-Label.selected");
        style.addProperty("border", "1px solid #E06020");
        addStyle(style);

        style = new Style(".datePickerMonthSelector .gwt-Label");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style("table.datePickerMonthSelector.multiple");
        style.addProperty("line-height", "24px");
        addStyle(style);

        style = new Style(".datePickerMonthSelector img");
        style.addProperty("width", "10px");
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(".datePickerMonthSelector img.top");
        style.addProperty("top", "3px");
        addStyle(style);

        style = new Style(".datePickerMonthSelector img.bottom");
        style.addProperty("top", "-3px");
        addStyle(style);

        style = new Style(".datePickerMonthSelector");
        style.addProperty("background-color", "#E4E4EC");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style(".datePickerGrid");
        style.addProperty("width", "100%");
        style.addProperty("background-color", "#E4E4EC");
        style.addProperty("padding", "10px");
        addStyle(style);

        style = new Style(".datePickerGrid tr.datePickerGridDaysRow");
        style.addProperty("height", "20px");
        addStyle(style);

        style = new Style(".datePickerGrid .datePickerGridDaysRow td");
        style.addProperty("border-bottom", "1px solid black");
        style.addProperty("margin-bottom", "5px");
        addStyle(style);

        style = new Style("monthSelectorNextMonth");
        style.addProperty("border-right", "1px solid #A8B8B8");
        addStyle(style);

        style = new Style(".monthSelectorNavigation.right");
        style.addProperty("border-right", "1px solid #A8B8B8");
        addStyle(style);

        style = new Style(".monthSelectorNavigation");
        style.addProperty("width", "15%");
        addStyle(style);

        style = new Style("monthSelectorMonthLabel");
        style.addProperty("width", "35%");
        addStyle(style);

        style = new Style(".monthSelectorYearLabel");
        style.addProperty("width", "25%");
        addStyle(style);

        style = new Style(".monthSelectorYearNavigation");
        style.addProperty("width", "10%");
        addStyle(style);
    }

    protected void initDashboard_Report() {
        String prefix = CSSNames.BASE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("border", "1px solid #aaa");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.Column));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.ColumnSpacer));
        style.addProperty("height", "4em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.Holder));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE2);
        style.addProperty("border", "1px solid #aaa");
        style.addProperty("margin", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.Holder, CSSNames.StyleDependent.maximized));
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderSetup));
        style.addProperty("background-color", ThemeColors.MANDATORY_TEXT_BACKGROUND);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderCaption));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE4);
        style.addProperty("font", "caption");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "#444");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderCaption) + ":hover");
        style.addProperty("background-color", ThemeColors.SELECTION);
        style.addProperty("color", ThemeColors.SELECTION_TEXT);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderHeading));
        style.addProperty("padding-top", "2px");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderMenu));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE1);
        style.addProperty("border", "1px solid #aaa");
        style.addProperty("font", "menu");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.DndPositioner));
        style.addProperty("border", "1px dotted #555");
        style.addProperty("margin", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.DndReportPositioner));
        style.addProperty("border", "1px dotted #555");
        style.addProperty("margin", "5px");
        addStyle(style);

        // overriding gwt-dnd styles:
        style = new Style(".dragdrop-handle");
        style.addProperty("cursor", "default");
        addStyle(style);
    }
}
