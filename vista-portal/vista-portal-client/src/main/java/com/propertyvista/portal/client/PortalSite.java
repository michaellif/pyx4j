/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.gwt.geo.GoogleAPI;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.Message;
import com.propertyvista.common.client.VistaSite;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class PortalSite extends VistaSite {
    private PortalGinjector ginjector;

    public PortalSite() {
        super(PortalSiteMap.class);
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        GoogleAPI.setGoogleAPIKey("ABQIAAAAZuLUizjWCGkAYOfiIpZpgxT2nw7IAgYZCN3UZ-Glm95U7gTjpRTVD1pxXeXBpUR-ZQ5Z0YCQkesTkg");

        ginjector = GWT.create(PortalGinjector.class);
        AppPlace defaultplace = new PortalSiteMap.FindApartment();
        getHistoryHandler().register(getPlaceController(), getEventBus(), defaultplace);

        RootPanel.get().add(RootLayoutPanel.get());

        RootLayoutPanel.get().add(ginjector.getSiteView());

        hideLoadingIndicator();

        SessionInactiveDialog.register();

        AppSite.getPlaceController().goTo(defaultplace);

    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        setMessage(new Message(message, title, buttonText, command));
    }

}
