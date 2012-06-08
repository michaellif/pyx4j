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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.svg.j2se.SvgFactoryForBatik;

import com.propertyvista.crm.server.services.dashboard.gadgets.ArrearsReportServiceImpl;
import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.StaticTemplateReportModelBuilder;
import com.propertyvista.crm.server.services.reports.util.SvgRasterizer;
import com.propertyvista.crm.server.services.reports.util.SvgRasterizerImpl;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsYOYComparisonDataDTO;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.svg.gadgets.ArrearsYoyAnalysisChartFactory;

public class ArrearsYoyAnalysisChartGadgetReportModelCreator implements GadgetReportModelCreator {

    private enum Params {

        TITLE, GRAPH;

    }

    private static final int HEIGHT = 250;

    private static final int WIDTH = 554;

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Key> selectedBuildings) {

        final ArrearsYOYAnalysisChartMetadata arrearsYOYAnalysisChartMetadata = (ArrearsYOYAnalysisChartMetadata) gadgetMetadata;

        new ArrearsReportServiceImpl().arrearsMonthlyComparison(new AsyncCallback<ArrearsYOYComparisonDataDTO>() {

            @Override
            public void onSuccess(ArrearsYOYComparisonDataDTO data) {

                SvgRasterizer rasterizer = new SvgRasterizerImpl();
                BufferedImage graph = rasterizer.rasterize(new ArrearsYoyAnalysisChartFactory(new SvgFactoryForBatik()).createChart(data, WIDTH, HEIGHT),
                        WIDTH, HEIGHT);

                //@formatter:off
                callback.onSuccess(new StaticTemplateReportModelBuilder(ArrearsYOYAnalysisChartMetadata.class)
                        .param(Params.TITLE.name(), arrearsYOYAnalysisChartMetadata.getEntityMeta().getCaption())
                        .param(Params.GRAPH.name(), graph)
                        .build());
                //@formatter:on
            }

            @Override
            public void onFailure(Throwable arg0) {
                callback.onFailure(arg0);
            }

        }, asStubs(selectedBuildings), arrearsYOYAnalysisChartMetadata.yearsToCompare().getValue());
    }

    // TODO this has to be refactored on more generic level (for now it's just a HACK)
    private static Vector<Building> asStubs(Vector<Key> selectedBuildings) {
        Vector<Building> stubs = new Vector<Building>();
        for (Key key : selectedBuildings) {
            Building stub = EntityFactory.create(Building.class);
            stub.setPrimaryKey(key);
            stub.setAttachLevel(AttachLevel.IdOnly);
        }
        return stubs;
    }

}
