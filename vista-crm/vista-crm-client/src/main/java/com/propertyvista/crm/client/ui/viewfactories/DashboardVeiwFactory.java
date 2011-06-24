/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementView;
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementViewImpl;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.dashboard.DashboardViewImpl;
import com.propertyvista.crm.client.ui.report.ReportView;
import com.propertyvista.crm.client.ui.report.ReportViewImpl;

public class DashboardVeiwFactory extends ViewFactoryBase {

    public static IsWidget instance(Class<?> type) {
        if (!map.containsKey(type)) {
            if (DashboardView.class.equals(type)) {
                map.put(type, new DashboardViewImpl());
            } else if (ReportView.class.equals(type)) {
                map.put(type, new ReportViewImpl());
            } else if (DashboardManagementView.class.equals(type)) {
                map.put(type, new DashboardManagementViewImpl());
            }
        }
        return map.get(type);
    }
}
