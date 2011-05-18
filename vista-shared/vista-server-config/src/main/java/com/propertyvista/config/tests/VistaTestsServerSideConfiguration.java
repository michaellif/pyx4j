/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.essentials.server.ReCaptchaAntiBot;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.security.shared.AclCreator;

public class VistaTestsServerSideConfiguration extends EssentialsServerSideConfiguration {

    static {
        LoggerConfig.setContextName("tests");
    }

    private final static Logger log = LoggerFactory.getLogger(VistaTestsServerSideConfiguration.class);

    private final boolean testOnMySQL;

    public VistaTestsServerSideConfiguration(boolean testOnMySQL) {
        this.testOnMySQL = testOnMySQL;
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        if (testOnMySQL) {
            return new VistaTestsDBConfigurationMySQL();
        } else {
            boolean hsqlFiles = false;
            if (hsqlFiles) {
                return new VistaTestsDBConfigurationHSQLFile();
            } else {
                return new VistaTestsDBConfigurationHSQLMemory();
            }
        }
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return true;
    }

    @Override
    public AbstractAntiBot getAntiBot() {
        return new ReCaptchaAntiBot() {

            @Override
            public void assertCaptcha(String challenge, String response) {
                if (ServerSideConfiguration.instance().isDevelopmentBehavior() && "x".equals(response)) {
                    log.debug("Development CAPTCHA Ok");
                } else {
                    super.assertCaptcha(challenge, response);
                }
            }
        };
    }

    @Override
    public AclCreator getAclCreator() {
        final String SERVER_SIDE_TESTS_ACL_CREATOR = this.getClass().getPackage().getName() + ".VistaTestModuleAclCreator";
        try {
            @SuppressWarnings("unchecked")
            Class<TestAclCreator> klass = (Class<TestAclCreator>) Class.forName(SERVER_SIDE_TESTS_ACL_CREATOR);
            return klass.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException("Can't create " + SERVER_SIDE_TESTS_ACL_CREATOR, e);
        }
    }
}
