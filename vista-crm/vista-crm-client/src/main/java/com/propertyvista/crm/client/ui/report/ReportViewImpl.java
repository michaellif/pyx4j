/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.report;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Singleton;
import com.propertyvista.crm.client.ui.gadgets.DemoGadget;
import com.propertyvista.crm.rpc.domain.GadgetMetadata;

import com.pyx4j.dashboard.client.DashboardPanel;
import com.pyx4j.dashboard.client.Layout;
import com.pyx4j.entity.shared.EntityFactory;

@Singleton
public class ReportViewImpl extends SimplePanel implements ReportView {

    DashboardPanel dashboardPanel = new DashboardPanel();

    public ReportViewImpl() {
        VerticalPanel main = new VerticalPanel();
        main.add(new HTML("<b>Report Menu goes here...</b>"));
        main.add(dashboardPanel);
        main.setWidth("100%");
        setWidget(main);

        dashboardPanel.setLayout(new Layout(1, 1, 12));
        fillDashboard();
    }

    private void fillDashboard() {

        // fill the dashboard with demo widgets:
        dashboardPanel.removeAllGadgets();

        int count = 0;
        for (int col = 0; col < dashboardPanel.getLayout().getColumns(); ++col)
            for (int row = 0; row < 5; ++row) {
                // initialize a widget
                GadgetMetadata gmd = EntityFactory.create(GadgetMetadata.class);
                gmd.title().setValue("Gadget #" + ++count);
                DemoGadget widget = new DemoGadget(gmd);
//                widget.setHeight(Random.nextInt(8) + 3 + "em");
                widget.setFullWidth(row % 2 > 0);
                dashboardPanel.addGadget(widget, col);
            }
    }
}
