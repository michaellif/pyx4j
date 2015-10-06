/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Oct 6, 2015
 * @author vlads
 */
package com.pyx4j.svg.gwt.chart;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.svg.chart.XYChart;

public class HoverXYChartTooltip extends FlowPanel implements MouseMoveHandler, MouseOutHandler, MouseOverHandler {

    private Widget chartPanel;

    private final XYChart chart;

    private HTML labelX;

    private HTML labelY;

    public static void inject(Panel container, Widget chartPanel, XYChart chart) {
        new HoverXYChartTooltip(container, chartPanel, chart);
    }

    public HoverXYChartTooltip(Panel container, Widget chartPanel, XYChart chart) {
        this.chart = chart;
        this.chartPanel = chartPanel;
        chartPanel.addDomHandler(this, MouseMoveEvent.getType());
        chartPanel.addDomHandler(this, MouseOutEvent.getType());
        chartPanel.addDomHandler(this, MouseOverEvent.getType());

        chartPanel.getElement().getStyle().setPadding(50, Unit.PX);
        chartPanel.getElement().getStyle().setMargin(0, Unit.PX);

        chartPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        chartPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);
        chartPanel.getElement().getStyle().setBorderColor("#070");

        Element target = ((Widget) chart.asSvgElement()).getElement();
        target.getStyle().setBorderStyle(BorderStyle.DOTTED);
        target.getStyle().setBorderWidth(5, Unit.PX);
        target.getStyle().setBorderColor("#700");

        target.getStyle().setPadding(50, Unit.PX);
        target.getStyle().setMargin(0, Unit.PX);

        this.getElement().getStyle().setPosition(Position.ABSOLUTE);

        this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        this.getElement().getStyle().setBorderWidth(1, Unit.PX);
        this.getElement().getStyle().setBorderColor("#666");
        this.getElement().getStyle().setProperty("borderRadius", "10px");
        this.getElement().getStyle().setProperty("background", "rgba(255,255,255,0.8)");

        container.add(this);

        this.add(labelX = new HTML(""));
        this.add(labelY = new HTML(""));
    }

    protected void positionLabels(int x, int y) {
        this.getElement().getStyle().setTop(y + 18, Unit.PX);
        this.getElement().getStyle().setLeft(x - 25, Unit.PX);
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        this.setVisible(true);
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        System.out.println("eX:" + event.getX() + "  " + chartPanel.getAbsoluteLeft());
        int x = chartPanel.getAbsoluteLeft() + event.getX();
        int y = chartPanel.getAbsoluteTop() + event.getY();
        System.out.println("X:" + x /* + " Y:" + event.getY() */);
        positionLabels(x, y);

        Element target = ((Widget) chart.asSvgElement()).getElement();

        int xr = event.getRelativeX(target);
        int yr = event.getRelativeY(target);

        labelX.setHTML("event X :" + event.getX() + " event Y :" + event.getY() + " <br/>" //
                + "event Xr:" + xr + ", event Yr:" + yr + "  " + "<br/>" //
                + "Absolute X:" + x + ", Absolute Y:" + y + "  " + "<br/>" //
                + "now X: " + chart.nowPosition + "<br/>" //
        );

        //labelX.setHTML("eX:" + event.getX() + "  " + formatLabelX(chart, chart.getXValue(x)));
        labelY.setHTML("c " + formatLabelX(chart, chart.getXValue(x)));
        //labelY.setHTML(formatLabelY(chart, chart.getYValue(event.getY())));
    }

    public String formatLabelX(XYChart chart, double x) {
        return "x:" + chart.configurator().getXAxisProducer().formatLabel(x);
    }

    public String formatLabelY(XYChart chart, double y) {
        return "y:" + String.valueOf(y);
    }

}
