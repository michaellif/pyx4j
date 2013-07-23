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
package com.propertyvista.common.client.ui.components.security;

import com.pyx4j.forms.client.validators.password.PasswordStrengthRule;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.rpc.PasswordResetQuestion;
import com.pyx4j.site.client.IsView;

public interface PasswordResetView extends IsView {

    public interface Presenter {

        void resetPassword(PasswordChangeRequest request);

    }

    public void setPresenter(Presenter presenter);

    public void setQuestion(PasswordResetQuestion question);

    public void setPasswordStrengthRule(PasswordStrengthRule passwordStrengthRule);

    void reset();

}