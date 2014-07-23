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
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.io.Serializable;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.rpc.shared.Service;
import com.pyx4j.security.shared.Context;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.shared.VistaUserVisit;

public class CommunicationStatusRpcServiceFilter implements IServiceFilter {

    @Override
    public Serializable filterIncomming(Class<? extends Service<?, ?>> serviceClass, Serializable request) {
        return request;
    }

    @Override
    public Serializable filterOutgoing(Class<? extends Service<?, ?>> serviceClass, Serializable response) {
        // TODO create message notification and send to GWT

        if (ServerContext.isUserLoggedIn()) {
            switch (Context.visit(VistaUserVisit.class).getApplication()) {
            case crm:
            case resident:
                ServerContext.addResponseSystemNotification(ServerSideFactory.create(CommunicationMessageFacade.class).getCommunicationStatus());
            }
        }

        return response;
    }
}
