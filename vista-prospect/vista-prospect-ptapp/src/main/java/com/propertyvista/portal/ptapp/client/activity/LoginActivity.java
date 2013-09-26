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
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.security.rpc.AuthenticationService;

import com.propertyvista.common.client.ui.components.login.AbstractLoginActivty;
import com.propertyvista.common.client.ui.components.login.LoginView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.services.PtAuthenticationService;

public class LoginActivity extends AbstractLoginActivty implements LoginView.Presenter {

    public LoginActivity(Place place) {
        super(place, PtAppViewFactory.instance(LoginView.class), GWT.<AuthenticationService> create(PtAuthenticationService.class),
                new PtSiteMap.PasswordResetRequest());
    }
}
