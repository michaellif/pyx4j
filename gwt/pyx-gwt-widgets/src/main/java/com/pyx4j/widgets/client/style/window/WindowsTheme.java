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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.widgets.client.style.CSSClass;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.Theme;
import com.pyx4j.widgets.client.style.ThemeColor;

public class WindowsTheme extends Theme {

    public WindowsTheme() {
        initThemeColors();
        initStyles();
    }

    protected void initStyles() {
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
        initButtonStyles();
        initTooltipStyle();
        initTreeStyle();
        initGlassPanelStyle();
        initTextBoxStyle();
        initCheckBoxStyle();
        initListBoxStyle();
        initDatePickerStyle();
    }

    protected void initThemeColors() {
        putThemeColor(ThemeColor.OBJECT_TONE1, "#ece9d8");
        putThemeColor(ThemeColor.OBJECT_TONE2, "#fdfae9");
        putThemeColor(ThemeColor.OBJECT_TONE3, "#dbd8c7");
        putThemeColor(ThemeColor.BORDER, "#666666");
        putThemeColor(ThemeColor.SELECTION, "#86adc4");
        putThemeColor(ThemeColor.SELECTION_TEXT, "#ffffff");
        putThemeColor(ThemeColor.TEXT, "#000000");
        putThemeColor(ThemeColor.TEXT_BACKGROUND, "#ffffff");
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, "#fafafa");
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, "#fcba84");
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, "#eeeeee");
        putThemeColor(ThemeColor.SEPARATOR, "#eeeeee");
    }

    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("color", ThemeColor.TEXT);
        addStyle(style);
    }

    protected void initSectionStyles() {
        Style style = new Style("." + CSSClass.pyx4j_Section_Border.name());
        style.addProperty("background-color", ThemeColor.BORDER);
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Section_SelectionBorder.name());
        style.addProperty("background-color", ThemeColor.SELECTION);
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Section_Background.name());
        style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Section_Content.name());
        style.addProperty("background-color", ThemeColor.TEXT_BACKGROUND);
        addStyle(style);
    }

    protected void initToolbarStyle() {
        Style style = new Style("." + CSSClass.pyx4j_Toolbar.name());
        style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("padding", "2 2 2 8");
        addStyle(style);
    }

    protected void initStatusBarStyle() {
        Style style = new Style("." + CSSClass.pyx4j_StatusBar.name());
        style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("padding", "2 2 2 8");
        addStyle(style);
    }

    protected void initBarSeparatorStyle() {
        Style style = new Style("." + CSSClass.pyx4j_BarSeparator.name());
        style.addProperty("border-left-width", "2px");
        style.addProperty("border-left-style", "ridge");
        style.addProperty("border-color", ThemeColor.OBJECT_TONE1);

        style.addProperty("height", "15px");
        style.addProperty("margin-left", "3px");
        style.addProperty("margin-top", "2px");
        style.addProperty("margin-bottom", "2px");
        addStyle(style);
    }

    protected void initProgressBarStyles() {
        List<Style> styles = new ArrayList<Style>();

        Style style = new Style(".gwt-ProgressBar-shell");
        style.addProperty("background-color", ThemeColor.BORDER);
        styles.add(style);

        style = new Style(".gwt-ProgressBar-bar");
        style.addProperty("background-color", ThemeColor.SELECTION);
        styles.add(style);

        style = new Style(".gwt-ProgressBar-text");
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        styles.add(style);

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
        style.addProperty("opacity", "0.2");
        style.addProperty("filter", "alpha(opacity=20)");
        addStyle(style);
    }

    protected void initDialogPanelStyles() {
        Style style = new Style("." + CSSClass.pyx4j_Dialog.name());
        style.addProperty("border-color", ThemeColor.SELECTION);
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Dialog_Caption.name());
        style.addProperty("background", ThemeColor.SELECTION);
        style.addProperty("filter", "alpha(opacity=90)");
        style.addProperty("opacity", "0.9");
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Dialog_Resizer.name());
        style.addProperty("background", ThemeColor.SELECTION);
        style.addProperty("filter", "alpha(opacity=90)");
        style.addProperty("opacity", "0.9");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Dialog_Content.name());
        style.addProperty("background-color", ThemeColor.TEXT_BACKGROUND);
        addStyle(style);
    }

    protected void initTabPanelStyles() {
        Style style = new Style(".gwt-TabPanel");
        addStyle(style);

        style = new Style(".gwt-TabPanelBottom");
        style.addProperty("padding", "2px");
        style.addProperty("margin", "0px");
        addStyle(style);

        style = new Style(".gwt-TabBarMoveLeft");
        style.addProperty("margin", "3px");
        addStyle(style);

        style = new Style(".gwt-TabBarMoveRight");
        style.addProperty("margin", "3px");
        addStyle(style);

        style = new Style(".gwt-TabBarItem");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("text-align", "center");
        style.addProperty("border-right-width", "1px");
        style.addProperty("border-right-style", "solid");
        style.addProperty("border-right-color", ThemeColor.BORDER);
        style.addProperty("background-color", ThemeColor.OBJECT_TONE3);
        addStyle(style);

        style = new Style(".gwt-TabBarItem-first");
        style.addProperty("border-left-width", "1px");
        style.addProperty("border-left-style", "solid");
        style.addProperty("border-left-color", ThemeColor.BORDER);
        addStyle(style);

        style = new Style(".gwt-TabBarItem-selected");
        style.addProperty("cursor", "default");
        style.addProperty("background-color", ThemeColor.SELECTION);
        addStyle(style);

        style = new Style(".gwt-TabBarItem-selected .gwt-TabBarItemLabel");
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        addStyle(style);

    }

    protected void initButtonStyles() {
        Style style = new Style("." + CSSClass.pyx4j_Button.name());
        style.addProperty("padding", "3px");
        style.addProperty("margin", "1px");
        style.addProperty("border", "2px solid transparent");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("outline", "none");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button.name() + "-hover");
        style.addProperty("border-width", "2px");
        style.addProperty("border-style", "outset");
        style.addProperty("border-color", ThemeColor.OBJECT_TONE3);
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button.name() + "-pushed");
        style.addProperty("border-width", "2px");
        style.addProperty("border-style", "ridge");
        style.addProperty("border-color", ThemeColor.OBJECT_TONE3);
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button.name() + "-checked");
        style.addProperty("background", ThemeColor.OBJECT_TONE2);
        style.addProperty("border-width", "2px");
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

        style = new Style("." + CSSClass.gwtButtonDefault.name());
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
        Style style = new Style("." + CSSClass.pyx4j_Tooltip.name());
        style.addProperty("border", "1px solid #000000");
        style.addProperty("background-color", "#FCFFDB");
        style.addProperty("padding", "1px 3px 1px 3px");
        style.addProperty("color", "#000000");
        addStyle(style);
        style = new Style("." + CSSClass.pyx4j_Tooltip_Shadow.name());
        style.addProperty("background-color", "gray");
        style.addProperty("opacity", "0.2");
        style.addProperty("filter", "alpha(opacity=20)");

        addStyle(style);
    }

    protected void initTreeStyle() {
        Style style = new Style(".gwt-Tree .gwt-TreeItem");
        style.addProperty("padding", "1px 0px");
        style.addProperty("margin", "0px");
        style.addProperty("white-space", "nowrap");
        style.addProperty("cursor", "hand");
        style.addProperty("cursor", "pointer");
        addStyle(style);
        style = new Style(".gwt-Tree .gwt-TreeItem-selected");
        style.addProperty("background", ThemeColor.SELECTION);
        addStyle(style);
    }

    protected void initGlassPanelStyle() {
        Style style = new Style("." + CSSClass.pyx4j_GlassPanel.name());
        style.addProperty("background-color", "#000");
        style.addProperty("opacity", "0.2");
        style.addProperty("filter", "alpha(opacity=20)");
        addStyle(style);
    }

    protected void initCheckBoxStyle() {
        Style style = new Style("." + CSSClass.pyx4j_CheckBox.name());
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }

    protected void initTextBoxStyle() {
        Style style = new Style("." + CSSClass.pyx4j_TextBox.name());
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }

    protected void initListBoxStyle() {
        Style style = new Style("." + CSSClass.pyx4j_ListBox.name());
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }

    protected void initDatePickerStyle() {
        Style style = new Style(".gwt-DatePicker");
        style.addProperty("margin", "2px 4px");
        style.addProperty("border", "2px inset " + ThemeColor.BORDER);
        style.addProperty("cursor", "default");
        style.addProperty("background-color", "#green");
        style.addProperty("color", "#red");
        addStyle(style);
    }

    //    /* ::::: DatePicker ::::: */
    //
    //    .gwt-DatePicker {
    //           margin: 2px 4px;
    //           border: 2px inset ButtonShadow;
    //            
    //            cursor: default;
    //
    //            background-color: ButtonFace;
    //            color: ButtonText; 
    //    }
    //    .gwt-DatePicker, x:-moz-any-link {
    //           border: 2px solid;
    //           -moz-border-top-colors: ThreeDShadow ThreeDDarkShadow;
    //           -moz-border-right-colors: ThreeDHighlight ThreeDLightShadow;
    //           -moz-border-bottom-colors: ThreeDHighlight ThreeDLightShadow;
    //           -moz-border-left-colors: ThreeDShadow ThreeDDarkShadow;
    //           background-color: -moz-Field;
    //           color: -moz-FieldText;
    //    }
    //    .gwt-DatePicker td,
    //    .datePickerMonthSelector td:focus {
    //            outline: none
    //    }
    //    .datePickerDays {
    //            width: 100%;
    //            background-color: buttonhighlight;
    //    }
    //    .datePickerDay,
    //    .datePickerWeekdayLabel,
    //    .datePickerWeekendLabel {
    //            text-align: center;
    //            padding: 4px;
    //            outline: none;
    //    }
    //    .datePickerWeekdayLabel,
    //    .datePickerWeekendLabel {
    //            background: url("images/button.png") repeat-x;
    //            -background: Highlight;
    //            background-color: Highlight;
    //            color: HighlightText;
    //            font-weight: bold;
    //            padding: 0px 4px 2px;
    //            cursor: default;
    //    }
    //    .datePickerDay {
    //            padding: 4px;
    //            cursor: hand;
    //            cursor: pointer;
    //    }
    //    .datePickerDayIsToday {
    //            border: 1px solid Highlight;
    //            padding: 3px;
    //    }
    //    .datePickerDayIsWeekend {
    //            background-color: ButtonFace;
    //    }
    //    .datePickerDayIsFiller {
    //            color: GrayText;
    //    }
    //    .datePickerDayIsValue {
    //            background-color: Highlight;
    //            color: HighlightText;
    //    }
    //    .datePickerDayIsDisabled {
    //            color: GrayText;
    //            font-style: italic;
    //    }
    //    .datePickerDayIsHighlighted {
    //            background-color: Highlight;
    //           color: HighlightText;
    //            opacity: 0.7;
    //            filter: alpha(opacity=70);
    //            zoom: 1;
    //    }
    //    .datePickerDayIsValueAndHighlighted {
    //            background-color: InfoBackground;
    //            color: Infotext;
    //    }
    //    .datePickerMonthSelector {
    //            background: url("images/button.png") repeat-x;
    //            -background: transparent;
    //            width: 100%;
    //    }
    //    td.datePickerMonth {
    //            text-align: center;
    //            vertical-align: center;
    //            white-space: nowrap;
    //            font-weight: bold;
    //            color: ButtonText;
    //    }
    //    .datePickerPreviousButton,
    //    .datePickerNextButton {
    //            font-size: 120%;
    //            line-height: 1em;
    //            color: ButtonText;
    //            cursor: hand;
    //            cursor: pointer;
    //            padding: 0px 4px;
    //           outline: none;
    //           border: 1px solid transparent;
    //           -border: 1px solid ButtonFace;
    //    }
    //    .datePickerPreviousButton:hover,
    //    .datePickerNextButton:hover {
    //           background-color: ButtonHighlight;
    //           border: 1px solid ButtonShadow;
    //    }
    //
    //    
}
