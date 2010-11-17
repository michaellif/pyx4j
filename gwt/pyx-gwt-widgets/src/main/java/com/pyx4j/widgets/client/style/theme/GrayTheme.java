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
package com.pyx4j.widgets.client.style.theme;

import com.pyx4j.widgets.client.style.ThemeColor;

public class GrayTheme extends WindowsTheme {

    public GrayTheme() {
        super();
    }

    @Override
    protected void initThemeColors() {
        putThemeColor(ThemeColor.OBJECT_TONE1, 0x404040);
        putThemeColor(ThemeColor.OBJECT_TONE2, 0x303030);
        putThemeColor(ThemeColor.OBJECT_TONE3, 0x505050);
        putThemeColor(ThemeColor.OBJECT_TONE4, 0x387CBB);
        putThemeColor(ThemeColor.BORDER, 0xffffff);
        putThemeColor(ThemeColor.SELECTION, 0xffa500);
        putThemeColor(ThemeColor.SELECTION_TEXT, 0x000000);
        putThemeColor(ThemeColor.TEXT_BACKGROUND, 0x000000);
        putThemeColor(ThemeColor.TEXT, 0xffffff);
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, 0xfcba84);
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemeColor.SEPARATOR, 0x999999);
    }

}
