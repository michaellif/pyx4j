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
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.landing;

import java.util.List;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.SystemWallMessage;

import com.propertyvista.common.client.ui.components.login.LoginView;

public class LandingViewImpl extends FlowPanel implements LandingView {

    static final I18n i18n = I18n.get(LandingViewImpl.class);

    private final LoginGadget loginGadget;

    private final SignUpGadget signUpGadget;

    public LandingViewImpl() {

        loginGadget = new LoginGadget(this);
        loginGadget.asWidget().setWidth("45%");

        HTML orLabel = new HTML("OR");
        orLabel.getElement().getStyle().setMarginTop(100, Unit.PX);
        orLabel.getElement().getStyle().setFloat(Float.LEFT);

        signUpGadget = new SignUpGadget(this);
        signUpGadget.asWidget().setWidth("45%");

        add(loginGadget);
        add(orLabel);
        add(signUpGadget);

    }

    @Override
    public void setPresenter(LoginView.Presenter presenter) {
        loginGadget.setPresenter((LandingPresenter) presenter);
        signUpGadget.setPresenter((LandingPresenter) presenter);
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
}
