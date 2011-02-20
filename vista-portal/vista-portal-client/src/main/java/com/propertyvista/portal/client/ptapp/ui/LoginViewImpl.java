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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.domain.DemoData;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;

public class LoginViewImpl extends FlowPanel implements LoginView {

    private static I18n i18n = I18nFactory.getI18n(LoginViewImpl.class);

    private Presenter presenter;

    private final CEntityEditableComponent<AuthenticationRequest> form;

    private int devCount = 1;

    private int devKey = 0;

    private HandlerRegistration handlerRegistration;

    public LoginViewImpl() {

        form = new LoginViewForm() {

            @Override
            public void forgotPassword() {
                presenter.forgotPassword();
            }
        };

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
        loginButton.getElement().getStyle().setProperty("margin", "0.7em 4em 1em 0");
        add(loginButton);

        VerticalPanel startPanel = new VerticalPanel();
        startPanel.add(loginButton);
        startPanel.setCellHorizontalAlignment(loginButton, HasHorizontalAlignment.ALIGN_RIGHT);

        if (ApplicationMode.isDevelopment()) {
            startPanel.add(new HTML("This application is running in <B>DEVELOPMENT</B> mode."));
            startPanel.add(new HTML("Press <i>Ctrl+Q</i> to login"));
        }

        startPanel.setWidth("100%");
        add(startPanel);

        setWidth("30%");

        getElement().getStyle().setMarginLeft(5, Unit.PCT);
        getElement().getStyle().setMarginRight(5, Unit.PCT);
        getElement().getStyle().setMarginTop(15, Unit.PX);
        getElement().getStyle().setMarginBottom(15, Unit.PX);
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
        int max = DemoData.maxCustomers;
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
