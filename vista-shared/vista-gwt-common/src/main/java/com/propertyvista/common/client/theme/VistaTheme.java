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
import com.pyx4j.commons.css.Selector;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;
import com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.widgets.client.ImageFactory;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;

public class VistaTheme extends Theme {

    public VistaTheme() {

    }

    protected void initGeneralStyles() {
        Style style = new Style("html");
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

    }

    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("background-color", ThemeColors.background);
        style.addProperty("color", ThemeColors.foreground);
        style.addProperty("margin", "0");
        style.addProperty("border", "none");
        style.addProperty("font", "12px/1.5em Arial, Helvetica, sans-serif");
        addStyle(style);

    }

    protected void initSectionStyles() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_Border));
        style.addProperty("background-color", ThemeColors.foreground, 0.6);
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_SelectionBorder));
        style.addProperty("background-color", ThemeColors.foreground, 0.8);
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_Background));
        style.addProperty("background-color", ThemeColors.foreground, 0.8);
        addStyle(style);

        style = new Style(Selector.valueOf(CSSClass.pyx4j_Section_Content));
        style.addProperty("background-color", ThemeColors.background);
        addStyle(style);
    }

    protected void initToolbarStyle() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_Toolbar));
        // style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("padding", "2 2 2 8");

        //style.addProperty("background-color", ThemeColor.OBJECT_TONE1);

        style.addGradient(ThemeColors.object1, 0.2, ThemeColors.object1, 0.5);

        addStyle(style);
    }

    protected void initStatusBarStyle() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_StatusBar));
        style.addProperty("background-color", ThemeColors.object1, 0.8);
        style.addProperty("padding", "2 2 2 8");
        addStyle(style);
    }

    protected void initBarSeparatorStyle() {
        Style style = new Style(Selector.valueOf(CSSClass.pyx4j_BarSeparator));
        style.addProperty("border-left", "2px ridge");
        style.addProperty("border-left-color", ThemeColors.object1, 0.8);

        style.addProperty("margin-left", "3px");
        addStyle(style);
    }

    protected void initProgressBarStyles() {
        Style style = new Style(".gwt-ProgressBar-shell");
        style.addProperty("background-color", ThemeColors.object1, 0.6);
        addStyle(style);

        style = new Style(".gwt-ProgressBar-bar");
        style.addProperty("background-color", ThemeColors.object1, 0.8);
        addStyle(style);

        style = new Style(".gwt-ProgressBar-text");
        style.addProperty("color", ThemeColors.object1, 0.2);
        addStyle(style);
    }

    protected void initMenuBarStyles() {
        Style style = new Style(".gwt-MenuBar");
        style.addProperty("cursor", "default");
        style.addProperty("color", ThemeColors.foreground, 0.2);
        addStyle(style);

        style = new Style(".gwt-MenuItem");
        style.addProperty("cursor", "default");
        style.addProperty("color", ThemeColors.foreground, 0.9);
        style.addProperty("background-color", ThemeColors.foreground, 0.2);
        addStyle(style);

        style = new Style(".gwt-MenuItem-selected");
        style.addProperty("background", ThemeColors.foreground, 0.8);
        style.addProperty("color", ThemeColors.foreground, 0.2);
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical");
        style.addProperty("margin-top", "0px");
        style.addProperty("margin-left", "0px");
        style.addProperty("background", ThemeColors.foreground);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.2);
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
        style.addProperty("border-color", ThemeColors.object1, 0.6);
        style.addProperty("background-color", ThemeColors.object1, 0.6);
        addStyle(style);

        style = new Style(".gwt-DialogBox .Caption");
        style.addProperty("background-color", ThemeColors.object1, 0.8);
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
        style.addProperty("border-bottom-color", ThemeColors.foreground, 0.3);
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
        style.addProperty("border-right-color", ThemeColors.foreground, 0.6);
        style.addProperty("border-left", "1px solid");
        style.addProperty("border-left-color", ThemeColors.foreground, 0.6);
        style.addProperty("border-top", "1px solid");
        style.addProperty("border-top-color", ThemeColors.foreground, 0.6);
        style.addProperty("background", ThemeColors.object1, 1);
        style.addProperty("color", ThemeColors.foreground, 0);
        style.addProperty("display", "inline-block");
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(".gwt-TabLayoutPanelTab-selected");
        style.addProperty("cursor", "default");
        style.addGradient(ThemeColors.foreground, 0.1, ThemeColors.foreground, 0.30);
        style.addProperty("color", "#333");
        addStyle(style);

        String prefix = VistaTabLayoutPanel.TAB_DIASBLED_STYLE;
        style = new Style(Selector.valueOf(prefix));
