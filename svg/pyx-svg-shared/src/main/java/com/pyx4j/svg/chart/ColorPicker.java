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
 * Created on 2012-08-13
 * @author Alex
 * @version $Id$
 */
package com.pyx4j.svg.chart;

//import com.google.gwt.user.client.ui.SimplePanel;
import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.Defs;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.LinearGradient;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Stop;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.util.Utils;

public class ColorPicker implements IsSvgElement {

    //private final DraggableMouseListener listener;

    private final boolean dragging = false;

    private int dragStartX;

    private int dragStartY;

    private final SvgFactory svgFactory;

    private final Group container;

    private String id;

    private final int radius;

    private final int hue;

    private final int xStart;

    private final int yStart;

    private final static int RIM_WIDTH = 25;

    protected final static int PADDING = 15;

    public ColorPicker(SvgFactory svgfactory, int xStart, int yStart, int radius, int hue) {
        this.svgFactory = svgfactory;
        container = svgFactory.createGroup();
        this.radius = radius;
        this.xStart = xStart;
        this.yStart = yStart;
        this.hue = hue;

        drawColorWheel();
    }

/*
 * public void onMouseDown(MouseDownEvent event) {
 * dragging = true;
 * 
 * //capturing the mouse to the dragged widget.
 * //DOM.setCapture(getElement());
 * //dragStartX = event.getRelativeX(getElement());
 * //dragStartY = event.getRelativeY(getElement());
 * System.out.println("onMouseDown");
 * }
 * 
 * public void onMouseUp(MouseUpEvent event) {
 * dragging = false;
 * //DOM.releaseCapture(getElement());
 * System.out.println("onMouseUp");
 * }
 * 
 * public void onMouseMove(MouseMoveEvent event) {
 * if (dragging) {
 * // we don’t want the widget to go off-screen, so the top/left
 * // values should always remain be positive.
 * //int newX = Math.max(0, event.getRelativeX(getElement()) + getAbsoluteLeft() - dragStartX);
 * //int newY = Math.max(0, event.getRelativeY(getElement()) + getAbsoluteTop() - dragStartY);
 * //DOM.setStyleAttribute(getElement(), "left", "" + newX);
 * //DOM.setStyleAttribute(getElement(), "top", "" + newY);
 * }
 * System.out.println("onMouseMove");
 * }
 */
    protected void drawColorWheel() {

        int nSegments = 100;
        double nudge = 8 / (double) radius / nSegments * Math.PI;
        double angle1 = 0, angle2;
        double am, tan, xm, ym;
        double d1 = 0, d2;
        String color1;
        String color2;
        float x1, x2, y1, y2;
        double r1 = (double) (radius + RIM_WIDTH / 2) / radius;
        double r2 = (double) (radius - RIM_WIDTH / 2) / radius; // inner/outer radius.

        //DraggableWidgetWrapper wrapper = new DraggableWidgetWrapper(new Widget());
        // Each segment goes from angle1 to angle2.
        for (int i = 0; i <= nSegments; ++i) {

            Defs defs = svgFactory.createDefs();

            d2 = (double) i / nSegments;
            angle2 = d2 * Math.PI * 2;

            // Endpoints
            x1 = (float) Math.sin(angle1);
            y1 = (float) (-Math.cos(angle1));
            x2 = (float) Math.sin(angle2);
            y2 = (float) (-Math.cos(angle2));
            // Midpoint chosen so that the endpoints are tangent to the circle.
            am = (angle1 + angle2) / 2;
            tan = 1 / Math.cos((angle2 - angle1) / 2);
            xm = Math.sin(am) * tan;
            ym = -Math.cos(am) * tan;
            double[] rgb = convertHSLtoRGB(d2, 1, (float) 0.5);
            color2 = pack(rgb);

            if (i > 0) {
                double corr = (1 + Math.min(Math.abs(Math.tan(angle1)), Math.abs(Math.tan(Math.PI / 2 - angle1)))) / nSegments;
                rgb = convertHSLtoRGB(d1 - 0.15 * corr, 1, 0.5);
                //rgb = hsl2rgb(d1 - 0.15 * corr, 1, 0.5);
                color1 = pack(rgb);
                rgb = convertHSLtoRGB(d2 + 0.15 * corr, 1, 0.5);
                //rgb = hsl2rgb(d2 + 0.15 * corr, 1, 0.5);
                color2 = pack(rgb);
                //fb.pack(fb.HSLToRGB([d2 + 0.15 * corr, 1, 0.5]));
                // Create gradient fill between the endpoints.
                float a = (float) Utils.round(xStart - x1 * radius, 2);
                float b = (float) Utils.round(yStart - y1 * radius, 2);
                float c = (float) Utils.round(xStart - x2 * radius, 2);
                float d = (float) Utils.round(yStart - y2 * radius, 2);
                LinearGradient linear = svgFactory.createLinearGradient(a, b, c, d);
                linear.setAttribute("gradientUnits", "userSpaceOnUse");
                Stop stop = svgFactory.createStop();
                stop.setAttribute("offset", "0");
                stop.setAttribute("style", "stop-color:" + color1);
                linear.add(stop);
                Stop stop1 = svgFactory.createStop();
                stop1.setAttribute("offset", "1");
                stop1.setAttribute("style", "stop-color:" + color2);
                linear.add(stop1);
                defs.add(linear);
                container.add(defs);

                StringBuilder path = new StringBuilder();
                path.append("M").append(Utils.round(xStart - x1 * r1 * radius, 2)).append(",").append(Utils.round(yStart - y1 * r1 * radius, 2));
                path.append("Q").append(Utils.round(xStart - xm * r1 * radius, 2)).append(",").append(Utils.round(yStart - ym * r1 * radius, 2));
                path.append(" ").append(Utils.round(xStart - x2 * r1 * radius, 2)).append(",").append(Utils.round(yStart - y2 * r1 * radius, 2));
                path.append("L").append(Utils.round(xStart - x2 * r2 * radius, 2)).append(",").append(Utils.round(yStart - y2 * r2 * radius, 2));
                path.append("Q").append(Utils.round(xStart - xm * r2 * radius, 2)).append(",").append(Utils.round(yStart - ym * r2 * radius, 2));
                path.append(" ").append(Utils.round(xStart - x1 * r2 * radius, 2)).append(",").append(Utils.round(yStart - y1 * r2 * radius, 2)).append("Z");

                Path p = svgFactory.createPath(path.toString());
                p.setAttribute("fill", "url(#" + linear.getId() + ")");
                p.setAttribute("stroke", "none");

                //p.setAttribute("onmouseup", "onMouseUp");
                //p.setAttribute("onmousedown", "onMouseDown");
                //p.setAttribute("onmousemove", "onMouseMove");
                //makeDraggable(p);
                container.add(p);
            }

            // Prevent seams where curves join.
            angle1 = angle2 - nudge;
            color1 = color2;
            d1 = d2;
        }

        drawMarkers(hue);
        return;

    }

