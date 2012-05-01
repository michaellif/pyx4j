/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding.rh;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.onboarding.OnboardingUserPasswordChangeRequestIO;
import com.propertyvista.onboarding.OnboardingUserTokenAuthenticationRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.security.OnboardingUserCredential;

public class OnboardingUserPasswordChangeRequestHandler extends AbstractRequestHandler<OnboardingUserPasswordChangeRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(OnboardingUserTokenAuthenticationRequestIO.class);

    private static final I18n i18n = I18n.get(OnboardingUserTokenAuthenticationRequestHandler.class);

    public OnboardingUserPasswordChangeRequestHandler() {
        super(OnboardingUserPasswordChangeRequestIO.class);
    }

    @Override
    public ResponseIO execute(OnboardingUserPasswordChangeRequestIO request) {
        ResponseIO response = EntityFactory.create(ResponseIO.class);

        String email = PasswordEncryptor.normalizeEmailAddress(request.email().getValue());
        AbstractAntiBot.assertLogin(email, null);

        EntityQueryCriteria<OnboardingUser> criteria = EntityQueryCriteria.create(OnboardingUser.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<OnboardingUser> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            AbstractAntiBot.authenticationFailed(email);
            response.success().setValue(Boolean.FALSE);
            return response;
        }
        OnboardingUser user = users.get(0);

        OnboardingUserCredential cr = Persistence.service().retrieve(OnboardingUserCredential.class, user.getPrimaryKey());
        if (cr == null) {
            throw new UserRuntimeException("Invalid User Account. Please Contact Support");
        }
        if (!cr.enabled().isBooleanTrue()) {
            response.success().setValue(Boolean.FALSE);
            return response;
        }

        if (!PasswordEncryptor.checkPassword(request.currentPassword().getValue(), cr.credential().getValue())) {
            AbstractAntiBot.authenticationFailed(email);
            log.info("Invalid password for user {}", email);
            response.success().setValue(Boolean.FALSE);
        }

        if (PasswordEncryptor.checkPassword(request.newPassword().getValue(), cr.credential().getValue())) {
            log.info("Invalid new password for user {}", email);
            response.errorMessage().setValue(i18n.tr("Your password cannot repeat your previous password"));
            response.success().setValue(Boolean.FALSE);
        }

        cr.accessKey().setValue(null);
        cr.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        cr.credentialUpdated().setValue(new Date());
        cr.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);
        Persistence.service().persist(cr);
        Persistence.service().commit();

        response.success().setValue(Boolean.TRUE);
        return response;
    }
}
