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
 */
package com.propertyvista.common.client.ui.components.login;

import com.pyx4j.security.rpc.PasswordRetrievalRequest;
import com.pyx4j.site.client.IsView;

public interface PasswordResetRequestView extends IsView {

    public interface PasswordResetRequestPresenter {

        void requestPasswordReset(PasswordRetrievalRequest value);

        void createNewCaptchaChallenge();
    }

    void setPresenter(PasswordResetRequestPresenter presenter);

    void createNewCaptchaChallenge();

    void displayPasswordResetFailedMessage();

    void reset();

    PasswordResetRequestPresenter getPresenter();

}