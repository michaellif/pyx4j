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
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style.gray;

import com.pyx4j.widgets.client.style.ThemeColor;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

public class GrayTheme extends WindowsTheme {

    public GrayTheme() {
        super();
    }

    @Override
    protected void initThemeColors() {
        putThemeColor(ThemeColor.OBJECT_TONE1, "#404040");
        putThemeColor(ThemeColor.OBJECT_TONE2, "#303030");
        putThemeColor(ThemeColor.OBJECT_TONE3, "#505050");
        putThemeColor(ThemeColor.BORDER, "white");
        putThemeColor(ThemeColor.SELECTION, "orange");
        putThemeColor(ThemeColor.SELECTION_TEXT, "#000000");
        putThemeColor(ThemeColor.TEXT_BACKGROUND, "#000000");
        putThemeColor(ThemeColor.TEXT, "#ffffff");
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, "#fafafa");
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, "#fcba84");
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, "#eeeeee");
        putThemeColor(ThemeColor.SEPARATOR, "#999999");
    }

}
