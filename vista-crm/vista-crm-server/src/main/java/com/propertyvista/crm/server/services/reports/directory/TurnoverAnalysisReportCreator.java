/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.directory;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.GridBasedChartConfigurator;
import com.pyx4j.svg.chart.LineChart;
import com.pyx4j.svg.j2se.SvgFactoryForBatik;
import com.pyx4j.svg.j2se.SvgRootImpl;

import com.propertyvista.crm.server.services.dashboard.gadgets.AvailabilityReportServiceImpl;
import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.ReportModelBuilder;
import com.propertyvista.crm.server.services.reports.util.SvgRasterizer;
import com.propertyvista.crm.server.services.reports.util.SvgRasterizerImpl;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.TurnoverAnalysisMetadata;

public class TurnoverAnalysisReportCreator implements GadgetReportModelCreator {

    private static final I18n i18n = I18n.get(TurnoverAnalysisReportCreator.class);

    private static final SimpleDateFormat MONTH_LABEL_FORMAT = new SimpleDateFormat("MMM-yy");

    private static final SimpleDateFormat REPORT_FORMAT = new SimpleDateFormat("yyyy-MMM-dd");

    private static final int HEIGHT = 250;

    private static final int WIDTH = 554;

    protected static final String TITLE = "TITLE";

    protected static final String GRAPH = "GRAPH";

    protected static final String AS_OF = "AS_OF";

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Key> selectedBuildings) {
        final TurnoverAnalysisMetadata turnoverAnalysisMetadata = (TurnoverAnalysisMetadata) gadgetMetadata;
        final LogicalDate asOf = turnoverAnalysisMetadata.customizeDate().isBooleanTrue() ? turnoverAnalysisMetadata.asOf().getValue() : new LogicalDate();

        new AvailabilityReportServiceImpl().turnoverAnalysis(new AsyncCallback<Vector<UnitTurnoversPerIntervalDTO>>() {





            @Override
            public void onSuccess(Vector<UnitTurnoversPerIntervalDTO> data) {

                DataSource ds = new DataSource();
                for (UnitTurnoversPerIntervalDTO intervalData : data) {
                    ArrayList<Double> values = new ArrayList<Double>();
                    if (!turnoverAnalysisMetadata.isTurnoverMeasuredByPercent().isBooleanTrue()) {
                        values.add((double) intervalData.unitsTurnedOverAbs().getValue().intValue());
                    } else {
                        values.add(intervalData.unitsTurnedOverPct().getValue());
                    }

                    ds.addDataSet(ds.new Metric(MONTH_LABEL_FORMAT.format(intervalData.intervalValue().getValue())), values);
                }

                SvgFactory factory = new SvgFactoryForBatik();

                GridBasedChartConfigurator config = new GridBasedChartConfigurator(factory, ds, WIDTH, HEIGHT);
                config.setTheme(ChartTheme.Bright);

                SvgRoot svgroot = factory.getSvgRoot();
                ((SvgRootImpl) svgroot).setAttributeNS(null, "width", String.valueOf(WIDTH));
                ((SvgRootImpl) svgroot).setAttributeNS(null, "height", String.valueOf(HEIGHT));
                svgroot.add(new LineChart(config));

                SvgRasterizer rasterizer = new SvgRasterizerImpl();
                BufferedImage graph = rasterizer.rasterize(svgroot, WIDTH, HEIGHT);

                callback.onSuccess(new ReportModelBuilder<TurnoverAnalysisMetadata>(TurnoverAnalysisMetadata.class)//@formatter:off
                        .param(TITLE, turnoverAnalysisMetadata.getEntityMeta().getCaption())
                        .param(AS_OF, i18n.tr("As of Date: {0}", REPORT_FORMAT.format(asOf)))
                        .param(GRAPH, graph)
                        .build());//@formatter:on
            }

            @Override
            public void onFailure(Throwable arg0) {
                callback.onFailure(arg0);
            }

        }, new Vector<Key>(), asOf);
    }

}
