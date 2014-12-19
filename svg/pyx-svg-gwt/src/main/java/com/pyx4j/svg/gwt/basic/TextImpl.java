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
 */
package com.pyx4j.svg.gwt.basic;

import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.gwt.SvgDOM;

public class TextImpl extends ShapeImpl implements Text {

    private String text;

    public TextImpl(String text, int x, int y) {
        super(SvgDOM.createElementNS(SvgDOM.SVG_NAMESPACE, "text"));
        this.text = text;
        getElement().setInnerText(text);
        getElement().setAttribute("x", String.valueOf(x));
        getElement().setAttribute("y", String.valueOf(y));
        getElement().setAttribute("stroke-width", "0");
        getElement().setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
        //Stroke black make fonts looks bold in FF 18
        getElement().setAttribute("fill", "black");
        setStroke("none");
        getElement().setAttribute("font-family", "Arial");
    }

    @Override
    public void setFont(String font) {
        getElement().setAttribute("font", font);
    }

    @Override
    public void setFontSize(String fontSize) {
        getElement().setAttribute("font-size", fontSize);
    }

    //possible values are :     start | middle | end |  inherit (think enumeration)
    public void setTextAnchor(String anchor) {
        getElement().setAttribute("text-anchor", anchor);
    }

    @Override
    public String getTextValue() {
        return text;
    }

    @Override
    public void setTextValue(String text) {
        getElement().setInnerText(text);
        this.text = text;
    }

}
