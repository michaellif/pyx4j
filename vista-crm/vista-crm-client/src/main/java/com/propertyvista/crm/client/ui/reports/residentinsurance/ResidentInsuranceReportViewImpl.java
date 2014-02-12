/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.residentinsurance;

import com.pyx4j.site.client.ui.reports.AbstractReport;

import com.propertyvista.domain.reports.ResidentInsuranceReportMetadata;

public class ResidentInsuranceReportViewImpl extends AbstractReport<ResidentInsuranceReportMetadata> implements ResidentInsuranceReportView {

    public ResidentInsuranceReportViewImpl() {
        setReportWidget(new ResidentInsuranceReportWidget(), new ResidentInsuranceReportSettingsForm(), null);
    }

}
