/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 21, 2013
 * @author michaellif
 */
package com.pyx4j.site.client.backoffice.ui.layout;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutTheme;

public class BackOfficeLayoutTheme extends ResponsiveLayoutTheme {

    public static enum StyleName implements IStyleName {
        BackOfficeLayoutInlineExtraPanel, BackOfficeLayoutInlineExtraPanelCaption
    }

    public BackOfficeLayoutTheme() {
        super();
    }

    @Override
    protected void initStyles() {
        Style style = new Style(".", StyleName.BackOfficeLayoutInlineExtraPanel);
        style.addProperty("border-top", "4px solid");
        style.addProperty("border-bottom", "4px solid");
        style.addProperty("border-left", "4px solid");
        style.addProperty("border-color", ThemeColor.object1);
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.BackOfficeLayoutInlineExtraPanelCaption);
        style.addProperty("line-height", "41px");
        style.addProperty("font-size", "1.2em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding", "5px");
        style.addGradient(ThemeColor.foreground, 0.2, ThemeColor.foreground, 0.15);
        addStyle(style);

        super.initStyles();
    }
}
