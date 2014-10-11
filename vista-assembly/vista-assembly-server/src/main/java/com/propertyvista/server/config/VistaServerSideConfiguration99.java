/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import com.pyx4j.security.server.ThrottleConfig;

/**
 * See the files https://svn.pyx4j.com/svn-configs/trunk/vista/testenv/apps/catalina.base/tomcatA/conf/vista99
 *
 */
public class VistaServerSideConfiguration99 extends VistaServerSideConfigurationCustom {

    @Override
    public Integer enviromentId() {
        return 99;
    }

    @Override
    public boolean isVistaQa() {
        return true;
    }

    @Override
    protected String getApplicationDeploymentProtocol() {
        return "https";
    }

    @Override
    public int interfaceSSHDPort() {
        return 8829;
    }

    @Override
    public String getApplicationURLNamespace(boolean secure) {
        if (isDepoymentUseNewDevDomains()) {
            return "-99.devpv.com/";
        } else {
            return "-99.birchwoodsoftwaregroup.com/";
        }
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Vista Support 99\" <support.www99@birchwoodsoftwaregroup.com>";
    }

    @Override
    public ThrottleConfig getThrottleConfig() {
        return null;
    }
}
