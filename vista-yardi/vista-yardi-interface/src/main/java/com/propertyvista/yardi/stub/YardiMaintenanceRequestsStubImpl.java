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
package com.propertyvista.yardi.stub;

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
import com.yardi.ws.operations.requests.ServiceRequestXml_type0;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.bean.Messages;
import com.propertyvista.yardi.bean.Properties;

public class YardiMaintenanceRequestsStubImpl extends AbstractYardiStub implements YardiMaintenanceRequestsStub {

    private final static Logger log = LoggerFactory.getLogger(YardiMaintenanceRequestsStubImpl.class);

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        try {
            init(Action.GetPropertyConfigurations);

            GetPropertyConfigurations request = new GetPropertyConfigurations();
            request.setUserName(yc.username().getValue());
            request.setPassword(yc.credential().getValue());
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);

            GetPropertyConfigurationsResponse response = getMaintenanceRequestsService(yc).getPropertyConfigurations(request);
            if (response.getGetPropertyConfigurationsResult() == null) {
                throw new Error("Received response is null");
            }

            String xml = response.getGetPropertyConfigurationsResult().getExtraElement().toString();

            log.info("GetPropertyConfigurations Result: {}", xml);

            // When Yardi has problems it returns invalid request with undocumented Error element inside !?
            String error = yardiErrorCheck(xml);
            if (error != null) {
                throw new YardiServiceException(error);
            }

            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }

            Properties properties = MarshallUtil.unmarshal(Properties.class, xml);

            if (log.isDebugEnabled()) {
                log.debug("\n--- GetPropertyConfigurations ---\n{}\n", properties);
            }

            return properties;

        } catch (JAXBException e) {
            throw new Error(e);
        }
    }

    @Override
    public YardiMaintenanceConfigMeta getMaintenanceConfigMeta(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        try {
            init(Action.GetCustomValues);

            GetCustomValues params = new GetCustomValues();
            params.setUserName(yc.username().getValue());
            params.setPassword(yc.credential().getValue());
            params.setServerName(yc.serverName().getValue());
            params.setDatabase(yc.database().getValue());
            params.setPlatform(yc.platform().getValue().name());
            params.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);

            GetCustomValuesResponse response = getMaintenanceRequestsService(yc).getCustomValues(params);
            String xml = response.getGetCustomValuesResult().getExtraElement().toString();

            log.info("GetCustomValues: {}", xml);
            // When Yardi has problems it returns invalid request with undocumented Error element inside !?
            String error = yardiErrorCheck(xml);
            if (error != null) {
                throw new YardiServiceException(error);
            }

            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }

            CustomConfig config = MarshallUtil.unmarshal(CustomConfig.class, xml);
            return config.getCustomValues();

        } catch (JAXBException e) {
            throw new Error(e);
        }
    }

    @Override
    public ServiceRequests getRequestsByParameters(PmcYardiCredential yc, GetServiceRequest_Search params) throws YardiServiceException, RemoteException {
        try {
            init(Action.GetServiceRequests);

            params.setUserName(yc.username().getValue());
            params.setPassword(yc.credential().getValue());
            params.setServerName(yc.serverName().getValue());
            params.setDatabase(yc.database().getValue());
            params.setPlatform(yc.platform().getValue().name());
            params.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);

            GetServiceRequest_SearchResponse response = getMaintenanceRequestsService(yc).getServiceRequest_Search(params);
            String xml = response.getGetServiceRequest_SearchResult().getExtraElement().toString();

            log.info("GetServiceRequests: {}", xml);

            // When Yardi has problems it returns invalid request with undocumented Error element inside !?
            String error = yardiErrorCheck(xml);
            if (error != null) {
                throw new YardiServiceException(error);
            }

            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    log.warn("Yardi Error: {}", messages.getMessages().get(0).getValue());
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }

            ServiceRequests requests = MarshallUtil.unmarshal(ServiceRequests.class, xml);
            return requests;

        } catch (JAXBException e) {
            throw new Error(e);
        }
    }

    @Override
    public ServiceRequests postMaintenanceRequests(PmcYardiCredential yc, ServiceRequests requests) throws YardiServiceException, RemoteException {
        try {
            Validate.notNull(requests, "requests can not be null");

            init(Action.CreateOrEditServiceRequests);
            validateWriteAccess(yc);

            CreateOrEditServiceRequests params = new CreateOrEditServiceRequests();
            params.setUserName(yc.username().getValue());
            params.setPassword(yc.credential().getValue());
            params.setServerName(yc.serverName().getValue());
            params.setDatabase(yc.database().getValue());
            params.setPlatform(yc.platform().getValue().name());
            params.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);

            ServiceRequestXml_type0 serviceRequestXml = new ServiceRequestXml_type0();
            String rawXml = MarshallUtil.marshall(requests);
            log.debug("{}", rawXml);
            OMElement element = AXIOMUtil.stringToOM(rawXml);
            serviceRequestXml.setExtraElement(element);
            params.setServiceRequestXml(serviceRequestXml);

            CreateOrEditServiceRequestsResponse response = getMaintenanceRequestsService(yc).createOrEditServiceRequests(params);
            String responseXml = response.getCreateOrEditServiceRequestsResult().getExtraElement().toString();
            log.debug("CreateOrEditServiceRequests: {}", responseXml);

            // When Yardi has problems it returns invalid request with undocumented Error element inside !?
            String error = yardiErrorCheck(responseXml);
            if (error != null) {
                throw new YardiServiceException(error);
            }

            if (Messages.isMessageResponse(responseXml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, responseXml);
                if (messages.isError()) {
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }

            return MarshallUtil.unmarshal(ServiceRequests.class, responseXml);
        } catch (JAXBException e) {
            throw new Error(e);
        } catch (XMLStreamException e) {
            throw new Error(e);
        }
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

    private String yardiErrorCheck(String s) {
        // When Yardi has problems it returns invalid request with undocumented Error element inside !?
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
        {
            String regex = ".*<ErrorMessages><Error>(.*)</Error></ErrorMessages>.*";
            if (s.matches(regex)) {
                return filterErrorMessage(s.replaceFirst(regex, "$1"));
            }
        }
        {
            String regex = ".*<ErrorMessage>(.*)</ErrorMessage>.*";
            if (s.matches(regex)) {
                return filterErrorMessage(s.replaceFirst(regex, "$1"));
            }
        }
        return null;
    }

    private String filterErrorMessage(String message) {
        if (message == null) {
            return null;
        } else if (message.contains("no work orders found")) {
            log.warn(message);
            return null;
            // There are no service requests found for these input values. 
        } else if (message.contains("no service requests found")) {
            log.warn(message);
            return null;
        } else {
            return message;
        }
    }
}
