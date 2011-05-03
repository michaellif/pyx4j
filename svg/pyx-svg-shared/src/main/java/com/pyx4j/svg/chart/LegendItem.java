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
 * Created on May 2, 2011
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.Shape;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.Text;

public class LegendItem implements IsSvgElement {

    private final int X_SHIFT = 10;

    private int Y_SHIFT;//TODO ideally needs to be calculated. Need to know text height 

    private final LegendIconType iconType;

    private final SvgFactory svgFactory;

    private Shape icon;

    private Text text;

    private int xc = 0;

    private int yc = 0;

    private int length = 5;

    private String color = "#000";

    private final Group group;

    public LegendItem(LegendIconType iconType, SvgFactory svgFactory, int x, int y, int length) {
        this.iconType = iconType;
        this.svgFactory = svgFactory;

        group = svgFactory.createGroup();

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
        group.add(this.icon);

    }

    public LegendItem(String text, LegendIconType iconType, SvgFactory svgFactory, int x, int y, int length) {
        this(iconType, svgFactory, x, y, length);
        this.text = svgFactory.createText(text, xc + X_SHIFT + length, yc + Y_SHIFT);
        group.add(this.text);
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

    //TODO create a set of methods to change the initial size and position of the group 
    public void setColor(String color) {
        this.color = color;
        icon.setFill(color);
    }

    @Override
    public SvgElement asSvgElement() {
        return group;
    }

}
