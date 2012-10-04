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
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementEditorView;
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementEditorViewImpl;
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementListerView;
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementListerViewImpl;
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementViewerView;
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementViewerViewImpl;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.dashboard.DashboardViewImpl;

public class DashboardViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (DashboardView.class.equals(type)) {
                map.put(type, new DashboardViewImpl());
            } else if (DashboardManagementListerView.class.equals(type)) {
                map.put(type, new DashboardManagementListerViewImpl());
            } else if (DashboardManagementEditorView.class.equals(type)) {
                map.put(type, new DashboardManagementEditorViewImpl());
            } else if (DashboardManagementViewerView.class.equals(type)) {
                map.put(type, new DashboardManagementViewerViewImpl());
            }
        }

        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}
