/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 4, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.ILayoutManager;

public interface CrmDashboardResources {

    interface Layout1ColumnResources extends ILayoutManager.Resources, ClientBundle {

        Layout1ColumnResources INSTANCE = GWT.create(Layout1ColumnResources.class);

        @Override
        @Source("DashboardLayout1-0.png")
        ImageResource layoutIcon();

        @Override
        @Source("DashboardLayout1-1.png")
        ImageResource layoutIconSelected();

    }

    interface Layout12ColumnResources extends ILayoutManager.Resources, ClientBundle {

        Layout12ColumnResources INSTANCE = GWT.create(Layout12ColumnResources.class);

        @Override
        @Source("DashboardLayout12-0.png")
        public ImageResource layoutIcon();

        @Override
        @Source("DashboardLayout12-1.png")
        public ImageResource layoutIconSelected();

    }

    interface Layout21ColumnResources extends ILayoutManager.Resources, ClientBundle {

        Layout21ColumnResources INSTANCE = GWT.create(Layout21ColumnResources.class);

        @Override
        @Source("DashboardLayout21-0.png")
        public ImageResource layoutIcon();

        @Override
        @Source("DashboardLayout21-1.png")
        public ImageResource layoutIconSelected();

    }

    interface Layout22ColumnResources extends ILayoutManager.Resources, ClientBundle {

        Layout22ColumnResources INSTANCE = GWT.create(Layout22ColumnResources.class);

        @Override
        @Source("DashboardLayout22-0.png")
        public ImageResource layoutIcon();

        @Override
        @Source("DashboardLayout22-1.png")
        public ImageResource layoutIconSelected();

    }

    interface Layout3ColumnResources extends ILayoutManager.Resources, ClientBundle {

        Layout3ColumnResources INSTANCE = GWT.create(Layout3ColumnResources.class);

        @Override
        @Source("DashboardLayout3-0.png")
        public ImageResource layoutIcon();

        @Override
        @Source("DashboardLayout3-1.png")
        public ImageResource layoutIconSelected();

    }

    interface LayoutRowsResources extends ILayoutManager.Resources, ClientBundle {

        LayoutRowsResources INSTANCE = GWT.create(LayoutRowsResources.class);

        @Override
        @Source("DashboardLayoutReportNotToggled.png")
        public ImageResource layoutIcon();

        @Override
        @Source("DashboardLayoutReport.png")
        public ImageResource layoutIconSelected();

    }
}