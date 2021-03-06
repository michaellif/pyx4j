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

import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.common.Animator;
import com.pyx4j.svg.gwt.SvgDOM;

public class PathImpl extends ShapeImpl implements Path {

    public PathImpl(String d) {
        super(SvgDOM.createElementNS(SvgDOM.SVG_NAMESPACE, "path"));
        getElement().setAttribute("d", d);

    }

    public PathImpl(String d, Animator animator) {
        super(SvgDOM.createElementNS(SvgDOM.SVG_NAMESPACE, "path"), animator);
        getElement().setAttribute("d", d);
    }

}
