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
package com.propertyvista.portal.client.ui.residents.login;

import com.google.gwt.place.shared.Place;

import com.propertyvista.common.client.ui.components.login.LoginView;

public interface LandingView extends LoginView {

    interface Presenter extends LoginView.Presenter {

        void signUp();

        void showVistaTerms();

        Class<? extends Place> getPortalTermsPlace();

    }

    /**
     * Resets the view and populates email and rememberMe fields, and disables captcha. Also it clears the signup greeting of the page, so if necessary it
     * should be populated via setSignupGreetingHtml()
     */
    @Override
    void reset(String email, boolean rememberUser);

    // TODO this is not used: a jolly static images from resources are displayed instead  
    @Deprecated
    void setSignupGreetingHtml(String html);

}