    public int[] hsl2rgb(double h, double s, double l) {
        int r, g, b;

        if (s == 0) {
            r = g = b = (int) l; // achromatic
        } else {

            double q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            double p = 2 * l - q;
            r = hue2rgb(p, q, h + 1 / 3);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1 / 3);
        }
        int[] rgb = new int[3];
        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;

        return rgb;
    }

    public int hue2rgb(double p, double q, double t) {
        if (t < 0)
            t += 1;
        if (t > 1)
            t -= 1;
        if (t < (double) 1 / 6)
            return (int) (p + (q - p) * 6 * t);
        if (t < (double) 1 / 2)
            return (int) q;
        if (t < (double) 2 / 3)
            return (int) (p + (q - p) * (2 / 3 - t) * 6);
        return (int) p;
    }

    public double[] convertHSLtoRGB(double h, double s, double l) {
        double m1, m2;
        double r, g, b;
        m2 = (l <= 0.5) ? l * (s + 1) : l + s - l * s;
        m1 = l * 2 - m2;
        r = convertHUEtoRGB(m1, m2, h + 0.33333);
        g = convertHUEtoRGB(m1, m2, h);
        b = convertHUEtoRGB(m1, m2, h - 0.33333);
        double[] rgb = new double[3];
        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;

        return rgb;
    }

    public double convertHUEtoRGB(double m1, double m2, double h) {
        h = (h + 1) % 1;
        if (h * 6 < 1)
            return (m1 + (m2 - m1) * h * 6);
        if (h * 2 < 1)
            return (int) m2;
        if (h * 3 < 2)
            return (m1 + (m2 - m1) * (0.66666 - h) * 6);
        return m1;
    }

    public String pack(double[] rgb) {

        int r = (int) Math.round(rgb[0] * 255);
        int g = (int) Math.round(rgb[1] * 255);
        int b = (int) Math.round(rgb[2] * 255);
        return '#' + dec2hex(r) + dec2hex(g) + dec2hex(b);
    }

    public String dec2hex(int x) {
        return (x < 16 ? '0' + Integer.toHexString(x) : Integer.toHexString(x));
    }

    public void drawMarkers(int hue) {
        // Determine marker dimensions
        int sz = RIM_WIDTH;
        float markerSize = (float) Utils.round(RIM_WIDTH * 0.3, 2);
        float lw = (float) Math.ceil(markerSize / 4);
        float r = markerSize - lw + 1;
        double angle = Utils.degree2radian(hue);
        double x1, x2, y1, y2;
        x1 = xStart - Math.sin(angle) * radius;
        y1 = yStart + Math.cos(angle) * radius;
        Circle circle = svgFactory.createCircle((int) Math.round(x1), (int) Math.round(y1), Math.round(r));
        circle.setFill("none");
        circle.setStroke("#000");
        circle.setStrokeWidth(Integer.toString((int) (lw + 1)));
        container.add(circle);
        Circle circle2 = svgFactory.createCircle((int) Math.round(x1), (int) Math.round(y1), Math.round(markerSize));
        circle2.setFill("none");
        circle2.setStroke("#fff");
        circle.setStrokeWidth(Integer.toString((int) lw));
        container.add(circle2);
    }

    @Override
    public SvgElement asSvgElement() {
        return container;
    }

