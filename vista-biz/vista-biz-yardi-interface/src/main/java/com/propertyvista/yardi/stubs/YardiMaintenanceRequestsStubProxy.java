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

import com.propertyvista.biz.system.yardi.YardiInterfaceNotConfiguredForPropertyException;
import com.propertyvista.biz.system.yardi.YardiResponseException;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Properties;

class YardiMaintenanceRequestsStubProxy extends YardiAbstractStubProxy implements YardiMaintenanceRequestsStub {

    YardiMaintenanceRequestsStubProxy() {
        setMessageErrorHandler(noPropertyAccessHandler);

        // When Yardi SR interface has problems, instead of Messages response it returns a response with undocumented
        // ErrorMessage element inside data element!?
        //   <ServiceRequests><ServiceRequest>
        //     <ErrorMessages><Error>There are no work orders found for these input values.</Error></ErrorMessages>
        //   </ServiceRequest></ServiceRequests>
        // or
        //   <ServiceRequests><ServiceRequest>
        //     <ServiceRequestId>0</ServiceRequestId>
        //     <PropertyCode>B1</PropertyCode>
        //     <UnitCode>#100</UnitCode>
        //     <ErrorMessages><Error>Could not find Property:B1.</Error></ErrorMessages>
        //  </ServiceRequest>
        // or
        //  <ServiceRequests><ServiceRequest>
        //     <PropertyCode>gibb0380</PropertyCode>
        //     <UnitCode>0100</UnitCode>
        //     <ErrorMessage>Interface 'Property Vista-Maintenance' is not Configured for property 'gibb0380'</ErrorMessage>
        //  </ServiceRequest></ServiceRequests>
        setDataErrorHandler(new DataErrorHandler() {
            @Override
            public void handle(String xml) throws YardiServiceException {
                for (String regex : new String[] { //
                ".*<ErrorMessages><Error>(.*)</Error></ErrorMessages>.*", //
                        ".*<ErrorMessage>(.*)</ErrorMessage>.*" //
                }) {
                    if (xml.matches(regex) && ignoreErrorMessage(xml.replaceFirst(regex, "$1"))) {
                        return;
                    }
                }
                throw new YardiServiceException(GENERIC_YARDI_ERROR);
            }

            private boolean ignoreErrorMessage(String message) throws YardiServiceException {
                String lcMessage = message == null ? null : message.toLowerCase();
                // we don't consider these an error
                boolean ignore = lcMessage == null || //
                        lcMessage.contains("no work orders found") || //
                        lcMessage.contains("no service requests found");
                if (ignore) {
                    return true;
                } else if (lcMessage.contains("is not configured for property")) {
                    throw new YardiInterfaceNotConfiguredForPropertyException(message);
                }
                return false;
            }
        });
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
