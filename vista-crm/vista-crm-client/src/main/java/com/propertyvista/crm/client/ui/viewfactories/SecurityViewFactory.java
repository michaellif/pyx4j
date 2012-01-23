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
import com.propertyvista.crm.client.ui.security.NewPasswordView;
import com.propertyvista.crm.client.ui.security.NewPasswordViewImpl;
import com.propertyvista.crm.client.ui.security.PasswordResetRequestSuccessView;
import com.propertyvista.crm.client.ui.security.PasswordResetRequestSuccessViewImpl;

public class SecurityViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (LoginView.class.equals(type)) {
                map.put(type, new LoginViewImpl());
            } else if (PasswordResetRequestSuccessView.class.equals(type)) {
                map.put(type, new PasswordResetRequestSuccessViewImpl());
            } else if (NewPasswordView.class.equals(type)) {
                map.put(type, new NewPasswordViewImpl());
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
