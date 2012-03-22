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

import com.propertyvista.admin.client.ui.administration.MaintenanceView;
import com.propertyvista.admin.client.ui.administration.MaintenanceViewImpl;
import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserEditorView;
import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserEditorViewImpl;
import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserListerView;
import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserListerViewImpl;
import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserViewerView;
import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserViewerViewImpl;

public class AdministrationVeiwFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView<E>> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (MaintenanceView.class.equals(type)) {
                map.put(type, new MaintenanceViewImpl());
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
