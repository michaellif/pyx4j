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
package com.pyx4j.entity.client.ui.folder;

import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleDependent.hover;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleDependent.readOnly;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolder;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderActionsBar;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderAddButton;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderAddButtonImage;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderAddButtonLabel;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderBoxDecorator;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderBoxItem;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderBoxItemDecorator;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderDownButton;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderRemoveButton;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderRowItemDecorator;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderTableDecorator;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderTableHeader;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderTableHeaderLabel;
import static com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName.EntityFolderUpButton;

import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;

public abstract class DefaultEntityFolderTheme extends Theme {

    public DefaultEntityFolderTheme() {
        initStyles();
    }

    protected void initStyles() {
        Style style = new Style(".", EntityFolder);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", EntityFolder, " img");
        style.addProperty("display", "block");
        addStyle(style);

        style = new Style(".", EntityFolderBoxItem, " .", EntityFolder);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", EntityFolderBoxDecorator);
        addStyle(style);

        style = new Style(".", EntityFolderTableDecorator);
        style.addProperty("margin-top", "2px");
        addStyle(style);

        style = new Style(".", EntityFolderBoxItem);
        style.addProperty("padding", "6px");
        addStyle(style);

        style = new Style(".", EntityFolderBoxItemDecorator);
        style.addProperty("margin", "6px");
        style.addProperty("border", "dotted 1px");
        style.addProperty("border-color", getBackgroundColor());
        addStyle(style);

        style = new Style(".", EntityFolderBoxItemDecorator, ":hover");
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", getBackgroundColor());
        addStyle(style);

        style = new Style(".", EntityFolderRowItemDecorator);
        style.addProperty("width", "0");
        style.addProperty("margin", "6px");
        addStyle(style);

        style = new Style(".", EntityFolderActionsBar);
        style.addProperty("opacity", "0.2");
        style.addProperty("width", "63px");
        addStyle(style);

        style = new Style(".", EntityFolderActionsBar, "-", hover);
        style.addProperty("opacity", "1");
        addStyle(style);

        style = new Style(".", EntityFolderAddButton);
        style.addProperty("display", "inline-block");
        style.addProperty("margin", "6px");
        addStyle(style);

        style = new Style(".", EntityFolderAddButtonImage);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", EntityFolderAddButtonLabel);
        style.addProperty("padding-left", "3px");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", EntityFolderRemoveButton);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", EntityFolderUpButton);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", EntityFolderDownButton);
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", EntityFolderRemoveButton);
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", EntityFolderRowItemDecorator, " .", EntityFolderRemoveButton);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", EntityFolderTableHeader);
        style.addProperty("border-bottom", "1px dotted");
        style.addProperty("border-bottom-color", getBackgroundColor());
        addStyle(style);

        style = new Style(".", EntityFolderTableHeaderLabel);
        style.addProperty("color", ThemeColors.foreground);
        style.addProperty("font-weight", "bold");
        style.addProperty("margin-left", "3px");
        style.addProperty("margin-right", "3px");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", EntityFolder, "-", readOnly, " .", EntityFolderTableHeaderLabel);
        style.addProperty("color", ThemeColors.foreground, 0.8);
        addStyle(style);

    }

    protected abstract ThemeColors getBackgroundColor();
}
