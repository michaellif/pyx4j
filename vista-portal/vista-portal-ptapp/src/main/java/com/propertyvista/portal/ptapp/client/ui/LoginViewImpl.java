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
package com.propertyvista.portal.ptapp.client.ui;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.AuthenticationRequest;

import com.propertyvista.common.client.ui.components.login.LoginForm;
import com.propertyvista.domain.DemoData;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;

public class LoginViewImpl extends FlowPanel implements LoginView {

    private static I18n i18n = I18n.get(LoginViewImpl.class);

    private Presenter presenter;

    private final CEntityEditor<AuthenticationRequest> form;

    private int devCount = 1;

    private int devKey = 0;

    private HandlerRegistration handlerRegistration;

    public LoginViewImpl() {

        FlowPanel leftColumn = new FlowPanel();
        leftColumn.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        add(leftColumn);

        HTML requirements = new HTML(PortalResources.INSTANCE.requirements().getText());
        requirements.getElement().getStyle().setPaddingLeft(95, Unit.PX);
        requirements.getElement().getStyle().setPaddingBottom(45, Unit.PX);

        requirements.getElement().getStyle().setProperty("background", "url(" + PortalImages.INSTANCE.requirements().getURL() + ") no-repeat");
        leftColumn.add(requirements);

        HTML time = new HTML(PortalResources.INSTANCE.time().getText());
        time.getElement().getStyle().setPaddingLeft(95, Unit.PX);
        time.getElement().getStyle().setPaddingBottom(45, Unit.PX);

        time.getElement().getStyle().setProperty("background", "url(" + PortalImages.INSTANCE.time().getURL() + ") no-repeat");
        leftColumn.add(time);

        HTML dontWorry = new HTML(PortalResources.INSTANCE.dontWorry().getText());
        dontWorry.getElement().getStyle().setPaddingLeft(95, Unit.PX);
        dontWorry.getElement().getStyle().setPaddingBottom(45, Unit.PX);

        dontWorry.getElement().getStyle().setProperty("background", "url(" + PortalImages.INSTANCE.dontWorry().getURL() + ") no-repeat");
        leftColumn.add(dontWorry);

        leftColumn.setWidth("45%");

        FlowPanel rightColumn = new FlowPanel();
        rightColumn.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        rightColumn.getElement().getStyle().setMarginLeft(5, Unit.PCT);
        add(rightColumn);

        form = new LoginForm(i18n.tr("Login"), new Command() {
            @Override
            public void execute() {
                submit();
            }
        }, new Command() {

            @Override
            public void execute() {
                presenter.gotoRetrievePassword();
            }
        }) {
            @Override
            protected void onDevLogin(NativeEvent event, int nativeKeyCode) {
                setDevLoginValues(event, nativeKeyCode);
            }
        };
        form.initContent();
        form.get(form.proto().captcha()).setVisible(false);
        form.populate(null);

        rightColumn.add(form);
    }

    @Override
    public void challengeVerificationRequired() {
        form.get(form.proto().captcha()).setVisible(true);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
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
        DemoData.UserType type = null;
        switch (nativeKeyCode) {
        case 'Q':
            type = DemoData.UserType.PTENANT;
            break;
        }
        if (type != null) {
            if (devKey != nativeKeyCode) {
                devCount = 1;
            } else {
                devCount++;
                if (devCount > type.getDefaultMax()) {
                    devCount = 1;
                }
            }
            devKey = nativeKeyCode;
            String devLogin = type.getEmail(devCount);
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
