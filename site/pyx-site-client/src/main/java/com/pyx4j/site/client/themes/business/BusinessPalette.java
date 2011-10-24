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
package com.pyx4j.site.client.themes.business;

import com.pyx4j.commons.css.Palette;
import com.pyx4j.commons.css.ThemeColors;

public class BusinessPalette extends Palette {

    public BusinessPalette() {
        putThemeColor(ThemeColors.OBJECT_TONE1, 0x404040);
        putThemeColor(ThemeColors.OBJECT_TONE2, 0x303030);
        putThemeColor(ThemeColors.OBJECT_TONE3, 0x505050);
        putThemeColor(ThemeColors.OBJECT_TONE4, 0x505050);
        putThemeColor(ThemeColors.OBJECT_TONE5, 0x505050);
        putThemeColor(ThemeColors.BORDER, 0x666666);
        putThemeColor(ThemeColors.SELECTION, 0xC90);
        putThemeColor(ThemeColors.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemeColors.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemeColors.TEXT, 0x000000);
        putThemeColor(ThemeColors.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemeColors.MANDATORY_TEXT_BACKGROUND, 0xfcba84);
        putThemeColor(ThemeColors.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemeColors.SEPARATOR, 0x999999);
    }

}
