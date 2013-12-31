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
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.rpc.server.LocalService;
import com.pyx4j.svg.j2se.SvgFactoryForBatik;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.StaticTemplateReportModelBuilder;
import com.propertyvista.crm.server.services.reports.util.SvgRasterizer;
import com.propertyvista.crm.server.services.reports.util.SvgRasterizerImpl;
import com.propertyvista.crm.svg.gadgets.ArrearsYoyAnalysisChartFactory;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsYOYComparisonDataDTO;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsYoyAnalysisChartGadgetReportModelCreator implements GadgetReportModelCreator {

    private enum Params {

        TITLE, GRAPH;

    }

    private static final int HEIGHT = 250;

    private static final int WIDTH = 554;

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Building> buildingsFilter) {

        final ArrearsYOYAnalysisChartGadgetMetadata arrearsYOYAnalysisChartMetadata = (ArrearsYOYAnalysisChartGadgetMetadata) gadgetMetadata;

        LocalService.create(ArrearsReportService.class).arrearsMonthlyComparison(new AsyncCallback<ArrearsYOYComparisonDataDTO>() {

            @Override
            public void onSuccess(ArrearsYOYComparisonDataDTO data) {

                SvgRasterizer rasterizer = new SvgRasterizerImpl();
                BufferedImage graph = rasterizer.rasterize(new ArrearsYoyAnalysisChartFactory(new SvgFactoryForBatik()).createChart(data, WIDTH, HEIGHT),
                        WIDTH, HEIGHT);

                //@formatter:off
                callback.onSuccess(new StaticTemplateReportModelBuilder(ArrearsYOYAnalysisChartGadgetMetadata.class)
                        .param(Params.TITLE.name(), arrearsYOYAnalysisChartMetadata.getEntityMeta().getCaption())
                        .param(Params.GRAPH.name(), graph)
                        .build());
                //@formatter:on
            }

            @Override
            public void onFailure(Throwable arg0) {
                callback.onFailure(arg0);
            }

        }, buildingsFilter, arrearsYOYAnalysisChartMetadata.yearsToCompare().getValue());
    }

}
