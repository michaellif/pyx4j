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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.folder;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public abstract class DefaultEntityFolderTheme extends Theme {

    public static enum StyleName implements IStyleName {
        EntityFolder, EntityFolderActionsBar, EntityFolderAddButton,

        EntityFolderRemoveButton, EntityFolderUpButton, EntityFolderDownButton, EntityFolderCustomButton,

        //Box
        EntityFolderBoxItem, EntityFolderBoxDecorator, EntityFolderBoxDecoratorAddButtonHolder,

        //Table
        EntityFolderRowItem, EntityFolderTableDecorator, EntityFolderRowItemDecorator, EntityFolderTableHeader, EntityFolderTableHeaderLabel
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, readOnly
    }

    public DefaultEntityFolderTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        Style style = new Style(".", StyleName.EntityFolder);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolder, "-", StyleDependent.readOnly);
        style.addProperty("font-weight", "bold");
        style.addProperty("color", ThemeColor.foreground, 1);
        addStyle(style);

        style = new Style(".", StyleName.EntityFolder, " img");
        style.addProperty("display", "block");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderBoxItem, " .", StyleName.EntityFolder);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderBoxDecorator);
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderBoxDecoratorAddButtonHolder);
        style.addProperty("width", "100%");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderTableDecorator);
        style.addProperty("margin-top", "2px");
        style.addProperty("background-color", getBackgroundColor(), 0.04);
        style.addProperty("padding", "4px");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderBoxItem);
        style.addProperty("padding", "18px 6px 6px");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderRowItemDecorator);
        //style.addProperty("width", "0");
        style.addProperty("margin", "6px");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderActionsBar);
        style.addProperty("opacity", "0.2");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderActionsBar, "-", StyleDependent.hover);
        style.addProperty("opacity", "1");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderAddButton);
        style.addProperty("display", "inline-block");
        style.addProperty("margin", "6px");
        style.addProperty("color", ThemeColor.object1, 0.8);
        style.addProperty("font-weight", "normal");
        style.addProperty("font-style", "italic");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderUpButton);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderDownButton);
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderRemoveButton);
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderRowItemDecorator, " .", StyleName.EntityFolderRemoveButton);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderCustomButton);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderRowItemDecorator, " .", StyleName.EntityFolderCustomButton);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderTableHeader);
        style.addProperty("border-bottom", "1px dotted");
        style.addProperty("border-bottom-color", getBackgroundColor());
        addStyle(style);

        style = new Style(".", StyleName.EntityFolderTableHeaderLabel);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("font-weight", "bold");
        style.addProperty("margin-left", "3px");
        style.addProperty("margin-right", "3px");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.EntityFolder, "-", StyleDependent.readOnly, " .", StyleName.EntityFolderTableHeaderLabel);
        style.addProperty("color", ThemeColor.foreground, 0.5);
        addStyle(style);

    }

    protected abstract ThemeColor getBackgroundColor();
}
