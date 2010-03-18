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

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.site.client.themes.business.BusinessTheme;
import com.pyx4j.site.client.themes.console.ConsoleTheme;
import com.pyx4j.site.client.themes.dark.DarkTheme;
import com.pyx4j.site.client.themes.light.LightTheme;
import com.pyx4j.site.shared.domain.DefaultSkins;
import com.pyx4j.site.shared.domain.SkinType;
import com.pyx4j.widgets.client.style.Theme;

public class DefaultSkinFactory implements SkinFactory {

    private final Map<SkinType, Theme> themes = new HashMap<SkinType, Theme>();

    public DefaultSkinFactory() {

    }

    @Override
    public Theme createSkin(SkinType type) {
        if (DefaultSkins.light.equals(type)) {
            if (!themes.containsKey(type)) {
                themes.put(type, new LightTheme());
            }
            return themes.get(type);
        } else if (DefaultSkins.dark.equals(type)) {
            if (!themes.containsKey(type)) {
                themes.put(type, new DarkTheme());
            }
            return themes.get(type);
        } else if (DefaultSkins.business.equals(type)) {
            if (!themes.containsKey(type)) {
                themes.put(type, new BusinessTheme());
            }
            return themes.get(type);
        } else if (DefaultSkins.console.equals(type)) {
            if (!themes.containsKey(type)) {
                themes.put(type, new ConsoleTheme());
            }
            return themes.get(type);
        }
        return null;
    }

}
