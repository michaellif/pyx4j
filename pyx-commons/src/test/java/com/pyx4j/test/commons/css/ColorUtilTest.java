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
 * Created on Oct 22, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.test.commons.css;

import junit.framework.TestCase;

import com.pyx4j.commons.css.ColorUtil;

public class ColorUtilTest extends TestCase {

    public void testParser() {
        assertEquals("#ffff00", ColorUtil.rgbToHex(ColorUtil.parseToRgb("#ffff00")));
        assertEquals("#ffff00", ColorUtil.rgbToHex(ColorUtil.parseToRgb("#ff0")));
        assertEquals("#ffff00", ColorUtil.rgbToHex(ColorUtil.parseToRgb("yellow")));
        assertEquals("#dcdcdc", ColorUtil.rgbToHex(ColorUtil.parseToRgb("#dcdcdc")));
        assertEquals("#dcdcdc", ColorUtil.rgbToHex(ColorUtil.parseToRgb("gainsboro")));
        assertEquals("#000000", ColorUtil.rgbToHex(ColorUtil.parseToRgb("#000000")));
        assertEquals("#ffffff", ColorUtil.rgbToHex(ColorUtil.parseToRgb("#ffffff")));
        assertNull(ColorUtil.parseToRgb("#fff00"));
        assertNull(ColorUtil.parseToRgb("yelow"));
        assertNull(ColorUtil.parseToRgb("#yellow"));

        assertEquals("#ffff00", ColorUtil.rgbToHex(ColorUtil.rgbToRgbv(ColorUtil.parseToRgb("yellow"), 1)));
        assertEquals("#ffff80", ColorUtil.rgbToHex(ColorUtil.rgbToRgbv(ColorUtil.parseToRgb("yellow"), (float) 0.5)));

        assertEquals("#313329", ColorUtil.rgbToHex(ColorUtil.hsbToRgb((float) 0.2, (float) 0.2, (float) 0.2)));

        assertEquals("#96998a", ColorUtil.rgbToHex(ColorUtil.hsbToRgb((float) 0.2, (float) 0.1, (float) 0.6)));
        assertEquals("#96998a", ColorUtil.rgbToHex(ColorUtil.hsbvToRgb((float) 0.2, (float) 0.2, (float) 0.2, (float) 0.5)));

        assertEquals("#161a0a", ColorUtil.rgbToHex(ColorUtil.hsbToRgb((float) 0.2, (float) 0.6, (float) 0.1)));
        assertEquals("#161a0a", ColorUtil.rgbToHex(ColorUtil.hsbvToRgb((float) 0.2, (float) 0.2, (float) 0.2, (float) 1.5)));

    }
}
