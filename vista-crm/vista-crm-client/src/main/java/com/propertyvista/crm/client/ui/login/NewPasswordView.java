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
package com.propertyvista.crm.client.ui.login;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.crm.rpc.PasswordChangeRequest;

public interface NewPasswordView extends IsWidget {

    public enum ConversationType {
        RESET, CHANGE
    }

    public void setPresenter(Presenter presenter);

    void setConversationType(ConversationType type);

    public interface Presenter {

        void passwordReset(PasswordChangeRequest request);

        void passwordChange(PasswordChangeRequest request);
    }
}