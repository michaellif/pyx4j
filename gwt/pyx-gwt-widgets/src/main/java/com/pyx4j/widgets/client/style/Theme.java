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
package com.pyx4j.widgets.client.style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Theme {

    private final List<Style> styles;

    private final Map<ThemeColor, String> themeColors;

    public Theme() {
        styles = new ArrayList<Style>();
        themeColors = new HashMap<ThemeColor, String>();
    }

    protected Theme(Theme other) {
        styles = new ArrayList<Style>(other.styles);
        themeColors = new HashMap<ThemeColor, String>(other.themeColors);
    }

    public List<Style> getAllStyles() {
        return styles;
    }

    public List<Style> getStyles(String selector) {
        List<Style> r = new Vector<Style>();
        for (Style style : styles) {
            if (selector.equals(style.getSelector())) {
                r.add(style);
            }
        }
        return Collections.unmodifiableList(r);
    }

    public void addStyle(Style style) {
        styles.add(style);
    }

    public String getThemeColor(ThemeColor color) {
        return themeColors.get(color);
    }

    public void putThemeColor(ThemeColor color, String value) {
        themeColors.put(color, value);
    }

    public Theme duplicate() {
        return new Theme(this);
    }

    public String getThemeId() {
        return this.getClass().getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Theme) {
            return getThemeId() != null && getThemeId().equals(((Theme) obj).getThemeId());
        }
        return false;
    }

}
