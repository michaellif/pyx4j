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

import com.propertyvista.crm.client.ui.login.LoginView;
import com.propertyvista.crm.client.ui.login.LoginViewImpl;
import com.propertyvista.crm.client.ui.login.NewPasswordView;
import com.propertyvista.crm.client.ui.login.NewPasswordViewImpl;
import com.propertyvista.crm.client.ui.login.RetrievePasswordView;
import com.propertyvista.crm.client.ui.login.RetrievePasswordViewImpl;

public class LoginVeiwFactory extends ViewFactoryBase {

    public static IsWidget instance(Class<?> type) {
        if (!map.containsKey(type)) {
            if (LoginView.class.equals(type)) {
                map.put(type, new LoginViewImpl());
            } else if (NewPasswordView.class.equals(type)) {
                map.put(type, new NewPasswordViewImpl());
            } else if (RetrievePasswordView.class.equals(type)) {
                map.put(type, new RetrievePasswordViewImpl());
            }
        }
        return map.get(type);
    }
}
