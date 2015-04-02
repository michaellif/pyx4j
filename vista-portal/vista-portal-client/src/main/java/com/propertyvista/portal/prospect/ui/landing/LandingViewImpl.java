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
 */
package com.propertyvista.portal.prospect.ui.landing;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.login.LoginView;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.shared.themes.DashboardTheme;

public class LandingViewImpl extends FlowPanel implements LandingView {

    static final I18n i18n = I18n.get(LandingViewImpl.class);

    private final LoginGadget loginGadget;

    private final SignUpGadget signUpGadget;

    private final SimplePanel orHolder;

    private final HTML orLabel;

    public LandingViewImpl() {

        setStyleName(DashboardTheme.StyleName.LandingPage.name());

        signUpGadget = new SignUpGadget(this);
        signUpGadget.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        signUpGadget.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        orLabel = new HTML("OR");
        orLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        orLabel.getElement().getStyle().setLineHeight(50, Unit.PX);
        orLabel.setPixelSize(50, 50);
        orLabel.getElement().getStyle().setFontSize(1.5, Unit.EM);
        orLabel.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
        orLabel.getElement().getStyle().setColor("white");
        orLabel.getElement().getStyle().setBackgroundColor("#999");
        orLabel.getElement().getStyle().setProperty("borderRadius", "50%");
        orLabel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        orHolder = new SimplePanel(orLabel);
        orHolder.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        orHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        orHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        loginGadget = new LoginGadget(this);
        loginGadget.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        loginGadget.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        add(signUpGadget);
        add(orHolder);
        add(loginGadget);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });

    }

    private void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            loginGadget.setWidth("100%");
            orHolder.setWidth("100%");
            signUpGadget.setWidth("100%");
            break;
        case tabletLandscape:
        case monitor:
        case huge:
            loginGadget.asWidget().setWidth("46%");
            orHolder.asWidget().setWidth("8%");
            signUpGadget.asWidget().setWidth("46%");
            break;
        }

    }

    @Override
    public void setPresenter(LoginView.Presenter presenter) {
        loginGadget.setPresenter((LandingPresenter) presenter);
        signUpGadget.setPresenter((LandingPresenter) presenter);

        setSignUpGadgetVisible(Window.Location.getParameter(ProspectPortalSiteMap.ARG_ILS_BUILDING_ID) != null);
    }

    @Override
    public void setDevLogin(List<? extends DevLoginCredentials> devCredientials, String appModeName) {
        loginGadget.setDevLogin(devCredientials, appModeName);
    }

    public void setTermsAndConditions(Class<? extends Place> place) {
        loginGadget.setTermsAndConditions(place);
    }

    @Override
    public void enableHumanVerification() {
        loginGadget.enableHumanVerification();
    }

    @Override
    public void setWallMessage(SystemWallMessage systemWallMessage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset(String email, boolean rememberUser) {
        loginGadget.reset(email, rememberUser);
    }

    private void setSignUpGadgetVisible(boolean visible) {
        if (visible) {
            signUpGadget.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            orLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        } else {
            signUpGadget.getElement().getStyle().setDisplay(Display.NONE);
            orLabel.getElement().getStyle().setDisplay(Display.NONE);
        }
    }
}
