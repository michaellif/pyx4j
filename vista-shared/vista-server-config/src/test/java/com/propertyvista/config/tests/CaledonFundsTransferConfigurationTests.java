/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 30, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config.tests;

import com.pyx4j.config.server.Credentials;

import com.propertyvista.config.CaledonFundsTransferConfiguration;

public class CaledonFundsTransferConfigurationTests extends CaledonFundsTransferConfiguration {

    @Override
    public String sftpHost() {
        throw new Error("not supported in tests");
    }

    @Override
    public int sftpPort() {
        throw new Error("not supported in tests");
    }

    @Override
    public Credentials sftpCredentials() {
        throw new Error("not supported in tests");
    }

    @Override
    public String getIntefaceCompanyId() {
        return "test";
    }

}
