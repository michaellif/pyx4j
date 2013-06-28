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
package com.propertyvista.biz.system.encryption;

import com.propertyvista.domain.security.PasswordIdentity;

public interface PasswordEncryptorFacade {

    public void activateDecryption();

    public String encryptUserPassword(String userPassword);

    public boolean checkUserPassword(String inputPassword, String encryptedPassword);

    String decryptPassword(PasswordIdentity passwordDescr);

    void encryptPassword(PasswordIdentity passwordDescr, String password);

}
