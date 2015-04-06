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
 */
package com.pyx4j.svg.j2se.basic;

import java.util.Map.Entry;

import org.w3c.dom.Element;

import com.pyx4j.svg.basic.Shape;
import com.pyx4j.svg.common.Animator;

public class ShapeImpl extends GraphicsElementImpl implements Shape {

    public ShapeImpl(Element elem) {
        super(elem);
        setStrokeWidth(String.valueOf(1));
        setStroke("black");
        setFill("none");
    }

    public ShapeImpl(Element elem, Animator animator) {
        this(elem);
        if (animator == null) {
            return;
        }
        Element animEl = elem.getOwnerDocument().createElement(animator.getTagName());
        elem.appendChild(animEl);
        for (Entry<String, String> attribute : animator.getAttributes().entrySet()) {
            animEl.setAttribute(attribute.getKey(), attribute.getValue());
        }

    }

    @Override
    public void setFill(String fill) {
        setAttribute("fill", fill);
    }

    @Override
    public void setStroke(String stroke) {
        setAttribute("stroke", stroke);

    }

    @Override
    public void setStrokeWidth(String strokeWidth) {
        setAttribute("stroke-width", strokeWidth);
    }

    @Override
    public void setStrokeDasharray(String dasharray) {
        setAttribute("stroke-dasharray", dasharray);
    }
}
