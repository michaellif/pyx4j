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
 * Created on Jan 6, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.commons.css;

//See http://jqueryui.com/themeroller/ for example of style manager

public enum ThemeColor {

    object1("#5B6E90"),

    object2("#A6D6E2"),

    contrast1("#E7AB6B"),

    contrast2("#E76B6B"),

    contrast3("#E76B6B"),

    contrast4("#E76B6B"),

    contrast5("#E76B6B"),

    contrast6("#E76B6B"),

    background("#FFFFFF"),

    foreground("#000000");

    private String defaultColor;

    private ThemeColor() {
        this("ffffff");
    }

    private ThemeColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    public String getDefaultColor() {
        return defaultColor;
    }
}