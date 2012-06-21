/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.ui.components.login.PasswordResetRequestView;
import com.propertyvista.common.client.ui.components.login.PasswordResetRequestViewImpl;
import com.propertyvista.common.client.ui.components.security.AccountRecoveryOptionsEditorView;
import com.propertyvista.common.client.ui.components.security.AccountRecoveryOptionsEditorViewImpl;
import com.propertyvista.common.client.ui.components.security.AccountRecoveryOptionsViewerView;
import com.propertyvista.common.client.ui.components.security.AccountRecoveryOptionsViewerViewImpl;
import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.common.client.ui.components.security.PasswordChangeViewImpl;
import com.propertyvista.common.client.ui.components.security.PasswordResetView;
import com.propertyvista.common.client.ui.components.security.PasswordResetViewImpl;
import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.crm.client.ui.crud.organisation.employee.LoginAttemptsListerView;
import com.propertyvista.crm.client.ui.crud.organisation.employee.LoginAttemptsListerViewImpl;

public class SecurityViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (PasswordResetView.class.equals(type)) {
                map.put(type, new PasswordResetViewImpl());
            } else if (PasswordChangeView.class.equals(type)) {
                map.put(type, new PasswordChangeViewImpl());
            } else if (PasswordResetView.class.equals(type)) {
                map.put(type, new PasswordResetViewImpl());
            } else if (PasswordResetRequestView.class.equals(type)) {
                map.put(type, new PasswordResetRequestViewImpl());
            } else if (AccountRecoveryOptionsViewerView.class.equals(type)) {
                map.put(type, new AccountRecoveryOptionsViewerViewImpl());
            } else if (AccountRecoveryOptionsEditorView.class.equals(type)) {
                map.put(type, new AccountRecoveryOptionsEditorViewImpl());
            } else if (LoginAttemptsListerView.class.equals(type)) {
                map.put(type, new LoginAttemptsListerViewImpl());
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
