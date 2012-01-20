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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class UserAccessUtils {

    private static final I18n i18n = I18n.get(UserAccessUtils.class);

    public static String createAccessToken(CrmUser user, int ttlDays) {
        CrmUserCredential credential = Persistence.service().retrieve(CrmUserCredential.class, user.getPrimaryKey());
        if (credential == null) {
            throw new UserRuntimeException(i18n.tr("Invalid Login Or Password")); // TODO is this a correct message?
        }
        credential.accessKey().setValue(AccessKey.createAccessKey());
        Calendar expire = new GregorianCalendar();
        expire.add(Calendar.DATE, ttlDays);
        credential.accessKeyExpire().setValue(expire.getTime());
        Persistence.service().persist(credential);

        String token = AccessKey.compressToken(user.email().getValue(), credential.accessKey().getValue());

        return token;
    }
}
