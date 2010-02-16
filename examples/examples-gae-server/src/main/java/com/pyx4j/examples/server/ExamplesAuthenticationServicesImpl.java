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
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.examples.domain.User;
import com.pyx4j.examples.domain.UserCredential;
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

    public static class AuthenticateImpl implements AuthenticationServices.Authenticate {

        @Override
        public AuthenticationResponse execute(AuthenticationRequest request) {
            if (CommonsStringUtils.isEmpty(request.email().getValue()) || CommonsStringUtils.isEmpty(request.password().getValue())) {
                throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
            if (!validEmailAddress(request.email().getValue())) {
                throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
            final User userMeta = EntityFactory.create(User.class);
            EntityCriteria<User> criteria = new EntityCriteria<User>(User.class);

            criteria.add(PropertyCriterion.eq(userMeta.email(), request.email().getValue()));
            List<User> users = PersistenceServicesFactory.getPersistenceService().query(criteria);
            if (users.size() != 1) {
                if (AbstractAntiBot.authenticationFailed(request.email().getValue())) {
                    throw new ChallengeVerificationRequired("Too many failed log-in attempts");
                } else {
                    throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }
            User user = users.get(0);

            final UserCredential userCredentialMeta = EntityFactory.create(UserCredential.class);
            EntityCriteria<UserCredential> crCriteria = new EntityCriteria<UserCredential>(UserCredential.class);
            crCriteria.add(PropertyCriterion.eq(userCredentialMeta.user(), user));
            List<UserCredential> crs = PersistenceServicesFactory.getPersistenceService().query(crCriteria);
            if (crs.size() != 1) {
                throw new RuntimeException("Invalid user account, contact support");
            }
            UserCredential cr = crs.get(0);
            if (!request.password().getValue().equals(cr.credential().getValue())) {
                log.info("Invalid password for user {}", request.email().getValue());
                if (AbstractAntiBot.authenticationFailed(request.email().getValue())) {
                    throw new ChallengeVerificationRequired("Too many failed log-in attempts");
                } else {
                    throw new RuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }
            if (!cr.accessKey().isNull()) {
                cr.accessKey().setValue(null);
                try {
                    PersistenceServicesFactory.getPersistenceService().persist(cr);
                } catch (CapabilityDisabledException e) {
                    // Datastore is read-only, degrade gracefully
                }
            }

            // Begin Session

            Set<Behavior> behaviors = new HashSet<Behavior>();
            behaviors.add(cr.behavior().getValue());
            Lifecycle.beginSession(new UserVisit(user.getPrimaryKey(), user.name().getValue()), behaviors);
            return AuthenticationServicesImpl.createAuthenticationResponse();
        }
    }

}
