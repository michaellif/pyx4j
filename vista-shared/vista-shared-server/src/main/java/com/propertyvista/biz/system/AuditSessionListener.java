/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.shared.VistaUserVisit;

public class AuditSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // To not record user logout
        if (Context.getRequest() != null) {
            return;
        }
        HttpSession session = se.getSession();
        String namespace = Lifecycle.getNamespaceFromSession(session);
        Visit visit = Lifecycle.getVisitFromSession(session);
        AbstractUser user = null;
        VistaApplication application = null;
        if ((visit != null) && (visit.getUserVisit() instanceof VistaUserVisit)) {
            user = ((VistaUserVisit<?>) visit.getUserVisit()).getCurrentUser();
            application = ((VistaUserVisit<?>) visit.getUserVisit()).getApplication();
        }
        ServerSideFactory.create(AuditFacade.class).sessionExpiration(namespace, application, user, session.getId());
    }
}
