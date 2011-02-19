/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server;

import com.propertyvista.portal.server.access.VistaAuthenticationServicesImpl;

import com.pyx4j.essentials.server.EssentialsRPCServiceFactory;
import com.pyx4j.rpc.shared.Service;
import com.pyx4j.security.rpc.AuthenticationServices;

public class VistaRPCServiceFactory extends EssentialsRPCServiceFactory {

    @Override
    public Class<? extends Service<?, ?>> getServiceClass(String serviceInterfaceClassName) throws ClassNotFoundException {
        if (AuthenticationServices.Authenticate.class.getName().equals(serviceInterfaceClassName)) {
            return VistaAuthenticationServicesImpl.AuthenticateImpl.class;
        } else if (AuthenticationServices.Logout.class.getName().equals(serviceInterfaceClassName)) {
            return VistaAuthenticationServicesImpl.LogoutImpl.class;
        }

        return super.getServiceClass(serviceInterfaceClassName);
    }
}
