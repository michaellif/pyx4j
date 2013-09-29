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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.maintenance.ServiceRequest;
import com.yardi.entity.maintenance.ServiceRequests;
import com.yardi.entity.maintenance.meta.YardiMaintenanceConfigMeta;
import com.yardi.ws.operations.requests.GetServiceRequest_Search;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.biz.financial.maintenance.yardi.YardiMaintenanceIntegrationAgent;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.shared.config.VistaFeatures;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.stub.YardiMaintenanceRequestsStub;

/*
 * The agent is responsible for persisting all imported data in the DB by requests from MaintenanceFacade.
 * The requesting facade will then grab the data directly from DB.
 */
public class YardiMaintenanceRequestsService extends YardiAbstractService {

    public static final String lastTicketUpdateCacheKey = "yardi-maintenance-requests-last-ticket-update";

    public static final String lastMetaUpdateCacheKey = "yardi-maintenance-requests-last-meta-update";

    private static final Logger log = LoggerFactory.getLogger(YardiMaintenanceRequestsService.class);

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd H:m:s");

    private static class SingletonHolder {
        public static final YardiMaintenanceRequestsService INSTANCE = new YardiMaintenanceRequestsService();
        static {
            // reset cache on startup
            for (PmcYardiCredential yc : VistaDeployment.getPmcYardiCredentials()) {
                CacheService.remove(getMetaUpdateCacheKey(yc));
                CacheService.remove(getTicketUpdateCacheKey(yc));
            }
        }
    }

    private static String getMetaUpdateCacheKey(PmcYardiCredential yc) {
        return lastMetaUpdateCacheKey + "-" + yc.getPrimaryKey().toString();
    }

    private static String getTicketUpdateCacheKey(PmcYardiCredential yc) {
        return lastTicketUpdateCacheKey + "-" + yc.getPrimaryKey().toString();
    }

    public static YardiMaintenanceRequestsService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Date getMetaTimestamp(PmcYardiCredential yc) {
        return (Date) CacheService.get(getMetaUpdateCacheKey(yc));
    }

    public Date getTicketTimestamp(PmcYardiCredential yc) {
        Date ticketTS = (Date) CacheService.get(getTicketUpdateCacheKey(yc));
        if (ticketTS == null) {
            ticketTS = setTicketTimestamp(YardiMaintenanceIntegrationAgent.getLastModifiedDate(), yc);
        }

        return ticketTS;
    }

    private Date setMetaTimestamp(Date ts, PmcYardiCredential yc) {
        CacheService.put(getMetaUpdateCacheKey(yc), ts);
        return ts;
    }

    private Date setTicketTimestamp(Date ts, PmcYardiCredential yc) {
        Date dateOnly;
        if (ts == null) {
            return dateOnly = new Date(0);
        } else {
            // Yardi provides only date-portion of the ticket update date-time, so we should reset time-portion if set
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(ts);
            cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
            cal.set(GregorianCalendar.MINUTE, 0);
            cal.set(GregorianCalendar.SECOND, 0);
            dateOnly = cal.getTime();
        }
        CacheService.put(getTicketUpdateCacheKey(yc), dateOnly);
        return dateOnly;
    }

    /*
     * We grab Metadata on first request and then every time a new category found in requested ticket.
     */
    public void loadMaintenanceRequestMeta(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration() && VistaFeatures.instance().yardiMaintenance();

        boolean load = false;
        Date metaTS = getMetaTimestamp(yc);
        if (metaTS == null) {
            // load on startup
            load = true;
        } else {
            // wait at least 1 min before reload
            Calendar now = GregorianCalendar.getInstance();
            now.setTime(SystemDateManager.getDate());
            now.add(Calendar.MINUTE, -1);
            load = now.getTime().after(metaTS);
        }
        if (load && VistaDeployment.getPmcYardiBuildings(yc).size() > 0) {
            loadMeta(yc);
        }
    }

    /*
     * We only grab tickets that have been modified since last request. If last request date is empty
     * we get the latest update date from previously persisted tickets.
     */
    public void loadMaintenanceRequests(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration() && VistaFeatures.instance().yardiMaintenance();
        if (VistaDeployment.getPmcYardiBuildings(yc).size() == 0) {
            return;
        }

        YardiMaintenanceRequestsStub stub = ServerSideFactory.create(YardiMaintenanceRequestsStub.class);

        // make sure meta was loaded
        loadMaintenanceRequestMeta(yc);

        Date ticketTS = getTicketTimestamp(yc);
        if (ticketTS != null) {
            // add 1 ms time gap
            ticketTS.setTime(ticketTS.getTime() - 1);
        }

        List<String> propertyCodes = null;
        if (yc.propertyListCodes().isNull()) {
            propertyCodes = getPropertyCodes(stub, yc);
        } else {
            propertyCodes = Arrays.asList(yc.propertyListCodes().getValue().trim().split("\\s*,\\s*"));
        }

        if (propertyCodes != null) {
            for (String propertyCode : propertyCodes) {
                Date lastModified = loadRequests(yc, ticketTS, propertyCode);
                if (getTicketTimestamp(yc).before(lastModified)) {
                    log.info("setting new ticket time stamp {} -> {}", getTicketTimestamp(yc), lastModified.getTime());
                    setTicketTimestamp(lastModified, yc);
                }
            }
        } else {
            log.trace("No PropertyCodes provided");
        }
    }

