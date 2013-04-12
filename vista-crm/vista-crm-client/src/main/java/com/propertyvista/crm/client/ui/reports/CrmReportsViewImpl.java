/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.site.client.ui.reports.AbstractReport;
import com.pyx4j.site.client.ui.reports.ReportFactory;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.crm.client.ui.reports.factories.AvailabilityReportFactory;
import com.propertyvista.crm.client.ui.reports.factories.CustomerCreditCheckReportFactory;
import com.propertyvista.crm.client.ui.reports.factories.PapReportFactory;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;
import com.propertyvista.domain.reports.CustomerCreditCheckReportMetadata;
import com.propertyvista.domain.reports.PapReportMetadata;

public class CrmReportsViewImpl extends AbstractReport implements CrmReportsView {

    private static Map<Class<? extends ReportMetadata>, ReportFactory<?>> factoryMap;

    static {
        factoryMap = new HashMap<Class<? extends ReportMetadata>, ReportFactory<?>>();
        factoryMap.put(AvailabilityReportMetadata.class, new AvailabilityReportFactory());
        factoryMap.put(CustomerCreditCheckReportMetadata.class, new CustomerCreditCheckReportFactory());
        factoryMap.put(PapReportMetadata.class, new PapReportFactory());
    }

    public CrmReportsViewImpl() {
        super(factoryMap);
    }

}
