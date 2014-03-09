/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Mar 5, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.svg.demo.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

import com.pyx4j.svg.demo.client.chart.ChartTestConfiguration;
import com.pyx4j.svg.demo.client.chart.TestChartBuilder;

public class ChartTestPanel extends SplitLayoutPanel {

    private final TestChartBuilder testChartBuilder;

    private final ChartHolderPanel chartHolder;

    private final Label south;

    private final Label east;

    private ChartTestConfiguration currnetTestConfiguration;

    private class ChartHolderPanel extends SimplePanel implements RequiresResize {

        @Override
        public void onResize() {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    createChart();
                    south.setText("height " + chartHolder.getOffsetHeight() + "px");
                    east.setText("width " + chartHolder.getOffsetWidth() + "px");
                }
            });
        }
    }

    public ChartTestPanel(TestChartBuilder testChartBuilder) {
        super(5);
        this.testChartBuilder = testChartBuilder;
        this.getElement().getStyle().setPosition(Position.ABSOLUTE);
        this.getElement().getStyle().setTop(150, Unit.PX);
        this.getElement().getStyle().setLeft(0, Unit.PX);
        this.getElement().getStyle().setRight(0, Unit.PX);
        this.getElement().getStyle().setBottom(0, Unit.PX);

        this.getElement().getStyle().setProperty("border", "3px solid #e7e7e7");

        this.addSouth(south = new Label("..."), 150);
        this.addEast(east = new Label("..."), 100);

        chartHolder = new ChartHolderPanel();
        this.add(chartHolder);

        Window.addResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                chartHolder.onResize();
            }
        });

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                chartHolder.onResize();
            }
        });
    }

    public void rebuildChart(ChartTestConfiguration testConfiguration) {
        currnetTestConfiguration = testConfiguration;
        createChart();
    }

    private void createChart() {
        if (currnetTestConfiguration != null) {
            chartHolder.setWidget(testChartBuilder.createChart(chartHolder.getOffsetWidth(), chartHolder.getOffsetHeight(), currnetTestConfiguration));
        }
    }

}
