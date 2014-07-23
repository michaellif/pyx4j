/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.entity.rpc.ReferenceDataService;
import com.pyx4j.essentials.server.EssentialsRPCServiceFactory;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.Service;

import com.propertyvista.biz.communication.CommunicationStatusRpcServiceFilter;
import com.propertyvista.server.common.reference.ReferenceDataServiceVistaImpl;

public class VistaRPCServiceFactory extends EssentialsRPCServiceFactory {

    @Override
    public Class<? extends IService> getIServiceClass(String serviceInterfaceClassName) throws ClassNotFoundException {
        if (serviceInterfaceClassName.equals(ReferenceDataService.class.getName())) {
            return ReferenceDataServiceVistaImpl.class;
        } else {
            return super.getIServiceClass(serviceInterfaceClassName);
        }
    }

    @Override
    public List<IServiceFilter> getServiceFilterChain(Class<? extends Service<?, ?>> serviceClass) {
        List<IServiceFilter> filters = new ArrayList<>();
        filters.addAll(super.getServiceFilterChain(serviceClass));
        filters.add(new CommunicationStatusRpcServiceFilter());
        return filters;
    }

}
