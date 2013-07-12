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
package com.propertyvista.operations.client.viewfactories.crud;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.common.client.ui.components.security.PasswordChangeViewImpl;
import com.propertyvista.operations.client.ui.crud.auditrecords.AuditRecordListerView;
import com.propertyvista.operations.client.ui.crud.auditrecords.AuditRecordListerViewImpl;
import com.propertyvista.operations.client.ui.crud.auditrecords.AuditRecordViewerView;
import com.propertyvista.operations.client.ui.crud.auditrecords.AuditRecordViewerViewImpl;

public class SecurityViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (PasswordChangeView.class.equals(type)) {
                map.put(type, new PasswordChangeViewImpl());
            } else if (AuditRecordListerView.class.equals(type)) {
                map.put(type, new AuditRecordListerViewImpl());
            } else if (AuditRecordViewerView.class.equals(type)) {
                map.put(type, new AuditRecordViewerViewImpl());
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
