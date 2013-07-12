/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.security.rpc.AuthenticationService;

import com.propertyvista.common.client.ui.components.login.AbstractPasswordResetRequestActivity;
import com.propertyvista.common.client.ui.components.login.PasswordResetRequestView;
import com.propertyvista.crm.client.ui.viewfactories.SecurityViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;

public class PasswordResetRequestActivity extends AbstractPasswordResetRequestActivity implements PasswordResetRequestView.PasswordResetRequestPresenter {

    public PasswordResetRequestActivity(Place place) {
        super(place, SecurityViewFactory.instance(PasswordResetRequestView.class), GWT.<AuthenticationService> create(CrmAuthenticationService.class),
                new CrmSiteMap.Login());
    }
}
