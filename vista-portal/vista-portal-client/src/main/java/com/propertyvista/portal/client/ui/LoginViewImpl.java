/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.AuthenticationRequest;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.PreloadConfig;

public class LoginViewImpl extends SimplePanel implements LoginView {
    private Presenter presenter;

    private static I18n i18n = I18nFactory.getI18n(LoginViewImpl.class);

    private final CEntityForm<AuthenticationRequest> form;

    private HandlerRegistration handlerRegistration;

    private int devCount = 1;

    private int devKey = 0;

    private PreloadConfig config = PreloadConfig.createDefault();

    public LoginViewImpl() {

        FlowPanel panel = new FlowPanel();
        HTML label = new HTML("This form should allow submitting authentification request via https<br>");
        panel.add(label);

        form = new LoginViewForm();
        form.initialize();
        form.get(form.proto().captcha()).setVisible(false);
        form.populate(null);
        panel.add(form);

        Button loginButton = new Button("Sign In");
        loginButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        loginButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                submit();
            }

        });

        loginButton.getElement().getStyle().setMarginLeft(90, Unit.PX);
        loginButton.getElement().getStyle().setMarginRight(1, Unit.EM);
        loginButton.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        panel.add(loginButton);

        CHyperlink forgotPassword = new CHyperlink(null, new Command() {

            @Override
            public void execute() {
                presenter.gotoRetrievePassword();
            }
        });

        forgotPassword.setValue(i18n.tr("Retrieve password"));
        panel.add(forgotPassword);

        if (ApplicationMode.isDevelopment()) {
            panel.add(new HTML("This application is running in <B>DEVELOPMENT</B> mode."));
            panel.add(new HTML("Press <i>Ctrl+Q</i> to login"));
        }

        getElement().getStyle().setMarginTop(1, Unit.EM);
        getElement().getStyle().setMarginBottom(1, Unit.EM);

        setWidget(panel);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void challengeVerificationRequired() {
        form.get(form.proto().captcha()).setVisible(true);

    }

    private void submit() {
        form.setVisited(true);
        if (!form.isValid()) {
            throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
        }
        presenter.login(form.getValue());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                if ((ApplicationMode.isDevelopment()) && (event.getTypeInt() == Event.ONKEYDOWN && event.getNativeEvent().getCtrlKey())) {
                    setDevLoginValues(event.getNativeEvent(), event.getNativeEvent().getKeyCode());
                }
                if (event.getTypeInt() == Event.ONKEYUP && (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)) {
                    submit();
                }
            }
        });
    }

    private void setDevLoginValues(NativeEvent event, int nativeKeyCode) {
        String devLoginUserPrefix = null;
        int max = config.getMaxCustomers();
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
        handlerRegistration.removeHandler();
        devCount = 1;
    }

}
