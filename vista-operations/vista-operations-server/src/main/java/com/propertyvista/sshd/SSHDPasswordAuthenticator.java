/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.sshd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import com.pyx4j.config.server.Credentials;

class SSHDPasswordAuthenticator implements PasswordAuthenticator {

    Map<String, Credentials> users = new HashMap<String, Credentials>();

    SSHDPasswordAuthenticator(Collection<Credentials> credentials) {
        for (Credentials credential : credentials) {
            users.put(credential.userName, credential);
        }
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession session) {
        Credentials credential = users.get(username);
        if (credential == null) {
            return false;
        } else {
            return credential.userName.equals(username) && credential.password.equals(password);
        }
    }

}
