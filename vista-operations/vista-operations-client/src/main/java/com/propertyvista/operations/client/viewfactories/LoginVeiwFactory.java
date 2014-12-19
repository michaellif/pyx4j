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
 */
package com.propertyvista.operations.client.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.ui.components.login.LoginView;
import com.propertyvista.common.client.ui.components.login.LoginWithTokenView;
import com.propertyvista.common.client.ui.components.login.LoginWithTokenViewImpl;
import com.propertyvista.common.client.ui.components.login.PasswordResetRequestView;
import com.propertyvista.common.client.ui.components.login.PasswordResetRequestViewImpl;
import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.operations.client.ui.login.LoginViewImpl;

public class LoginVeiwFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (LoginView.class.equals(type)) {
                map.put(type, new LoginViewImpl());
            } else if (LoginWithTokenView.class.equals(type)) {
                map.put(type, new LoginWithTokenViewImpl());
            } else if (PasswordResetRequestView.class.equals(type)) {
                map.put(type, new PasswordResetRequestViewImpl());
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
