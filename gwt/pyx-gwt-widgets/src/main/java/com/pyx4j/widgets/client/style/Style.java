/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 26, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.widgets.client.style.Theme.ThemeColorProperty;

public class Style {

    private final String selector;

    private final List<Property> properties = new ArrayList<Property>();

    public Style(String selector) {
        this.selector = selector;
    }

    public String getSelector() {
        return selector;
    }

    public void addProperty(Property propertie) {
        properties.add(propertie);
    }

    public void addProperty(String name, String value) {
        properties.add(new Property(name, value));
    }

    public void addProperty(String name, ThemeColorProperty color) {
        properties.add(new Property(name, color));
    }

    public void removeProperty(String name) {
        for (Property property : properties) {
            if (property.getName().equals(name)) {
                properties.remove(property);
                break;
            }
        }
    }

    public void addProperties(String styleText) {
        String[] lines = styleText.split(";");
        for (String line : lines) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            addProperty(new Property(line));
        }
    }

    public void updateProperties(String styleText) {
        properties.clear();
        addProperties(styleText);
    }

    public String toEditableString() {
        StringBuilder builder = new StringBuilder();
        for (Property property : properties) {
            builder.append(property.toString()).append("\n");
        }
        return builder.toString();
    }

    public String toString(Theme theme) {
        StringBuilder builder = new StringBuilder();
        builder.append(selector).append(" {\n");
        for (Property property : properties) {
            builder.append("  ").append(property.toString(theme)).append("\n");
        }
        builder.append("}\n");
        return builder.toString();
    }

}
