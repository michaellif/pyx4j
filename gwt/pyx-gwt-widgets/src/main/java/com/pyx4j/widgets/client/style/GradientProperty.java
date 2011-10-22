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

import com.pyx4j.widgets.client.util.BrowserType;

public class GradientProperty extends Property {

    private final ThemeColors startColor;

    private final double startVibrance;

    private final ThemeColors endColor;

    private final double endVibrance;

    public GradientProperty(ThemeColors startColor, double startVibrance, ThemeColors endColor, double endVibrance) {
        super(null);
        this.startColor = startColor;
        this.startVibrance = startVibrance;
        this.endColor = endColor;
        this.endVibrance = endVibrance;
    }

    @Override
    protected String convertToString(Theme theme, Palette palette) {
        String color1 = palette.getThemeColor(startColor, startVibrance);
        String color2 = palette.getThemeColor(endColor, endVibrance);
        if (BrowserType.isIE()) {
            return "filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='" + color1 + "', endColorstr='" + color2 + "');";
        } else if (BrowserType.isFirefox()) {
            return "background:-moz-linear-gradient(top,  " + color1 + ",  " + color2 + ");";
        } else {
            return "background:-webkit-gradient(linear, left top, left bottom, from(" + color1 + "), to(" + color2 + "));";
        }
    }
}