//    public static MouseEventHandler makeDraggable(Shape item{
//        return new MouseEventHandler(ite;
//    }

/*
 * private class DraggableMouseListener implements MouseDownHandler, MouseUpHandler, MouseMoveHandler {
 * 
 * private boolean dragging = false;
 * 
 * private int dragStartX;
 * 
 * private int dragStartY;
 * 
 * @Override
 * public void onMouseDown(MouseDownEvent event) {
 * dragging = true;
 * 
 * //capturing the mouse to the dragged widget.
 * //DOM.setCapture(getElement());
 * //dragStartX = event.getRelativeX(getElement());
 * //dragStartY = event.getRelativeY(getElement());
 * }
 * 
 * @Override
 * public void onMouseUp(MouseUpEvent event) {
 * dragging = false;
 * //DOM.releaseCapture(getElement());
 * }
 * 
 * @Override
 * public void onMouseMove(MouseMoveEvent event) {
 * if (dragging) {
 * // we don’t want the widget to go off-screen, so the top/left
 * // values should always remain be positive.
 * //int newX = Math.max(0, event.getRelativeX(getElement()) + getAbsoluteLeft() - dragStartX);
 * //int newY = Math.max(0, event.getRelativeY(getElement()) + getAbsoluteTop() - dragStartY);
 * //DOM.setStyleAttribute(getElement(), "left", "" + newX);
 * //DOM.setStyleAttribute(getElement(), "top", "" + newY);
 * }
 * }
 * 
 * }
 * 
 * @Override
 * public void onBrowserEvent(Event event) {
 * switch (DOM.eventGetType(event)) {
 * case Event.ONMOUSEDOWN:
 * case Event.ONMOUSEUP:
 * case Event.ONMOUSEMOVE:
 * DomEvent.fireNativeEvent(event, this);
 * break;
 * }
 * }
 * 
 * @Override
 * public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
 * return addDomHandler(handler, MouseDownEvent.getType());
 * }
 * 
 * @Override
 * public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
 * return addDomHandler(handler, MouseUpEvent.getType());
 * }
 * 
 * @Override
 * public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
 * return addDomHandler(handler, MouseMoveEvent.getType());
 * }
 * 
 * @Override
 * public void onPreviewNativeEvent(NativePreviewEvent event) {
 * //Event e = Event.as(event.getNativeEvent());
 * //if (DOM.eventGetType(e) == Event.ONMOUSEDOWN && DOM.isOrHasChild(getElement(), DOM.eventGetTarget(e))) {
 * // DOM.eventPreventDefault(e);
 * //}
 * }
 */
}
