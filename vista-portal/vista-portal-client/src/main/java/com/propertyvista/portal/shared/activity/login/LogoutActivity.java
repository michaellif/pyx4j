/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.shared.activity.login;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RPCStatusChangeEvent;
import com.pyx4j.rpc.client.RPCStatusChangeHandler;
import com.pyx4j.security.client.ClientContext;

public class LogoutActivity extends AbstractActivity {

    private HandlerRegistration reg;

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        // DO not logout if there are running Requests, e.g. we are saving application.
        if (RPCManager.isRpcIdle()) {
            ClientContext.logout(null);
        } else {
            reg = RPCManager.addRPCStatusChangeHandler(new RPCStatusChangeHandler() {
                @Override
                public void onRPCStatusChange(RPCStatusChangeEvent event) {
                    if (event.isRpcIdle()) {
                        reg.removeHandler();
                        ClientContext.logout(null);
                    }
                }
            });
        }
    }

}