    public MaintenanceRequest postMaintenanceRequest(PmcYardiCredential yc, MaintenanceRequest request) throws YardiServiceException, RemoteException {
        YardiMaintenanceRequestsStub stub = ServerSideFactory.create(YardiMaintenanceRequestsStub.class);

        ServiceRequest serviceRequest = new YardiMaintenanceProcessor().convertRequest(request);
        ServiceRequests requests = new ServiceRequests();
        requests.getServiceRequest().add(serviceRequest);

        ServiceRequests result = stub.postMaintenanceRequests(yc, requests);
        ServiceRequest sr = result.getServiceRequest().get(0);
        // In case of error Yardi may return request xml with invalid scheme...
        if (!isResponseValid(sr)) {
            throw new YardiServiceException("Posting request failed");
        }
        MaintenanceRequest mr = new YardiMaintenanceProcessor().updateRequest(yc, request, sr);
        Persistence.service().persist(mr);
        return mr;
    }

    protected Date loadRequests(final PmcYardiCredential yc, Date fromDate, String propertyCode) throws YardiServiceException, RemoteException {
        YardiMaintenanceRequestsStub stub = ServerSideFactory.create(YardiMaintenanceRequestsStub.class);
        GetServiceRequest_Search params = new GetServiceRequest_Search();
        params.setYardiPropertyId(propertyCode);
        if (fromDate != null) {
            params.setFromDate(dateFormat.format(fromDate));
        }
        log.info("Getting tickets for {} modified after {}", params.getYardiPropertyId(), params.getFromDate());
        final ServiceRequests newRequests = stub.getRequestsByParameters(yc, params);
        return new UnitOfWork(TransactionScopeOption.Nested).execute(new Executable<Date, YardiServiceException>() {
            @Override
            public Date execute() throws YardiServiceException {
                Date lastUpdate = new Date(0);
                YardiMaintenanceProcessor processor = new YardiMaintenanceProcessor();
                for (ServiceRequest request : newRequests.getServiceRequest()) {
                    // When no records found Yardi returns response xml with invalid scheme...
                    if (!isResponseValid(request)) {
                        log.debug("Invalid request skipped");
                        continue;
                    }
                    try {
                        MaintenanceRequest mr = processor.mergeRequest(yc, request);
                        Persistence.service().persist(mr);
                        // get the newest update time
                        if (lastUpdate.before(mr.updated().getValue())) {
                            lastUpdate.setTime(mr.updated().getValue().getTime());
                        }
                    } catch (YardiServiceException e) {
                        // just a warning, don't break the loop
                        log.warn("Request processing failed: {}", e);
                    }
                }

                log.info("loaded requests: {}; last modified: {}", newRequests.getServiceRequest().size(), lastUpdate);
                return lastUpdate;
            }
        });
    }

    protected void loadMeta(final PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        YardiMaintenanceRequestsStub stub = ServerSideFactory.create(YardiMaintenanceRequestsStub.class);
        final YardiMaintenanceConfigMeta yardiMeta = stub.getMaintenanceConfigMeta(yc);
        final MaintenanceRequestMetadata meta = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(
                VistaDeployment.getPmcYardiBuildings(yc).get(0));
        new UnitOfWork(TransactionScopeOption.Nested).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                Date now = SystemDateManager.getDate();
                YardiMaintenanceProcessor processor = new YardiMaintenanceProcessor();
                // categories
                processor.mergeCategories(yardiMeta.getCategories(), meta);
                Persistence.service().persist(meta.rootCategory());
                log.info("loaded categories: {}", yardiMeta.getCategories().getCategory().size());
                // statuses
                processor.mergeStatuses(yardiMeta.getStatuses(), meta);
                log.info("loaded statuses: {}", meta.statuses().size());
                // priorities
                processor.mergePriorities(yardiMeta.getPriorities(), meta);
                log.info("loaded priorities: {}", meta.priorities().size());
                Persistence.service().persist(meta);
                setMetaTimestamp(now, yc);

                return null;
            }
        });
    }

    public List<String> getPropertyCodes(YardiMaintenanceRequestsStub stub, PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        List<String> propertyCodes = new ArrayList<String>();
        Properties properties = stub.getPropertyConfigurations(yc);
        for (com.propertyvista.yardi.bean.Property property : properties.getProperties()) {
            if (StringUtils.isNotEmpty(property.getCode())) {
                propertyCodes.add(property.getCode());
            }
        }
        return propertyCodes;
    }

    private boolean isResponseValid(ServiceRequest request) {
        // When Yardi has problems it returns invalid response xml with undocumented Error element inside !?
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
        if (request.getServiceRequestId() == null || request.getServiceRequestId() == 0) {
            return false;
        }

        return true;
    }
}
