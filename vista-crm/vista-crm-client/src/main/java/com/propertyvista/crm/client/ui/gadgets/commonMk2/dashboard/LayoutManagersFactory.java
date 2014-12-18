/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-24
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.widgets.client.dashboard.BoardLayout;

import com.propertyvista.crm.client.resources.CrmDashboardResources;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;

public class LayoutManagersFactory {

    public static List<ILayoutManager> createLayoutManagers() {
        return Arrays.<ILayoutManager> asList(//@formatter:off
                new DashboardLayoutManager(LayoutType.One, BoardLayout.One, CrmDashboardResources.Layout1ColumnResources.INSTANCE),                
                new DashboardLayoutManager(LayoutType.Two11, BoardLayout.Two11, CrmDashboardResources.Layout22ColumnResources.INSTANCE),
                new DashboardLayoutManager(LayoutType.Two12, BoardLayout.Two12, CrmDashboardResources.Layout12ColumnResources.INSTANCE),
                new DashboardLayoutManager(LayoutType.Two21, BoardLayout.Two21, CrmDashboardResources.Layout21ColumnResources.INSTANCE),                
                new DashboardLayoutManager(LayoutType.Three, BoardLayout.Three, CrmDashboardResources.Layout3ColumnResources.INSTANCE),
                new DashboardLayoutManager(LayoutType.Report, BoardLayout.Report, CrmDashboardResources.LayoutRowsResources.INSTANCE)
        );//@formatter:on
    }

}
