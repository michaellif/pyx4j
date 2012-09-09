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
 * Created on 2012-08-15
 * @author Alex
 * @version $Id$
 */
package com.pyx4j.svg.j2se;

import org.w3c.dom.Document;

import com.pyx4j.svg.basic.LinearGradient;

public class LinearGradientImpl extends ContainerElementImpl implements LinearGradient {

    public LinearGradientImpl(Document document, float x1, float y1, float x2, float y2) {
        super(document.createElementNS(SvgRootImpl.SVG_NAMESPACE, "linearGradient"));
        getElement().setAttribute("x1", String.valueOf(x1));
        getElement().setAttribute("y1", String.valueOf(y1));
        getElement().setAttribute("x2", String.valueOf(x2));
        getElement().setAttribute("y2", String.valueOf(y2));
    }
}
