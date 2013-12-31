/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 8, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.svg.gadgets;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.BarChart2D;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.GridBasedChart;
import com.pyx4j.svg.chart.GridBasedChartConfigurator;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;

import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsComparisonDTO;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsValueDTO;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsYOYComparisonDataDTO;

public class ArrearsYoyAnalysisChartFactory {

    private final SvgFactory svgFactory;

    public ArrearsYoyAnalysisChartFactory(SvgFactory svgFactory) {
        this.svgFactory = svgFactory;
    }

    public SvgRoot createChart(ArrearsYOYComparisonDataDTO data, int width, int height) {

        SvgRoot svgroot = svgFactory.getSvgRoot();
        svgroot.add(createChart(createDataSource(data), width, height));
        return svgroot;
    }

    DataSource createDataSource(ArrearsYOYComparisonDataDTO data) {
        DataSource dataSource = new DataSource();
        dataSource.setSeriesDescription(createSeriesDescription(data));

        for (ArrearsComparisonDTO comparison : data.comparisonsByMonth()) {
            dataSource.addDataSet(asMetric(dataSource, comparison), asValues(comparison));
        }

        return dataSource;
    }

    GridBasedChart createChart(DataSource ds, int width, int height) {

        return new BarChart2D(createConfigurator(ds, width, height));

    }

    GridBasedChartConfigurator createConfigurator(DataSource dataSource, int width, int height) {

        GridBasedChartConfigurator config = new GridBasedChartConfigurator(svgFactory, dataSource, width, height);
        config.setGridType(GridType.Both);
        config.setChartColors(ChartTheme.bright);
        config.setShowValueLabels(false);
        config.setLegend(true);
        config.setZeroBased(false);

        return config;
    }

    List<String> createSeriesDescription(ArrearsYOYComparisonDataDTO data) {
        ArrearsComparisonDTO comparison = data.comparisonsByMonth().get(0);
        List<String> description = new ArrayList<String>();
        for (ArrearsValueDTO value : comparison.values()) {
            description.add(Integer.toString(value.year().getValue()));
        }
        return description;
    }

    DataSource.Metric asMetric(DataSource ds, ArrearsComparisonDTO comparison) {
        Date month = new Date(0, comparison.month().getValue() + 1, 0);
        String monthLabel = TimeUtils.simpleFormat(month, "MMM");
        return ds.new Metric(monthLabel);
    }

    List<Double> asValues(ArrearsComparisonDTO comparison) {
        List<Double> values = new ArrayList<Double>();
        for (ArrearsValueDTO value : comparison.values()) {
            values.add(value.totalArrears().getValue().doubleValue());
        }
        return values;
    }

}
