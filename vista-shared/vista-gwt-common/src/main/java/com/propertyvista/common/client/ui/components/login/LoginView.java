/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.login;

import java.util.List;

import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.site.client.IsView;

import com.propertyvista.domain.DemoData;

public interface LoginView extends IsView {

    interface DevLoginCredentials {

        int getHotKey();

        DemoData.UserType getUserType();

    }

    interface Presenter {

        void login(AuthenticationRequest value);

        void gotoResetPassword();

    }

    void setPresenter(Presenter presenter);

    void enableHumanVerification();

    void reset(String email, boolean rememberUser);

    void setWallMessage(SystemWallMessage systemWallMessage);

    /**
     * @param devLoginData
     *            pass <code>null</code> to disable DevLogin or some relevant dev login to enable dev login.
     * @param appModeName
     *            just a name of application mode, this value is ignored if devLoginData is null
     */
    void setDevLogin(List<? extends DevLoginCredentials> devLoginData, String appModeName);

}