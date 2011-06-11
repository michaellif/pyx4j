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
package com.pyx4j.svg.basic;

import com.pyx4j.svg.common.Animator;

public interface SvgFactory {

    SvgRoot getSvgRoot();

    Group createGroup();

    Path createPath(String d);

    Path createPath(String d, Animator animator);

    Rect createRect(int x, int y, int width, int height, int rx, int ry);

    public Rect createRect(int x, int y, int width, int height, int rx, int ry, Animator animator);

    Circle createCircle(int cx, int cy, int r);

    Ellipse createEllipse(int cx, int cy, int rx, int ry);

    Line createLine(int x1, int y1, int x2, int y2);

    Polyline createPolyline(String points);

    Polygon createPolygon(String points);

    Image createImage(String url, int x, int y, int width, int height);

    Text createText(String text, int x, int y);

    ClipPath createClipPath(String id, String path);

}
