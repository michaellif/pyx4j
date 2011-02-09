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
package com.propertyvista.portal.server.access;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.User;
import com.propertyvista.server.domain.UserCredential;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.rpc.shared.IsIgnoreSessionTokenService;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.server.AppengineHelper;
import com.pyx4j.security.server.AuthenticationServicesImpl;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Lifecycle;

public class VistaAuthenticationServicesImpl extends AuthenticationServicesImpl {

    private final static Logger log = LoggerFactory.getLogger(VistaAuthenticationServicesImpl.class);

    public static class AuthenticateImpl implements AuthenticationServices.Authenticate, IsIgnoreSessionTokenService {

        @Override
        public AuthenticationResponse execute(AuthenticationRequest request) {
            if (ServerSideConfiguration.instance().datastoreReadOnly() || AppengineHelper.isDBReadOnly()) {
                throw new UnRecoverableRuntimeException(EntityServicesImpl.applicationReadOnlyMessage());
            }
            if (CommonsStringUtils.isEmpty(request.email().getValue()) || CommonsStringUtils.isEmpty(request.password().getValue())) {
                throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
            String email = request.email().getValue().toLowerCase(Locale.ENGLISH).trim();
            AbstractAntiBot.assertLogin(email, request.captcha().getValue());

            EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
            List<User> users = PersistenceServicesFactory.getPersistenceService().query(criteria);
            if (users.size() != 1) {
                log.debug("Invalid log-in attempt {} rs {}", email, users.size());
                if (AbstractAntiBot.authenticationFailed(email)) {
                    throw new ChallengeVerificationRequired("Too many failed log-in attempts");
                } else {
                    throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }
            User user = users.get(0);

            UserCredential cr = PersistenceServicesFactory.getPersistenceService().retrieve(UserCredential.class, user.getPrimaryKey());
            if (cr == null) {
                throw new RuntimeException("Invalid user account, contact support");
            }
            if (!cr.enabled().isBooleanTrue()) {
                throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
            if (!request.password().getValue().equals(cr.credential().getValue())) {
                log.info("Invalid password for user {}", email);
                if (AbstractAntiBot.authenticationFailed(email)) {
                    throw new ChallengeVerificationRequired("Too many failed log-in attempts");
                } else {
                    throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }

            // Begin Session
            beginSession(user, cr);
            return AuthenticationServicesImpl.createAuthenticationResponse(request.logoutApplicationUrl().getValue());
        }
    }

    static void beginSession(User user, UserCredential userCredential) {
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.add(userCredential.behavior().getValue());
        UserVisit visit = new UserVisit(user.getPrimaryKey(), user.name().getValue());
        visit.setEmail(user.email().getValue());
        Lifecycle.beginSession(visit, behaviors);
    }
}
