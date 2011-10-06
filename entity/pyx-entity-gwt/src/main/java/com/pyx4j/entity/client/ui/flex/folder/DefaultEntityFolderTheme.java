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

import com.pyx4j.widgets.client.style.IStyleName;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.Theme;

public class DefaultEntityFolderTheme extends Theme {

    public DefaultEntityFolderTheme() {
        initStyles();
    }

    protected void initStyles() {
        Style style = new Style((IStyleName) CEntityFolder.StyleName.EntityFolder);
        style.addProperty("margin-top", "2px");
        style.addProperty("margin-left", "6px");
        style.addProperty("width", "80em");
        addStyle(style);

        style = new Style((IStyleName) CEntityFolder.StyleName.EntityFolderBoxItem);
        style.addProperty("padding", "6px");
        addStyle(style);

        style = new Style((IStyleName) CEntityFolder.StyleName.EntityFolderBoxDecorator);
        style.addProperty("margin", "6px");
        style.addProperty("width", "100%");
        style.addProperty("border", "dotted 1px");
        style.addProperty("border-color", "#333");
        addStyle(style);

        style = new Style(new Selector.Builder(CEntityFolder.StyleName.EntityFolderBoxDecorator).hover().build());
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", "#333");
        addStyle(style);

        style = new Style((IStyleName) CEntityFolder.StyleName.EntityFolderRowDecorator);
        style.addProperty("margin", "6px");
        addStyle(style);

        style = new Style(new Selector.Builder(CEntityFolder.StyleName.EntityFolderActionsBar).build());
        style.addProperty("opacity", "0.2");
        addStyle(style);

        style = new Style(new Selector.Builder(CEntityFolder.StyleName.EntityFolderActionsBar).discriminator(
                new Selector.Builder(CEntityFolder.StyleName.EntityFolderBoxDecorator).hover().build().toString()).build());
        style.addProperty("opacity", "1");
        addStyle(style);

        style = new Style(new Selector.Builder(CEntityFolder.StyleName.EntityFolderActionsBar).discriminator(
                new Selector.Builder(CEntityFolder.StyleName.EntityFolderRowDecorator).hover().build().toString()).build());
        style.addProperty("opacity", "1");
        addStyle(style);

        style = new Style(new Selector.Builder(CEntityFolder.StyleName.EntityFolderAddButton).build());
        style.addProperty("display", "inline-block");
        style.addProperty("margin", "6px");
        addStyle(style);

        style = new Style(new Selector.Builder(CEntityFolder.StyleName.EntityFolderAddButtonImage).build());
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(new Selector.Builder(CEntityFolder.StyleName.EntityFolderAddButtonLabel).build());
        style.addProperty("padding-left", "3px");
        style.addProperty("float", "left");
        addStyle(style);

    }
}
