/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt.services;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.server.AuthenticationServicesImpl;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;
import com.propertyvista.portal.rpc.pt.PasswordChangeRequest;
import com.propertyvista.portal.rpc.pt.PasswordRetrievalRequest;
import com.propertyvista.portal.rpc.pt.services.ActivationService;
import com.propertyvista.portal.server.mail.MessageTemplates;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.UserCredential;

public class ActivationServiceImpl extends ApplicationEntityServiceImpl implements ActivationService {

    private final static Logger log = LoggerFactory.getLogger(ActivationServiceImpl.class);

    /**
     * Verify that Unit exists before we allow to start Application process, e.g.
     * createAccount
     */
    @Override
    public void unitExists(AsyncCallback<Boolean> callback, UnitSelectionCriteria selectionCriteria) {
        log.debug("Checking if unit exists {}", selectionCriteria);
        callback.onSuccess(new ApartmentServiceImpl().areUnitsAvailable(selectionCriteria));
    }

    public static boolean validEmailAddress(String email) {

        // check email using regular expressions
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

        // Match the given string with the pattern
        Matcher m = p.matcher(email);

        // check whether match is found 
        boolean matchFound = m.matches();
        if (!matchFound) {
            return false;
        }

        try {
            new InternetAddress(email);
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    @Override
    public void createAccount(AsyncCallback<AuthenticationResponse> callback, AccountCreationRequest request) {
        if (ServerSideConfiguration.instance().datastoreReadOnly()) {
            throw new UnRecoverableRuntimeException(EntityServicesImpl.applicationReadOnlyMessage());
        }

        // validate email
        String email = request.email().getValue().toLowerCase(); // intentionally convert this to lowercase before validation
        log.info("Creating account for email={}", email);
        if (!validEmailAddress(email)) {
            log.info("Invalid Email [{}]", email);
            throw new UserRuntimeException(i18n.tr("Invalid Email " + email));
        }

        // captcha (TODO this should probably go even before we validate email)
        AbstractAntiBot.assertCaptcha(request.captcha().getValue());

        // check if user is already registered
        EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<User> users = PersistenceServicesFactory.getPersistenceService().query(criteria);
        if (users.size() != 0) {
            throw new UserRuntimeException(i18n.tr("Email already registered"));
        }

        // validate password
        String password = request.password().getValue();
        if (password == null || password.length() == 0) {
            throw new UserRuntimeException(i18n.tr("Password can not be empty"));
        }

        // create account (user + credential)
        UserCredential credential = EntityFactory.create(UserCredential.class);

        User user = EntityFactory.create(User.class);
        user.email().setValue(email);
        user.name().setValue(request.email().getValue());

        credential.credential().setValue(PasswordEncryptor.encryptPassword(password));
        credential.enabled().setValue(Boolean.TRUE);
        credential.behavior().setValue(VistaBehavior.POTENTIAL_TENANT);

        // save to db
        PersistenceServicesFactory.getPersistenceService().persist(user);
        credential.setPrimaryKey(user.getPrimaryKey());
        PersistenceServicesFactory.getPersistenceService().persist(credential);

        //            Audit audit = EntityFactory.create(Audit.class);
        //            audit.user().set(user);
        //            audit.clientIP().setValue(Context.getRequestRemoteAddr());
        //            audit.event().setValue("registered");
        //            PersistenceServicesFactory.getPersistenceService().persist(audit);

        VistaAuthenticationServicesImpl.beginSession(user, credential);

        AuthenticationResponse authenticationResponse = AuthenticationServicesImpl.createAuthenticationResponse(null);
        callback.onSuccess(authenticationResponse);
    }

    /**
     * Request E-mail to be sent to customer with 'token' for PasswordReset.
     * 
     * E-mail is sent if no exception thrown.
     */
    @Override
    public void passwordReminder(AsyncCallback<VoidSerializable> callback, PasswordRetrievalRequest request) {
        // validate email
        if (!validEmailAddress(request.email().getValue())) {
            throw new UserRuntimeException(i18n.tr("Invalid Email"));
        }
        AbstractAntiBot.assertCaptcha(request.captcha().getValue());

        // find user(s) with the same email
        EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), request.email().getValue().toLowerCase()));
        List<User> users = PersistenceServicesFactory.getPersistenceService().query(criteria);
        if (users.size() == 0) {
            throw new UserRuntimeException(i18n.tr("E-mail not registered"));
        }
        User user = users.get(0);

        UserCredential credential = PersistenceServicesFactory.getPersistenceService().retrieve(UserCredential.class, user.getPrimaryKey());
        if (credential == null) {
            throw new UserRuntimeException(i18n.tr("Invalid login/password")); // TODO is this a correct message?
        }
        credential.accessKey().setValue(AccessKey.createAccessKey());
        Calendar expire = new GregorianCalendar();
        expire.add(Calendar.DATE, 1);
        credential.accessKeyExpire().setValue(expire.getTime());
        PersistenceServicesFactory.getPersistenceService().persist(credential);

        String token = AccessKey.compressToken(user.email().getValue(), credential.accessKey().getValue());

        MailMessage m = new MailMessage();
        m.setTo(user.email().getValue());
        m.setSender(MessageTemplates.getSender());
        m.setSubject(i18n.tr("Property Vista password reset"));
        m.setHtmlBody(MessageTemplates.createPasswordResetEmail(user.name().getValue(), token));

        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service is temporary unavalable, try again later"));
        }
        log.debug("pwd change token {} is sent to {}", token, user.email().getValue());

        callback.onSuccess(new VoidSerializable());
    }

    /**
     * Reset password in the system base on token received in E-mail
     */
    @Override
    public void passwordReset(AsyncCallback<AuthenticationResponse> callback, PasswordChangeRequest request) {
        // Log-in with token
        AccessKey.TokenParser token = new AccessKey.TokenParser(request.token().getValue());
        if (!validEmailAddress(token.email)) {
            throw new RuntimeExceptionSerializable(i18n.tr("Invalid Email"));
        }

        final User userMeta = EntityFactory.create(User.class);
        EntityQueryCriteria<User> criteria = new EntityQueryCriteria<User>(User.class);

        criteria.add(PropertyCriterion.eq(userMeta.email(), token.email));
        List<User> users = PersistenceServicesFactory.getPersistenceService().query(criteria);
        if (users.size() != 1) {
            throw new RuntimeExceptionSerializable(i18n.tr("Invalid request"));
        }
        User user = users.get(0);

        UserCredential cr = PersistenceServicesFactory.getPersistenceService().retrieve(UserCredential.class, user.getPrimaryKey());
        if (cr == null) {
            throw new RuntimeExceptionSerializable(i18n.tr("Invalid user account, contact support"));
        }
        if (!cr.enabled().isBooleanTrue()) {
            throw new RuntimeExceptionSerializable(i18n.tr("Your account is suspended"));
        }
        if (!token.accessKey.equals(cr.accessKey().getValue())) {
            AbstractAntiBot.authenticationFailed(token.email);
            throw new RuntimeException(i18n.tr("Invalid request"));
        }
        if ((new Date().after(cr.accessKeyExpire().getValue()))) {
            throw new RuntimeExceptionSerializable(i18n.tr("Token has expired."));
        }
        cr.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        cr.accessKey().setValue(null);
        PersistenceServicesFactory.getPersistenceService().persist(cr);

        VistaAuthenticationServicesImpl.beginSession(user, cr);

        AuthenticationResponse response = AuthenticationServicesImpl.createAuthenticationResponse(null);
        callback.onSuccess(response);
    }
}
