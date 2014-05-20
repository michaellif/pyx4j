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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;

import com.propertyvista.domain.reports.ResidentInsuranceReportMetadata;

public class ResidentInsuranceReportSettingsForm extends CForm<ResidentInsuranceReportMetadata> {

    public ResidentInsuranceReportSettingsForm() {
        super(ResidentInsuranceReportMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        DualColumnForm formPanel = new DualColumnForm(this);
        formPanel.append(Location.Left, proto().onlyLeasesWithInsurance()).decorate();
        return formPanel;
    }

}
