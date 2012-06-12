/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.report.JasperReportModel;

import com.propertyvista.crm.server.services.reports.directory.ArrearsStatusReportModelCreator;
import com.propertyvista.crm.server.services.reports.directory.ArrearsSummaryReportModelCreator;
import com.propertyvista.crm.server.services.reports.directory.ArrearsYoyAnalysisChartGadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.directory.BuildingListerReportCreator;
import com.propertyvista.crm.server.services.reports.directory.PaymentRecordsReportModelCreator;
import com.propertyvista.crm.server.services.reports.directory.PaymentsSummaryReportModelCreator;
import com.propertyvista.crm.server.services.reports.directory.TurnoverAnalysisChartReportModelCreator;
import com.propertyvista.crm.server.services.reports.directory.UnitAvailabilityStatusReportCreator;
import com.propertyvista.crm.server.services.reports.directory.UnitAvailabilitySummaryReportCreator;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingLister;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentRecordsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.TurnoverAnalysisMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMeta;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilitySummaryGMeta;

public class ReportModelCreatorDispatcher implements GadgetReportModelCreator {

    private static Object mutex = new Object();

    private static volatile ReportModelCreatorDispatcher instance = null;

    Map<Class<? extends GadgetMetadata>, GadgetReportModelCreator> map;

    private ReportModelCreatorDispatcher() {
        map = new ConcurrentHashMap<Class<? extends GadgetMetadata>, GadgetReportModelCreator>();

        map.put(BuildingLister.class, new BuildingListerReportCreator());

        map.put(UnitAvailabilityGadgetMeta.class, new UnitAvailabilityStatusReportCreator());
        map.put(UnitAvailabilitySummaryGMeta.class, new UnitAvailabilitySummaryReportCreator());
        map.put(TurnoverAnalysisMetadata.class, new TurnoverAnalysisChartReportModelCreator());

        map.put(ArrearsStatusGadgetMetadata.class, new ArrearsStatusReportModelCreator());
        map.put(ArrearsSummaryGadgetMetadata.class, new ArrearsSummaryReportModelCreator());
        map.put(ArrearsYOYAnalysisChartMetadata.class, new ArrearsYoyAnalysisChartGadgetReportModelCreator());

        map.put(PaymentRecordsGadgetMetadata.class, new PaymentRecordsReportModelCreator());
        map.put(PaymentsSummaryGadgetMetadata.class, new PaymentsSummaryReportModelCreator());

        // add more GadgetReportModelCreators here
    }

    @Override
    public void createReportModel(AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Key> selectedBuildings) {
        GadgetReportModelCreator creator = map.get(gadgetMetadata.getInstanceValueClass());

        if (creator != null) {
            creator.createReportModel(callback, gadgetMetadata, selectedBuildings);
        } else {
            callback.onSuccess(createReportNotImplementedModel(gadgetMetadata.getInstanceValueClass().getSimpleName(), gadgetMetadata.docking().column()
                    .getValue() == -1));
        }
    }

    public static JasperReportModel createReportNotImplementedModel(String gadgetName, boolean isFullWidth) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("GADGET_NAME", gadgetName);
        return new JasperReportModel("reports.NotImplemented" + (isFullWidth ? "FullWidth" : "HalfWidth"), null, parameters);
    }

    public static ReportModelCreatorDispatcher instance() {
        if (instance == null) {
            synchronized (mutex) {
                if (instance == null) {
                    instance = new ReportModelCreatorDispatcher();
                }
            }
            return instance;
        } else {
            return instance;
        }
    }

}
