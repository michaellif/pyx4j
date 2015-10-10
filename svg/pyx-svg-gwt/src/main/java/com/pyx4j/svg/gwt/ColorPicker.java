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
 */
package com.pyx4j.svg.gwt;

import java.awt.Color;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.Defs;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.LinearGradient;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.basic.Stop;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.gwt.event.MouseEventHandler;
import com.pyx4j.svg.util.Utils;
import com.pyx4j.commons.css.ColorUtil;

public class ColorPicker implements IsSvgElement {

    private final SvgFactory svgFactory;

    private final Group container;

    private final Widget content;

    private final int xStart;

    private final int yStart;

    private final static int RIM_WIDTH = 25;

    protected final static int PADDING = 15;

    protected final static int RED_COLOR = 0xFF0000;

    private final int radius;

    private Rect rect;

    private Text text;

    private int inputColor;

    private double saturation = 1;

    private double lightness = 0.5;

    private float markerSize;

    private int color;

    private Circle circleIn;

    private Circle circleOut;

    private int currentMarkerX;

    private int currentMarkerY;

    public static enum PickerType {
        Hue, Color
    }

    private PickerType pickerType;

    public ColorPicker(SvgFactory svgfactory, Widget content, PickerType pickerType, int radius, int color) {
        this.svgFactory = svgfactory;
        container = svgFactory.createGroup();
        this.radius = radius;
        inputColor = color;
        if (pickerType == PickerType.Hue) {
            this.inputColor = ColorUtil.hslToRgb((Utils.degree2radian(color) / (Math.PI * 2) + 1) % 1, saturation, lightness);
        } else {
            this.inputColor = (color == 0) ? RED_COLOR : color;
        }
        this.pickerType = pickerType;
        this.content = content;
        xStart = radius + PADDING + RIM_WIDTH / 2;
        yStart = radius + RIM_WIDTH / 2;
        drawColorWheel();
    }

    public int getColor() {
        return color;
    }

    private class ColorPickerDragDrop extends MouseEventHandler {
        protected boolean inMotion = false;

        protected boolean circleDrag = false;

        private int X1, X2, Y1, Y2;

        private double hueRadian;

        public ColorPickerDragDrop(Widget dragHandle) {
            super(dragHandle);
        }

        public void onMouseUp(MouseUpEvent event) {
            super.onMouseUp(event);
            inMotion = false;
            circleDrag = false;
        }

        public void onMouseDown(MouseDownEvent event) {
            super.onMouseDown(event);
            hueRadian = Math.atan2(yStart - event.getY(), xStart - event.getX() - PADDING) + Math.PI / 2;
            X1 = (int) Math.round(xStart - PADDING - Math.sin(hueRadian) * (radius - RIM_WIDTH / 2));
            Y1 = (int) Math.round(yStart + Math.cos(hueRadian) * (radius - RIM_WIDTH / 2));
            X2 = (int) Math.round(xStart - PADDING - Math.sin(hueRadian) * (radius + RIM_WIDTH / 2));
            Y2 = (int) Math.round(yStart + Math.cos(hueRadian) * (radius + RIM_WIDTH / 2));
            if (event.getX() >= Math.min(X1, X2) && event.getX() <= Math.max(X1, X2) && event.getY() >= Math.min(Y1, Y2) && event.getY() <= Math.max(Y1, Y2)) {
                circleDrag = true;
            }

            if (circleDrag) {
                drawMarkers(hueRadian, -1);
            }
        }

        @Override
        public void handleDrag(int dragEndX, int dragEndY) {
            hueRadian = Math.atan2(yStart - dragEndY, xStart - dragEndX - PADDING) + Math.PI / 2;
            if (!inMotion) {
                X1 = (int) Math.round(xStart - PADDING - Math.sin(hueRadian) * (radius - RIM_WIDTH / 2));
                Y1 = (int) Math.round(yStart + Math.cos(hueRadian) * (radius - RIM_WIDTH / 2));
                X2 = (int) Math.round(xStart - PADDING - Math.sin(hueRadian) * (radius + RIM_WIDTH / 2));
                Y2 = (int) Math.round(yStart + Math.cos(hueRadian) * (radius + RIM_WIDTH / 2));
                if (dragEndX >= Math.min(X1, X2) && dragEndX <= Math.max(X1, X2) && dragEndY >= Math.min(Y1, Y2) && dragEndY <= Math.max(Y1, Y2)) {
                    circleDrag = true;
                }
                inMotion = true;
            }

            if (circleDrag) {
                drawMarkers(hueRadian, -1);
            }
        }
    }

