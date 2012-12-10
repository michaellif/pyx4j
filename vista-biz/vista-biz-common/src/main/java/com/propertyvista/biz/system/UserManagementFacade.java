/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.util.Set;

import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.security.AbstractUserCredential;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.server.domain.security.CrmUserCredential;

public interface UserManagementFacade {

    <E extends AbstractUserCredential<? extends AbstractUser>> void selfChangePassword(Class<E> credentialClass, PasswordChangeRequest request);

    <E extends AbstractUserCredential<? extends AbstractUser>> void managedSetPassword(Class<E> credentialClass, PasswordChangeRequest request);

    Set<Behavior> getBehaviors(CrmUserCredential userCredentialId);

    OnboardingUserCredential createOnboardingUser(String firstName, String lastName, String email, String password, VistaOnboardingBehavior role,
            String onboardingAccountId);
}
