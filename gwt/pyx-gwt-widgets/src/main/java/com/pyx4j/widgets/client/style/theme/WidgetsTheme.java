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
 * Created on Nov 14, 2011
 * @author michaellif
 */
package com.pyx4j.widgets.client.style.theme;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.ImageFactory;

public class WidgetsTheme extends Theme {

    public static enum StyleName implements IStyleName {
        TextBox, TextBoxContainer, TextBoxActionButton,

        ListBox, Toolbar, ToolbarItem, ToolbarSeparator,

        StatusBar,

        Button, ButtonText,

        ImageButton, ImageButtonText,

        Label,

        CheckBox,

        Anchor,

        RateIt, RateItBar,

        ImageGallery,

        OptionGroup, OptionGroupItem,

        GlassPanel, GlassPanelLabel,

        DropDownPanel,

        Recaptcha,

        Slideshow, SlideshowAction,

        ImageSlider, ImageSliderEditAction,

        CollapsablePanel, CollapsablePanelImage,

        SuggestBoxPopup, SelectionPickerPanel, SelectionPickerPanelItem, SelectionPickerPanelNoMatchesLabel,

        SelectedItemClose, SelectedItemHolder, SelectedItemHolderLabel, SelectorListBoxValuePanel,

        SelectedItemEditor, SelectedItemEditorContent,

        SelectedItemEditorBodyHolder,

        DownArrow;
    }

    public static enum StyleDependent implements IStyleDependent {
        watermark, hover, focused, readonly, disabled, active, semitransparent, singleLine, selected, playing, paused, left, right, editable, editing, horizontal
    }

