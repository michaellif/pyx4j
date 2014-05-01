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
 * Created on Oct 20, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.panels;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public abstract class TwoColumnFormPanelTheme extends Theme {

    public static enum StyleName implements IStyleName {
        FluidPanel, FluidPanelBlock,

        FormPanelLeftCell, FormPanelRightCell, FormPanelDualCell,

        FormPanelCaptionLabel, FormPanelHR, FormPanelH1, FormPanelH1Image, FormPanelH1Label, FormPanelH2, FormPanelH2Label, FormPanelH3, FormPanelH3Label, FormPanelH4, FormPanelH4Label, FormPanelActionWidget
    }

    public static enum StyleDependent implements IStyleDependent {
        collapsed, left, right, dual;
    }

    public TwoColumnFormPanelTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {

        Style style = new Style(".", StyleName.FluidPanel);
        style.addProperty("border-spacing", "0");
        style.addProperty("box-sizing", "border-box");
        style.addProperty("-moz-box-sizing", "border-box");
        style.addProperty("-webkit-box-sizing", "border-box");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.FluidPanel, " td");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style(".", StyleName.FluidPanelBlock);
        style.addProperty("vertical-align", "top");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.FluidPanelBlock, "-", StyleDependent.left);
        style.addProperty("width", "50%");
        style.addProperty("text-align", "right");
        addStyle(style);

        style = new Style(".", StyleName.FluidPanelBlock, "-", StyleDependent.right);
        style.addProperty("width", "50%");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.FluidPanelBlock, "-", StyleDependent.dual);
        style.addProperty("width", "100%");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.FluidPanel, "-", StyleDependent.collapsed, " .", StyleName.FluidPanelBlock, "-", StyleDependent.left);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.FluidPanel, "-", StyleDependent.collapsed, " .", StyleName.FluidPanelBlock, "-", StyleDependent.right);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.FluidPanel, "-", StyleDependent.collapsed, " .", StyleName.FluidPanelBlock, "-", StyleDependent.dual);

        addStyle(style);

        style = new Style(".", StyleName.FormPanelLeftCell);
        style.addProperty("width", "90%");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelRightCell);
        style.addProperty("width", "90%");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelDualCell);
        style.addProperty("width", "90%");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.FluidPanel, "-", StyleDependent.collapsed, " .", StyleName.FormPanelLeftCell);
        addStyle(style);

        style = new Style(".", StyleName.FluidPanel, "-", StyleDependent.collapsed, " .", StyleName.FormPanelRightCell);
        addStyle(style);

        style = new Style(".", StyleName.FluidPanel, "-", StyleDependent.collapsed, " .", StyleName.FormPanelDualCell);
        style.addProperty("width", "500px");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelCaptionLabel);
        style.addProperty("font-size", "1.5em");
        style.addProperty("padding", "10px 5px 5px 5px");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelHR);
        style.addProperty("background-color", getBackgroundColor(), 0.6);
        style.addProperty("height", "1px");
        style.addProperty("margin", "6px 0 4px 0");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelH1);
        style.addProperty("background-color", getBackgroundColor(), 0.1);
        style.addProperty("margin", "6px 0 4px 0");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelH1Image);
        style.addProperty("margin-right", "10px");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelH1Label);
        style.addProperty("color", getBackgroundColor(), 0.8);
        style.addProperty("padding", "4px");
        style.addProperty("font-size", "1.3em");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelH2);
        style.addProperty("border-bottom", "solid 2px");
        style.addProperty("border-bottom-color", getBackgroundColor(), 1);
        style.addProperty("margin", "6px 0 4px 0");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelH2Label);
        style.addProperty("color", getBackgroundColor(), 1);
        style.addProperty("padding", "3px");
        style.addProperty("font-size", "1.2em");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelH3);
        style.addProperty("border-bottom", "solid 1px");
        style.addProperty("border-bottom-color", getBackgroundColor(), 0.6);
        style.addProperty("margin", "6px 0 4px 0");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelH3Label);
        style.addProperty("color", getBackgroundColor(), 1.3);
        style.addProperty("padding", "2px");
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-style", "italic");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelH4);
        style.addProperty("background-color", getBackgroundColor(), 0);
        style.addProperty("margin", "6px 0 4px 0");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelH4Label);
        style.addProperty("color", getBackgroundColor(), 0.8);
        style.addProperty("padding", "1px");
        style.addProperty("font-size", "1.0em");
        style.addProperty("font-style", "italic");
        addStyle(style);

        style = new Style(".", StyleName.FormPanelActionWidget);
        style.addProperty("float", "right");
        style.addProperty("margin-right", "20px");
        addStyle(style);
    }

    protected abstract ThemeColor getBackgroundColor();
}
