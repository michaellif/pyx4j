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
package com.propertyvista.field.client.activity.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.security.rpc.AuthenticationService;

import com.propertyvista.common.client.ui.components.login.AbstractLoginActivty;
import com.propertyvista.common.client.ui.components.login.LoginView;
import com.propertyvista.field.client.ui.viewfactories.LoginViewFactory;
import com.propertyvista.field.rpc.FieldSiteMap;
import com.propertyvista.field.rpc.services.FieldAuthenticationService;

public class LoginActivity extends AbstractLoginActivty implements LoginView.Presenter {

    public LoginActivity(Place place) {
        super(place, LoginViewFactory.instance(LoginView.class), GWT.<AuthenticationService> create(FieldAuthenticationService.class),
                new FieldSiteMap.PasswordResetRequest());
    }
}
