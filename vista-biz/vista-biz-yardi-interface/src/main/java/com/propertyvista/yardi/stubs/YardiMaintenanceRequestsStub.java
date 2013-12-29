/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.stubs;

import java.rmi.RemoteException;

import com.yardi.entity.maintenance.ServiceRequests;
import com.yardi.entity.maintenance.meta.YardiMaintenanceConfigMeta;
import com.yardi.ws.operations.requests.GetServiceRequest_Search;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Properties;

public interface YardiMaintenanceRequestsStub {

    Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException;

    YardiMaintenanceConfigMeta getMaintenanceConfigMeta(PmcYardiCredential yc) throws YardiServiceException, RemoteException;

    ServiceRequests getRequestsByParameters(PmcYardiCredential yc, GetServiceRequest_Search params) throws YardiServiceException, RemoteException;

    ServiceRequests postMaintenanceRequests(PmcYardiCredential yc, ServiceRequests requests) throws YardiServiceException, RemoteException;

}
