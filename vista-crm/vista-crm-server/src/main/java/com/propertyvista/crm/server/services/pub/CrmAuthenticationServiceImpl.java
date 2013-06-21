/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.pub;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.j2se.CredentialsFileStorage;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.server.common.security.CrmUserBuildingDatasetAccessBuilder;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class CrmAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<CrmUser, CrmUserCredential> implements CrmAuthenticationService {

    public CrmAuthenticationServiceImpl() {
        super(CrmUser.class, CrmUserCredential.class);
    }

    @Override
    protected VistaApplication getVistaApplication() {
        return VistaApplication.crm;
    }

    @Override
    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.CRM;
    }

    @Override
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaBasicBehavior.CRMPasswordChangeRequired;
    }

    @Override
    protected Collection<Behavior> getAccountSetupRequiredBehaviors() {
        return Arrays.asList(new Behavior[] { getPasswordChangeRequiredBehavior(), VistaBasicBehavior.CRMSetupAccountRecoveryOptionsRequired });
    }

    private boolean isAccountRecoveryOptionsConfigured(CrmUserCredential userCredential) {
        return !userCredential.securityQuestion().isNull() & !userCredential.securityAnswer().isNull();
    }

    @Override
    protected boolean checkPassword(CrmUser user, CrmUserCredential credentials, String email, String inputPassword, String encryptedPassword) {
        if (email.equals(CrmUser.VISTA_SUPPORT_ACCOUNT_EMAIL)) {
            AbstractVistaServerSideConfiguration config = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance());
            Credentials storedCredentials = CredentialsFileStorage.getCredentials(new File(config.getConfigDirectory(), "support-credentials.properties"));
            return storedCredentials.password.equals(inputPassword);
        } else {
            return super.checkPassword(user, credentials, email, inputPassword, encryptedPassword);
        }
    }

    @Override
    public String beginSession(CrmUser user, CrmUserCredential userCredential, Set<Behavior> behaviors, IEntity additionalConditions) {
        Set<Behavior> actualBehaviors;
        if (behaviors.contains(VistaBasicBehavior.CRMPasswordChangeRequiresSecurityQuestion) && (!isAccountRecoveryOptionsConfigured(userCredential))) {
            actualBehaviors = new HashSet<Behavior>();
            actualBehaviors.add(getVistaApplication());
            actualBehaviors.add(VistaBasicBehavior.CRMPasswordChangeRequiresSecurityQuestion);
            actualBehaviors.add(VistaBasicBehavior.CRMSetupAccountRecoveryOptionsRequired);
        } else {
            actualBehaviors = behaviors;
        }
        return super.beginSession(user, userCredential, actualBehaviors, additionalConditions);
    }

    @Override
    protected Set<Behavior> getBehaviors(CrmUserCredential userCredential) {
        Set<Behavior> behaviors = ServerSideFactory.create(UserManagementFacade.class).getBehaviors(userCredential);
        if (!userCredential.accessAllBuildings().isBooleanTrue()) {
            CrmUserBuildingDatasetAccessBuilder.updateAccessList(userCredential.user());
        }
        return behaviors;
    }

    @Override
    protected void sendPasswordRetrievalToken(CrmUser user) {
        ServerSideFactory.create(CommunicationFacade.class).sendCrmPasswordRetrievalToken(user);
        Persistence.service().commit();
    }

}
