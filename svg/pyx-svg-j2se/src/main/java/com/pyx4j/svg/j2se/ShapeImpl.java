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
 * Created on May 4, 2011
 * @author vadims
 * @version $Id$
 */
package com.pyx4j.svg.j2se;

import org.w3c.dom.Element;

import com.pyx4j.svg.basic.Shape;

public class ShapeImpl extends GraphicsElementImpl implements Shape {

    private final String id;

    private final Element el;

    public ShapeImpl(Element elem) {
        el = elem;
        id = SvgRootImpl.createUniqueId();
        el.setAttribute("id", id);
        setStrokeWidth(String.valueOf(1));
        setStroke("black");
        //TODO transparent is not valid. Finish later
        // setFill("transparent");
    }

    @Override
    public void setFill(String fill) {

        el.setAttribute("fill", fill);

    }

    @Override
    public void setStroke(String stroke) {
        el.setAttribute("stroke", stroke);

    }

    @Override
    public void setStrokeWidth(String strokeWidth) {
        el.setAttribute("stroke-width", strokeWidth);

    }

    @Override
    public String getId() {
        return id;
    }

    public Element getElement() {
        return el;
    }

    @Override
    public void setTransform(String transform) {
        el.setAttribute("transform", transform);

    }

}
