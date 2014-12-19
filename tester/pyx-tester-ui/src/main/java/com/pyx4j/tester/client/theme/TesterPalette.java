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
 * Created on Oct 6, 2011
 * @author michaellif
 */
package com.pyx4j.tester.client.theme;

import com.pyx4j.commons.css.Palette;
import com.pyx4j.commons.css.ThemeColor;

public class TesterPalette extends Palette {

    public TesterPalette() {
        putThemeColor(ThemeColor.object1, "#318FB2");
        putThemeColor(ThemeColor.object2, "B26C1F");
        putThemeColor(ThemeColor.contrast1, "red");
        putThemeColor(ThemeColor.contrast2, "orange");
        putThemeColor(ThemeColor.formBackground, "#fefefe");
        putThemeColor(ThemeColor.foreground, "#666666");
    }
}
