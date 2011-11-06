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

public enum ThemeColors {

    object1("#5B6E90"),

    object2("#A6D6E2"),

    contrast1("#E7AB6B"),

    contrast2("#E76B6B"),

    background("#FFFFFF"),

    foreground("#000000"),

    //=================================== Old stuff

    OBJECT_TONE1,

    OBJECT_TONE2,

    OBJECT_TONE3,

    OBJECT_TONE4,

    OBJECT_TONE5,
//
// new, extended colour set:
//    
    OBJECT_TONE10,

    OBJECT_TONE15,

    OBJECT_TONE20,

    OBJECT_TONE25,

    OBJECT_TONE30,

    OBJECT_TONE35,

    OBJECT_TONE40,

    OBJECT_TONE45,

    OBJECT_TONE50,

    OBJECT_TONE55,

    OBJECT_TONE60,

    OBJECT_TONE65,

    OBJECT_TONE70,

    OBJECT_TONE75,

    OBJECT_TONE80,

    OBJECT_TONE85,

    OBJECT_TONE90,

    OBJECT_TONE95,
//
// other colours:
//    
    BORDER,

    SELECTION,

    SELECTION_TEXT,

    SEPARATOR,

    TEXT,

    TEXT_BACKGROUND,

    DISABLED_TEXT_BACKGROUND,

    MANDATORY_TEXT_BACKGROUND,

    READ_ONLY_TEXT_BACKGROUND;

    private String defaultColor;

    private ThemeColors() {
        this("ffffff");
    }

    private ThemeColors(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    public String getDefaultColor() {
        return defaultColor;
    }
}