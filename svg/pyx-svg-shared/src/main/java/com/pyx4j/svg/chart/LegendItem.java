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
 * Created on May 1, 2011
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import com.pyx4j.svg.basic.Shape;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.Text;

public class LegendItem {

    private final int X_SHIFT = 10;

    private int Y_SHIFT;//TODO ideally needs to be calculated. Need to know text height 

    private final LegendIconType iconType;

    private SvgFactory svgFactory;

    private Shape icon;

    private Text text;

    private int xc = 0;

    private int yc = 0;

    private int length = 5;

    private String color = "#000";

    public LegendItem(LegendIconType iconType, SvgFactory svgFactory) {
        this.iconType = iconType;
        this.svgFactory = svgFactory;
    }

    public LegendItem(LegendIconType iconType, int x, int y, int length) {

        this.iconType = iconType;
        xc = x;
        yc = y;
        this.length = length;
        switch (iconType) {
        case Rect: {
            this.icon = svgFactory.createRect(xc, yc, length, length, 0, 0);
            Y_SHIFT = 10;
            break;
        }
        default: {
            Y_SHIFT = 4;
            this.icon = svgFactory.createCircle(xc, yc, length);
        }
        }
        this.setColor(color);
        text = null;

    }

    public LegendItem(String text, LegendIconType iconType, int x, int y, int length) {
        this(iconType, x, y, length);
        this.text = svgFactory.createText(text, xc + X_SHIFT + length, yc + Y_SHIFT);
    }

    public Text getText() {
        return text;
    }

    public Shape getIcon() {
        return icon;
    }

    public LegendIconType getIconType() {
        return iconType;
    }

    //TODO create a set of methods to change the initial size of the group 

//    public void setX(int x) {
//        xc = x;
//        icon.getElement().setAttribute("x", String.valueOf(xc));
//        if (text != null)
//            text.getElement().setAttribute("x", String.valueOf(xc + X_SHIFT + length));
//
//    }
//
//    public void setY(int y) {
//        icon.getElement().setAttribute("y", String.valueOf(yc));
//        if (text != null)
//            text.getElement().setAttribute("y", String.valueOf(yc + Y_SHIFT));
//    }
//
//    public void setTransform(String transform) {
//        icon.getElement().setAttribute("transform", transform);
//        if (text != null)
//            text.getElement().setAttribute("transform", transform);
//    }
//
//    public void setShapeLength(int length) {
//        this.length = length;
//        switch (iconType) {
//        case Rect: {
//            icon.getElement().setAttribute("width", String.valueOf(length));
//            icon.getElement().setAttribute("height", String.valueOf(length));
//        }
//        default: {
//            icon.getElement().setAttribute("r", String.valueOf(length));
//        }
//        }
//        if (text != null)
//            text.getElement().setAttribute("x", String.valueOf(xc + X_SHIFT + length));
//    }

    public void setColor(String color) {
        this.color = color;
        icon.setFill(color);
    }
}
