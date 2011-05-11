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
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.svg.gwt;

import com.pyx4j.svg.basic.Text;

public class TextImpl extends ShapeImpl implements Text {

    private final String text;

    public TextImpl(String text, int x, int y) {
        super(SvgDOM.createElementNS(SvgDOM.SVG_NAMESPACE, "text"));
        this.text = text;
        getElement().setInnerText(text);
        getElement().setAttribute("x", String.valueOf(x));
        getElement().setAttribute("y", String.valueOf(y));
        getElement().setAttribute("stroke-width", "0");
        getElement().setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
        getElement().setAttribute("fill", "black");
        getElement().setAttribute("font-family", "Arial");
        getElement().setAttribute("dominant-baseline", "mathematical");
    }

    public void setFont(String font) {
        getElement().setAttribute("font", font);
    }

    //possible values are :     start | middle | end |  inherit (think enumeration)
    public void setTextAnchor(String anchor) {
        getElement().setAttribute("text-anchor", anchor);
    }

    @Override
    public String getTextValue() {
        return text;
    }

}
