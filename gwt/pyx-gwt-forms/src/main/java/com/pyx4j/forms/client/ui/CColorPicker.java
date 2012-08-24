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
 * Created on Jun 11, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

public class CColorPicker extends CFocusComponent<Integer, NColorPicker> {

    private Integer color;

    public CColorPicker() {
    }

    @Override
    protected NColorPicker createWidget() {
        return new NColorPicker(this);
    }

    public void setColor(int r, int g, int b, int a) {
        testColorValueRange(r, g, b, a);
        color = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
    }

    public Integer getColor() {
        return color;
    }

    private static void testColorValueRange(int r, int g, int b, int a) {
        boolean rangeError = false;
        StringBuilder badComponents = new StringBuilder();

        if (a < 0 || a > 255) {
            rangeError = true;
            badComponents.append(" Alpha");
        }
        if (r < 0 || r > 255) {
            rangeError = true;
            badComponents.append(" Red");
        }
        if (g < 0 || g > 255) {
            rangeError = true;
            badComponents.append(" Green");
        }
        if (b < 0 || b > 255) {
            rangeError = true;
            badComponents.append(" Blue");
        }
        if (rangeError == true) {
            throw new IllegalArgumentException("Color parameter outside of expected range:" + badComponents);
        }
    }
}
