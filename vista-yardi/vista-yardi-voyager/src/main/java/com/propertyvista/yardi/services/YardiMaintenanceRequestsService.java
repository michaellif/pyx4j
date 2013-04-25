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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.maintenance.ServiceRequest;
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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.financial.maintenance.yardi.YardiMaintenanceIntegrationAgent;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.shared.config.VistaFeatures;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.bean.Messages;
import com.propertyvista.yardi.mapper.MaintenanceRequestMapper;

/*
 * The agent is responsible for persisting all imported data in the DB by requests from MaintenanceFacade.
 * The requesting facade will then grab the data directly from DB.
 */
public class YardiMaintenanceRequestsService {

    private static final Logger log = LoggerFactory.getLogger(YardiMaintenanceRequestsService.class);

    private final Map<String, Date> lastTicketUpdate = new HashMap<String, Date>();

    private final Map<String, Date> lastMetaUpdate = new HashMap<String, Date>();

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'H:m:s");

    private static class SingletonHolder {
        public static final YardiMaintenanceRequestsService INSTANCE = new YardiMaintenanceRequestsService();
    }

    public static YardiMaintenanceRequestsService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Date getMetaTimestamp() {
        return lastMetaUpdate.get(NamespaceManager.getNamespace());
    }

    public Date getTicketTimestamp() {
        return lastTicketUpdate.get(NamespaceManager.getNamespace());
    }

    /*
     * We grab Metadata on first request and then every time a new category found in requested ticket.
     */
    public void loadMaintenanceRequestMeta() throws YardiServiceException {
        assert VistaFeatures.instance().yardiIntegration();

        PmcYardiCredential yc = getYardiCredential();
        if (lastMetaUpdate.get(NamespaceManager.getNamespace()) == null) {
            loadMeta(yc);
        }
    }

    /*
     * We only grab tickets that have been modified since last request. If last request date is empty
     * we get the latest update date from previously persisted tickets.
     */
    public void loadMaintenanceRequests() throws YardiServiceException {
        assert VistaFeatures.instance().yardiIntegration();
        PmcYardiCredential yc = getYardiCredential();
        Date lastModified = lastTicketUpdate.get(NamespaceManager.getNamespace());
        if (lastModified == null) {
            lastModified = YardiMaintenanceIntegrationAgent.getLastModifiedDate();
        }
        loadRequests(yc, lastModified);
    }

    public void postMaintenanceRequest(MaintenanceRequest request) throws YardiServiceException {
        ServiceRequest serviceRequest = new MaintenanceRequestMapper().map(request);
        ServiceRequests requests = new ServiceRequests();
        requests.getServiceRequest().add(serviceRequest);

        YardiMaintenanceRequestsService.getInstance().postMaintenanceRequests(getYardiCredential(), requests);
    }

    protected void loadRequests(final PmcYardiCredential yc, Date fromDate) throws YardiServiceException {
        GetServiceRequest_Search params = new GetServiceRequest_Search();
        // ensure buildings are available
        String propertyList = new YardiMaintenanceProcessor().getProprtyList();
        if (propertyList == null || propertyList.length() == 0) {
            return;
        }
        params.setYardiPropertyId(propertyList);
        final Date now = SystemDateManager.getDate();
        if (fromDate != null) {
            params.setFromDate(dateFormat.format(fromDate));
        }
        final ServiceRequests newRequests = YardiMaintenanceRequestsService.getInstance().getRequestsByParameters(yc, params);
        new UnitOfWork(TransactionScopeOption.Nested).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                YardiMaintenanceProcessor processor = new YardiMaintenanceProcessor();
                for (ServiceRequest request : newRequests.getServiceRequest()) {
                    MaintenanceRequest mr = processor.mergeRequest(request);
                    if (mr != null) {
                        Persistence.service().persist(mr);
                    }
                }
                lastTicketUpdate.put(NamespaceManager.getNamespace(), now);
                log.debug("loaded requests: {}", newRequests.getServiceRequest().size());
                return null;
            }
        });
    }

    protected void loadMeta(final PmcYardiCredential yc) throws YardiServiceException {
        final YardiMaintenanceConfigMeta meta = YardiMaintenanceRequestsService.getInstance().getMaintenanceConfigMeta(yc);
        new UnitOfWork(TransactionScopeOption.Nested).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                Date now = SystemDateManager.getDate();
                YardiMaintenanceProcessor processor = new YardiMaintenanceProcessor();
                // categories
                Persistence.service().persist(processor.mergeCategories(meta.getCategories()));
                log.debug("loaded categories: {}", meta.getCategories().getCategory().size());
                // statuses
                List<MaintenanceRequestStatus> statuses = processor.mergeStatuses(meta.getStatuses());
                if (statuses != null) {
                    Persistence.service().persist(statuses);
                }
                log.debug("loaded statuses: {}", statuses.size());
                // priorities
                List<MaintenanceRequestPriority> priorities = processor.mergePriorities(meta.getPriorities());
                if (priorities != null) {
                    Persistence.service().persist(priorities);
                }
                log.debug("loaded priorities: {}", priorities.size());
                lastMetaUpdate.put(NamespaceManager.getNamespace(), now);
                return null;
            }
        });
    }

    protected PmcYardiCredential getYardiCredential() {
        return VistaDeployment.getPmcYardiCredential();
    }

    protected YardiMaintenanceConfigMeta getMaintenanceConfigMeta(PmcYardiCredential yc) throws YardiServiceException {
        try {

            YardiClient client = ServerSideFactory.create(YardiClient.class);
            client.setPmcYardiCredential(yc);
            client.transactionIdStart();
            client.setCurrentAction(Action.GetCustomValues);

            GetCustomValues params = new GetCustomValues();
            params.setUserName(yc.username().getValue());
            params.setPassword(yc.credential().getValue());
            params.setServerName(yc.serverName().getValue());
            params.setDatabase(yc.database().getValue());
            params.setPlatform(yc.platform().getValue().name());
            params.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);

            GetCustomValuesResponse response = client.getMaintenanceRequestsService().getCustomValues(params);
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

    protected ServiceRequests getRequestsByParameters(PmcYardiCredential yc, GetServiceRequest_Search params) throws YardiServiceException {
        try {
            YardiClient client = ServerSideFactory.create(YardiClient.class);
            client.setPmcYardiCredential(yc);
            client.transactionIdStart();
            client.setCurrentAction(Action.GetServiceRequests);

            params.setUserName(yc.username().getValue());
            params.setPassword(yc.credential().getValue());
            params.setServerName(yc.serverName().getValue());
            params.setDatabase(yc.database().getValue());
            params.setPlatform(yc.platform().getValue().name());
            params.setInterfaceEntity(YardiConstants.MAINTENANCE_INTERFACE_ENTITY);

            GetServiceRequest_SearchResponse response = client.getMaintenanceRequestsService().getServiceRequest_Search(params);
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

    protected void postMaintenanceRequests(PmcYardiCredential yc, ServiceRequests requests) throws YardiServiceException {
        try {
            Validate.notNull(requests, "requests can not be null");

            YardiClient client = ServerSideFactory.create(YardiClient.class);
            client.setPmcYardiCredential(yc);
            client.transactionIdStart();
            client.setCurrentAction(Action.CreateOrEditServiceRequests);

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

            CreateOrEditServiceRequestsResponse response = client.getMaintenanceRequestsService().createOrEditServiceRequests(params);
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
