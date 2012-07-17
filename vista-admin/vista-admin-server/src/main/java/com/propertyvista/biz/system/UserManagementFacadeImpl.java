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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.security.AbstractUserCredential;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.common.security.VistaContext;
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

        if (credentialClass.equals(OnboardingUserCredential.class)) {
            OnboardingUserCredential cr = (OnboardingUserCredential) credential;

            if (cr.pmc().getPrimaryKey() != null) {
                Pmc pmc = cr.pmc();

                if (pmc.status().getValue() != PmcStatus.Created) {
                    String curNameSpace = NamespaceManager.getNamespace();

                    try {
                        NamespaceManager.setNamespace(pmc.namespace().getValue());

                        EntityQueryCriteria<CrmUserCredential> crmUCrt = EntityQueryCriteria.create(CrmUserCredential.class);
                        crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().roles().$().behaviors(), VistaCrmBehavior.PropertyVistaAccountOwner));
                        crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().onboardingUser(), cr.getPrimaryKey()));
                        CrmUserCredential crmCredential = Persistence.service().retrieve(crmUCrt);

                        if (crmCredential == null) {
                            throw new UserRuntimeException(i18n.tr("Invalid User Account. Please Contact Support"));
                        }
                        if (!crmCredential.enabled().isBooleanTrue()) {
                            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                        }

                        crmCredential.accessKey().setValue(null);
                        crmCredential.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
                        crmCredential.credentialUpdated().setValue(new Date());
                        crmCredential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);
                        Persistence.service().persist(crmCredential);
                        Persistence.service().commit();

                    } finally {
                        NamespaceManager.setNamespace(curNameSpace);
                    }
                }
            }
        }

        credential.accessKey().setValue(null);
        credential.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        credential.credentialUpdated().setValue(new Date());
        credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);
        Persistence.service().persist(credential);
        Persistence.service().commit();

        log.info("password changed by user {} {}", Context.getVisit().getUserVisit().getEmail(), VistaContext.getCurrentUserPrimaryKey());
    }

    @Override
    public <E extends AbstractUserCredential<? extends AbstractUser>> void managedSetPassword(Class<E> credentialClass, PasswordChangeRequest request) {

        E credential = Persistence.service().retrieve(credentialClass, request.userPk().getValue());

        if (credentialClass.equals(OnboardingUserCredential.class)) {
            OnboardingUserCredential cr = (OnboardingUserCredential) credential;

            if (cr.pmc().getPrimaryKey() != null) {
                Pmc pmc = cr.pmc();

                if (pmc.status().getValue() != PmcStatus.Created) {
                    String curNameSpace = NamespaceManager.getNamespace();

                    try {
                        NamespaceManager.setNamespace(pmc.namespace().getValue());

                        EntityQueryCriteria<CrmUserCredential> crmUCrt = EntityQueryCriteria.create(CrmUserCredential.class);
                        crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().roles().$().behaviors(), VistaCrmBehavior.PropertyVistaAccountOwner));
                        crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().onboardingUser(), cr.getPrimaryKey()));
                        CrmUserCredential crmCredential = Persistence.service().retrieve(crmUCrt);

                        crmCredential.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
                        if (request.requireChangePasswordOnNextSignIn().isBooleanTrue()) {
                            crmCredential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.TRUE);
                        }

                        Persistence.service().persist(crmCredential);
                        Persistence.service().commit();

                    } finally {
                        NamespaceManager.setNamespace(curNameSpace);
                    }
                }
            }
        }

        credential.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        if (request.requireChangePasswordOnNextSignIn().isBooleanTrue()) {
            credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.TRUE);
        }
        Persistence.service().persist(credential);
        Persistence.service().commit();

        log.info("password changed by user {} for {}", Context.getVisit().getUserVisit().getEmail(), request.userPk());
    }

}
