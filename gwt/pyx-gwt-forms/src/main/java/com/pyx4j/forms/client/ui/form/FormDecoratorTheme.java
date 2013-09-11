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
 * Created on Jul 25, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.form;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class FormDecoratorTheme extends Theme {

    public static enum StyleName implements IStyleName {

        FormDecoratorPanel, FormDecoratorMain,

        FormDecoratorHeader, FormDecoratorCaption,

        FormDecoratorFooter

    }

    public FormDecoratorTheme() {
        initContentStyles();
        initHeaderStyles();
        initFooterStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initContentStyles() {
        Style style = new Style(".", StyleName.FormDecoratorPanel);
        addStyle(style);
    }

    protected void initHeaderStyles() {
        Style style = new Style(".", StyleName.FormDecoratorHeader);
        style.addProperty("width", "100%");
        style.addProperty("height", "2em");
        style.addProperty("border-bottom", "1px solid");
        style.addProperty("margin-bottom", "0.5em");
        style.addProperty("white-space", "nowrap");
        style.addProperty("font-size", "1.3em");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.FormDecoratorCaption);
        style.addProperty("float", "left");
        style.addProperty("padding", "0 1em");
        addStyle(style);

    }

    protected void initFooterStyles() {
        Style style = new Style(".", StyleName.FormDecoratorFooter);
        style.addProperty("padding", "2px 0");
        style.addProperty("margin", "0.5em 0");
        addStyle(style);

        style = new Style(".", StyleName.FormDecoratorFooter, " .", DefaultWidgetsTheme.StyleName.Toolbar);
        style.addProperty("padding", "2px");
        addStyle(style);
    }

}