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
import java.util.concurrent.ConcurrentHashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.report.JasperReportModel;

import com.propertyvista.crm.server.services.reports.directory.BuildingListerReportCreator;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingLister;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class ReportModelCreatorDispatcher implements GadgetReportModelCreator {

    private static Object mutex = new Object();

    private static volatile ReportModelCreatorDispatcher instance = null;

    Map<Class<? extends GadgetMetadata>, AbstractGadgetReportModelCreator<?>> map;

    private ReportModelCreatorDispatcher() {
        map = new ConcurrentHashMap<Class<? extends GadgetMetadata>, AbstractGadgetReportModelCreator<?>>();

        map.put(BuildingLister.class, new BuildingListerReportCreator());

        // add more GadgetReportModelCreators here
    }

    @Override
    public void createReportModel(AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata) {
        AbstractGadgetReportModelCreator<?> creator = map.get(gadgetMetadata.getInstanceValueClass());

        if (creator != null) {
            creator.createReportModel(callback, gadgetMetadata);
        } else {
            callback.onSuccess(createReportNotImplementedModel(gadgetMetadata.getInstanceValueClass().getSimpleName()));
        }
    }

    public static JasperReportModel createReportNotImplementedModel(String gadgetName) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("GADGET_NAME", gadgetName);
        return new JasperReportModel("reports.NotImplemented", null, parameters);
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
