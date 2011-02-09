/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 16, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.apphosting.api.ApiProxy.CapabilityDisabledException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.examples.domain.User;
import com.pyx4j.examples.domain.UserCredential;
import com.pyx4j.rpc.shared.IsIgnoreSessionTokenService;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.server.AuthenticationServicesImpl;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Lifecycle;

public class ExamplesAuthenticationServicesImpl extends AuthenticationServicesImpl {

    private final static Logger log = LoggerFactory.getLogger(ExamplesAuthenticationServicesImpl.class);

    public static boolean validEmailAddress(String address) {
        try {
            new InternetAddress(address);
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    public static class AuthenticateImpl implements AuthenticationServices.Authenticate, IsIgnoreSessionTokenService {

        @Override
        public AuthenticationResponse execute(AuthenticationRequest request) {
            if (CommonsStringUtils.isEmpty(request.email().getValue()) || CommonsStringUtils.isEmpty(request.password().getValue())) {
                throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
            if (!validEmailAddress(request.email().getValue())) {
                throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }

            AbstractAntiBot.assertLogin(request.email().getValue(), request.captcha().getValue());

            EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), request.email().getValue()));
            List<User> users = PersistenceServicesFactory.getPersistenceService().query(criteria);
            if (users.size() != 1) {
                if (AbstractAntiBot.authenticationFailed(request.email().getValue())) {
                    throw new ChallengeVerificationRequired("Too many failed log-in attempts");
                } else {
                    throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }
            User user = users.get(0);
            UserCredential userCredential = PersistenceServicesFactory.getPersistenceService().retrieve(UserCredential.class, user.getPrimaryKey());
            if (userCredential == null) {
                throw new RuntimeException("Invalid user account, contact support");
            }
            if (userCredential.enabled().isNull() || (!userCredential.enabled().getValue())) {
                throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
            if (!request.password().getValue().equals(userCredential.credential().getValue())) {
                log.info("Invalid password for user {}", request.email().getValue());
                if (AbstractAntiBot.authenticationFailed(request.email().getValue())) {
                    throw new ChallengeVerificationRequired("Too many failed log-in attempts");
                } else {
                    throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }
            if (!userCredential.accessKey().isNull()) {
                userCredential.accessKey().setValue(null);
                try {
                    PersistenceServicesFactory.getPersistenceService().persist(userCredential);
                } catch (CapabilityDisabledException e) {
                    // Datastore is read-only, degrade gracefully
                }
            }

            // Begin Session
            beginSession(user, userCredential);

            return AuthenticationServicesImpl.createAuthenticationResponse(request.logoutApplicationUrl().getValue());
        }
    }

    static void beginSession(User user, UserCredential userCredential) {
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.add(userCredential.behavior().getValue());
        Lifecycle.beginSession(new UserVisit(user.getPrimaryKey(), user.name().getValue()), behaviors);
    }

}
