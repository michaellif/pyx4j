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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.resources.SiteResources;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.rpc.shared.UserRuntimeException;

public class CreateAccountViewImpl extends FlowPanel implements CreateAccountView {

    private static I18n i18n = I18nFactory.getI18n(CreateAccountViewImpl.class);

    private Presenter presenter;

    private final CEntityEditableComponent<AccountCreationRequest> form;

    private int devCount = 1;

    private int devKey = 0;

    private HandlerRegistration handlerRegistration;

    public CreateAccountViewImpl() {

        FlowPanel leftColumn = new FlowPanel();
        add(leftColumn);

        HTML requirements = new HTML(SiteResources.INSTANCE.requirements().getText());
        requirements.getElement().getStyle().setPaddingLeft(95, Unit.PX);
        requirements.getElement().getStyle().setPaddingBottom(45, Unit.PX);

        requirements.getElement().getStyle().setProperty("background", "url(" + SiteImages.INSTANCE.requirements().getURL() + ") no-repeat");
        leftColumn.add(requirements);

        FlowPanel rightColumn = new FlowPanel();
        add(rightColumn);

        form = new CreateAccountViewForm();
        form.populate(null);
        rightColumn.add(form);

        Button viewButton = new Button(i18n.tr("Let's Start"));
        viewButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        viewButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                if (!form.isValid()) {
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                }

                CCaptcha captcha = ((CCaptcha) form.get(form.proto().captcha()));
                // Captcha do not have events is Google component. We need to fix this! 
                captcha.retrieveValue();

                presenter.createAccount(form.getValue());
            }

        });
        viewButton.getElement().getStyle().setProperty("margin", "0.5em 6em 1em 0");
        rightColumn.add(viewButton);

        if (ApplicationMode.isDevelopment()) {
            rightColumn.add(new HTML("<br/>This application is running in <B>DEVELOPMENT</B> mode.<br/>"));
            rightColumn.add(new HTML("Press <i>Ctrl+Q</i> to create new user"));
        }

        Button signinButton = new Button(i18n.tr("Signin"));
        signinButton.ensureDebugId("Signin");
        signinButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.goToSignin();
            }

        });
        signinButton.getElement().getStyle().setProperty("margin", "3px 20px 3px 8px");
        rightColumn.add(signinButton);

        getElement().getStyle().setMarginLeft(5, Unit.PCT);
        getElement().getStyle().setMarginRight(5, Unit.PCT);
        getElement().getStyle().setMarginTop(15, Unit.PX);
        getElement().getStyle().setMarginBottom(15, Unit.PX);

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
        int max = 100;
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
            String devLogin = devLoginUserPrefix + CommonsStringUtils.d00(devCount) + DemoData.USERS_DOMAIN;
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
