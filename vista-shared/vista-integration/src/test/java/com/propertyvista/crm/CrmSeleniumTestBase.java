/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-11
 * @author TPRGLET
 * @version $Id$
 */
package com.propertyvista.crm;

import com.propertvista.generator.PTGenerator;

import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.D;
import com.pyx4j.selenium.ISeleniumTestConfiguration;
import com.pyx4j.selenium.SeleniumTestBase;

import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.unit.VistaDevLogin;
import com.propertyvista.unit.config.ApplicationId;
import com.propertyvista.unit.config.VistaSeleniumTestConfiguration;

public class CrmSeleniumTestBase extends SeleniumTestBase {

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new VistaSeleniumTestConfiguration(ApplicationId.crm);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        VistaDevLogin.login(selenium);
        selenium.waitWhileWorking();
        selenium.setPropagateLogToClient(true);
    }

    protected void login() {
        PTGenerator generator = new PTGenerator(VistaDevPreloadConfig.createTest());
        TenantUser user = generator.createUser(1);
        selenium.type(D.id(proto(AuthenticationRequest.class).email()), "pm001@pyx4j.com");
        selenium.type(D.id(proto(AuthenticationRequest.class).password()), "pm001@pyx4j.com");
        selenium.click(CrudDebugId.Criteria_Submit);
    }
}
