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

import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.common.Animator;

public class RectImpl extends ShapeImpl implements Rect {

    public RectImpl(int x, int y, int width, int height, int rx, int ry) {
        super(SvgDOM.createElementNS(SvgDOM.SVG_NAMESPACE, "rect"));
        initRect(x, y, width, height, rx, ry);

    }

    public RectImpl(int x, int y, int width, int height, int rx, int ry, Animator animator) {
        super(SvgDOM.createElementNS(SvgDOM.SVG_NAMESPACE, "rect"), animator);
        initRect(x, y, width, height, rx, ry);

    }

    private void initRect(int x, int y, int width, int height, int rx, int ry) {
        getElement().setAttribute("x", String.valueOf(x));
        getElement().setAttribute("y", String.valueOf(y));
        getElement().setAttribute("width", String.valueOf(width));
        getElement().setAttribute("height", String.valueOf(height));
        getElement().setAttribute("rx", String.valueOf(rx));
        getElement().setAttribute("ry", String.valueOf(ry));

    }

}
