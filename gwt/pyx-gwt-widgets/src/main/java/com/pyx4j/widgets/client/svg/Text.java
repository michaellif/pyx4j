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
 * @author vadims
 * @version $Id$
 */
package com.pyx4j.widgets.client.svg;

/**
 * 
 * @author vadims
 *         As per SVG 1.1 Text is not technically a shape but shares a lot of the saame
 *         attributes
 *         So for now it extends shape
 * 
 */
public class Text extends Shape {

    public Text(Coordinate coordinate, String text) {
        setElement(SvgDOM.createElementNS(SvgDOM.SVG_NAMESPACE, "text"));
        getElement().setAttribute("x", coordinate.getX().toString());
        getElement().setAttribute("y", coordinate.getY().toString());
        getElement().setInnerText(text);
    }

    public Text(int x, int y, String text) {
        this(new Coordinate(x, y), text);
    }

    public void setFont(String font) {
        getElement().setAttribute("font", font);
    }

    //possible values are :     start | middle | end |  inherit (think enumeration)
    public void setTextAnchor(String anchor) {
        getElement().setAttribute("text-anchor", anchor);
    }

}
