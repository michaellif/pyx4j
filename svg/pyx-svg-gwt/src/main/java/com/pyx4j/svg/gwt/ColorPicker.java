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
package com.pyx4j.svg.gwt;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.Defs;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.LinearGradient;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Stop;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.gwt.event.MouseEventHandler;
import com.pyx4j.svg.util.Utils;
//import com.pyx4j.commons.css.ColorUtil;

public class ColorPicker implements IsSvgElement {

    private final SvgFactory svgFactory;

    private final Group container;

    private final Widget content; 
    
    private final int xStart;

    private final int yStart;

    private final static int RIM_WIDTH = 25;

    protected final static int PADDING = 15;
    
    private final int radius;

    private int hueInt;
    
    public double hue;
    
    private double saturation = 1;
    
    private double lightness = 0.5;

    private float markerSize;
    
    private String color;
    
    private Circle circleIn;
    
    private Circle circleOut;
    
    private Circle circleColor;
   
    private int currentMarkerX;
    
    private int currentMarkerY;

    public ColorPicker(SvgFactory svgfactory, Widget content, int radius, int hueInt) {
        this.svgFactory = svgfactory;
        container = svgFactory.createGroup();
        this.radius = radius;
        this.hueInt = hueInt;
        this.content = content;
        xStart = radius + PADDING + RIM_WIDTH/2;
        yStart = radius + RIM_WIDTH/2;
        drawColorWheel();
    }

    private class ColorPickerDragDrop extends MouseEventHandler {
        protected boolean inMotion = false;
        protected boolean circleDrag = false;
   	
        public ColorPickerDragDrop(Widget dragHandle) {
          super(dragHandle);
        }

        public void onMouseUp(MouseUpEvent event) {
            super.onMouseUp(event);
            inMotion = false;
            circleDrag = false;
          }

        @Override
        public void handleDrag(int dragEndX, int dragEndY) {
        	
         	if(!inMotion) {
            	circleDrag = Math.max(Math.abs(currentMarkerX-PADDING-dragStartX), Math.abs(currentMarkerY-dragStartY)) < markerSize;
            	inMotion = true;
         	}
         	
            if (circleDrag) {
            	double hueRadian = Math.atan2(yStart - dragEndY,xStart - dragEndX - PADDING ) + Math.PI/2;
           	    drawMarkers(hueRadian, false);
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
            color2 = rgbToHex(hslToRgb(hue2, saturation, lightness));

            if (i > 0) {
                double corr = (1 + Math.min(Math.abs(Math.tan(angle1)), Math.abs(Math.tan(Math.PI / 2 - angle1)))) / nSegments;
                color1 = rgbToHex(hslToRgb(hue1 - 0.15 * corr, saturation, lightness));
                color2 = rgbToHex(hslToRgb(hue2 + 0.15 * corr, saturation, lightness));
                
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

        new ColorPickerDragDrop( content );
        drawMarkers(Utils.degree2radian(hueInt),true);
        
        return;
    }

    public void drawMarkers(double hueValue, boolean first) {

        float innerWidth = (float) Math.ceil(markerSize / 4);
        int innerRadius = Math.round(markerSize - innerWidth + 1);
        currentMarkerX = (int) Math.round(xStart - Math.sin(hueValue) * radius);
        currentMarkerY = (int) Math.round(yStart + Math.cos(hueValue) * radius);
        
        color = rgbToHex(hslToRgb( (hueValue / 6.28 + 1) % 1, saturation, lightness));
       	//System.out.println("color=" + color);

        if(first) {        	
            circleIn = svgFactory.createCircle(currentMarkerX, currentMarkerY, innerRadius);
            circleIn.setFill("none");
            circleIn.setStroke("#000");
            circleIn.setStrokeWidth(Integer.toString((int) (innerWidth + 1)));
            circleIn.setTransform("matrix(1,0,0,1,0,0)");
            container.add(circleIn);
            circleOut = svgFactory.createCircle( currentMarkerX, currentMarkerY, Math.round(markerSize));
            circleOut.setFill("none");
            circleOut.setStroke("#fff");
            circleOut.setStrokeWidth(Integer.toString((int) innerWidth));
            circleOut.setTransform("matrix(1,0,0,1,0,0)");
            container.add(circleOut);    
            
            circleColor = svgFactory.createCircle(xStart, yStart, radius/2);
            circleColor.setFill(color);
            circleColor.setStroke("black");
            circleColor.setStrokeWidth("2");
            circleColor.setTransform("matrix(1,0,0,1,0,0)");
            container.add(circleColor);
        } else {
        	circleIn.setAttribute("cx", String.valueOf(currentMarkerX));
        	circleIn.setAttribute("cy", String.valueOf(currentMarkerY));
           	circleOut.setAttribute("cx", String.valueOf(currentMarkerX));
        	circleOut.setAttribute("cy", String.valueOf(currentMarkerY));
            circleColor.setFill(color);
       }
    }
 
    public int hslToRgb(double hue, double saturation, double lightness) {
        double m2 = (lightness <= 0.5) ? lightness * (saturation + 1) : lightness + saturation - lightness * saturation;
        double m1 = lightness * 2 - m2;
    	int r = (int) Math.round(convertHUEtoRGB(m1, m2, hue + 0.33333) * 255);
    	int g = (int) Math.round(convertHUEtoRGB(m1, m2, hue) * 255);
    	int b = (int) Math.round(convertHUEtoRGB(m1, m2, hue - 0.33333) * 255);
        return (r << 16) | (g << 8) | (b << 0);
    }

    public double convertHUEtoRGB(double p, double q, double t) {
        t = (t + 1) % 1;
        if (t * 6 < 1)
            return (p + (q - p) * t * 6);
        if (t * 2 < 1)
            return (int) q;
        if (t * 3 < 2)
            return (p + (q - p) * (0.66666 - t) * 6);
        return p;
    }

    public static String rgbToHex(int rgb) {
        String colorString = Integer.toHexString(rgb);
        int length = colorString.length();
        for (int i = 0; i < (6 - length); i++) {
            colorString = "0" + colorString;
        }
        return "#" + colorString;
    }

    @Override
    public SvgElement asSvgElement() {
        return container;
    }
}
