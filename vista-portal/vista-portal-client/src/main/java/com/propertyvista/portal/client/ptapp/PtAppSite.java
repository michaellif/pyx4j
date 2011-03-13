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
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import com.propertyvista.common.client.Message;
import com.propertyvista.common.client.VistaSite;
import com.propertyvista.portal.rpc.pt.SiteMap;

public class PtAppSite extends VistaSite {

    private SiteGinjector ginjector;

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        ginjector = GWT.create(SiteGinjector.class);

        RootPanel.get().add(ginjector.getSiteView());

        hideLoadingIndicator();

        PtAppWizardManager.initWizard(ginjector);

    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        setMessage(new Message(message, title, buttonText, command));
        ginjector.getPlaceController().goTo(new SiteMap.GenericMessage());
    }

}
