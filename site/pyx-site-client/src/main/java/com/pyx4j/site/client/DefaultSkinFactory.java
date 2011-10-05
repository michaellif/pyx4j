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
 * Created on Mar 18, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import com.pyx4j.site.client.themes.business.BusinessPalette;
import com.pyx4j.site.client.themes.business.BusinessTheme;
import com.pyx4j.site.client.themes.console.ConsolePalette;
import com.pyx4j.site.client.themes.console.ConsoleTheme;
import com.pyx4j.site.client.themes.dark.DarkPalette;
import com.pyx4j.site.client.themes.dark.DarkTheme;
import com.pyx4j.site.client.themes.light.LightPalette;
import com.pyx4j.site.client.themes.light.LightTheme;
import com.pyx4j.site.shared.domain.DefaultSkins;
import com.pyx4j.widgets.client.style.Palette;
import com.pyx4j.widgets.client.style.Theme;

/**
 * @deprecated Create SkinFactory for each Site. This takes too much to download in
 *             initial code (-17K in particular).
 */
@Deprecated
public class DefaultSkinFactory implements SkinFactory {

    public DefaultSkinFactory() {

    }

    @Override
    public Theme createTheme(String skinName) {
        if (DefaultSkins.light.name().equals(skinName)) {
            return new LightTheme();
        } else if (DefaultSkins.dark.name().equals(skinName)) {
            return new DarkTheme();
        } else if (DefaultSkins.business.name().equals(skinName)) {
            return new BusinessTheme();
        } else if (DefaultSkins.console.name().equals(skinName)) {
            return new ConsoleTheme();
        }
        return null;
    }

    @Override
    public Palette createPalette(String skinName) {
        if (DefaultSkins.light.name().equals(skinName)) {
            return new LightPalette();
        } else if (DefaultSkins.dark.name().equals(skinName)) {
            return new DarkPalette();
        } else if (DefaultSkins.business.name().equals(skinName)) {
            return new BusinessPalette();
        } else if (DefaultSkins.console.name().equals(skinName)) {
            return new ConsolePalette();
        }
        return null;
    }

}
