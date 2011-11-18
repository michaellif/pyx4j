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
import com.pyx4j.security.shared.UserVisit;

import com.propertyvista.domain.User;
import com.propertyvista.server.domain.UserCredential;

public abstract class VistaAuthenticationServicesImpl extends com.pyx4j.security.server.AuthenticationServiceImpl {

    private final static Logger log = LoggerFactory.getLogger(VistaAuthenticationServicesImpl.class);

    private static I18n i18n = I18n.get(VistaAuthenticationServicesImpl.class);

    protected abstract boolean hasRequiredSiteBehavior();

    @Override
    @IgnoreSessionToken
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, String sessionToken) {
        if (!hasRequiredSiteBehavior()) {
            VistaLifecycle.endSession();
        }
        super.authenticate(callback, sessionToken);
    }

    @Override
    @IgnoreSessionToken
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, AuthenticationRequest request) {
        // Try to begin Session
        String sessionToken = beginSession(request);
        if (!hasRequiredSiteBehavior()) {
            VistaLifecycle.endSession();
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        callback.onSuccess(createAuthenticationResponse(sessionToken));
    }

    public static String beginSession(AuthenticationRequest request) {
        switch (SystemMaintenance.getState()) {
        case Unavailable:
            throw new UserRuntimeException(SystemMaintenance.getApplicationMaintenanceMessage());
        }

        if (CommonsStringUtils.isEmpty(request.email().getValue()) || CommonsStringUtils.isEmpty(request.password().getValue())) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        String email = request.email().getValue().toLowerCase(Locale.ENGLISH).trim();
        AbstractAntiBot.assertLogin(email, request.captcha().getValue());

        EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<User> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            if (AbstractAntiBot.authenticationFailed(email)) {
                throw new ChallengeVerificationRequired(i18n.tr("Too many failed Log In attempts"));
            } else {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
        }
        User user = users.get(0);

        UserCredential cr = Persistence.service().retrieve(UserCredential.class, user.getPrimaryKey());
        if (cr == null) {
            throw new UserRuntimeException(i18n.tr("Invalid User Account. Please Contact Support"));
        }
        if (!cr.enabled().isBooleanTrue()) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        if (!PasswordEncryptor.checkPassword(request.password().getValue(), cr.credential().getValue())) {
            log.info("Invalid password for user {}", email);
            if (AbstractAntiBot.authenticationFailed(email)) {
                throw new ChallengeVerificationRequired(i18n.tr("Too many failed Log In attempts"));
            } else {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
        }
        return beginSession(user, cr);
    }

    public static String beginSession(User user, UserCredential userCredential) {
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.add(userCredential.behavior().getValue());
        UserVisit visit = new UserVisit(user.getPrimaryKey(), user.name().getValue());
        visit.setEmail(user.email().getValue());
        return VistaLifecycle.beginSession(visit, behaviors);
    }

    @Override
    @IgnoreSessionToken
    public void logout(AsyncCallback<AuthenticationResponse> callback) {
        VistaLifecycle.endSession();
        callback.onSuccess(createAuthenticationResponse(null));
    }

}
