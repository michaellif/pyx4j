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

    private final LegendIconType iconType;

    private final SvgFactory svgFactory;

    private Shape icon;

    private final Text text;

    private int xc = 0;

    private int yc = 0;

    private int length = 7;

    private String color = "#000";

    private final Group group;

    //private 

    public LegendItem(SvgFactory svgFactory, String text, LegendIconType iconType, int x, int y) {
        this.iconType = iconType;
        this.svgFactory = svgFactory;

        group = svgFactory.createGroup();
        xc = x;
        yc = y;
        this.text = svgFactory.createText(text, xc + X_SHIFT + length, y);
        //move Y to be in the middle of the text
        String fs = this.text.getAttribute("font-size");

        if (fs == null)
            yc = y - Text.DEFAULT_FONT_SIZE / 2;
        else
            //assume that font size contains digits only 
            yc = y - Integer.valueOf(fs) / 2;

        switch (iconType) {
        case Rect: {
            length = 14;
            this.icon = svgFactory.createRect(xc - length / 2, yc - length / 2, length, length, 0, 0);
            break;
        }
        default: {
            this.icon = svgFactory.createCircle(xc, yc, length);
        }
        }
        this.setColor(color);
        group.add(this.icon);
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

    public void setColor(String color) {
        this.color = color;
        icon.setFill(color);
        icon.setStroke(color);
    }

    @Override
    public SvgElement asSvgElement() {
        return group;
    }

    /**
     * TODO need to calculate font width;
     * 
     * @return
     */
    public int getWidth() {
        int textlen = text.getTextValue() == null ? 0 : text.getTextValue().length();
        int width = length + X_SHIFT + (int) (textlen * Text.DEFAULT_FONT_SIZE * .65);
        if (iconType == LegendIconType.Circle)
            width += length;
        return width;
    }

    public int getHeight() {
        return Text.DEFAULT_FONT_SIZE;
    }

    public int getIconSize() {
        return length;
    }

}
