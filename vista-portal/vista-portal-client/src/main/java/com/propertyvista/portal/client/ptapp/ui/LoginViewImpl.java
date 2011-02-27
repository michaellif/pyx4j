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
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.propertyvista.portal.domain.DemoData;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.security.rpc.AuthenticationRequest;

public class LoginViewImpl extends FlowPanel implements LoginView {

    private static I18n i18n = I18nFactory.getI18n(LoginViewImpl.class);

    private Presenter presenter;

    private final CEntityEditableComponent<AuthenticationRequest> form;

    private int devCount = 1;

    private int devKey = 0;

    private HandlerRegistration handlerRegistration;

    public LoginViewImpl() {

        form = new LoginViewForm();
        form.get(form.proto().captcha()).setVisible(false);
        form.populate(null);
        add(form);

        Button loginButton = new Button("Login");
        loginButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        loginButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.login(form.getValue());
            }

        });

        //                loginButton.getElement().getStyle().setProperty("margin", "0.7em 1m 1em 3em");
        loginButton.getElement().getStyle().setMarginLeft(6.4, Unit.EM);
        loginButton.getElement().getStyle().setMarginRight(1, Unit.EM);
        loginButton.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        add(loginButton);

        CHyperlink forgotPassword = new CHyperlink(null, new Command() {

            @Override
            public void execute() {
                presenter.gotoRetrievePassword();
            }
        });

        forgotPassword.setValue(i18n.tr("Retrieve password"));
        add(forgotPassword);

        if (ApplicationMode.isDevelopment()) {
            add(new HTML("This application is running in <B>DEVELOPMENT</B> mode."));
            add(new HTML("Press <i>Ctrl+Q</i> to login"));
        }

        getElement().getStyle().setMarginTop(1, Unit.EM);
        getElement().getStyle().setMarginBottom(1, Unit.EM);
    }

    @Override
    public void challengeVerificationRequired() {
        form.get(form.proto().captcha()).setVisible(true);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (ApplicationMode.isDevelopment()) {
            handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                @Override
                public void onPreviewNativeEvent(NativePreviewEvent event) {
                    if (event.getTypeInt() == Event.ONKEYDOWN && event.getNativeEvent().getCtrlKey()) {
                        setDevLoginValues(event.getNativeEvent(), event.getNativeEvent().getKeyCode());
                    }
                }
            });
        }
    }

    private void setDevLoginValues(NativeEvent event, int nativeKeyCode) {
        String devLoginUserPrefix = null;
        int max = DemoData.MAX_CUSTOMERS;
        switch (nativeKeyCode) {
        case 'A':
            devLoginUserPrefix = DemoData.CRM_ADMIN_USER_PREFIX;
            break;
        case 'Q':
            devLoginUserPrefix = DemoData.CRM_CUSTOMER_USER_PREFIX;
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
            form.get(form.proto().email()).setValue(devLogin);
            form.get(form.proto().password()).setValue(devLogin);
        }

    }

    @Override
    protected void onUnload() {
        super.onUnload();
        if (ApplicationMode.isDevelopment()) {
            handlerRegistration.removeHandler();
        }
    }

}
