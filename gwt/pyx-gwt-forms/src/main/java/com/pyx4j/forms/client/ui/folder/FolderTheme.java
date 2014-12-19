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
 * Created on Oct 5, 2011
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.folder;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.forms.client.ui.CComponentTheme;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public abstract class FolderTheme extends Theme {

    public static enum StyleName implements IStyleName {

        CFolder, CFolderActionsBar, CFolderAddButton, CFolderContent, CFolderMessagePanel,

        CFolderRemoveButton, CFolderUpButton, CFolderDownButton, CFolderCustomButton,

        CFolderNoDataMessage,

        //Box
        CFolderBoxItem, CFolderBoxDecorator, CFolderBoxDecoratorAddButtonHolder,

        //Table
        CFolderRowItem, CFolderTableDecorator, CFolderRowItemDecorator, CFolderTableHeader, CFolderTableHeaderLabel
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, readOnly
    }

    public FolderTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        Style style = new Style(".", StyleName.CFolder);
        style.addProperty("width", "100%");
        style.addProperty("min-height", "40px");
        addStyle(style);

        style = new Style(".", StyleName.CFolder, "-", StyleDependent.readOnly);
        style.addProperty("font-weight", "bold");
        style.addProperty("color", ThemeColor.foreground, 1);
        addStyle(style);

        style = new Style(".", StyleName.CFolder, " img");
        style.addProperty("display", "block");
        addStyle(style);

        style = new Style(".", StyleName.CFolderBoxDecorator);
        addStyle(style);

        style = new Style(".", StyleName.CFolderMessagePanel, " .", CComponentTheme.StyleName.NoteLabel);
        style.addProperty("font-size", "1.2em");
        addStyle(style);

        style = new Style(".", StyleName.CFolderMessagePanel, " .", CComponentTheme.StyleName.ValidationLabel);
        style.addProperty("font-size", "1.2em");
        addStyle(style);

        style = new Style(".", StyleName.CFolderBoxDecoratorAddButtonHolder);
        style.addProperty("width", "100%");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.CFolderTableDecorator);
        style.addProperty("margin-top", "2px");
        style.addProperty("display", "inline-block");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.CFolderContent);
        addStyle(style);

        style = new Style(".", StyleName.CFolderBoxItem);
        style.addProperty("padding", "6px");
        addStyle(style);

        style = new Style(".", StyleName.CFolderBoxItem, " .", StyleName.CFolder);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.CFolderRowItemDecorator);
        //style.addProperty("width", "0");
        addStyle(style);

        style = new Style(".", StyleName.CFolderActionsBar);
        style.addProperty("opacity", "0.2");
        addStyle(style);

        style = new Style(".", StyleName.CFolderActionsBar, "-", StyleDependent.hover);
        style.addProperty("opacity", "1");
        addStyle(style);

        style = new Style(".", StyleName.CFolderAddButton, ".", WidgetsTheme.StyleName.Button);
        style.addProperty("display", "inline-block");
        style.addProperty("margin", "6px");
        style.addProperty("color", ThemeColor.object1, 0.8);
        style.addProperty("font-weight", "normal");
        style.addProperty("font-style", "italic");
        style.addProperty("cursor", "pointer");
        style.addProperty("line-height", "1em");
        style.addProperty("border", "none");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(".", StyleName.CFolderAddButton, " .", WidgetsTheme.StyleName.ButtonText);
        style.addProperty("color", ThemeColor.foreground, 0.5);
        addStyle(style);

        style = new Style(".", StyleName.CFolderUpButton);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.CFolderDownButton);
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.CFolderRemoveButton);
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.CFolderRowItemDecorator);
        style.addProperty("line-height", "1em");
        addStyle(style);

        style = new Style(".", StyleName.CFolderRowItemDecorator, " .", StyleName.CFolderRemoveButton);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.CFolderCustomButton);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.CFolderRowItemDecorator, " .", StyleName.CFolderCustomButton);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.CFolderTableHeader);
        style.addProperty("margin-top", "6px");
        style.addProperty("border-bottom", "1px dotted");
        style.addProperty("border-bottom-color", getBackgroundColor());
        style.addProperty("line-height", "1.5em");
        addStyle(style);

        style = new Style(".", StyleName.CFolderTableHeaderLabel);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("font-weight", "bold");
        style.addProperty("margin-left", "3px");
        style.addProperty("margin-right", "3px");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.CFolder, "-", StyleDependent.readOnly, " .", StyleName.CFolderTableHeaderLabel);
        style.addProperty("color", ThemeColor.foreground, 0.5);
        addStyle(style);

        style = new Style(".", StyleName.CFolderNoDataMessage);
        style.addProperty("font-style", "italic");
        style.addProperty("color", ThemeColor.foreground, 0.7);
        style.addProperty("padding", "10px");
        addStyle(style);

    }

    protected abstract ThemeColor getBackgroundColor();
}
