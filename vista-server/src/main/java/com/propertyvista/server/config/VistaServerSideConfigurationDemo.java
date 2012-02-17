/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import javax.servlet.ServletContext;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.security.server.ThrottleConfig;

public class VistaServerSideConfigurationDemo extends VistaServerSideConfiguration {

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        // Disable environment selection.  All defined in tomcatX.wrapper.conf -Dcom.pyx4j.appConfig=
        return this;
    }

    @Override
    public boolean openIdrequired() {
        return false;
    }

    @Override
    public ThrottleConfig getThrottleConfig() {
        return null;
    }
}
