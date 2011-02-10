/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt;

import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;

import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.rpc.AccountCreationRequest;
import com.propertyvista.portal.rpc.pt.ActivationServices;
import com.propertyvista.portal.server.access.AntiBot;
import com.propertyvista.portal.server.access.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.UserCredential;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.server.AuthenticationServicesImpl;

public class ActivationServicesImpl implements ActivationServices {

    private final static Logger log = LoggerFactory.getLogger(ActivationServicesImpl.class);

    private static I18n i18n = I18nFactory.getI18n();

    public static boolean validEmailAddress(String address) {
        try {
            new InternetAddress(address);
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    public static class CreateAccountImpl implements ActivationServices.CreateAccount {

        @Override
        public AuthenticationResponse execute(AccountCreationRequest request) {
            if (ServerSideConfiguration.instance().datastoreReadOnly()) {
                throw new UnRecoverableRuntimeException(EntityServicesImpl.applicationReadOnlyMessage());
            }
            if (!validEmailAddress(request.email().getValue())) {
                throw new UserRuntimeException(i18n.tr("Invalid Email"));
            }
            String email = request.email().getValue().toLowerCase();
            AntiBot.assertCaptcha(request.captcha().getValue());

            EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
            List<User> users = PersistenceServicesFactory.getPersistenceService().query(criteria);
            if (users.size() != 0) {
                throw new UserRuntimeException(i18n.tr("Email already registered"));
            }

            UserCredential credential = EntityFactory.create(UserCredential.class);

            User user = EntityFactory.create(User.class);
            user.email().setValue(email);
            user.name().setValue(request.email().getValue());

            credential.credential().setValue(VistaAuthenticationServicesImpl.encryptPassword(request.password().getValue()));

            PersistenceServicesFactory.getPersistenceService().persist(user);
            credential.setPrimaryKey(user.getPrimaryKey());

            PersistenceServicesFactory.getPersistenceService().persist(credential);

            //            Audit audit = EntityFactory.create(Audit.class);
            //            audit.user().set(user);
            //            audit.clientIP().setValue(Context.getRequestRemoteAddr());
            //            audit.event().setValue("registered");
            //            PersistenceServicesFactory.getPersistenceService().persist(audit);

            VistaAuthenticationServicesImpl.beginSession(user, credential);

            return AuthenticationServicesImpl.createAuthenticationResponse(null);
        }

    }
}
