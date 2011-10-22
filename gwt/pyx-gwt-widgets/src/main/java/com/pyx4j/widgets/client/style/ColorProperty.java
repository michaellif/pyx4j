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
package com.pyx4j.widgets.client.style;

public class ColorProperty extends Property {

    private final ThemeColors color;

    private final double vibrance;

    public ColorProperty(String name, ThemeColors color, double vibrance) {
        super(name);
        this.color = color;
        this.vibrance = vibrance;
    }

    public ThemeColors getColor() {
        return color;
    }

    @Override
    protected String convertToString(Theme theme, Palette palette) {
        if (color == null) {
            throw new RuntimeException("theme property " + getName() + " should be set with color");
        }
        return getName() + ": " + palette.getThemeColor(color, vibrance) + ";";
    }

}
