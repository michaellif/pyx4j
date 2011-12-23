/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestEditorView;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestListerView;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestListerViewImpl;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestViewerView;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestViewerViewImpl;

public class MaintenanceViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView<E>> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (MaintenanceRequestListerView.class.equals(type)) {
                map.put(type, new MaintenanceRequestListerViewImpl());
            } else if (MaintenanceRequestViewerView.class.equals(type)) {
                map.put(type, new MaintenanceRequestViewerViewImpl());
            } else if (MaintenanceRequestEditorView.class.equals(type)) {
                map.put(type, new MaintenanceRequestEditorViewImpl());
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
