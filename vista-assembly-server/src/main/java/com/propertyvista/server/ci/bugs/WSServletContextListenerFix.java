/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.ci.bugs;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.sun.xml.ws.transport.http.servlet.WSServletContextListener;

import com.propertyvista.misc.VistaTODO;

public class WSServletContextListenerFix implements ServletContextListener {

    private WSServletContextListener delegate;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        if (VistaTODO.removedForProductionOAPI) {
            return;
        }
        delegate = new WSServletContextListener();
        delegate.contextInitialized(event);
        JAXWS.fixMemoryLeaks();
        JAXB.fixMemoryLeaks();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (delegate != null) {
            delegate.contextDestroyed(event);
            delegate = null;
            JAXWS.fixMemoryLeaks();
            JAXB.fixMemoryLeaks();
        }
    }
}
