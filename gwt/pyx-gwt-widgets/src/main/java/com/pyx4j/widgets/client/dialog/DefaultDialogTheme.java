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
package com.pyx4j.widgets.client.dialog;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;

public class DefaultDialogTheme extends Theme {

    public static enum StyleName implements IStyleName {
        Dialog, DialogCaption, DialogResizer, DialogContent,
    }

    public static enum StyleDependent implements IStyleDependent {

    }

    public DefaultDialogTheme() {
        initStyles();
    }

    protected void initStyles() {
        Style style = new Style(".", StyleName.Dialog);
        style.addProperty("background-color", ThemeColors.object1, 0.8);
        addStyle(style);

        style = new Style(".", StyleName.DialogCaption);
        style.addProperty("background", ThemeColors.object1, 0.8);
        style.addProperty("filter", "alpha(opacity=95)");
        style.addProperty("opacity", "0.95");
        style.addProperty("color", ThemeColors.object1, 0.2);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.DialogResizer);
        style.addProperty("background", ThemeColors.object1, 0.8);
        style.addProperty("filter", "alpha(opacity=95)");
        style.addProperty("opacity", "0.95");
        addStyle(style);

        style = new Style(".", StyleName.DialogContent);
        style.addProperty("background-color", ThemeColors.background);
        addStyle(style);

    }

}
