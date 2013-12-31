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
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.server.LocalService;
import com.pyx4j.svg.j2se.SvgFactoryForBatik;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitTurnoverAnalysisGadgetService;
import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.StaticTemplateReportModelBuilder;
import com.propertyvista.crm.server.services.reports.util.SvgRasterizer;
import com.propertyvista.crm.server.services.reports.util.SvgRasterizerImpl;
import com.propertyvista.crm.svg.gadgets.TurnoverAnalysisChartFactory;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.type.UnitTurnoverAnalysisGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class TurnoverAnalysisChartReportModelCreator implements GadgetReportModelCreator {

    private static final I18n i18n = I18n.get(TurnoverAnalysisChartReportModelCreator.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd");

    private static final int HEIGHT = 250;

    private static final int WIDTH = 554;

    protected static final String TITLE = "TITLE";

    protected static final String GRAPH = "GRAPH";

    protected static final String AS_OF = "AS_OF";

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Building> buildingsFilter) {

        final UnitTurnoverAnalysisGadgetMetadata turnoverAnalysisMetadata = (UnitTurnoverAnalysisGadgetMetadata) gadgetMetadata;
        final LogicalDate asOf = turnoverAnalysisMetadata.customizeDate().isBooleanTrue() ? turnoverAnalysisMetadata.asOf().getValue() : new LogicalDate(
                SystemDateManager.getDate());

        LocalService.create(UnitTurnoverAnalysisGadgetService.class).turnoverAnalysis(new AsyncCallback<Vector<UnitTurnoversPerIntervalDTO>>() {

            @Override
            public void onSuccess(Vector<UnitTurnoversPerIntervalDTO> data) {
                SvgRasterizer rasterizer = new SvgRasterizerImpl();
                BufferedImage graph = rasterizer.rasterize(new TurnoverAnalysisChartFactory(new SvgFactoryForBatik()).createChart(data,
                        turnoverAnalysisMetadata.isTurnoverMeasuredByPercent().getValue(), WIDTH, HEIGHT), WIDTH, HEIGHT);

                callback.onSuccess(new StaticTemplateReportModelBuilder(UnitTurnoverAnalysisGadgetMetadata.class)//@formatter:off
                        .param(TITLE, turnoverAnalysisMetadata.getEntityMeta().getCaption())
                        .param(AS_OF, i18n.tr("As of Date: {0}", DATE_FORMAT.format(asOf)))
                        .param(GRAPH, graph)
                        .build());//@formatter:on
            }

            @Override
            public void onFailure(Throwable arg0) {
                callback.onFailure(arg0);
            }

        }, buildingsFilter, asOf);
    }

}
