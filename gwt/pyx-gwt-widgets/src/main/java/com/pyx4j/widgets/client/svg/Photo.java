/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 6, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.svg;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

public class Photo extends Group {

    private static long index = 0;

    private final Image leftImage;

    private final Image rightImage;

    private final Image topImage;

    private final Image bottomImage;

    private final int width;

    private final int height;

    public Photo(String url, Coordinate coordinate, final int width, final int height) {

        this.width = width;
        this.height = height;

        String id = ++index + "";

        leftImage = new Image(url, coordinate, width + "px", height + "px");
        leftImage.getElement().setAttribute("clip-path", "url(#leftImage" + id + ")");
        leftImage.setTransform("translate(1 0)");
        add(leftImage);
        rightImage = new Image(url, coordinate, width + "px", height + "px");
        rightImage.getElement().setAttribute("clip-path", "url(#rightImage" + id + ")");
        rightImage.setTransform("translate(-1 0)");
        add(rightImage);
        topImage = new Image(url, coordinate, width + "px", height + "px");
        topImage.getElement().setAttribute("clip-path", "url(#topImage" + id + ")");
        add(topImage);
        bottomImage = new Image(url, coordinate, width + "px", height + "px");
        bottomImage.getElement().setAttribute("clip-path", "url(#bottomImage" + id + ")");
        add(bottomImage);

        add(new ClipPath("leftImage" + id, "M 0 0 " + width / 2 + " " + height / 2 + " 0 " + height + " z"));
        add(new ClipPath("rightImage" + id, "M " + width + " 0 " + width / 2 + " " + height / 2 + " " + width + " " + height + " z"));
        add(new ClipPath("topImage" + id, "M 0 0 " + width / 2 + " " + height / 2 + " " + width + " 0 z"));
        add(new ClipPath("bottomImage" + id, "M 0 " + height + " " + width / 2 + " " + height / 2 + " " + width + " " + height + " z"));

        //For debug
        if (false) {
            Rect rect = new Rect(coordinate, new Length(width), new Length(height));
            rect.setOpacity("0");
            add(rect);
            rect.addMouseMoveHandler(new MouseMoveHandler() {

                @Override
                public void onMouseMove(MouseMoveEvent event) {

                    double x = event.getX() - (double) width / 2;
                    double y = event.getY() - (double) height / 2;

                    x = x / 50;
                    y = y / 80;

                    System.out.println(x + " " + y);

                    move(x, y);
                }
            });
        }

    }

    public void move(double x, double y) {
        {
            double a = (2 * x + width) / width;
            double b = 2 * y / width;

            String leftImageTransform = a + " " + b + " 0 1 0 0";
            leftImage.setTransform("matrix(" + leftImageTransform + ") translate(1 0)");
        }

        {
            double a = 2 * (width / 2 - x) / width;
            double b = -2 * y / width;
            double e = 2 * x;
            double f = 2 * y;

            String rightImageTransform = a + " " + b + " 0 1 " + e + " " + f;
            rightImage.setTransform("matrix(" + rightImageTransform + ")");
        }

        {
            double c = 2 * (x) / height;
            double d = 2 * (y + (double) height / 2) / height;

            String topImageTransform = "1 0 " + c + " " + d + " 0 0";
            topImage.setTransform("matrix(" + topImageTransform + ")");
        }

        {
            double c = -2 * x / height;
            double d = 2 * ((double) height / 2 - y) / height;
            double e = 2 * x;
            double f = 2 * y;

            String bottomImageTransform = "1 0 " + c + " " + d + " " + e + " " + f;
            bottomImage.setTransform("matrix(" + bottomImageTransform + ")");
        }
    }
}
