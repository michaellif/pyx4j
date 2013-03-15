/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 2, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports;

import java.util.Map;

import net.sf.ehcache.store.chm.ConcurrentHashMap;

import com.pyx4j.site.server.services.reports.AbstractReportsService;
import com.pyx4j.site.server.services.reports.ReportGenerator;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.crm.rpc.services.reports.CrmReportsService;
import com.propertyvista.crm.server.services.reports.generators.AvailabilityReportsGenerator;
import com.propertyvista.crm.server.services.reports.generators.CustomerCreditCheckReportGenerator;
import com.propertyvista.crm.server.services.reports.generators.PapReportGenerator;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;
import com.propertyvista.domain.reports.CustomerCreditCheckReportMetadata;
import com.propertyvista.domain.reports.PapReportMetadata;

public class CrmReportsServiceImpl extends AbstractReportsService implements CrmReportsService {

    private static final Map<Class<? extends ReportMetadata>, ReportGenerator> reportsGeneratorMap;

    static {
        reportsGeneratorMap = new ConcurrentHashMap<Class<? extends ReportMetadata>, ReportGenerator>();

        reportsGeneratorMap.put(AvailabilityReportMetadata.class, new AvailabilityReportsGenerator());
        reportsGeneratorMap.put(CustomerCreditCheckReportMetadata.class, new CustomerCreditCheckReportGenerator());
        reportsGeneratorMap.put(PapReportMetadata.class, new PapReportGenerator());
    }

    public CrmReportsServiceImpl() {
        super(reportsGeneratorMap);
    }

}
