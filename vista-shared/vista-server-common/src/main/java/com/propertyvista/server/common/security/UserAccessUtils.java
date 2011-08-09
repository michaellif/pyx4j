/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 9, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.xnap.commons.i18n.I18n;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.User;
import com.propertyvista.server.domain.UserCredential;

public class UserAccessUtils {

    protected static I18n i18n = I18nFactory.getI18n();

    public static String createAccessToken(User user, int ttlDays) {
        UserCredential credential = PersistenceServicesFactory.getPersistenceService().retrieve(UserCredential.class, user.getPrimaryKey());
        if (credential == null) {
            throw new UserRuntimeException(i18n.tr("Invalid login/password")); // TODO is this a correct message?
        }
        credential.accessKey().setValue(AccessKey.createAccessKey());
        Calendar expire = new GregorianCalendar();
        expire.add(Calendar.DATE, ttlDays);
        credential.accessKeyExpire().setValue(expire.getTime());
        PersistenceServicesFactory.getPersistenceService().persist(credential);

        String token = AccessKey.compressToken(user.email().getValue(), credential.accessKey().getValue());

        return token;
    }
}
