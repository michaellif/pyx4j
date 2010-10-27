/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2010-09-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.BlockingAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.widgets.client.GroupBoxPanel;

public class SessionControlPanel extends GroupBoxPanel {

    final Button btnSessionStart;

    final Button btnSessionEnd;

    SessionControlPanel() {
        super(true);
        setCaption("Server Session Control");

        HorizontalPanel panelBtns = new HorizontalPanel();
        this.add(panelBtns);

        btnSessionStart = new Button("Start Session/Sign In");
        panelBtns.add(btnSessionStart);

        btnSessionStart.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                login();
            }
        });

        btnSessionEnd = new Button("End Session/Sign Out");
        panelBtns.add(btnSessionEnd);

        btnSessionEnd.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ClientContext.logout(new AsyncCallback<AuthenticationResponse>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(AuthenticationResponse result) {
                        setStatus();
                    }
                });
            }
        });

        ClientContext.obtainAuthenticationData(new AsyncCallback<Boolean>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Boolean result) {
                setStatus();
            }
        });
    }

    private void setStatus() {
        boolean on = ClientContext.hasServerSession();
        btnSessionStart.setEnabled(!on);
        btnSessionEnd.setEnabled(on);
    }

    private void login() {
        AsyncCallback<AuthenticationResponse> callback = new BlockingAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
                setStatus();
            }
        };
        AuthenticationRequest request = EntityFactory.create(AuthenticationRequest.class);
        RPCManager.execute(AuthenticationServices.Authenticate.class, request, callback);
    }
}
