/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 3, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import com.pyx4j.config.server.FacadeFactory;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.VistaSystemFacade;

public class NotificationFacadeFactory implements FacadeFactory<NotificationFacade> {

    @Override
    public NotificationFacade getFacade() {
        if (ServerSideFactory.create(VistaSystemFacade.class).isCommunicationsDisabled()) {
            return ServerSideFactory.createEmptyImplementation(NotificationFacade.class);
        } else {
            return ServerSideFactory.createSafeImplementation(NotificationFacade.class, new NotificationFacadeImpl());
        }
    }

}
