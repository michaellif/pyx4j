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
package com.propertyvista.payment.sshd;

import java.io.File;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.j2se.CredentialsFileStorage;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;

class SSHDPasswordAuthenticator implements PasswordAuthenticator {

    private final Credentials credentials;

    SSHDPasswordAuthenticator() {
        credentials = CredentialsFileStorage.getCredentials(new File(((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance())
                .getConfigDirectory(), "sftp-payment-credentials.properties"));
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession session) {
        return credentials.userName.equals(username) && credentials.password.equals(password);
    }

}
