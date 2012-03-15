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
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.reports;

import com.pyx4j.essentials.rpc.report.ReportService;

import com.propertyvista.domain.dashboard.DashboardMetadata;

public interface DashboardReportService extends ReportService<DashboardMetadata> {

    String PARAM_SELECTED_BUILDINGS = "selected-buildings";

}
