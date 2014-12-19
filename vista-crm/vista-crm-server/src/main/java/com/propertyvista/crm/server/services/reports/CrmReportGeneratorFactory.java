/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 27, 2014
 * @author vlads
 */
package com.propertyvista.crm.server.services.reports;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.server.services.reports.ReportGenerator;
import com.pyx4j.essentials.server.services.reports.ReportGeneratorFactory;
import com.pyx4j.security.shared.SecurityViolationException;
import com.pyx4j.site.shared.domain.reports.ReportTemplate;

import com.propertyvista.crm.rpc.services.reports.CrmReportsMapper;
import com.propertyvista.crm.server.services.reports.generators.AutoPayChangesReportGenerator;
import com.propertyvista.crm.server.services.reports.generators.AvailabilityReportsGenerator;
import com.propertyvista.crm.server.services.reports.generators.CustomerCreditCheckReportGenerator;
import com.propertyvista.crm.server.services.reports.generators.EftReportGenerator;
import com.propertyvista.crm.server.services.reports.generators.EftVarianceReportGenerator;
import com.propertyvista.crm.server.services.reports.generators.ResidentInsuranceReportGenerator;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;
import com.propertyvista.domain.reports.CustomerCreditCheckReportMetadata;
import com.propertyvista.domain.reports.EftReportMetadata;
import com.propertyvista.domain.reports.EftVarianceReportMetadata;
import com.propertyvista.domain.reports.ResidentInsuranceReportMetadata;

public class CrmReportGeneratorFactory extends ReportGeneratorFactory {

    private static final Map<Class<? extends ReportTemplate>, Class<? extends ReportGenerator>> reportsGeneratorMap;

    static {
        reportsGeneratorMap = new ConcurrentHashMap<Class<? extends ReportTemplate>, Class<? extends ReportGenerator>>();

        reportsGeneratorMap.put(AvailabilityReportMetadata.class, AvailabilityReportsGenerator.class);
        reportsGeneratorMap.put(CustomerCreditCheckReportMetadata.class, CustomerCreditCheckReportGenerator.class);
        reportsGeneratorMap.put(EftReportMetadata.class, EftReportGenerator.class);
        reportsGeneratorMap.put(EftVarianceReportMetadata.class, EftVarianceReportGenerator.class);
        reportsGeneratorMap.put(AutoPayChangesReportMetadata.class, AutoPayChangesReportGenerator.class);
        reportsGeneratorMap.put(ResidentInsuranceReportMetadata.class, ResidentInsuranceReportGenerator.class);
    }

    @Override
    public Class<? extends ReportGenerator> getReportGeneratorClass(Class<? extends ReportTemplate> reportMetadataClass) {

        if (!CrmReportsSecurity.currentUserAvailableReportTypes().contains(CrmReportsMapper.getReportType(reportMetadataClass))) {
            if (ApplicationMode.isDevelopment()) {
                throw new SecurityViolationException("Permission denied " + ApplicationMode.DEV + "Report "
                        + CrmReportsMapper.getReportType(reportMetadataClass));
            } else {
                throw new SecurityViolationException("Permission denied");
            }
        }

        return reportsGeneratorMap.get(reportMetadataClass);
    }
}
