/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.landing;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;

import com.propertyvista.portal.web.client.ui.AbstractWizardView;

public class PasswordResetRequestWizardViewImpl extends AbstractWizardView<PasswordRetrievalRequest> implements PasswordResetRequestWizardView {

    static final I18n i18n = I18n.get(PasswordResetRequestWizardViewImpl.class);

    public PasswordResetRequestWizardViewImpl() {
        setWizard(new PasswordResetRequestWizard(this));
    }

    @Override
    public void createNewCaptchaChallenge() {
        ((PasswordResetRequestWizard) getWizard()).createNewCaptchaChallenge();
    }

}
