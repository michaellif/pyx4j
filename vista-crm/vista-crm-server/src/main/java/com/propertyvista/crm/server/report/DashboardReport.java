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

public class DashboardReport {

    public static final String title = "Dashboard Report";

    public static JasperReportModel createModel(DashboardMetadata metaData) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ReportTitle", title);
        parameters.put("DIGITAL_SIG", "Digital Signature...");
        return new JasperReportModel(DashboardReport.class.getPackage().getName() + ".Dashboard", Arrays.asList(new DashboardMetadata[] { metaData }),
                parameters, generateJrXml(metaData));
    }

    private static String generateJrXml(DashboardMetadata metaData) {
        // in memory Jasper report XML here...
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" name=\"Dashboard Report\" pageWidth=\"612\" pageHeight=\"792\" columnWidth=\"555\" leftMargin=\"20\" rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\">    <property name=\"ireport.zoom\" value=\"1.5\"/>    <property name=\"ireport.x\" value=\"0\"/>    <property name=\"ireport.y\" value=\"0\"/></jasperReport>";
    }
}
