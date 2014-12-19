/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 31, 2012
 * @author ArtyomB
 */
package com.propertyvista.portal.shared.activity.login;

import com.google.gwt.place.shared.Place;

import com.propertyvista.common.client.ui.components.login.AbstractLoginWithTokenActivity;
import com.propertyvista.common.client.ui.components.login.LoginWithTokenView;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.shared.PortalSite;

public class LoginWithTokenActivity extends AbstractLoginWithTokenActivity {

    public LoginWithTokenActivity(Place place) {
        super(place, PortalSite.getViewFactory().getView(LoginWithTokenView.class), new PortalSiteMap.PasswordReset(), new PortalSiteMap.Login());
    }

}
