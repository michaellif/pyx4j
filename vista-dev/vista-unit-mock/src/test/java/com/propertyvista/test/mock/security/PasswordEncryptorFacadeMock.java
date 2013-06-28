/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 28, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.security;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.security.PasswordIdentity;

public class PasswordEncryptorFacadeMock implements PasswordEncryptorFacade {

    @Override
    public void activateDecryption() {
    }

    @Override
    public String encryptUserPassword(String userPassword) {
        return userPassword;
    }

    @Override
    public boolean checkUserPassword(String inputPassword, String encryptedPassword) {
        return inputPassword.equals(encryptedPassword);
    }

    @Override
    public String decryptPassword(PasswordIdentity passwordDescr) {
        return passwordDescr.number().getValue();
    }

    @Override
    public void encryptPassword(PasswordIdentity passwordDescr, String password) {
        passwordDescr.number().setValue(password);
    }

}
