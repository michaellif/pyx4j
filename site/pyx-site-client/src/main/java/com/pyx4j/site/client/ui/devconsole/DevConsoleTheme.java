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
 * @version $Id$
 */
package com.pyx4j.site.client.ui.devconsole;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class DevConsoleTheme extends Theme {

    public static enum StyleName implements IStyleName {
        DevConsole, DevConsoleHandler, DevConsoleHandlerLabel, DevConsoleHandlerImage, DevConsoleContent;
    }

    public DevConsoleTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        Style style = new Style(".", StyleName.DevConsole);
        style.addProperty("width", "100%");
        style.addProperty("position", "absolute");
        style.addProperty("bottom", "0");
        addStyle(style);

        style = new Style(".", StyleName.DevConsoleHandler);
        style.addProperty("margin", "-34px 20px 0");
        style.addProperty("line-height", "34px");
        style.addProperty("padding", "0 10px");
        style.addProperty("height", "34px");
        style.addProperty("float", "right");
        style.addProperty("color", ThemeColor.background, 1.0);
        style.addProperty("cursor", "pointer");
        style.addProperty("background-color", ThemeColor.foreground, 1.0);
        addStyle(style);

        style = new Style(".", StyleName.DevConsoleHandlerLabel);
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.DevConsoleHandlerImage);
        style.addProperty("margin-left", "10px");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.DevConsoleContent);
        style.addProperty("text-align", "left");
        style.addProperty("margin", "0 20px");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.foreground, 1.0);
        style.addProperty("background-color", ThemeColor.background, 1.0);
        addStyle(style);

    }

}
