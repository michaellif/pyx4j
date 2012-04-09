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
package com.propertyvista.admin.client.viewfactories.crud;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserEditorView;
import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserEditorViewImpl;
import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserListerView;
import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserListerViewImpl;
import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserViewerView;
import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserViewerViewImpl;
import com.propertyvista.admin.client.ui.crud.maintenance.MaintenanceEditorView;
import com.propertyvista.admin.client.ui.crud.maintenance.MaintenanceEditorViewImpl;
import com.propertyvista.admin.client.ui.crud.maintenance.MaintenanceViewerView;
import com.propertyvista.admin.client.ui.crud.maintenance.MaintenanceViewerViewImpl;
import com.propertyvista.admin.client.ui.crud.simulation.SimulationEditorView;
import com.propertyvista.admin.client.ui.crud.simulation.SimulationEdtiorViewImpl;
import com.propertyvista.admin.client.ui.crud.simulation.SimulationViewerView;
import com.propertyvista.admin.client.ui.crud.simulation.SimulationViewerViewImpl;

public class AdministrationVeiwFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView<E>> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (MaintenanceViewerView.class.equals(type)) {
                map.put(type, new MaintenanceViewerViewImpl());
            } else if (MaintenanceEditorView.class.equals(type)) {
                map.put(type, new MaintenanceEditorViewImpl());

            } else if (SimulationViewerView.class.equals(type)) {
                map.put(type, new SimulationViewerViewImpl());
            } else if (SimulationEditorView.class.equals(type)) {
                map.put(type, new SimulationEdtiorViewImpl());

            } else if (AdminUserViewerView.class.equals(type)) {
                map.put(type, new AdminUserViewerViewImpl());
            } else if (AdminUserEditorView.class.equals(type)) {
                map.put(type, new AdminUserEditorViewImpl());
            } else if (AdminUserListerView.class.equals(type)) {
                map.put(type, new AdminUserListerViewImpl());
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
