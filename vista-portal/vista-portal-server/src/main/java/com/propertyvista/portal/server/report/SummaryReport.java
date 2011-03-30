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
package com.propertyvista.portal.server.report;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.propertyvista.portal.domain.pt.Summary;

import com.pyx4j.entity.report.JasperReportModel;

public class SummaryReport {

    public static final String title = "Summary Report";

    public static JasperReportModel createModel(Summary summary) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ReportTitle", title);
        parameters.put("LEASE_PRICE", "Pricing and Availability...");
        parameters.put("LEASE_TERMS", "Lease Terms text...");
        parameters.put("DIGITAL_SIG", "Digital Signature...");
        return new JasperReportModel(SummaryReport.class.getPackage().getName() + ".Summary", Arrays.asList(new Summary[] { summary }), parameters);
    }
}
