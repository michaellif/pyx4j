/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.mock.stub;

import java.rmi.RemoteException;

import com.yardi.entity.maintenance.ServiceRequests;
import com.yardi.entity.maintenance.meta.YardiMaintenanceConfigMeta;
import com.yardi.ws.operations.requests.GetServiceRequest_Search;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.stubs.YardiMaintenanceRequestsStub;

public class YardiMockMaintenanceRequestsStubImpl implements YardiMaintenanceRequestsStub {

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public YardiMaintenanceConfigMeta getMaintenanceConfigMeta(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceRequests getRequestsByParameters(PmcYardiCredential yc, GetServiceRequest_Search params) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceRequests postMaintenanceRequests(PmcYardiCredential yc, ServiceRequests requests) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

}
