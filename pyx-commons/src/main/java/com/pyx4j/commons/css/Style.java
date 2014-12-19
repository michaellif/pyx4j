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
 */
package com.pyx4j.commons.css;

import java.util.ArrayList;
import java.util.List;

public class Style {

    private String selector;

    private Selector selectorNew;

    private final List<Property> properties = new ArrayList<Property>();

    public Style(IStyleName styleName) {
        this.selectorNew = new Selector.Builder(styleName).build();
    }

    public Style(IStyleName styleName, IStyleDependent dependent) {
        this.selectorNew = new Selector.Builder(styleName).dependent(dependent).build();
    }

    public Style(String discriminator, IStyleName styleName, IStyleDependent dependent) {
        this.selectorNew = new Selector.Builder(styleName).discriminator(discriminator).dependent(dependent).build();
    }

    public Style(Selector selector) {
        this.selectorNew = selector;
    }

    public Style(String selector) {
        this.selectorNew = new Selector(selector);
    }

    public Style(Object... selector) {
        StringBuilder builder = new StringBuilder();
        for (Object object : selector) {
            builder.append(object);
        }
        this.selectorNew = new Selector(builder.toString());
    }

    public void addProperty(Property propertie) {
        properties.add(propertie);
    }

    public void addProperty(String name, String value) {
        properties.add(new StringProperty(name, value));
    }

    public void addProperty(String name, ThemeColor color, double vibrance) {
        properties.add(new ColorProperty(name, color, vibrance));
    }

    public void addProperty(String name, ThemeColor color) {
        properties.add(new ColorProperty(name, color, 1));
    }

    public void addGradient(ThemeColor startColor, double startVibrance, ThemeColor endColor, double endVibrance) {
        properties.add(new GradientProperty(startColor, startVibrance, endColor, endVibrance));
    }

    public void addGradient(ThemeColor startColor, ThemeColor endColor) {
        properties.add(new GradientProperty(startColor, 1, endColor, 1));
    }

    public void addTextShadow(ThemeColor color, String value) {
        properties.add(new TextShadowProperty(color, 1, value));
    }

    public void addTextShadow(ThemeColor color, double vibrance, String value) {
        properties.add(new TextShadowProperty(color, vibrance, value));
    }

    public void addBoxShadow(ThemeColor color, String value) {
        properties.add(new BoxShadowProperty(color, 1, value));
    }

    public void addBoxShadow(ThemeColor color, double vibrance, String value) {
        properties.add(new BoxShadowProperty(color, vibrance, value));
    }

    public String getCss(Theme theme, Palette palette) {
        StringBuilder builder = new StringBuilder();

        if (theme.getDiscriminator() != null) {
            builder.append(theme.getDiscriminator()).append(" ");
        }

        if (selectorNew != null) {
            builder.append(selectorNew).append(" {\n");
        } else {
            builder.append(selector).append(" {\n");
        }
        for (Property property : properties) {
            builder.append("  ").append(property.toString(theme, palette)).append("\n");
        }
        builder.append("}\n");
        return builder.toString();
    }

    @Deprecated
    public Style(Enum<?> selector) {
        this.selector = "." + selector.name();
    }

    @Deprecated
    public Style(Enum<?> selector, String ext) {
        this.selector = "." + selector.name() + ext;
    }

    @Deprecated
    public String getSelector() {
        return selector;
    }
}
