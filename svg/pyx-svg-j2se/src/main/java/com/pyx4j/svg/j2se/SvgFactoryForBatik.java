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
 */
package com.pyx4j.svg.j2se;

import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.ClipPath;
import com.pyx4j.svg.basic.Defs;
import com.pyx4j.svg.basic.Ellipse;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.Image;
import com.pyx4j.svg.basic.Line;
import com.pyx4j.svg.basic.LinearGradient;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Polygon;
import com.pyx4j.svg.basic.Polyline;
import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.basic.Stop;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.common.Animator;
import com.pyx4j.svg.j2se.basic.CircleImpl;
import com.pyx4j.svg.j2se.basic.DefsImpl;
import com.pyx4j.svg.j2se.basic.EllipseImpl;
import com.pyx4j.svg.j2se.basic.GroupImpl;
import com.pyx4j.svg.j2se.basic.LineImpl;
import com.pyx4j.svg.j2se.basic.LinearGradientImpl;
import com.pyx4j.svg.j2se.basic.PathImpl;
import com.pyx4j.svg.j2se.basic.PolygonImpl;
import com.pyx4j.svg.j2se.basic.PolylineImpl;
import com.pyx4j.svg.j2se.basic.RectImpl;
import com.pyx4j.svg.j2se.basic.StopImpl;
import com.pyx4j.svg.j2se.basic.SvgRootImpl;
import com.pyx4j.svg.j2se.basic.TextImpl;

public class SvgFactoryForBatik implements SvgFactory {

    private final SvgRootImpl rootSVG;

    public SvgFactoryForBatik() {
        rootSVG = new SvgRootImpl();
    }

    @Override
    public SvgRoot getSvgRoot() {
        return rootSVG;
    }

    @Override
    public Group createGroup() {
        return new GroupImpl(rootSVG.getDocument());
    }

    @Override
    public Stop createStop() {
        return new StopImpl(rootSVG.getDocument());
    }

    @Override
    public Defs createDefs() {
        return new DefsImpl(rootSVG.getDocument());
    }

    @Override
    public LinearGradient createLinearGradient(float x1, float y1, float x2, float y2) {
        return new LinearGradientImpl(rootSVG.getDocument(), x1, y1, x2, y2);
    }

    /*
     * @Override
     * public RadialGradient createRadialGradient() {
     * return new RadialGradientImpl(rootSVG.getDocument());
     * }
     */
    @Override
    public Path createPath(String d) {
        return new PathImpl(rootSVG.getDocument(), d);
    }

    @Override
    public Path createPath(String d, Animator animator) {
        return new PathImpl(rootSVG.getDocument(), d, animator);
    }

    @Override
    public Rect createRect(int x, int y, int width, int height, int rx, int ry) {
        return new RectImpl(rootSVG.getDocument(), x, y, width, height, rx, ry);
    }

    @Override
    public Rect createRect(int x, int y, int width, int height, int rx, int ry, Animator animator) {
        return new RectImpl(rootSVG.getDocument(), x, y, width, height, rx, ry, animator);
    }

    @Override
    public Circle createCircle(int cx, int cy, int r) {
        return new CircleImpl(rootSVG.getDocument(), cx, cy, r);
    }

    @Override
    public Ellipse createEllipse(int cx, int cy, int rx, int ry) {
        return new EllipseImpl(rootSVG.getDocument(), cx, cy, rx, ry);
    }

    @Override
    public Line createLine(int x1, int y1, int x2, int y2) {
        return new LineImpl(rootSVG.getDocument(), x1, y1, x2, y2);
    }

    @Override
    public Polyline createPolyline(String points) {
        return new PolylineImpl(rootSVG.getDocument(), points);
    }

    @Override
    public Polygon createPolygon(String points) {
        return new PolygonImpl(rootSVG.getDocument(), points);
    }

    @Override
    public Image createImage(String url, int x, int y, int width, int height) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Text createText(String text, int x, int y) {
        return new TextImpl(rootSVG.getDocument(), text, x, y);
    }

    @Override
    public ClipPath createClipPath(String id, String path) {
        // TODO Auto-generated method stub
        return null;
    }

}