//            style.addProperty("background-color", ThemeColor.DISABLED_TEXT_BACKGROUND);
        style.addProperty("color", ThemeColors.foreground, 0.2);
        style.addProperty("cursor", "default");
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
        style.addProperty("background", "url('" + ImageFactory.getImages().comboBoxPicker().getSafeUri().asString() + "') no-repeat 100%");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Picker + "-hover");
        style.addProperty("background", "url('" + ImageFactory.getImages().comboBoxPickerHover().getSafeUri().asString() + "') no-repeat 100%");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Picker + "-pushed");
        style.addProperty("background", "url('" + ImageFactory.getImages().comboBoxPickerPushed().getSafeUri().asString() + "') no-repeat 100%");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_PickerPanel);
        style.addProperty("border-color", ThemeColors.object1, 0.6);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("background-color", "#fff");
        addStyle(style);

        addStyle(style);
        style = new Style("." + CSSClass.pyx4j_PickerPanel + " td");
        style.addProperty("padding", "0px");

        style = new Style("." + CSSClass.pyx4j_PickerPanel + " table");
        style.addProperty("border-collapse", "collapse");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_PickerLine + " .gwt-TreeItem-selected");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_PickerLine_Selected);
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
        style.addProperty("border-color", ThemeColors.object1, 0.5);
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("outline", "none");
        style.addProperty("background-color", ThemeColors.object1, 0.9);
        addStyle(style);

        style = new Style(CSSClass.pyx4j_ButtonImage);
        style.addProperty("padding-right", "4px");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button + "-hover" + " ." + CSSClass.pyx4j_ButtonContent);
        style.addProperty("background-color", ThemeColors.object1, 0.4);
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button + "-pushed" + " ." + CSSClass.pyx4j_ButtonContent);
        style.addProperty("border-style", "ridge");
        addStyle(style);

        style = new Style("." + CSSClass.pyx4j_Button + "-checked" + " ." + CSSClass.pyx4j_ButtonContent);
        style.addProperty("background", ThemeColors.object1, 0.3);
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
        style.addProperty("background", ThemeColors.object1, 0.8);
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
        Style style = new Style(".", DefaultEntityFolderTheme.StyleName.EntityFolderRowItemDecorator, " .", CSSClass.pyx4j_CheckBox);
        style.addProperty("margin-left", "45%");
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
        style.addProperty("border-color", ThemeColors.object1, 0.5);
        addStyle(style);

        style = new Style(CSSClass.pyx4j_GroupBox, "-collapsed");
        style.addProperty("border", "none");
        style.addProperty("border-top", "1px solid");
        style.addProperty("border-color", ThemeColors.object1, 0.5);
        addStyle(style);

        style = new Style(CSSClass.pyx4j_GroupBox_Caption);
        style.addProperty("padding", "5px 2px 2px 2px");
        style.addProperty("verticalAlign", "top");
        style.addProperty("color", ThemeColors.object1, 0.5);
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
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowItem().getSafeUri().asString() + "') no-repeat");
        style.addProperty("width", "17px");
        style.addProperty("height", "16px");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-disabled");
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-left");
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowLeft().getSafeUri().asString() + "') no-repeat");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-right");
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowRight().getSafeUri().asString() + "') no-repeat");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-selected");
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowSelectedItem().getSafeUri().asString() + "') no-repeat");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-playing");
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowPause().getSafeUri().asString() + "') no-repeat");
        addStyle(style);

        style = new Style(CSSClass.pyx4j_SlideshowAction, "-paused");
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowPlay().getSafeUri().asString() + "') no-repeat");
        addStyle(style);

    }

    protected void initSuggestBoxStyle() {
        Style style = new Style(".gwt-SuggestBoxPopup");
        style.addProperty("background-color", "white");
        style.addProperty("padding", "2px");
        style.addProperty("border-color", ThemeColors.object1, 0.6);
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