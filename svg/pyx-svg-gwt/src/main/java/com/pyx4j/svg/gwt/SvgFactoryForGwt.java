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

import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.ClipPath;
import com.pyx4j.svg.basic.Ellipse;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.Image;
import com.pyx4j.svg.basic.Line;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Polygon;
import com.pyx4j.svg.basic.Polyline;
import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.common.Animator;

public class SvgFactoryForGwt implements SvgFactory {

    @Override
    public SvgRoot getSvgRoot() {
        return new SvgRootImpl();
    }

    @Override
    public Group createGroup() {
        return new GroupImpl();
    }

    @Override
    public Path createPath(String d) {
        return new PathImpl(d);
    }

    @Override
    public Path createPath(String d, Animator animator) {
        return new PathImpl(d, animator);
    }

    @Override
    public Rect createRect(int x, int y, int width, int height, int rx, int ry) {
        return new RectImpl(x, y, width, height, rx, ry);
    }

    @Override
    public Rect createRect(int x, int y, int width, int height, int rx, int ry, Animator animator) {
        return new RectImpl(x, y, width, height, rx, ry, animator);
    }

    @Override
    public Circle createCircle(int cx, int cy, int r) {
        return new CircleImpl(cx, cy, r);
    }

    @Override
    public Ellipse createEllipse(int cx, int cy, int rx, int ry) {
        return new EllipseImpl(cx, cy, rx, ry);
    }

    @Override
    public Line createLine(int x1, int y1, int x2, int y2) {
        return new LineImpl(x1, y1, x2, y2);
    }

    @Override
    public Polyline createPolyline(String points) {
        return new PolylineImpl(points);
    }

    @Override
    public Polygon createPolygon(String points) {
        return new PolygonImpl(points);
    }

    @Override
    public Text createText(String text, int x, int y) {
        return new TextImpl(text, x, y);
    }

    @Override
    public Image createImage(String url, int x, int y, int width, int height) {
        throw new Error("Not implemented");
    }

    @Override
    public ClipPath createClipPath(String id, String path) {
        throw new Error("Not implemented");
    }

}
