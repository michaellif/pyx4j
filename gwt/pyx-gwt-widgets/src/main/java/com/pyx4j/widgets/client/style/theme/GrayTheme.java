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

import com.pyx4j.widgets.client.style.ThemePalette;

public class GrayTheme extends WindowsTheme {

    public GrayTheme() {
        super();
    }

    @Override
    protected void initThemeColors() {
        putThemeColor(ThemePalette.OBJECT_TONE1, 0x404040);
        putThemeColor(ThemePalette.OBJECT_TONE2, 0x404040);
        putThemeColor(ThemePalette.OBJECT_TONE3, 0x303030);
        putThemeColor(ThemePalette.OBJECT_TONE4, 0x505050);
        putThemeColor(ThemePalette.OBJECT_TONE5, 0x387CBB);
        putThemeColor(ThemePalette.BORDER, 0xffffff);
        putThemeColor(ThemePalette.SELECTION, 0xffa500);
        putThemeColor(ThemePalette.SELECTION_TEXT, 0x000000);
        putThemeColor(ThemePalette.TEXT_BACKGROUND, 0x000000);
        putThemeColor(ThemePalette.TEXT, 0xffffff);
        putThemeColor(ThemePalette.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemePalette.MANDATORY_TEXT_BACKGROUND, 0xfcba84);
        putThemeColor(ThemePalette.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemePalette.SEPARATOR, 0x999999);
    }

}
