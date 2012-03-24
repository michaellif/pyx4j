/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import javax.servlet.ServletContext;

import com.pyx4j.config.server.ServerSideConfiguration;

public class VistaServerSideConfigurationProd extends VistaServerSideConfiguration {

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        // This environment selector defined in tomcatX.wrapper.conf -Dcom.pyx4j.appConfig=Prod
        if ("vista-pangroup".equals(contextName)) {
            return new VistaServerSideConfigurationProdPangroup();
        } else if ("vista-main".equals(contextName)) {
            return new VistaServerSideConfigurationProdMain();
        } else {
            return new VistaServerSideConfigurationCustom();
        }
    }

    @Override
    public boolean openDBReset() {
        return false;
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return false;
    }

}
