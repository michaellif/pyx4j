/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-01-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server;

import com.propertyvista.portal.server.access.VistaAccessControlList;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.security.server.ThrottleConfig;
import com.pyx4j.security.shared.AclCreator;

public class VistaServerSideConfiguration extends EssentialsServerSideConfiguration {

    static final String recaptchaPrivateKey = "6LfVZMESAAAAANrD2Ln5t4yWg3czLMBjQRuKosGx";

    static final String recaptchaPublicKey = "6LfVZMESAAAAAJaoJgKeTN_F9CKs6_-XGqG4nsth";

    @Override
    public ServerSideConfiguration selectInstanceByContextName(String contextName) {
        if ("vista33".equals(contextName)) {

        }
        return this;
    }

    @Override
    public AclCreator getAclCreator() {
        return new VistaAccessControlList();
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaConfigurationMySQL();
    }

    @Override
    public IServiceFactory getRPCServiceFactory() {
        return new VistaRPCServiceFactory();
    }

    @Override
    public DataPreloaderCollection getDataPreloaders() {
        return new VistaDataPreloaders();
    }

    @Override
    public ThrottleConfig getThrottleConfig() {
        return new ThrottleConfig() {
            @Override
            public long getInterval() {
                return Consts.SEC2MSEC;
            }

            @Override
            public long getMaxRequests() {
                return 60000;
            }
        };
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return true;
    }

    public String getReCaptchaPrivateKey() {
        return recaptchaPrivateKey;
    }

    public String getReCaptchaPublicKey() {
        return recaptchaPublicKey;
    }

}
