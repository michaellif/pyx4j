/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.tester.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.propertyvista.common.client.VistaSite;

import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.site.client.AppSite;

public class TesterSite extends VistaSite {

    private TesterGinjector ginjector;

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        ginjector = GWT.create(TesterGinjector.class);
        getHistoryHandler().register(getPlaceController(), getEventBus(), new TesterSiteMap.Forms());

        RootPanel.get().add(RootLayoutPanel.get());

        RootLayoutPanel.get().add(ginjector.getSiteView());

        hideLoadingIndicator();

        SessionInactiveDialog.register();

        AppSite.getPlaceController().goTo(new TesterSiteMap.Forms());

    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        // nothing to do here
    }

}
