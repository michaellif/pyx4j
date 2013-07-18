/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.security;

import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.validators.password.PasswordStrengthRule;
import com.pyx4j.security.rpc.PasswordChangeRequest;

public interface PasswordChangeView extends IsWidget {

    public interface Presenter {

        public static final String PRINCIPAL_PK_ARG = "id";

        public static final String PRINCIPAL_NAME_ARG = "username";

        public static final String PRINCIPAL_CLASS = "class";

        enum PrincipalClass {
            TENANT, GUARANTOR, EMPLOYEE, ADMIN
        }

        void changePassword(PasswordChangeRequest request);

        void cancel();
    }

    public void setPresenter(Presenter presenter);

    void reset();

    /**
     * @param userName
     *            can be <code>null</code>, if it has to become invisible
     */
    public void initialize(Key userPk, String userName);

    public PasswordChangeRequest getValue();

    void setAskForCurrentPassword(boolean isCurrentPasswordRequired);

    /**
     * @param maskPassword
     *            enables masking of the password and password confirm field
     */
    void setMaskPassword(boolean maskPassword);

    /**
     * Display UI that allows to set up require to change password flag.
     * 
     * @param isRequireChangePasswordOnNextSignInRequired
     *            display the UI Display UI that allows to set up require to change password flag
     * @param requireChangePasswordOnNextSignIn
     *            use this to set the value of the flag or pass <code>null</code> to leave this value unchanged
     * @param enforcedStrenthThreshold
     *            when not <code>null</code>, the password strength that less or equal this value will force the 'Require to change password on next log in
     *            flag'
     */
    void setAskForRequireChangePasswordOnNextSignIn(boolean isRequireChangePasswordOnNextSignInRequired, Boolean requireChangePasswordOnNextSignIn,
            PasswordStrengthRule.PasswordStrengthVerdict enforcedStrenthThreshold);

    void setPasswordStrengthRule(PasswordStrengthRule passwordStrengthRule);

    /**
     * @param validPasswordStrengths
     *            set this value to require validation of password by strength, pass the
     */
    void setEnforcedPasswordStrengths(Set<PasswordStrengthRule.PasswordStrengthVerdict> validPasswordStrengths);

}
