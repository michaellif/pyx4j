/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 5, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.stubs;

import java.rmi.RemoteException;

import com.yardi.entity.maintenance.ServiceRequests;
import com.yardi.entity.maintenance.meta.YardiMaintenanceConfigMeta;
import com.yardi.ws.operations.requests.GetServiceRequest_Search;

import com.propertyvista.biz.system.yardi.YardiResponseException;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Properties;

public class YardiMaintenanceRequestsStubProxy extends YardiAbstractStubProxy implements YardiMaintenanceRequestsStub {

    public YardiMaintenanceRequestsStubProxy() {
        setMessageErrorHandler(noPropertyAccessHandler);
    }

    private YardiMaintenanceRequestsStub getStub(PmcYardiCredential yc) {
        return getStubInstance(YardiMaintenanceRequestsStub.class, yc);
    }

    @Override
    public String ping(PmcYardiCredential yc) {
        return getStub(yc).ping(yc);
    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) {
        return getStub(yc).getPluginVersion(yc);
    }

    @Override
    public void validate(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        getStub(yc).validate(yc);
    }

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        try {
            return getStub(yc).getPropertyConfigurations(yc);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public YardiMaintenanceConfigMeta getMaintenanceConfigMeta(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        try {
            return getStub(yc).getMaintenanceConfigMeta(yc);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public ServiceRequests getRequestsByParameters(PmcYardiCredential yc, GetServiceRequest_Search params) throws YardiServiceException, RemoteException {
        try {
            return getStub(yc).getRequestsByParameters(yc, params);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public ServiceRequests postMaintenanceRequests(PmcYardiCredential yc, ServiceRequests requests) throws YardiServiceException, RemoteException {
        try {
            return getStub(yc).postMaintenanceRequests(yc, requests);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

}
