/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.reports;

import java.util.HashMap;

import com.pyx4j.site.rpc.ReportsAppPlace;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.reports.AvailableCrmReport.CrmReportType;

public class CrmReportsMapper {

    private static HashMap<CrmReportType, ReportsAppPlace<?>> places = new HashMap<>();

    private static HashMap<Class<? extends ReportMetadata>, CrmReportType> types = new HashMap<>();

    protected static void register(CrmReportType type, ReportsAppPlace<?> place) {
        places.put(type, place);
        types.put(place.getReportMetadataClass(), type);
    }

    public static ReportsAppPlace<?> resolvePlace(CrmReportType type) {
        return places.get(type);
    }

    public static CrmReportType getReportType(Class<? extends ReportMetadata> reportMetadataClass) {
        return types.get(reportMetadataClass);
    }

    static {
        register(CrmReportType.AutoPayChanges, new CrmSiteMap.Reports.AutoPayChanges());
        register(CrmReportType.Availability, new CrmSiteMap.Reports.Availability());
        register(CrmReportType.CustomerCreditCheck, new CrmSiteMap.Reports.CustomerCreditCheck());
        register(CrmReportType.EFT, new CrmSiteMap.Reports.Eft());
        register(CrmReportType.EftVariance, new CrmSiteMap.Reports.EftVariance());
        register(CrmReportType.ResidentInsurance, new CrmSiteMap.Reports.ResidentInsurance());
    }

}
