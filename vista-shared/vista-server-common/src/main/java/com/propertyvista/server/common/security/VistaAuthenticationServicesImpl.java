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
import org.xnap.commons.i18n.I18n;

import com.propertyvista.common.domain.User;
import com.propertyvista.common.domain.VistaBehavior;
import com.propertyvista.crm.rpc.CrmUserVisit;
import com.propertyvista.portal.rpc.pt.PtUserVisit;
import com.propertyvista.server.domain.UserCredential;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.rpc.shared.IsIgnoreSessionTokenService;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.server.AppengineHelper;
import com.pyx4j.security.server.AuthenticationServicesImpl;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.UserVisit;

public class VistaAuthenticationServicesImpl extends AuthenticationServicesImpl {

    private final static Logger log = LoggerFactory.getLogger(VistaAuthenticationServicesImpl.class);

    private static I18n i18n = I18nFactory.getI18n();

    public static class AuthenticateImpl implements AuthenticationServices.Authenticate, IsIgnoreSessionTokenService {

        @Override
        public AuthenticationResponse execute(AuthenticationRequest request) {
            if (ServerSideConfiguration.instance().datastoreReadOnly() || AppengineHelper.isDBReadOnly()) {
                throw new UserRuntimeException(EntityServicesImpl.applicationReadOnlyMessage());
            }
            if (CommonsStringUtils.isEmpty(request.email().getValue()) || CommonsStringUtils.isEmpty(request.password().getValue())) {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
            String email = request.email().getValue().toLowerCase(Locale.ENGLISH).trim();
            AbstractAntiBot.assertLogin(email, request.captcha().getValue());

            EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
            List<User> users = PersistenceServicesFactory.getPersistenceService().query(criteria);
            if (users.size() != 1) {
                log.debug("Invalid log-in attempt {} rs {}", email, users.size());
                if (AbstractAntiBot.authenticationFailed(email)) {
                    throw new ChallengeVerificationRequired(i18n.tr("Too many failed log-in attempts"));
                } else {
                    throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }
            User user = users.get(0);

            UserCredential cr = PersistenceServicesFactory.getPersistenceService().retrieve(UserCredential.class, user.getPrimaryKey());
            if (cr == null) {
                throw new UserRuntimeException(i18n.tr("Invalid user account, contact support"));
            }
            if (!cr.enabled().isBooleanTrue()) {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
            if (!PasswordEncryptor.checkPassword(request.password().getValue(), cr.credential().getValue())) {
                log.info("Invalid password for user {}", email);
                if (AbstractAntiBot.authenticationFailed(email)) {
                    throw new ChallengeVerificationRequired(i18n.tr("Too many failed log-in attempts"));
                } else {
                    throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }

            // Begin Session
            beginSession(user, cr);
            return AuthenticationServicesImpl.createAuthenticationResponse(request.logoutApplicationUrl().getValue());
        }
    }

    public static void beginSession(User user, UserCredential userCredential) {
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.add(userCredential.behavior().getValue());
        final UserVisit visit;
        if (behaviors.contains(VistaBehavior.POTENTIAL_TENANT)) {
            visit = new PtUserVisit(user.getPrimaryKey(), user.name().getValue());
        } else if (behaviors.contains(VistaBehavior.PROPERTY_MANAGER)) {
            visit = new CrmUserVisit(user.getPrimaryKey(), user.name().getValue());
        } else {
            visit = new UserVisit(user.getPrimaryKey(), user.name().getValue());
        }
        visit.setEmail(user.email().getValue());

        VistaLifecycle.beginSession(visit, behaviors);
    }

    public static class LogoutImpl implements AuthenticationServices.Logout {

        @Override
        public AuthenticationResponse execute(String request) {
            VistaLifecycle.endSession();
            return createAuthenticationResponse(request);
        }
    }
}
