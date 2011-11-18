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
package com.propertyvista.portal.server.ptapp.services;

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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;
import com.pyx4j.security.server.AuthenticationServiceImpl;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.domain.User;
import com.propertyvista.portal.rpc.ptapp.services.ActivationService;
import com.propertyvista.server.common.mail.MessageTemplates;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.UserCredential;

public class ActivationServiceImpl extends ApplicationEntityServiceImpl implements ActivationService {

    private final static Logger log = LoggerFactory.getLogger(ActivationServiceImpl.class);

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
        List<User> users = Persistence.service().query(criteria);
        if (users.size() == 0) {
            throw new UserRuntimeException(i18n.tr("Email Not Registered"));
        }
        User user = users.get(0);

        UserCredential credential = Persistence.service().retrieve(UserCredential.class, user.getPrimaryKey());
        if (credential == null) {
            throw new UserRuntimeException(i18n.tr("Invalid Login Or Password")); // TODO is this a correct message?
        }
        credential.accessKey().setValue(AccessKey.createAccessKey());
        Calendar expire = new GregorianCalendar();
        expire.add(Calendar.DATE, 1);
        credential.accessKeyExpire().setValue(expire.getTime());
        Persistence.service().persist(credential);

        String token = AccessKey.compressToken(user.email().getValue(), credential.accessKey().getValue());

        MailMessage m = new MailMessage();
        m.setTo(user.email().getValue());
        m.setSender(MessageTemplates.getSender());
        m.setSubject(i18n.tr("Property Vista password reset"));
        m.setHtmlBody(MessageTemplates.createPasswordResetEmail(user.name().getValue(), token));

        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
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
        List<User> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            throw new RuntimeExceptionSerializable(i18n.tr("Invalid Request"));
        }
        User user = users.get(0);

        UserCredential cr = Persistence.service().retrieve(UserCredential.class, user.getPrimaryKey());
        if (cr == null) {
            throw new RuntimeExceptionSerializable(i18n.tr("Invalid User Account. Please Contact Support"));
        }
        if (!cr.enabled().isBooleanTrue()) {
            throw new RuntimeExceptionSerializable(i18n.tr("Your Account Is Suspended"));
        }
        if (!token.accessKey.equals(cr.accessKey().getValue())) {
            AbstractAntiBot.authenticationFailed(token.email);
            throw new RuntimeException(i18n.tr("Invalid Request"));
        }
        if ((new Date().after(cr.accessKeyExpire().getValue()))) {
            throw new RuntimeExceptionSerializable(i18n.tr("Token has expired"));
        }
        cr.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        cr.accessKey().setValue(null);
        Persistence.service().persist(cr);

        callback.onSuccess(AuthenticationServiceImpl.createAuthenticationResponse(VistaAuthenticationServicesImpl.beginSession(user, cr)));

    }
}
