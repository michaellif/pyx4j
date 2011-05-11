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
 * Created on May 6, 2011
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.svg.j2se;

import org.w3c.dom.Element;

import com.pyx4j.svg.basic.SvgElement;

public class SvgElementImpl implements SvgElement {
    private final String id;

    private final Element element;

    public SvgElementImpl(Element element) {
        this.element = element;
        id = SvgRootImpl.createUniqueId();
        element.setAttribute("id", id);
    }

    public Element getElement() {
        return element;
    }

    public void setTransform(String transform) {
        element.setAttribute("transform", transform);
    }

    public void setAttributeNS(String attr0, String attr1, String attr2) {
        element.setAttributeNS(attr0, attr1, attr2);
    }

    @Override
    public void setAttribute(String attr1, String attr2) {
        element.setAttribute(attr1, attr2);
    }

    public String getId() {
        return id;
    }

    @Override
    public String getAttribute(String param) {
        return element.getAttribute(param);
    }

}
