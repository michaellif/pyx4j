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
 * Created on Apr 26, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import java.util.ArrayList;
import java.util.List;

public class Style {

    private String selector;

    private final List<Property> properties = new ArrayList<Property>();

    public Style(String... selector) {
        this.selector = "";
        for (int i = 0; i < selector.length; i++) {
            if (this.selector.length() != 0) {
                this.selector += " ";
            }
            this.selector += selector[i];
        }
    }

    public Style(Enum<?> selector) {
        this.selector = "." + selector.name();
    }

    public Style(Enum<?> selector, String ext) {
        this.selector = "." + selector.name() + ext;
    }

    public String getSelector() {
        return selector;
    }

    public void addProperty(Property propertie) {
        properties.add(propertie);
    }

    public void addProperty(String name, String value) {
        properties.add(new StringProperty(name, value));
    }

    public void addProperty(String name, ThemeColors color) {
        properties.add(new ColorProperty(name, color));
    }

    public void addGradient(ThemeColors startColor, ThemeColors endColor) {
        properties.add(new GradientProperty(startColor, endColor));
    }

    public String toString(Theme theme, Palette palette) {
        StringBuilder builder = new StringBuilder();
        builder.append(selector).append(" {\n");
        for (Property property : properties) {
            builder.append("  ").append(property.toString(theme, palette)).append("\n");
        }
        builder.append("}\n");
        return builder.toString();
    }

}
