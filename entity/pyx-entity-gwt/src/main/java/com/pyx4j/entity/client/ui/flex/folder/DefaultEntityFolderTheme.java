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
package com.pyx4j.entity.client.ui.flex.folder;

import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleDependent.hover;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleDependent.readOnly;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolder;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolderActionsBar;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolderAddButton;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolderAddButtonImage;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolderAddButtonLabel;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolderBoxDecorator;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolderBoxItem;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolderBoxItemDecorator;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolderRowItemDecorator;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolderTableDecorator;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolderTableHeader;
import static com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName.EntityFolderTableHeaderLabel;

import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.Theme;

public class DefaultEntityFolderTheme extends Theme {

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
        style.addProperty("border-color", "#333");
        addStyle(style);

        style = new Style(".", EntityFolderBoxItemDecorator, ":hover");
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", "#333");
        addStyle(style);

        style = new Style(".", EntityFolderRowItemDecorator);
        style.addProperty("width", "0");
        style.addProperty("margin", "6px");
        addStyle(style);

        style = new Style(".", EntityFolderActionsBar);
        style.addProperty("opacity", "0.2");
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

        style = new Style(".", EntityFolderTableHeader);
        style.addProperty("border-bottom", "1px dotted #333");
        addStyle(style);

        style = new Style(".", EntityFolderTableHeaderLabel);
        style.addProperty("color", "#333333");
        style.addProperty("font-weight", "bold");
        style.addProperty("margin-left", "3px");
        style.addProperty("margin-right", "3px");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", EntityFolder, "-", readOnly, " .", EntityFolderTableHeaderLabel);
        style.addProperty("color", "#888888");
        addStyle(style);

    }
}
