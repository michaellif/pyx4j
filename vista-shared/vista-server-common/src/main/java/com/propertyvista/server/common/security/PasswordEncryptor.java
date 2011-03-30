/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.pyx4j.config.shared.ApplicationMode;

public class PasswordEncryptor {

    private static org.jasypt.util.password.PasswordEncryptor getPasswordEncryptor() {
        if (ApplicationMode.isDevelopment()) {
            return new BasicPasswordEncryptor();
        } else {
            return new StrongPasswordEncryptor();
        }
    }

    public static String encryptPassword(String userPassword) {
        return getPasswordEncryptor().encryptPassword(userPassword);
    }

    public static boolean checkPassword(String inputPassword, String encryptedPassword) {
        return getPasswordEncryptor().checkPassword(inputPassword, encryptedPassword);
    }
}
