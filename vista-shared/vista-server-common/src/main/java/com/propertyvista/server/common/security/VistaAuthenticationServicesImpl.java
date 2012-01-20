/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.IgnoreSessionToken;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.UserVisit;

import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.server.domain.security.AbstractUserCredential;

public abstract class VistaAuthenticationServicesImpl<U extends AbstractUser, E extends AbstractUserCredential<U>> extends
        com.pyx4j.security.server.AuthenticationServiceImpl {

    private final static Logger log = LoggerFactory.getLogger(VistaAuthenticationServicesImpl.class);

    private static final I18n i18n = I18n.get(VistaAuthenticationServicesImpl.class);

    protected final Class<U> userClass;

    protected final Class<E> credentialClass;

    protected VistaAuthenticationServicesImpl(Class<U> userClass, Class<E> credentialClass) {
        this.userClass = userClass;
        this.credentialClass = credentialClass;
    }

    protected abstract VistaBasicBehavior getApplicationBehavior();

    @Override
    @IgnoreSessionToken
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String sessionToken) {
        if (!SecurityController.checkBehavior(getApplicationBehavior())) {
            VistaLifecycle.endSession();
        }
        super.authenticate(callback, clientSystemInfo, sessionToken);
    }

    @Override
    @IgnoreSessionToken
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, AuthenticationRequest request) {
        assertClientSystemInfo(clientSystemInfo);
        // Try to begin Session
        String sessionToken = beginSession(request);
        if (!SecurityController.checkBehavior(getApplicationBehavior())) {
            VistaLifecycle.endSession();
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        callback.onSuccess(createAuthenticationResponse(sessionToken));
    }

    public String beginSession(AuthenticationRequest request) {
        switch (SystemMaintenance.getState()) {
        case Unavailable:
            throw new UserRuntimeException(SystemMaintenance.getApplicationMaintenanceMessage());
        }

        if (CommonsStringUtils.isEmpty(request.email().getValue()) || CommonsStringUtils.isEmpty(request.password().getValue())) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        String email = request.email().getValue().toLowerCase(Locale.ENGLISH).trim();
        AbstractAntiBot.assertLogin(email, request.captcha().getValue());

        EntityQueryCriteria<U> criteria = new EntityQueryCriteria<U>(userClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<U> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            if (AbstractAntiBot.authenticationFailed(email)) {
                throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
            } else {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
        }
        AbstractUser user = users.get(0);

        E cr = Persistence.service().retrieve(credentialClass, user.getPrimaryKey());
        if (cr == null) {
            throw new UserRuntimeException(i18n.tr("Invalid User Account. Please Contact Support"));
        }
        if (!cr.enabled().isBooleanTrue()) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        if (!PasswordEncryptor.checkPassword(request.password().getValue(), cr.credential().getValue())) {
            log.info("Invalid password for user {}", email);
            if (AbstractAntiBot.authenticationFailed(email)) {
                throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
            } else {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
        }
        return beginSession(user, cr);
    }

    public String beginSession(AbstractUser user, E userCredential) {
        Set<Behavior> behaviors = new HashSet<Behavior>();

        behaviors.add(getApplicationBehavior());
        addBehaviors(userCredential, behaviors);

        UserVisit visit = new UserVisit(user.getPrimaryKey(), user.name().getValue());
        visit.setEmail(user.email().getValue());
        return VistaLifecycle.beginSession(visit, behaviors);
    }

    protected abstract void addBehaviors(E userCredential, Set<Behavior> behaviors);

    @Override
    @IgnoreSessionToken
    public void logout(AsyncCallback<AuthenticationResponse> callback) {
        VistaLifecycle.endSession();
        callback.onSuccess(createAuthenticationResponse(null));
    }

}
