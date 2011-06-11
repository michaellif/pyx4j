/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.admin.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.client.BaseLogInPanel;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.util.BrowserType;

import com.propertyvista.common.domain.DemoData;

public abstract class LogInPanel extends BaseLogInPanel {

    private int devCount = 1;

    private int devKey = 0;

    public static final boolean useLoginPopup = false;

    private HandlerRegistration handlerRegistration;

    protected Dialog dialog;

    public static void show() {
        final LogInPanel logInPanel = new LogInPanel() {
            @Override
            public void onLogInComplete() {
                this.dialog.hide();
                if (ClientContext.isAuthenticated()) {
                    AbstractSiteDispatcher.show(SignInCommand.getLogedInHistoryToken());
                }
            }
        };
        logInPanel.setSize("400px", "200px");
        logInPanel.dialog = new Dialog("Sign In", logInPanel);
        logInPanel.dialog.setBody(logInPanel);
        logInPanel.dialog.show();
    }

    public LogInPanel() {
        super();

        rememberID.setVisible(false);
        forgotPassword.setVisible(false);
        //googleLogin.setVisible(true);

        if (ApplicationMode.isDevelopment()) {
            add(new HTML("<br/>This is <B>DEMO</B> Application.<br/>"));
            if (BrowserType.isMobile()) {
                add(new HTML("Press <i>E Enter</i> to login as Employee<br/>Press <i>A Enter</i> to login as Admin"));
            } else {
                add(new HTML("Press <i>Ctrl+E</i> to login as Employee<br/>Press <i>Ctrl+A</i> to login as Admin"));
            }

            if (BrowserType.isMobile()) {
                addDomHandler(new KeyDownHandler() {
                    @Override
                    public void onKeyDown(KeyDownEvent event) {
                        if (!((event.getNativeKeyCode() == KeyCodes.KEY_ENTER) || (event.getNativeKeyCode() == 10))) {
                            return;
                        }
                        String em = ((TextBox) getForm().get(getForm().proto().email()).asWidget()).getValue();
                        if (em.length() != 1) {
                            return;
                        }
                        setDevLoginValues(event.getNativeEvent(), em.toUpperCase().charAt(0));
                    }
                }, KeyDownEvent.getType());
            }

            // Set Default values
            String devLogin = DemoData.CRM_ADMIN_USER_PREFIX + CommonsStringUtils.d000(1) + DemoData.USERS_DOMAIN;
            getForm().get(getForm().proto().email()).setValue(devLogin);
            getForm().get(getForm().proto().password()).setValue(devLogin);
        }

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                if (event.getTypeInt() == Event.ONKEYDOWN && event.getNativeEvent().getCtrlKey()) {
                    setDevLoginValues(event.getNativeEvent(), event.getNativeEvent().getKeyCode());
                }
            }
        });
    }

    private void setDevLoginValues(NativeEvent event, int nativeKeyCode) {
        String devLoginUserPrefix = null;
        int max = 1;
        switch (nativeKeyCode) {
        case 'A':
            devLoginUserPrefix = DemoData.CRM_ADMIN_USER_PREFIX;
            max = DemoData.MAX_ADMIN;
            break;
        case 'E':
            devLoginUserPrefix = DemoData.CRM_PROPERTY_MANAGER_USER_PREFIX;
            max = DemoData.MAX_PROPERTY_MANAGER;
            break;
        case 'O':
            devLoginUserPrefix = DemoData.CRM_CUSTOMER_USER_PREFIX;
            max = DemoData.MAX_CUSTOMERS;
            break;
        }
        if (devLoginUserPrefix != null) {
            if (devKey != nativeKeyCode) {
                devCount = 1;
            } else {
                devCount++;
                if (devCount > max) {
                    devCount = 1;
                }
            }
            devKey = nativeKeyCode;
            String devLogin = devLoginUserPrefix + CommonsStringUtils.d000(devCount) + DemoData.USERS_DOMAIN;
            event.preventDefault();
            getForm().get(getForm().proto().email()).setValue(devLogin);
            getForm().get(getForm().proto().password()).setValue(devLogin);
        }

    }

    @Override
    protected void onUnload() {
        super.onUnload();
        handlerRegistration.removeHandler();
    }

}
