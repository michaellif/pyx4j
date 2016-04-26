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

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.svg.chart.XYChart;

public class HoverXYChartTooltip extends FlowPanel implements MouseMoveHandler, MouseOutHandler, MouseOverHandler {

    private Widget chartPanel;

    private final XYChart chart;

    protected final HTML labelX;

    protected final HTML labelY;

    public static void inject(Panel container, Widget chartPanel, XYChart chart) {
        new HoverXYChartTooltip(container, chartPanel, chart);
    }

    public HoverXYChartTooltip(Panel container, Widget chartPanel, XYChart chart) {
        this.chart = chart;
        this.chartPanel = chartPanel;
        chartPanel.addDomHandler(this, MouseMoveEvent.getType());
        chartPanel.addDomHandler(this, MouseOutEvent.getType());
        chartPanel.addDomHandler(this, MouseOverEvent.getType());

        this.getStyle().setPosition(Position.ABSOLUTE);

        this.getStyle().setBorderStyle(BorderStyle.SOLID);
        this.getStyle().setBorderWidth(1, Unit.PX);
        this.getStyle().setBorderColor("#666");
        this.getStyle().setProperty("borderRadius", "10px");
        this.getStyle().setProperty("background", "rgba(255,255,255,0.8)");
        this.getStyle().setPadding(5, Unit.PX);

        container.add(this);

        this.add(labelY = new HTML(""));
        this.add(labelX = new HTML(""));
        labelX.getStyle().setTextAlign(TextAlign.CENTER);
        labelX.getStyle().setWidth(100, Unit.PCT);
        labelY.getStyle().setTextAlign(TextAlign.CENTER);
        labelY.getStyle().setWidth(100, Unit.PCT);
    }

    protected void positionLabels(int x, int y) {
        this.getStyle().setTop(y + 30, Unit.PX);
        this.getStyle().setLeft(x - this.getOffsetWidth() / 2 + 10, Unit.PX);
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
//        int x = Window.getScrollLeft() + event.getClientX();// chartPanel.getAbsoluteLeft() + event.getX() - chartPanel.getOffsetWidth();
//        int y = Window.getScrollTop() + event.getClientY();// chartPanel.getOffsetHeight() + chartPanel.getAbsoluteTop() + event.getY();

        int x = event.getRelativeX(this.getParent().getElement());
        int y = event.getRelativeY(this.getParent().getElement());

        positionLabels(x, y);

        labelX.setHTML(formatLabelX(chart, chart.getXValue(event.getX())));
        labelY.setHTML(formatLabelY(chart, chart.getYValue(event.getY())));
    }

    public String formatLabelX(XYChart chart, double x) {
        return "x: " + chart.configurator().getXAxisProducer().formatLabel(x);
    }

    public String formatLabelY(XYChart chart, double y) {
        return "y: " + chart.configurator().getYAxisProducer().formatLabel(y);
    }

}