    public WidgetsTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        initTextBoxStyle();
        initListBoxStyle();
        initLabelStyle();
        initButtonStyle();
        initImageButtonStyle();
        initCheckBoxStyle();
        initToolbarStyle();
        initAnchorStyle();
        initStatusBarStyle();
        initRateItStyle();
        initImageGalleryStyle();
        initRadioGroupStyle();
        initGlassPanelStyle();
        initRecaptchaStyle();
        initImageSliderStyle();
        initSlideshow();
        initCollapsablePanel();
        initSelectorBoxStyle();
    }

    protected void initAnchorStyle() {
        Style style = new Style(".", StyleName.Anchor);
        style.addProperty("color", ThemeColor.foreground);
        addStyle(style);
    }

    private void initLabelStyle() {
        Style style = new Style(".", StyleName.Label);
        addStyle(style);
    }

    protected void initTextBoxStyle() {

        Style style = new Style(".", StyleName.TextBox);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("padding", "0 5px");
        style.addProperty("box-sizing", "border-box");
        style.addProperty("-moz-box-sizing", "border-box");
        style.addProperty("-webkit-box-sizing", "border-box");
        style.addProperty("font-family", "inherit");
        style.addProperty("border-width", "0");
        style.addProperty("width", "100%");
        style.addProperty("outline", "0");
        addStyle(style);

        style = new Style(".", StyleName.TextBox, " td");
        style.addProperty("padding", "0px");
        addStyle(style);

        style = new Style(".", StyleName.TextBox, "-", StyleDependent.watermark);
        style.addProperty("color", ThemeColor.foreground, 0.5);
        addStyle(style);

        style = new Style(".", StyleName.TextBoxContainer);
        style.addProperty("background-color", "white");
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.4);
        style.addProperty("margin", "2px 0");
        addStyle(style);

        style = new Style(".", StyleName.TextBoxContainer, "-", StyleDependent.focused);
        style.addProperty("border-color", ThemeColor.foreground, 0.8);
        addStyle(style);

        style = new Style(".", StyleName.TextBoxContainer, "-", StyleDependent.disabled);
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        style.addProperty("color", ThemeColor.foreground, 0.6);
        addStyle(style);

        style = new Style(".", StyleName.TextBoxContainer, "-", StyleDependent.readonly);
        style.addProperty("background-color", ThemeColor.foreground, 0);
        style.addProperty("border-color", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.TextBoxContainer, "-", StyleDependent.singleLine);
        style.addProperty("height", "2em");
        style.addProperty("line-height", "2em");
        addStyle(style);

        style = new Style(".", StyleName.TextBoxActionButton);
        style.addProperty("vertical-align", "middle");
        style.addProperty("margin", "0 4px");
        style.addProperty("height", "2em");
        style.addProperty("cursor", "pointer");
        style.addProperty("-webkit-touch-callout", "none");
        style.addProperty("-webkit-user-select", "none");
        style.addProperty("-khtml-user-select", "none");
        style.addProperty("-moz-user-select", "none");
        style.addProperty("-ms-user-select", "none");
        style.addProperty("user-select", "none");
        addStyle(style);

        style = new Style(".", StyleName.TextBoxActionButton, "-", WidgetsTheme.StyleDependent.disabled);
        style.addGradient(ThemeColor.foreground, 0.1, ThemeColor.foreground, 0.1);
        style.addProperty("cursor", "default");
        style.addProperty("opacity", "0.4");
        addStyle(style);

    }

    protected void initCheckBoxStyle() {
        Style style = new Style(".", StyleName.CheckBox, " input");
        style.addProperty("margin-right", "4px");
        addStyle(style);

        style = new Style(".", StyleName.CheckBox, " label");
        addStyle(style);
    }

    protected void initListBoxStyle() {
        Style style = new Style(".", StyleName.ListBox);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("background-color", "white");
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.4);
        style.addProperty("padding", "2px 1px 2px 5px");
        style.addProperty("margin-top", "2px 0");
        style.addProperty("box-sizing", "border-box");
        style.addProperty("-moz-box-sizing", "border-box");
        style.addProperty("-webkit-box-sizing", "border-box");
        style.addProperty("font-family", "inherit");
        style.addProperty("outline", "none");
        addStyle(style);

        style = new Style(".", StyleName.ListBox, "-", StyleDependent.readonly);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.1);
        style.addProperty("background-color", ThemeColor.foreground, 0);
        addStyle(style);

        style = new Style(".", StyleName.ListBox, "-", StyleDependent.disabled);
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.ListBox, "-", StyleDependent.singleLine);
        style.addProperty("height", "2em");
        addStyle(style);

        style = new Style(".", StyleName.ListBox, ":focus");
        style.addProperty("border-color", ThemeColor.foreground, 0.8);
        addStyle(style);

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

        style = new Style(".", StyleName.DownArrow);
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(".", StyleName.DownArrow, ":", "after");
        style.addProperty("content", "\"\"");
        style.addProperty("display", "inline-block");
        style.addProperty("border-left", "4px solid transparent");
        style.addProperty("border-right", "4px solid transparent");
        style.addProperty("border", "4px solid rgba(0, 0, 0, 0)");
        style.addProperty("border-top-color", ThemeColor.object1, 0.1);
        style.addProperty("margin-left", "4px");

        addStyle(style);

    }

    protected void initImageButtonStyle() {
        Style style = new Style(".", StyleName.ImageButton);
        style.addProperty("padding", "4px");
        style.addProperty("display", "inline-block");
        style.addProperty("cursor", "pointer");
        style.addProperty("-webkit-touch-callout", "none");
        style.addProperty("-webkit-user-select", "none");
        style.addProperty("-khtml-user-select", "none");
        style.addProperty("-moz-user-select", "none");
        style.addProperty("-ms-user-select", "none");
        style.addProperty("user-select", "none");
        style.addProperty("outline", "0");
        addStyle(style);

        style = new Style(".", StyleName.ImageButtonText);
        style.addProperty("display", "inline");
        style.addProperty("whiteSpace", "nowrap");
        style.addProperty("text-indent", "0");
        style.addProperty("height", "100%");
        style.addProperty("text-align", "center");
        style.addProperty("padding", "0 3px");
        addStyle(style);

        style = new Style(".", StyleName.ImageButton, "-", WidgetsTheme.StyleDependent.hover);
        style.addProperty("color", ThemeColor.foreground, 1.2);
        addStyle(style);

        style = new Style(".", StyleName.ImageButton, "-", WidgetsTheme.StyleDependent.disabled);
        style.addProperty("cursor", "default");
        addStyle(style);

    }

    protected void initToolbarStyle() {

        Style style = new Style(".", StyleName.Toolbar);
        style.addProperty("white-space", "nowrap");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.ToolbarItem);
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
        addStyle(style);

        style = new Style(".", StyleName.ToolbarSeparator);
        style.addProperty("border-left", "1px solid");
        style.addProperty("border-left-color", ThemeColor.foreground, 0.5);
        style.addProperty("margin-left", "8px");
        style.addProperty("height", "20px");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.Toolbar, " .", StyleName.Anchor);
        style.addProperty("color", ThemeColor.object1, 1);
        style.addProperty("padding", "3px 6px");
        addStyle(style);
    }

    protected void initStatusBarStyle() {
        Style style = new Style(".", StyleName.StatusBar);
        style.addProperty("background-color", ThemeColor.object1, 0.2);
        style.addProperty("padding", "2 2 2 8");
        addStyle(style);
    }

    protected void initRateItStyle() {
        Style style = new Style(".", StyleName.RateItBar);
        style.addProperty("cursor", "pointer");
        addStyle(style);

    }

    protected void initImageGalleryStyle() {
        String imgGallery = "ImageGallery";
        Style style = new Style("." + imgGallery);
        style.addProperty("margin", "0");
        style.addProperty("padding", "3px 5px");
        style.addProperty("border", "1px solid #ccc");
        style.addProperty("background", "white");
        addStyle(style);

        String imgFrame = "ImageFrame";
        style = new Style("." + imgGallery + "-" + imgFrame);
        style.addProperty("border", "2px solid #eee");
        addStyle(style);

        style = new Style("." + imgGallery + "-" + imgFrame + ":hover");
        style.addProperty("border", "2px solid #ccc");
        addStyle(style);
    }

    protected void initRadioGroupStyle() {
        Style style = new Style(".", StyleName.OptionGroup);
        addStyle(style);

        style = new Style(".", StyleName.OptionGroupItem);
        style.addProperty("white-space", "nowrap");
        style.addProperty("line-height", "1.5em");
        style.addProperty("padding", "0 10px");
        style.addProperty("display", "block");
        addStyle(style);

        style = new Style(".", StyleName.OptionGroupItem, " input");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style(".", StyleName.OptionGroupItem, " label");
        style.addProperty("padding-left", "5px");
        addStyle(style);

        style = new Style(".", StyleName.OptionGroupItem, "-", WidgetsTheme.StyleDependent.active);
        addStyle(style);

        style = new Style(".", StyleName.OptionGroupItem, "-", WidgetsTheme.StyleDependent.disabled);
        style.addProperty("color", ThemeColor.foreground, 0.3);
        addStyle(style);

        style = new Style(".", StyleName.OptionGroup, "-", WidgetsTheme.StyleDependent.horizontal);
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(".", StyleName.OptionGroup, "-", WidgetsTheme.StyleDependent.horizontal, " .", StyleName.OptionGroupItem);
        style.addProperty("display", "inline-block");
        addStyle(style);

    }

    protected void initGlassPanelStyle() {
        Style style = new Style(".", StyleName.GlassPanel);
        addStyle(style);

        style = new Style(".", StyleName.GlassPanelLabel);
        style.addProperty("color", "#652C02");
        style.addProperty("background-color", "#EDD3AE");
        style.addProperty("padding", "1px 7px");
        style.addProperty("margin-top", "3px");
        style.addProperty("top", "0");
        style.addProperty("border", "1px solid #db5f03");
        style.addProperty("border-radius", "5px 5px 5px 5px");
        addStyle(style);

        style = new Style(".", StyleName.GlassPanel, "-", WidgetsTheme.StyleDependent.semitransparent);
        style.addProperty("background-color", "#000");
        style.addProperty("opacity", "0.2");
        style.addProperty("filter", "alpha(opacity=20)");
        addStyle(style);

    }

    protected void initRecaptchaStyle() {
        Style style = new Style("#recaptcha_image");
        style.addProperty("width", "250px !important;");
        addStyle(style);

        style = new Style("#recaptcha_image img");
        style.addProperty("width", "250px !important;");
        addStyle(style);
    }

    protected void initImageSliderStyle() {
        Style style = new Style(".", StyleName.ImageSliderEditAction);
        style.addProperty("width", "100%");
        style.addProperty("line-height", "40px");
        style.addProperty("text-align", "center");
        style.addProperty("background", ThemeColor.foreground, 0.7);
        style.addProperty("color", "white");
        style.addProperty("opacity", "0.8");
        style.addProperty("cursor", "pointer");
        addStyle(style);
    }

    protected void initSlideshow() {
        Style style = new Style(".", StyleName.Slideshow);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        addStyle(style);

        style = new Style(".", WidgetsTheme.StyleName.SlideshowAction);
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowItem().getSafeUri().asString() + "') no-repeat");
        style.addProperty("width", "17px");
        style.addProperty("height", "16px");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        addStyle(style);

        style = new Style(".", WidgetsTheme.StyleName.SlideshowAction, "-", WidgetsTheme.StyleDependent.disabled);
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(".", WidgetsTheme.StyleName.SlideshowAction, "-", WidgetsTheme.StyleDependent.left);
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowLeft().getSafeUri().asString() + "') no-repeat");
        addStyle(style);

        style = new Style(".", WidgetsTheme.StyleName.SlideshowAction, "-", WidgetsTheme.StyleDependent.right);
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowRight().getSafeUri().asString() + "') no-repeat");
        addStyle(style);

        style = new Style(".", WidgetsTheme.StyleName.SlideshowAction, "-", WidgetsTheme.StyleDependent.selected);
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowSelectedItem().getSafeUri().asString() + "') no-repeat");
        addStyle(style);

        style = new Style(".", WidgetsTheme.StyleName.SlideshowAction, "-", WidgetsTheme.StyleDependent.playing);
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowPause().getSafeUri().asString() + "') no-repeat");
        addStyle(style);

        style = new Style(".", WidgetsTheme.StyleName.SlideshowAction, "-", WidgetsTheme.StyleDependent.paused);
        style.addProperty("background", "url('" + ImageFactory.getImages().slideshowPlay().getSafeUri().asString() + "') no-repeat");
        addStyle(style);
    }

    private void initCollapsablePanel() {
        Style style = new Style(".", StyleName.CollapsablePanel);
        addStyle(style);

        style = new Style(".", StyleName.CollapsablePanelImage);
        style.addProperty("height", "2em");
        addStyle(style);
    }

    protected void initSelectorBoxStyle() {

        Style style = new Style(".", StyleName.SuggestBoxPopup);
        style.addProperty("box-shadow", "10px 10px 5px rgba(0, 0, 0, 0.3)");
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemHolder);
        style.addProperty("display", "inline-block");
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("background-color", ThemeColor.object1, 0.2);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.4);
        style.addProperty("padding", "0 4px");
        style.addProperty("box-sizing", "border-box");
        style.addProperty("-moz-box-sizing", "border-box");
        style.addProperty("-webkit-box-sizing", "border-box");
        style.addProperty("font-family", "inherit");
        style.addProperty("margin", "3px");
        style.addProperty("border-radius", "4px");
        style.addProperty("line-height", "2em");
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemHolder, "-", StyleDependent.editable);
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemHolder, "-", StyleDependent.editable, ":hover");
        style.addProperty("background-color", ThemeColor.object1, 0.3);
        style.addProperty("color", ThemeColor.foreground, 1.2);
        addStyle(style);

        style = new Style(".", StyleName.SelectionPickerPanel);
        style.addProperty("background-color", "white");
        style.addProperty("padding", "4px");
        style.addProperty("border-color", ThemeColor.foreground, 0.4);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        addStyle(style);

        style = new Style(".", StyleName.SelectionPickerPanelItem, "-", WidgetsTheme.StyleDependent.hover);
        style.addGradient(ThemeColor.foreground, 0.1, ThemeColor.foreground, 0.05);
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemHolderLabel);
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        style.addProperty("max-width", "150px");
        style.addProperty("overflow", "hidden");
        style.addProperty("text-overflow", "ellipsis");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".", StyleName.SelectionPickerPanelNoMatchesLabel);
        style.addProperty("color", ThemeColor.foreground, 1.2);
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
        style.addProperty("font-style", "italic");
        style.addProperty("background", ThemeColor.foreground, 0.3);
        style.addProperty("padding", "5px");
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemClose);
        style.addProperty("vertical-align", "middle");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style(".", StyleName.SelectorListBoxValuePanel);
        style.addProperty("position", "relative");
        style.addProperty("text-align", "left");
        style.addProperty("min-height", "3.5em");
        addStyle(style);

        style = new Style(".", StyleName.SelectorListBoxValuePanel, "-", StyleDependent.focused);
        style.addProperty("border-color", ThemeColor.foreground, 0.8);
        addStyle(style);

        style = new Style(".", StyleName.SelectorListBoxValuePanel, " .", StyleName.TextBoxContainer);
        style.addProperty("display", "inline-block");
        style.addProperty("border-width", "0px");
        addStyle(style);

        style = new Style(".", StyleName.SelectorListBoxValuePanel, " .", StyleName.TextBox);
        style.addProperty("width", "80px");
        addStyle(style);

        style = new Style(".", StyleName.SelectorListBoxValuePanel, " .", WidgetsTheme.StyleName.Button);
        style.addProperty("margin", "4px");
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemEditor);
        style.addProperty("box-shadow", "10px 10px 5px rgba(0, 0, 0, 0.3)");
        style.addProperty("background-color", "white");
        style.addProperty("padding", "4px");
        style.addProperty("border-color", ThemeColor.foreground, 0.4);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemEditorContent);
        style.addProperty("margin", "4px");
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemEditorContent, " .", WidgetsTheme.StyleName.Toolbar);
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemEditorContent, " .", WidgetsTheme.StyleName.Toolbar, " .", WidgetsTheme.StyleName.Button);
        style.addProperty("margin", "6px");
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemHolder, "-", StyleDependent.editing);
        style.addProperty("background-color", "white");
        style.addProperty("border-bottom-right-radius", "0px");
        style.addProperty("border-bottom-left-radius", "0px");
        style.addProperty("border-bottom-width", "0");
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemEditorBodyHolder);
        style.addProperty("max-height", "400px");
        style.addProperty("overflow", "auto");
        addStyle(style);

        style = new Style(".", StyleName.SelectedItemHolder, "-", StyleDependent.editing, ":hover");
        style.addProperty("background-color", "white");
        addStyle(style);

    }
}
