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
 * Created on Jan 13, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.examples.site.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.essentials.client.BaseLogInPanel;
import com.pyx4j.essentials.client.GoogleAccountsLoginPopup;
import com.pyx4j.examples.domain.DemoData;
import com.pyx4j.examples.domain.ExamplesBehavior;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.widgets.client.dialog.Dialog;

public abstract class LogInPanel extends BaseLogInPanel {

    private int devCount = 1;

    private int devKey = 0;

    protected Dialog dialog;

    public static final boolean useLoginPopup = true;

    public static void asyncShow() {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onSuccess() {
                show();
            }

            @Override
            public void onFailure(Throwable reason) {
                throw new UnrecoverableClientError(reason);
            }
        });
    }

    public static void show() {
        final LogInPanel logInPanel = new LogInPanel() {
            @Override
            public void onLogInComplete() {
                this.dialog.hide();
                if (ClientContext.isAuthenticated()) {
                    AbstractSiteDispatcher.show(ExamplesSiteMap.Crm.Customers.class);
                }
            }

        };
        logInPanel.setSize("400px", "200px");
        logInPanel.dialog = new Dialog("Sign In", logInPanel);
        logInPanel.dialog.setBody(logInPanel);
        logInPanel.dialog.show();
    }

    public LogInPanel() {

        rememberID.setVisible(false);
        forgotPassword.setValue("Login using Google Accounts");

        if (ExamplesBehavior.development) {

            add(new HTML("<br/>This is <B>DEMO</B> Application.<br/>Press <i>Ctrl+E</i> to login as Employee<br/>Press <i>Ctrl+A</i> to login as Admin"));

            addDomHandler(new KeyDownHandler() {
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    if (!event.isControlKeyDown()) {
                        return;
                    }

                    String devLoginUserPrefix = null;
                    int max = 1;
                    switch (event.getNativeKeyCode()) {
                    case 'A':
                        devLoginUserPrefix = DemoData.CRM_ADMIN_USER_PREFIX;
                        max = 1;
                        break;
                    case 'E':
                        devLoginUserPrefix = DemoData.CRM_EMPLOYEE_USER_PREFIX;
                        max = DemoData.maxEmployee;
                        break;
                    case 'O':
                        devLoginUserPrefix = DemoData.CRM_CUSTOMER_USER_PREFIX;
                        max = DemoData.maxCustomers;
                        break;
                    }
                    if (devLoginUserPrefix != null) {
                        if (devKey != event.getNativeKeyCode()) {
                            devCount = 1;
                        } else {
                            devCount++;
                            if (devCount > max) {
                                devCount = 1;
                            }
                        }
                        devKey = event.getNativeKeyCode();
                        String devLogin = devLoginUserPrefix + CommonsStringUtils.d000(devCount) + DemoData.USERS_DOMAIN;
                        event.preventDefault();
                        getForm().get(getForm().meta().email()).setValue(devLogin);
                        getForm().get(getForm().meta().password()).setValue(devLogin);
                    }

                }
            }, KeyDownEvent.getType());
        }

    }

    @Override
    public void onForgotPasswordRequest() {
        if (useLoginPopup) {
            GoogleAccountsLoginPopup.open();
        } else {
            ClientContext.googleAccountsLogin(ExamplesSiteDispatcher.getLogedInURL());
        }
    }

}