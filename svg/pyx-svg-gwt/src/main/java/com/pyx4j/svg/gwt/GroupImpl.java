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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.SvgElement;

public class GroupImpl extends ContainerElementImpl implements Group {
    private final String id;

    public GroupImpl() {
        setElement(SvgDOM.createElementNS(SvgDOM.SVG_NAMESPACE, "g"));
        id = DOM.createUniqueId();
        SvgDOM.setAttributeNS(getElement(), "id", id);
    }

    @Override
    public void add(SvgElement element) {
        super.add((Widget) element, getElement());
    }

    @Override
    public String getId() {
        return id;
    }

}
