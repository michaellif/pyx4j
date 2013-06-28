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

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.AbstractAntiBot.LoginType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.portal.rpc.ptapp.services.ActivationService;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.domain.security.CustomerUserCredential;

public class ActivationServiceImpl extends ApplicationEntityServiceImpl implements ActivationService {

    private static final I18n i18n = I18n.get(ActivationServiceImpl.class);

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

        final CrmUser userMeta = EntityFactory.create(CrmUser.class);
        EntityQueryCriteria<CustomerUser> criteria = new EntityQueryCriteria<CustomerUser>(CustomerUser.class);

        criteria.add(PropertyCriterion.eq(userMeta.email(), token.email));
        List<CustomerUser> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            throw new RuntimeExceptionSerializable(i18n.tr("Invalid Request"));
        }
        CustomerUser user = users.get(0);

        CustomerUserCredential cr = Persistence.service().retrieve(CustomerUserCredential.class, user.getPrimaryKey());
        if (cr == null) {
            throw new RuntimeExceptionSerializable(i18n.tr("Invalid User Account. Please Contact Support"));
        }
        if (!cr.enabled().isBooleanTrue()) {
            throw new RuntimeExceptionSerializable(i18n.tr("Your Account Is Suspended"));
        }
        if (!token.accessKey.equals(cr.accessKey().getValue())) {
            AbstractAntiBot.authenticationFailed(LoginType.userLogin, token.email);
            throw new RuntimeException(i18n.tr("Invalid Request"));
        }
        if ((new Date().after(cr.accessKeyExpire().getValue()))) {
            throw new RuntimeExceptionSerializable(i18n.tr("Token Has Expired"));
        }
        cr.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(request.newPassword().getValue()));
        cr.accessKey().setValue(null);
        Persistence.service().persist(cr);

        //callback.onSuccess(AuthenticationServiceImpl.createAuthenticationResponse(new PtAuthenticationServiceImpl().beginSession(user, cr)));

    }
}
