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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.rpc.shared.UserRuntimeException;

public class CreateAccountViewImpl extends VerticalPanel implements CreateAccountView {

    private static I18n i18n = I18nFactory.getI18n(CreateAccountViewImpl.class);

    private Presenter presenter;

    private final CEntityEditableComponent<AccountCreationRequest> form;

    private int devCount = 1;

    private int devKey = 0;

    private HandlerRegistration handlerRegistration;

    public CreateAccountViewImpl() {

        Button signinButton = new Button("Signin");
        signinButton.ensureDebugId("Signin");
        signinButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.goToSignin();
            }

        });
        signinButton.getElement().getStyle().setProperty("margin", "3px 20px 3px 8px");
        add(signinButton);

        form = new CreateAccountViewForm();
        form.populate(null);
        add(form);

        Button viewButton = new Button("Let's Start");
        viewButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        viewButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                //TODO validation!

                CCaptcha captcha = ((CCaptcha) form.get(form.proto().captcha()));
                if (captcha.isValueEmpty()) {
                    throw new UserRuntimeException(i18n.tr("Validation failed.") + " " + i18n.tr("Captcha code is required."));
                }
                // Captcha do not have events is Google component. We need to fix this! 
                captcha.retrieveValue();

                //
                //                ValidationResults validationResults = form.getValidationResults();
                //                if (!validationResults.isValid()) {
                //                    MessageDialog.warn(i18n.tr("Validation failed."), validationResults.getMessagesText(false));
                //                    return;
                //                }

                presenter.createAccount(form.getValue());
            }

        });
        viewButton.getElement().getStyle().setProperty("margin", "3px 20px 3px 8px");
        add(viewButton);
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
                    if (event.getTypeInt() == Event.ONKEYDOWN && event.getNativeEvent().getCtrlKey() && event.getNativeEvent().getAltKey()) {
                        setDevLoginValues(event.getNativeEvent(), event.getNativeEvent().getKeyCode());
                    }
                }
            });
        }
    }

    private void setDevLoginValues(NativeEvent event, int nativeKeyCode) {
        String devLoginUserPrefix = null;
        int max = 10;
        switch (nativeKeyCode) {
        case 'A':
            devLoginUserPrefix = DemoData.CRM_ADMIN_USER_PREFIX;
            break;
        case 'T':
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