    protected void drawColorWheel() {

        int nSegments = 90;
        markerSize = (float) Utils.round(RIM_WIDTH * 0.3, 2);

        double nudge = 8 / (double) radius / nSegments * Math.PI;
        double angle1 = 0, angle2;
        double am, tan, xm, ym;
        double hue1 = 0, hue2;
        String color1, color2;
        float x1, x2, y1, y2;

        double r1 = (double) (radius + RIM_WIDTH / 2) / radius;
        double r2 = (double) (radius - RIM_WIDTH / 2) / radius;

        // Each segment goes from angle1 to angle2.
        for (int i = 0; i <= nSegments; ++i) {

            Defs defs = svgFactory.createDefs();

            hue2 = (double) i / nSegments;
            angle2 = hue2 * Math.PI * 2;
            x1 = (float) Math.sin(angle1);
            y1 = (float) (-Math.cos(angle1));
            x2 = (float) Math.sin(angle2);
            y2 = (float) (-Math.cos(angle2));

            // Midpoint chosen so that the endpoints are tangent to the circle.
            am = (angle1 + angle2) / 2;
            tan = 1 / Math.cos((angle2 - angle1) / 2);
            xm = Math.sin(am) * tan;
            ym = -Math.cos(am) * tan;
            color2 = ColorUtil.rgbToHex(ColorUtil.hslToRgb(hue2, saturation, lightness));

            if (i > 0) {
                double corr = (1 + Math.min(Math.abs(Math.tan(angle1)), Math.abs(Math.tan(Math.PI / 2 - angle1)))) / nSegments;
                color1 = ColorUtil.rgbToHex(ColorUtil.hslToRgb(hue1 - 0.15 * corr, saturation, lightness));
                color2 = ColorUtil.rgbToHex(ColorUtil.hslToRgb(hue2 + 0.15 * corr, saturation, lightness));

                float a = (float) Utils.round(xStart - x1 * radius, 2);
                float b = (float) Utils.round(yStart - y1 * radius, 2);
                float c = (float) Utils.round(xStart - x2 * radius, 2);
                float d = (float) Utils.round(yStart - y2 * radius, 2);
                LinearGradient linear = svgFactory.createLinearGradient(a, b, c, d);
                linear.setAttribute("gradientUnits", "userSpaceOnUse");
                Stop stopStart = svgFactory.createStop();
                stopStart.setAttribute("offset", "0");
                stopStart.setAttribute("style", "stop-color:" + color1);
                linear.add(stopStart);
                Stop stopEnd = svgFactory.createStop();
                stopEnd.setAttribute("offset", "1");
                stopEnd.setAttribute("style", "stop-color:" + color2);
                linear.add(stopEnd);
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

                container.add(p);
            }
            // Prevent seams where curves join.
            angle1 = angle2 - nudge;
            color1 = color2;
            hue1 = hue2;
        }

        int yCoord = yStart + radius + RIM_WIDTH;
        int xCoord = (pickerType == PickerType.Hue) ? (xStart - 10) : (xStart - 25);
        rect = svgFactory.createRect(xStart - radius, yCoord, radius * 2, 20, 0, 0);
        container.add(rect);

        text = svgFactory.createText("", xCoord, yCoord + 16);
        text.setFont("Verdana");
        text.setFontSize("16");
        container.add(text);

        new ColorPickerDragDrop(content);
        float[] hsb = ColorUtil.rgbToHsb(inputColor);
        drawMarkers(Utils.degree2radian((int) Math.round((hsb[0] * 360))), inputColor);

        return;
    }

    public void drawMarkers(double hueValue, int inputColor) {

        float innerWidth = (float) Math.ceil(markerSize / 4);
        int innerRadius = Math.round(markerSize - innerWidth + 1);
        currentMarkerX = (int) Math.round(xStart - Math.sin(hueValue) * radius);
        currentMarkerY = (int) Math.round(yStart + Math.cos(hueValue) * radius);

        if (inputColor == -1) {
            color = ColorUtil.hslToRgb((hueValue / (Math.PI * 2) + 1) % 1, saturation, lightness);
        } else {
            color = inputColor;
        }
        String colorStr = ColorUtil.rgbToHex(color);
        rect.setFill(colorStr);
        int hue = (int) Math.round(Utils.radian2degree(hueValue));
        if (hue < 0) {
            hue += 360;
        }
        if (hue >= 200 && hue <= 300) {
            text.setFill("white");
        } else {
            text.setFill("black");
        }

        if (pickerType == PickerType.Hue) {
            text.setTextValue(Integer.toString(hue));
        } else {
            text.setTextValue(colorStr);
        }

        if (inputColor != -1) {
            circleIn = svgFactory.createCircle(currentMarkerX, currentMarkerY, innerRadius);
            circleIn.setFill("none");
            circleIn.setStroke("#000");
            circleIn.setStrokeWidth(Integer.toString((int) (innerWidth + 1)));
            circleIn.setTransform("matrix(1,0,0,1,0,0)");
            container.add(circleIn);
            circleOut = svgFactory.createCircle(currentMarkerX, currentMarkerY, Math.round(markerSize));
            circleOut.setFill("none");
            circleOut.setStroke("#fff");
            circleOut.setStrokeWidth(Integer.toString((int) innerWidth));
            circleOut.setTransform("matrix(1,0,0,1,0,0)");
            container.add(circleOut);
        } else {
            circleIn.setAttribute("cx", String.valueOf(currentMarkerX));
            circleIn.setAttribute("cy", String.valueOf(currentMarkerY));
            circleOut.setAttribute("cx", String.valueOf(currentMarkerX));
            circleOut.setAttribute("cy", String.valueOf(currentMarkerY));
        }
    }

    public static float[] rgbToHsl(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb >> 0) & 0xFF;
        float r1 = (float) ((r) / 255.0f);
        float g1 = (float) ((g) / 255.0f);
        float b1 = (float) ((b) / 255.0f);

        float max = (r1 > g1) ? r1 : g1;
        if (b1 > max)
            max = b1;
        float min = (r1 < g1) ? r1 : g1;
        if (b1 < min)
            min = b1;

        float hue, saturation;
        float lightness = (max + min) / 2;

        if (max == min) {
            hue = saturation = 0;
        } else {
            float d = max - min;
            saturation = lightness > 0.5 ? d / (2 - max - min) : d / (max + min);
            if (max == r1) {
                hue = (g1 - b1) / d + (g1 < b1 ? 6 : 0);
            } else if (max == g1) {
                hue = (b1 - r1) / d + 2;
            } else {
                hue = (r1 - g1) / d + 4;
            }
            hue /= 6;
        }
        float[] hslvals = new float[3];
        hslvals[0] = hue;
        hslvals[1] = saturation;
        hslvals[2] = lightness;
        return hslvals;
    }

    @Override
    public SvgElement asSvgElement() {
        return container;
    }
}
