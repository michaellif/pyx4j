/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-09
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.reports.availability;

import com.pyx4j.site.client.backoffice.ui.prime.report.AbstractPrimeReport;

import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class AvailabilityReportViewImpl extends AbstractPrimeReport<AvailabilityReportMetadata> implements AvailabilityReportView {

    public AvailabilityReportViewImpl() {
        setReportWidget(new AvailabilityReportWidget(), new AvailabilityReportSettingsAdvancedForm(), new AvailabilityReportSettingsSimpleForm());
    }

}
