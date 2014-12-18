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

import com.pyx4j.svg.basic.Line;
import com.pyx4j.svg.gwt.SvgDOM;

public class LineImpl extends ShapeImpl implements Line {

    public LineImpl(int x1, int y1, int x2, int y2) {
        super(SvgDOM.createElementNS(SvgDOM.SVG_NAMESPACE, "line"));
        getElement().setAttribute("x1", String.valueOf(x1));
        getElement().setAttribute("y1", String.valueOf(y1));
        getElement().setAttribute("x2", String.valueOf(x2));
        getElement().setAttribute("y2", String.valueOf(y2));
    }

}
