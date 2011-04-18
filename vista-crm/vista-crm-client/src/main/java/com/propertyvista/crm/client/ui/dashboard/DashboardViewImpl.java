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
package com.propertyvista.crm.client.ui.dashboard;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Singleton;

import com.pyx4j.dashboard.client.DashboardPanel;
import com.pyx4j.dashboard.client.Layout;
import com.pyx4j.dashboard.demo.client.DemoGadget;

@Singleton
public class DashboardViewImpl extends SimplePanel implements DashboardView {

    DashboardPanel dashboardPanel = new DashboardPanel();

    public DashboardViewImpl() {
        VerticalPanel main = new VerticalPanel();
        main.add(new HTML("<b>Dashboard Menu goes here...</b>"));
        main.add(dashboardPanel);
        main.setWidth("100%");
        setWidget(main);

        dashboardPanel.setLayout(new Layout(3, 1, 12));
        fillDashboard();
    }

    private void fillDashboard() {

        // fill the dashboard with demo widgets:
        dashboardPanel.removeAllGadgets();

        int count = 0;
        for (int col = 0; col < dashboardPanel.getLayout().getColumns(); ++col)
            for (int row = 0; row < 3; ++row) {
                // initialize a widget
                DemoGadget widget = new DemoGadget("&nbsp;Gadget&nbsp;#" + ++count);
                widget.setHeight(Random.nextInt(8) + 10 + "em");
                dashboardPanel.addGadget(widget, col);
            }
    }
}
