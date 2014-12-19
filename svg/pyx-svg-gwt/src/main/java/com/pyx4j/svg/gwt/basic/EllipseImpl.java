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

import com.pyx4j.svg.basic.Ellipse;
import com.pyx4j.svg.gwt.SvgDOM;

public class EllipseImpl extends ShapeImpl implements Ellipse {

    public EllipseImpl(int cx, int cy, int rx, int ry) {
        super(SvgDOM.createElementNS(SvgDOM.SVG_NAMESPACE, "ellipse"));
        getElement().setAttribute("cx", String.valueOf(cx));
        getElement().setAttribute("cy", String.valueOf(cy));
        getElement().setAttribute("rx", String.valueOf(rx));
        getElement().setAttribute("ry", String.valueOf(ry));
    }

}
