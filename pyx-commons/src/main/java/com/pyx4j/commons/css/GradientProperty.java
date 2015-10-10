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
 */
package com.pyx4j.commons.css;

public class GradientProperty extends Property {

    private final ThemeColor startColor;

    private final double startVibrance;

    private final ThemeColor endColor;

    private final double endVibrance;

    private GradientDirection direction;

    public enum GradientDirection {
        top, left
    }

    public GradientProperty(ThemeColor startColor, double startVibrance, ThemeColor endColor, double endVibrance) {
        this(startColor, startVibrance, endColor, endVibrance, GradientDirection.top);
    }

    public GradientProperty(ThemeColor startColor, double startVibrance, ThemeColor endColor, double endVibrance, GradientDirection direction) {
        super(null);
        this.startColor = startColor;
        this.startVibrance = startVibrance;
        this.endColor = endColor;
        this.endVibrance = endVibrance;
        this.direction = direction;
    }

    @Override
    protected String convertToString(Theme theme, Palette palette) {
        String color1 = (startColor != null) ? palette.getThemeColor(startColor, startVibrance) : "rgba(255, 255, 255, 0)";
        String color2 = (endColor != null) ? palette.getThemeColor(endColor, endVibrance) : "rgba(255, 255, 255, 0)";
        String noSupport = "background:" + color1 + ";";
        String ie = "background:-ms-linear-gradient(" + direction + ",  " + color1 + ",  " + color2 + ");";
        String ff = "background:-moz-linear-gradient(" + direction + ",  " + color1 + ",  " + color2 + ");";
        String other = direction.equals(GradientDirection.top)
                ? "background:-webkit-gradient(linear, left top, left bottom, from(" + color1 + "), to(" + color2 + "));"
                : "background:-webkit-gradient(linear, left top, right top, from(" + color1 + "), to(" + color2 + "));";
        return noSupport + "\n" + ie + "\n" + ff + "\n" + other;
    }
}
