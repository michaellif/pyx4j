/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 28, 2015
 * @author ernestog
 */
package com.propertyvista.biz.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.security.common.AbstractUser;

public class AddresseUtils {

    private final static Logger log = LoggerFactory.getLogger(AddresseUtils.class);

    public static String getCompleteEmail(AbstractUser user) {
        if (user == null) {
            log.warn("User is null");
            return null;
        }
        return getCompleteEmail(user.name().getValue(), user.email().getValue());
    }

    public static String getCompleteEmail(String name, String email) {
        StringBuffer completeEmail = new StringBuffer();
        completeEmail.append(name != null ? name : "");
        completeEmail.append(" ");
        completeEmail.append("<");
        completeEmail.append(email);
        completeEmail.append(">");

        return completeEmail.toString();
    }

}
