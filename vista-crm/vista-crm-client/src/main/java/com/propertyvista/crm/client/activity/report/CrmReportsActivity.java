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
package com.propertyvista.crm.client.activity.report;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.ui.reports.AbstractReportsActivity;
import com.pyx4j.site.client.ui.reports.HasAdvancedSettings;
import com.pyx4j.site.client.ui.reports.ReportSettings;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.reports.CrmReportsView;
import com.propertyvista.crm.client.ui.reports.MockupReportSettings;
import com.propertyvista.crm.client.ui.viewfactories.DashboardViewFactory;

public class CrmReportsActivity extends AbstractReportsActivity {

    public CrmReportsActivity(AppPlace place) {
        super(DashboardViewFactory.instance(CrmReportsView.class), place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        MockupReportSettings mockup = EntityFactory.create(MockupReportSettings.class);
        mockup.valueX().setValue("X");
        mockup.valueY().setValue("Y");
        mockup.valueZ().setValue("Z");

        mockup.advancedValueX().setValue("What!?!");
        mockup.advancedValueY().setValue("No soup for you!!!");
        mockup.advancedValueZ().setValue("Come back, ONE YEAR!");

        view.setReportSettings(mockup);
    }

    @Override
    public void apply(ReportSettings settings) {
        String mode = (settings instanceof HasAdvancedSettings) ? (((HasAdvancedSettings) settings).isInAdvancedMode().isBooleanTrue() ? "advanced" : "simple")
                : "simple";
        MessageDialog.info("you have selected " + settings.getInstanceValueClass().getName() + ", mode " + mode);
    }

}
