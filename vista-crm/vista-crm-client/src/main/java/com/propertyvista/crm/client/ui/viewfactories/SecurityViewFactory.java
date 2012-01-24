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

import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.crm.client.ui.login.LoginView;
import com.propertyvista.crm.client.ui.login.LoginViewImpl;
import com.propertyvista.crm.client.ui.security.PasswordChangeView;
import com.propertyvista.crm.client.ui.security.PasswordChangeViewImpl;
import com.propertyvista.crm.client.ui.security.PasswordResetRequestResultView;
import com.propertyvista.crm.client.ui.security.PasswordResetRequestResultViewImpl;
import com.propertyvista.crm.client.ui.security.PasswordResetView;
import com.propertyvista.crm.client.ui.security.PasswordResetViewImpl;

public class SecurityViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (LoginView.class.equals(type)) {
                map.put(type, new LoginViewImpl());
            } else if (PasswordResetRequestResultView.class.equals(type)) {
                map.put(type, new PasswordResetRequestResultViewImpl());
            } else if (PasswordResetView.class.equals(type)) {
                map.put(type, new PasswordResetViewImpl());
            } else if (PasswordChangeView.class.equals(type)) {
                map.put(type, new PasswordChangeViewImpl());
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
