/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.maintenance.ServiceRequests;
import com.yardi.entity.maintenance.meta.CustomConfig;
import com.yardi.entity.maintenance.meta.YardiMaintenanceConfigMeta;
import com.yardi.ws.operations.CreateOrEditServiceRequests;
import com.yardi.ws.operations.CreateOrEditServiceRequestsResponse;
import com.yardi.ws.operations.GetCustomValues;
import com.yardi.ws.operations.GetCustomValuesResponse;
import com.yardi.ws.operations.GetServiceRequest_Search;
import com.yardi.ws.operations.GetServiceRequest_SearchResponse;
import com.yardi.ws.operations.ServiceRequestXml_type0;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.bean.Messages;

public class YardiMaintenanceRequestsService {

    private static final Logger log = LoggerFactory.getLogger(YardiMaintenanceRequestsService.class);

    private static class SingletonHolder {
        public static final YardiMaintenanceRequestsService INSTANCE = new YardiMaintenanceRequestsService();
    }

    public static YardiMaintenanceRequestsService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public YardiMaintenanceConfigMeta getMaintenanceConfigMeta(PmcYardiCredential yc) throws YardiServiceException {
        try {

            YardiClient c = new YardiClient(yc.maintenanceRequestsServiceURL().getValue());
            c.transactionIdStart();
            c.setCurrentAction(Action.GetCustomValues);

            GetCustomValues params = new GetCustomValues();
            params.setUserName(yc.username().getValue());
            params.setPassword(yc.credential().getValue());
            params.setServerName(yc.serverName().getValue());
            params.setDatabase(yc.database().getValue());
            params.setPlatform(yc.platform().getValue().name());
            params.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);

            GetCustomValuesResponse response = c.getMaintenanceRequestsService().getCustomValues(params);
            String xml = response.getGetCustomValuesResult().getExtraElement().toString();

            log.info("GetCustomValues: {}", xml);
            if (Messages.isErrorMessageResponse(xml)) {
                throw new YardiServiceException(getErrors(xml));
            }

            CustomConfig config = MarshallUtil.unmarshal(CustomConfig.class, xml);
            return config.getCustomValues();

        } catch (JAXBException e) {
            throw new Error(e);
        } catch (RemoteException e) {
            throw new Error(e);
        }
    }

    public ServiceRequests getOpenMaintenanceRequests(PmcYardiCredential yc, String propertyCode, String residentCode) throws YardiServiceException {
        return getRequestsByParameters(yc, "Open", propertyCode, residentCode);
    }

    public ServiceRequests getClosedMaintenanceRequests(PmcYardiCredential yc, String propertyCode, String residentCode) throws YardiServiceException {
        return getRequestsByParameters(yc, "Closed", propertyCode, residentCode);
    }

    private ServiceRequests getRequestsByParameters(PmcYardiCredential yc, String openOrClosed, String propertyCode, String residentCode)
            throws YardiServiceException {
        try {
            Validate.notEmpty(residentCode, "residentCode can not be empty or null");
            Validate.notEmpty(propertyCode, "propertyCode can not be empty or null");

            YardiClient c = new YardiClient(yc.maintenanceRequestsServiceURL().getValue());
            c.transactionIdStart();
            c.setCurrentAction(Action.GetServiceRequests);

            GetServiceRequest_Search params = new GetServiceRequest_Search();
            params.setUserName(yc.username().getValue());
            params.setPassword(yc.credential().getValue());
            params.setServerName(yc.serverName().getValue());
            params.setDatabase(yc.database().getValue());
            params.setPlatform(yc.platform().getValue().name());
            params.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);
            params.setYardiPropertyId(propertyCode);
            params.setResidentCode(residentCode);
            params.setOpenOrClosed(openOrClosed);

            GetServiceRequest_SearchResponse response = c.getMaintenanceRequestsService().getServiceRequest_Search(params);
            String xml = response.getGetServiceRequest_SearchResult().getExtraElement().toString();

            log.info("GetServiceRequests: {}", xml);
            if (Messages.isErrorMessageResponse(xml)) {
                throw new YardiServiceException(getErrors(xml));
            }

            ServiceRequests requests = MarshallUtil.unmarshal(ServiceRequests.class, xml);
            return requests;

        } catch (JAXBException e) {
            throw new Error(e);
        } catch (RemoteException e) {
            throw new Error(e);
        }
    }

    public void postMaintenanceRequests(PmcYardiCredential yc, ServiceRequests requests) throws YardiServiceException {
        try {
            Validate.notNull(requests, "requests can not be null");

            YardiClient c = new YardiClient(yc.maintenanceRequestsServiceURL().getValue());
            c.transactionIdStart();
            c.setCurrentAction(Action.CreateOrEditServiceRequests);

            CreateOrEditServiceRequests params = new CreateOrEditServiceRequests();
            params.setUserName(yc.username().getValue());
            params.setPassword(yc.credential().getValue());
            params.setServerName(yc.serverName().getValue());
            params.setDatabase(yc.database().getValue());
            params.setPlatform(yc.platform().getValue().name());
            params.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);

            ServiceRequestXml_type0 serviceRequestXml = new ServiceRequestXml_type0();
            String rawXml = MarshallUtil.marshall(requests);
            log.info(rawXml);
            OMElement element = AXIOMUtil.stringToOM(rawXml);
            serviceRequestXml.setExtraElement(element);
            params.setServiceRequestXml(serviceRequestXml);

            CreateOrEditServiceRequestsResponse response = c.getMaintenanceRequestsService().createOrEditServiceRequests(params);
            String responseXml = response.getCreateOrEditServiceRequestsResult().getExtraElement().toString();
            log.info("CreateOrEditServiceRequests: {}", responseXml);

            if (Messages.isErrorMessageResponse(responseXml)) {
                throw new YardiServiceException(getErrors(responseXml));
            }
        } catch (JAXBException e) {
            throw new Error(e);
        } catch (RemoteException e) {
            throw new Error(e);
        } catch (XMLStreamException e) {
            throw new Error(e);
        }
    }

    private String getErrors(String xml) {
        String[] errors = StringUtils.substringsBetween(xml, "<Error>", "</Error>");

        StringBuilder sb = new StringBuilder();
        for (String error : errors) {
            sb.append(error).append("\n");
        }
        return sb.toString();
    }
}
