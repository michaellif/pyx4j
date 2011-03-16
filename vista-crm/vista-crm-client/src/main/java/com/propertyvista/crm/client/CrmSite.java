/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import com.propertyvista.common.client.Message;
import com.propertyvista.common.client.VistaSite;
import com.propertyvista.crm.rpc.SiteMap;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;

public class CrmSite extends VistaSite {

    private CrmGinjector ginjector;

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        ginjector = GWT.create(CrmGinjector.class);
        getHistoryHandler().register(getPlaceController(), getEventBus(), new SiteMap.CreateAccount());

        RootPanel.get().add(ginjector.getSiteView());

        hideLoadingIndicator();

        obtainAuthenticationData();
    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        setMessage(new Message(message, title, buttonText, command));
        //TODO set place to show message
        //ginjector.getPlaceController().goTo(new SiteMap.GenericMessage());
    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                CrmSite.instance().getHistoryHandler().handleCurrentHistory();
            }

            //TODO remove this when initial application message is implemented
            @Override
            public void onFailure(Throwable caught) {
                CrmSite.instance().getHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        });
    }
}
