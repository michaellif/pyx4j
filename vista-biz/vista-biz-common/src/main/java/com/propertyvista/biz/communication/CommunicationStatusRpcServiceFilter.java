/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 23, 2014
 * @author vlads
 */
package com.propertyvista.biz.communication;

import java.io.Serializable;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.rpc.shared.Service;
import com.pyx4j.security.shared.Context;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.domain.security.VistaDataAccessBehavior;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.shared.VistaUserVisit;

public class CommunicationStatusRpcServiceFilter implements IServiceFilter {
    private static final String TIMESTAMPED_ATTRIBUTE = CommunicationMessageFacade.class.getName();

    private static final short REFRESH_TIME = 60; // in sec

    @Override
    public Serializable filterIncomming(Class<? extends Service<?, ?>> serviceClass, Serializable request) {
        return request;
    }

    @Override
    public Serializable filterOutgoing(Class<? extends Service<?, ?>> serviceClass, Serializable response) {
        if (ServerContext.isUserLoggedIn()) {

            if (VistaApplication.resident.equals(Context.visit(VistaUserVisit.class).getApplication())) {
                if (!SecurityController.check(VistaDataAccessBehavior.ResidentInPortal) && !SecurityController.check(VistaDataAccessBehavior.GuarantorInPortal)) {
                    return response;
                }
            } else if (VistaApplication.crm.equals(Context.visit(VistaUserVisit.class).getApplication())) {
                if (!SecurityController.check(VistaAccessGrantedBehavior.CRM)) {
                    return response;
                }
            } else if (!VistaApplication.prospect.equals(Context.visit(VistaUserVisit.class).getApplication())) {
                return response;
            }
            if (!isCached()) {
                Serializable communicationNotification = ServerSideFactory.create(CommunicationMessageFacade.class).getCommunicationStatus();
                if (communicationNotification != null) {
                    ServerContext.addResponseSystemNotification(communicationNotification);
                    ServerContext.getVisit().setAttribute(TIMESTAMPED_ATTRIBUTE, new Long(System.currentTimeMillis()));
                }
            }
        }

        return response;
    }

    private boolean isCached() {
        Serializable cached = ServerContext.getVisit().getAttribute(TIMESTAMPED_ATTRIBUTE);
        if (cached != null) {
            Long cachedTimeStamp = (Long) cached;

            if (cachedTimeStamp != null) {
                long now = System.currentTimeMillis();
                long diff = now - cachedTimeStamp;
                long diffSeconds = diff / 1000;
                if (diffSeconds < REFRESH_TIME) {
                    return true;
                }
            }
        }
        return false;
    }
}
