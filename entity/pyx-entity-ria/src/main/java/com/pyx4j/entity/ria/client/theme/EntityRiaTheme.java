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
 * Created on Jan 18, 2010
 * @author michaellif
 * @version $Id: SiteTheme.java 7522 2010-11-17 11:36:20Z michaellif $
 */
package com.pyx4j.entity.ria.client.theme;

import com.pyx4j.entity.client.EntityCSSClass;
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.ria.client.theme.RiaTheme;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;

public abstract class EntityRiaTheme extends RiaTheme {

    EntityRiaTheme(float hue, float saturation, float brightness) {
        super(hue, saturation, brightness);
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        initEntityCRUDStyles();
        initEntityDataTableStyles();
    }

    protected void initEntityCRUDStyles() {
        Style style = new Style("." + EntityCSSClass.pyx4j_Entity_EntitySearchCriteria.name());
        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("padding", "2px");
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
        style = new Style("." + EntityCSSClass.pyx4j_Entity_EntityEditor.name());
        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("padding", "3px");
        style.addProperty("margin-bottom", "10px");
        addStyle(style);
    }

    protected void initEntityDataTableStyles() {
        String prefix = DataTable.BASE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("margin", "2px 0px 2px 0px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row));
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.even));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.odd));
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.nodetails));
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Header));
        style.addProperty("background-color", "#A0A0A0");
        style.addProperty("color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ActionsBar));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE4);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }

}
