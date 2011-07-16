/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.report;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.report.JasperReportModel;

import com.propertyvista.domain.dashboard.DashboardMetadata;

public class ReportReport {

    public static final String title = "Report Report";

    public static JasperReportModel createModel(DashboardMetadata metaData) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ReportTitle", title);
        parameters.put("DIGITAL_SIG", "Digital Signature...");
        return new JasperReportModel(ReportReport.class.getPackage().getName() + ".Report", Arrays.asList(new DashboardMetadata[] { metaData }), parameters,
                generateJrXml(metaData));
    }

    private static String generateJrXml(DashboardMetadata metaData) {
        // in memory Jasper report XML here...
        return null;
    }
}
