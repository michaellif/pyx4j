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
 * Created on 2011-05-01
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.svg.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.svg.demo.client.chart.ChartTestConfiguration;
import com.pyx4j.svg.demo.client.chart.ChartTestConfigurationFactory;
import com.pyx4j.svg.demo.client.chart.TestChartBuilder;
import com.pyx4j.svg.demo.client.chart.TestChartBuilderFactory;
import com.pyx4j.svg.demo.client.ui.ChartTestPanel;
import com.pyx4j.svg.demo.client.ui.ChartTestSelectorPanel;

public class SVGChartDemoEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {

        TestChartBuilder testChartBuilder = new TestChartBuilderFactory();
        ChartTestSelectorPanel selector = new ChartTestSelectorPanel();
        final ChartTestPanel testPanel = new ChartTestPanel(testChartBuilder);

        selector.addValueChangeHandler(new ValueChangeHandler<ChartTestConfiguration>() {
            @Override
            public void onValueChange(ValueChangeEvent<ChartTestConfiguration> event) {
                testPanel.rebuildChart(event.getValue());
                ChartTestConfigurationFactory.save(event.getValue());
            }
        });

        selector.setConfiguration(ChartTestConfigurationFactory.getChartTestConfiguration());

        VerticalPanel content = new VerticalPanel();
        content.add(selector);
        content.add(testPanel);

        RootPanel.get().add(content);

    }
}
