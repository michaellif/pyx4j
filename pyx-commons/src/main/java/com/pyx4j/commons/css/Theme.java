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
 */
package com.pyx4j.commons.css;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class Theme {

    private final List<String> atRules;

    private final List<Style> styles;

    private final HashMap<ThemeId, Theme> mixinThemes;

    public Theme() {
        atRules = new ArrayList<String>();
        styles = new ArrayList<Style>();
        mixinThemes = new LinkedHashMap<ThemeId, Theme>();
    }

    public abstract ThemeId getId();

    public void addAtRule(String atRule) {
        atRules.add(atRule);
    }

    public List<String> getAllAtRules() {
        List<String> retValue = new ArrayList<String>(atRules);
        for (Theme theme : mixinThemes.values()) {
            retValue.addAll(theme.getAllAtRules());
        }
        return retValue;
    }

    public void addStyle(Style style) {
        if (style == null) {
            throw new Error("Style can't be null");
        }
        styles.add(style);
    }

    public List<Style> getAllStyles() {
        List<Style> retValue = new ArrayList<Style>(styles);
        for (Theme theme : mixinThemes.values()) {
            retValue.addAll(theme.getAllStyles());
        }
        return retValue;
    }

    public void addTheme(Theme theme) {
        mixinThemes.put(theme.getId(), theme);
    }

    public Theme getTheme(ThemeId id) {
        return mixinThemes.get(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Theme) {
            return this.getClass().equals(((Theme) obj).getClass());
        }
        return false;
    }

    public String getCss(Palette palette) {
        StringBuilder stylesString = new StringBuilder();

        // Add do-not-print css:
        stylesString.append("@media print {");
        stylesString.append("." + StyleManager.DO_NOT_PRINT_CLASS_NAME + ", ." + StyleManager.DO_NOT_PRINT_CLASS_NAME + " * {display: none !important;}");
        stylesString.append("}\n");

        for (String atRule : getAllAtRules()) {
            stylesString.append(atRule);
        }

        for (Style style : getAllStyles()) {
            stylesString.append(style.getCss(this, palette));
        }

        return stylesString.toString();
    }
}
