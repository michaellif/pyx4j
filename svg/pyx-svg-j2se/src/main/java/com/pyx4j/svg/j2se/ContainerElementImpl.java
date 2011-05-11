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

import com.pyx4j.svg.basic.ContainerElement;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.SvgElement;

public class ContainerElementImpl extends SvgElementImpl implements ContainerElement {

    public ContainerElementImpl(Element element) {
        super(element);
    }

    @Override
    public void add(SvgElement element) {
        getElement().appendChild(((SvgElementImpl) element).getElement());
    }

    @Override
    public void add(IsSvgElement element) {
        add(element.asSvgElement());
    }

}
