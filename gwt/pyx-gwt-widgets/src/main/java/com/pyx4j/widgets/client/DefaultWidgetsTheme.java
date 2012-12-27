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
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.gwt.commons.BrowserType;

public class DefaultWidgetsTheme extends Theme {

    public static enum StyleName implements IStyleName {
        TextBox, ListBox, Toolbar, ToolbarItem, ToolbarSeparator,

        Button, ButtonContent, ButtonText,

        Anchor,

        RateIt, RateItBar,

        ImageGallery,

        RadioGroup, RadioGroupItem
    }

    public static enum StyleDependent implements IStyleDependent {
        watermark, hover, disabled, pushed
    }

    public DefaultWidgetsTheme() {
        initStyles();
    }

    protected void initStyles() {
        initTextBoxStyle();
        initListBoxStyle();
        initButtonStyle();
        initToolbarStyle();
        initRateItStyle();
        initImageGalleryStyle();
        initRadioGroupStyle();
    }

    protected void initTextBoxStyle() {

        Style style = new Style(".", StyleName.TextBox);

// TODO check why is it necessary to add /1.4em parameter - it seems that IE9 uses it correctly and shifts TextBox text-line to the bottom,
//      while all other browsers (Firefox, Chrome) or ignore it or thomehow different calculate text line position.
//        style.addProperty("font", "12px/1.4em Arial, Helvetica, sans-serif");

        style.addProperty("font", "12px Arial, Helvetica, sans-serif");
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("background-color", ThemeColor.background);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.4);
        if (!BrowserType.isIE7()) {
            style.addProperty("padding", "2px 5px");
        }
        style.addProperty("box-sizing", "border-box");
        style.addProperty("-moz-box-sizing", "border-box");
        style.addProperty("-webkit-box-sizing", "border-box");
        addStyle(style);

        style = new Style(".", StyleName.TextBox, " td");
        style.addProperty("padding", "0px");
        addStyle(style);

        style = new Style(".", StyleName.TextBox, "-", StyleDependent.watermark);
        style.addProperty("color", ThemeColor.foreground, 0.3);
        addStyle(style);

    }

    protected void initListBoxStyle() {
        Style style = new Style(".", StyleName.ListBox);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.4);
        addStyle(style);

    }

    protected void initButtonStyle() {
        Style style = new Style(".", StyleName.Button);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.4);
        style.addProperty("padding", "0 3px");
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

        style = new Style(".", StyleName.ButtonContent);
        addStyle(style);

        style = new Style(".", StyleName.ButtonText);
        style.addProperty("vertical-align", "middle");
        style.addProperty("whiteSpace", "nowrap");
        style.addProperty("position", "relative");
        style.addProperty("text-indent", "0");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.Button, "-", DefaultWidgetsTheme.StyleDependent.hover);
        style.addGradient(ThemeColor.foreground, 0.2, ThemeColor.foreground, 0);
        addStyle(style);

        style = new Style(".", StyleName.Button, "-", DefaultWidgetsTheme.StyleDependent.disabled);
        style.addGradient(ThemeColor.foreground, 0.1, ThemeColor.foreground, 0.1);
        style.addProperty("cursor", "default");
        style.addProperty("opacity", "0.4");
        addStyle(style);

    }

    protected void initToolbarStyle() {

        Style style = new Style(".", StyleName.Toolbar);
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".", StyleName.ToolbarItem);
        style.addProperty("display", "inline-block");
        style.addProperty("padding-left", "4px");
        style.addProperty("vertical-align", "middle");
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
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
        style.addProperty("padding", "3px 6px");
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
        Style style = new Style(".", StyleName.RadioGroup);
        addStyle(style);

        style = new Style(".", StyleName.RadioGroupItem);
        addStyle(style);

        style = new Style(".", StyleName.RadioGroupItem, "-", DefaultWidgetsTheme.StyleDependent.pushed);
        addStyle(style);

        style = new Style(".", StyleName.RadioGroupItem, "-", DefaultWidgetsTheme.StyleDependent.disabled);
        style.addProperty("color", ThemeColor.foreground, 0.3);
        addStyle(style);

    }

}
