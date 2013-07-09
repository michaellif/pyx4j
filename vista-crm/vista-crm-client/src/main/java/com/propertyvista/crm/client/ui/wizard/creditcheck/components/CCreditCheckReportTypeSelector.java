/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.creditcheck.components;

import java.math.BigDecimal;

import com.pyx4j.forms.client.ui.CFocusComponent;

import com.propertyvista.domain.pmc.CreditCheckReportType;

public class CCreditCheckReportTypeSelector extends CFocusComponent<CreditCheckReportType, NCreditCheckReportTypeSelector> {

    private final ReportTypeDetailsResources reportDetailsResources;

    public CCreditCheckReportTypeSelector(ReportTypeDetailsResources reportDetailsResources) {
        this.reportDetailsResources = reportDetailsResources;
        setNativeWidget(new NCreditCheckReportTypeSelector(this, reportDetailsResources));
    }

    public void setFees(CreditCheckReportType reportType, BigDecimal setupFee, BigDecimal perApplicantFee) {
        getWidget().setFees(reportType, setupFee, perApplicantFee);
    }

}
