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

import com.propertyvista.crm.rpc.dto.account.GlobalLoginResponseDTO;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.AbstractUserCredential;
import com.propertyvista.server.domain.security.CrmUserCredential;

public interface UserManagementFacade {

    <E extends AbstractUserCredential<? extends AbstractUser>> void selfChangePassword(Class<E> credentialClass, PasswordChangeRequest request);

    <E extends AbstractUserCredential<? extends AbstractUser>> void managedSetPassword(Class<E> credentialClass, PasswordChangeRequest request);

    <E extends AbstractUser> void clearSecurityQuestion(Class<? extends AbstractUserCredential<E>> credentialClass, E user);

    Set<Behavior> getBehaviors(CrmUserCredential userCredentialId);

    /**
     * Used in global login
     */
    GlobalLoginResponseDTO globalFindAndVerifyCrmUser(String email, String password);

    void createGlobalCrmUserIndex(CrmUser user);

    void updateGlobalCrmUserIndex(CrmUser user);

}
