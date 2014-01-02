/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.crm.rpc.dto.account.GlobalLoginResponseDTO;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.VistaDataAccessBehavior;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.AbstractUserCredential;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.server.common.security.VistaPasswordResetServiceImpl;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class UserManagementFacadeImpl implements UserManagementFacade {

    private static Logger log = LoggerFactory.getLogger(UserManagementFacadeImpl.class);

    private static final I18n i18n = I18n.get(VistaPasswordResetServiceImpl.class);

    @Override
    public <E extends AbstractUserCredential<? extends AbstractUser>> void selfChangePassword(Class<E> credentialClass, PasswordChangeRequest request) {
        E credential = Persistence.service().retrieve(credentialClass, request.userPk().getValue());

        if (credential == null) {
            throw new UserRuntimeException(i18n.tr("Invalid User Account. Please Contact Support"));
        }
        if (!credential.enabled().isBooleanTrue()) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }

        credential.accessKey().setValue(null);
        credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(request.newPassword().getValue()));
        credential.passwordUpdated().setValue(new Date());
        credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);
        Persistence.service().persist(credential);
        Persistence.service().commit();

        log.info("password changed by user {} {}", Context.getVisit().getUserVisit().getEmail(), VistaContext.getCurrentUserPrimaryKey());
    }

    @Override
    public <E extends AbstractUserCredential<? extends AbstractUser>> void managedSetPassword(Class<E> credentialClass, PasswordChangeRequest request) {
        E credential = Persistence.service().retrieve(credentialClass, request.userPk().getValue());
        credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(request.newPassword().getValue()));
        if (request.requireChangePasswordOnNextSignIn().isBooleanTrue()) {
            credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.TRUE);
        }
        Persistence.service().persist(credential);
        Persistence.service().commit();

        log.info("password changed by user {} for {}", Context.getVisit().getUserVisit().getEmail(), request.userPk());
    }

    @Override
    public Set<Behavior> getBehaviors(CrmUserCredential userCredentialId) {
        CrmUserCredential credential = Persistence.service().retrieve(CrmUserCredential.class, userCredentialId.getPrimaryKey());
        Set<Behavior> behaviors = new HashSet<Behavior>();
        addAllBehaviors(behaviors, credential.roles(), new HashSet<CrmRole>());

        if (credential.accessAllBuildings().isBooleanTrue()) {
            behaviors.add(VistaDataAccessBehavior.BuildingsAll);
        } else {
            behaviors.add(VistaDataAccessBehavior.BuildingsAssigned);
        }

        return behaviors;
    }

    private void addAllBehaviors(Set<Behavior> behaviors, Collection<CrmRole> roles, Set<CrmRole> processed) {
        for (CrmRole role : roles) {
            if (!processed.contains(role)) {
                Persistence.service().retrieve(role);
                processed.add(role);
                behaviors.addAll(role.behaviors());
                addAllBehaviors(behaviors, role.roles(), processed);
            }
            if (role.requireSecurityQuestionForPasswordReset().isBooleanTrue()) {
                behaviors.add(VistaBasicBehavior.CRMPasswordChangeRequiresSecurityQuestion);
            }
        }
    }

    @Override
    public <E extends AbstractUser> void clearSecurityQuestion(Class<? extends AbstractUserCredential<E>> credentialClass, E user) {
        AbstractUserCredential<?> credential = Persistence.service().retrieve(credentialClass, user.getPrimaryKey());
        credential.securityAnswer().setValue(null);
        credential.securityQuestion().setValue(null);
        Persistence.service().persist(credential);
    }

    @Override
    public GlobalLoginResponseDTO globalFindAndVerifyCrmUser(String email, String password) {
        GlobalLoginResponseDTO dto = new GlobalLoginManager().findAndVerifyCrmUser(email, password);
        if (dto == null) {
            dto = new GlobalLoginManager().findAndVerifyOprationsUser(email, password);
        }
        return dto;
    }

    @Override
    public void createGlobalCrmUserIndex(CrmUser user) {
        new GlobalLoginManager().createGlobalCrmUserIndex(user);
    }

    @Override
    public void updateGlobalCrmUserIndex(CrmUser user) {
        new GlobalLoginManager().updateGlobalCrmUserIndex(user);
    }

}
