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
 * Created on Jan 6, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.svg;

public class Circle extends GraphicsElement {

    public Circle(Coordinate center, Length radius) {
        setElement(SvgDOM.createElementNS(SvgDOM.SVG_NAMESPACE, "circle"));
        getElement().setAttribute("cx", center.getX().toString());
        getElement().setAttribute("cy", center.getY().toString());
        getElement().setAttribute("r", radius.toString());
    }

    public Circle(int cx, int cy, int r) {
        this(new Coordinate(cx, cy), new Length(r));
    }
}