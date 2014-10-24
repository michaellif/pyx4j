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

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.maintenance.ServiceRequests;
import com.yardi.entity.maintenance.meta.CustomConfig;
import com.yardi.entity.maintenance.meta.YardiMaintenanceConfigMeta;
import com.yardi.ws.ItfServiceRequests;
import com.yardi.ws.ItfServiceRequestsStub;
import com.yardi.ws.operations.requests.CreateOrEditServiceRequests;
import com.yardi.ws.operations.requests.CreateOrEditServiceRequestsResponse;
import com.yardi.ws.operations.requests.GetCustomValues;
import com.yardi.ws.operations.requests.GetCustomValuesResponse;
import com.yardi.ws.operations.requests.GetPropertyConfigurations;
import com.yardi.ws.operations.requests.GetPropertyConfigurationsResponse;
import com.yardi.ws.operations.requests.GetServiceRequest_Search;
import com.yardi.ws.operations.requests.GetServiceRequest_SearchResponse;
import com.yardi.ws.operations.requests.GetVersionNumber;
import com.yardi.ws.operations.requests.GetVersionNumberResponse;
import com.yardi.ws.operations.requests.Ping;
import com.yardi.ws.operations.requests.PingResponse;
import com.yardi.ws.operations.requests.ServiceRequestXml_type0;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiInterfaceType;
import com.propertyvista.yardi.beans.Properties;

class YardiMaintenanceRequestsStubImpl extends AbstractYardiStub implements YardiMaintenanceRequestsStub {

    private final static Logger log = LoggerFactory.getLogger(YardiMaintenanceRequestsStubImpl.class);

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        init(yc, Action.GetPropertyConfigurations);

        GetPropertyConfigurations request = new GetPropertyConfigurations();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.Maintenance, yc));

        GetPropertyConfigurationsResponse response = getMaintenanceRequestsService(yc).getPropertyConfigurations(request);
        String xml = response.getGetPropertyConfigurationsResult().getExtraElement().toString();
        return ensureResult(xml, Properties.class);
    }

    @Override
    public YardiMaintenanceConfigMeta getMaintenanceConfigMeta(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        init(yc, Action.GetCustomValues);

        GetCustomValues request = new GetCustomValues();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.Maintenance, yc));

        GetCustomValuesResponse response = getMaintenanceRequestsService(yc).getCustomValues(request);
        String xml = response.getGetCustomValuesResult().getExtraElement().toString();
        CustomConfig config = ensureResult(xml, CustomConfig.class);
        return config.getCustomValues();
    }

    @Override
    public ServiceRequests getRequestsByParameters(PmcYardiCredential yc, GetServiceRequest_Search request) throws YardiServiceException, RemoteException {
        init(yc, Action.GetServiceRequests);

        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.Maintenance, yc));

        GetServiceRequest_SearchResponse response = getMaintenanceRequestsService(yc).getServiceRequest_Search(request);
        String xml = response.getGetServiceRequest_SearchResult().getExtraElement().toString();
        return ensureResult(xml, ServiceRequests.class);
    }

    @Override
    public ServiceRequests postMaintenanceRequests(PmcYardiCredential yc, ServiceRequests requests) throws YardiServiceException, RemoteException {
        Validate.notNull(requests, "requests can not be null");

        init(yc, Action.CreateOrEditServiceRequests);
        validateWriteAccess(yc);

        CreateOrEditServiceRequests request = new CreateOrEditServiceRequests();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.Maintenance, yc));

        try {
            ServiceRequestXml_type0 serviceRequestXml = new ServiceRequestXml_type0();
            String rawXml = MarshallUtil.marshall(requests);
            log.debug("{}", rawXml);
            OMElement element = AXIOMUtil.stringToOM(rawXml);
            serviceRequestXml.setExtraElement(element);
            request.setServiceRequestXml(serviceRequestXml);
        } catch (JAXBException e) {
            throw new Error(e);
        } catch (XMLStreamException e) {
            throw new Error(e);
        }

        CreateOrEditServiceRequestsResponse response = getMaintenanceRequestsService(yc).createOrEditServiceRequests(request);
        String xml = response.getCreateOrEditServiceRequestsResult().getExtraElement().toString();
        return ensureResult(xml, ServiceRequests.class);
    }

    @Override
    public String ping(PmcYardiCredential yc) {
        try {
            init(yc, Action.Ping);
            PingResponse response = getMaintenanceRequestsService(yc).ping(new Ping());
            return response.getPingResult();
        } catch (YardiServiceException | RemoteException e) {
            throw new Error(e);
        }
    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) {
        try {
            init(yc, Action.GetVersionNumber);
            GetVersionNumberResponse response = getMaintenanceRequestsService(yc).getVersionNumber(new GetVersionNumber());
            return response.getGetVersionNumberResult();
        } catch (YardiServiceException | RemoteException e) {
            throw new Error(e);
        }
    }

    @Override
    public void validate(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        // try to pull properties
        getPropertyConfigurations(yc);
    }

    private ItfServiceRequests getMaintenanceRequestsService(PmcYardiCredential yc) throws AxisFault {
        ItfServiceRequestsStub serviceStub = new ItfServiceRequestsStub(maintenanceRequestsServiceURL(yc));
        addMessageContextListener("ServiceRequests", serviceStub, null);
        setTransportOptions(serviceStub, yc);
        return serviceStub;
    }

    private String maintenanceRequestsServiceURL(PmcYardiCredential yc) {
        if (yc.maintenanceRequestsServiceURL().isNull()) {
            return serviceWithPath(yc, "webservices/itfservicerequests.asmx");
        } else {
            return yc.maintenanceRequestsServiceURL().getValue();
        }
    }
}
