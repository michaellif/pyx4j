/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.login;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.TextResource;

public interface LoginAndSignUpResources extends ClientBundleWithLookup {

    LoginAndSignUpResources INSTANCE = GWT.create(LoginAndSignUpResources.class);

    static final String TERMS_AND_AGREEMENTS_ANCHOR_TAG = "TermsAndConditionsAnchor";

    @Source("login-page-terms-agreement-text.html")
    TextResource loginViewTermsAgreementText();

    @Source("signup-page-terms-agreement-text.html")
    TextResource signUpViewTermsAgreementText();

}
