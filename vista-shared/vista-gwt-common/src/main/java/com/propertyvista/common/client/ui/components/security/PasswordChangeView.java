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

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
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

    void setAskForRequireChangePasswordOnNextSignIn(boolean isRequireChangePasswordOnNextSignInRequired);

    void setDictionary(List<String> dictionary);
}
