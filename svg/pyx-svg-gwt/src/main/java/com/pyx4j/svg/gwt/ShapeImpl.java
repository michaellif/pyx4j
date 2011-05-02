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

import com.google.gwt.dom.client.Element;

import com.pyx4j.svg.basic.Shape;

public class ShapeImpl extends GraphicsElementImpl implements Shape {

    public ShapeImpl(Element elem) {
        setElement(elem);
        setStrokeWidth(String.valueOf(1));
        setStroke("black");
        setFill("transparent");
    }

    @Override
    public void setFill(String fill) {
        getElement().setAttribute("fill", fill);
    }

    @Override
    public void setStroke(String stroke) {
        getElement().setAttribute("stroke", stroke);
    }

    @Override
    public void setStrokeWidth(String strokeWidth) {
        getElement().setAttribute("stroke-width", strokeWidth);
    }

}
