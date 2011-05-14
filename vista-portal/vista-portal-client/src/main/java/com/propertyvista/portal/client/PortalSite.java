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
import com.propertyvista.common.client.Message;
import com.propertyvista.common.client.VistaSite;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.site.client.AppSite;

public class PortalSite extends VistaSite {
    private PortalGinjector ginjector;

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        ginjector = GWT.create(PortalGinjector.class);
        getHistoryHandler().register(getPlaceController(), getEventBus(), new PortalSiteMap.FindApartment());

        RootPanel.get().add(RootLayoutPanel.get());

        RootLayoutPanel.get().add(ginjector.getSiteView());

        hideLoadingIndicator();

        SessionInactiveDialog.register();

        AppSite.getPlaceController().goTo(new PortalSiteMap.FindApartment());

    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        setMessage(new Message(message, title, buttonText, command));
    }

}
