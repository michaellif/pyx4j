/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.ui.prime.form.IViewer;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.domain.security.CrmUser;

public interface EmployeeViewerView extends IViewer<EmployeeDTO> {

    interface Presenter extends IViewer.Presenter {

        void goToChangePassword(Key userId, String userName);

        void goToLoginHistory(CrmUser userStub);

        boolean canGoToAccountRecoveryOptions();

        void goToAccountRecoveryOptions(String password);

        boolean canClearSecurityQuestion();

        void clearSecurityQuestionAction(DefaultAsyncCallback<VoidSerializable> asyncCallback, EmployeeDTO employeeId);

        boolean canSendPasswordResetEmail();

        void sendPasswordResetEmailAction(DefaultAsyncCallback<VoidSerializable> defaultAsyncCallback, EmployeeDTO employeeId);

    }

    void restrictSecuritySensitiveControls(boolean isManager, boolean isSelfEditor);
}
