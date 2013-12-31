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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.GridBasedChartConfigurator;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.chart.LineChart;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO;

public class TurnoverAnalysisChartFactory {

    private final SvgFactory svgFactory;

    public TurnoverAnalysisChartFactory(SvgFactory svgFactory) {
        this.svgFactory = svgFactory;
    }

    public SvgRoot createChart(List<UnitTurnoversPerIntervalDTO> data, boolean isPercent, int width, int height) {

        SvgRoot svgroot = svgFactory.getSvgRoot();
        svgroot.add(createChart(createDataSource(data, isPercent), width, height));

        return svgroot;
    }

    private LineChart createChart(DataSource ds, int width, int height) {
        return new LineChart(createConfigurator(ds, width, height));
    }

    private GridBasedChartConfigurator createConfigurator(DataSource ds, int width, int height) {
        GridBasedChartConfigurator config = new GridBasedChartConfigurator(svgFactory, ds, width, height);
        config.setGridType(GridType.Both);
        config.setChartColors(ChartTheme.bright);
        config.setShowValueLabels(true);
        config.setZeroBased(false);
        return config;
    }

    private DataSource createDataSource(List<UnitTurnoversPerIntervalDTO> data, boolean isPercent) {
        DataSource ds = new DataSource();

        for (UnitTurnoversPerIntervalDTO intervalData : data) {
            ArrayList<Double> values = new ArrayList<Double>();
            if (!isPercent) {
                values.add((double) intervalData.unitsTurnedOverAbs().getValue().intValue());
            } else {
                values.add(intervalData.unitsTurnedOverPct().getValue());
            }

            ds.addDataSet(ds.new Metric(TimeUtils.simpleFormat(intervalData.intervalValue().getValue(), "MMM-yy")), values);
        }

        return ds;
    }

}
