/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.biz.financial.maintenance.yardi;

import java.util.List;

import org.apache.commons.lang.Validate;

import com.yardi.entity.maintenance.ServiceRequest;
import com.yardi.entity.maintenance.ServiceRequests;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.yardi.mapper.MaintenanceRequestMapper;
import com.propertyvista.yardi.services.YardiMaintenanceRequestsService;

public class YardiIntegrationMaintenanceAgent {

    private static class SingletonHolder {
        public static final YardiIntegrationMaintenanceAgent INSTANCE = new YardiIntegrationMaintenanceAgent();
    }

    static YardiIntegrationMaintenanceAgent instance() {
        return SingletonHolder.INSTANCE;
    }

    void postMaintenanceRequest(MaintenanceRequest maintenanceRequest) throws YardiServiceException {
        Validate.notNull(maintenanceRequest, "maintenanceRequest can not be null");
        Validate.notNull(maintenanceRequest.leaseParticipant(), "leaseParticipant can not be null");

        ServiceRequest serviceRequest = new MaintenanceRequestMapper().map(maintenanceRequest);

        YardiMaintenanceRequestsService.getInstance().postMaintenanceRequests(getYardiCredential(), wrapToRequests(serviceRequest));
    }

    List<MaintenanceRequest> getClosedMaintenanceRequests(Tenant tenant) throws YardiServiceException {
        Validate.notNull(tenant, "tenant can not be null");

        String propertyCode = tenant.lease().unit().building().propertyCode().getValue();
        String residentCode = tenant.participantId().getValue();
        ServiceRequests serviceRequests = YardiMaintenanceRequestsService.getInstance().getClosedMaintenanceRequests(getYardiCredential(), propertyCode,
                residentCode);

        return new MaintenanceRequestMapper().map(serviceRequests);
    }

    List<MaintenanceRequest> getOpenMaintenanceRequests(Tenant tenant) throws YardiServiceException {
        Validate.notNull(tenant, "tenant can not be null");

        String propertyCode = tenant.lease().unit().building().propertyCode().getValue();
        String residentCode = tenant.participantId().getValue();
        ServiceRequests serviceRequests = YardiMaintenanceRequestsService.getInstance().getOpenMaintenanceRequests(getYardiCredential(), propertyCode,
                residentCode);

        return new MaintenanceRequestMapper().map(serviceRequests);
    }

    void cancelMaintenanceRequest(MaintenanceRequest maintenanceRequest) throws YardiServiceException {
        Validate.notNull(maintenanceRequest, "maintenanceRequest can not be null");

        ServiceRequest serviceRequest = new MaintenanceRequestMapper().mapStrict(maintenanceRequest);
        serviceRequest.setCurrentStatus("Canceled");

        YardiMaintenanceRequestsService.getInstance().postMaintenanceRequests(getYardiCredential(), wrapToRequests(serviceRequest));
    }

    private ServiceRequests wrapToRequests(ServiceRequest request) {
        ServiceRequests requests = new ServiceRequests();
        requests.getServiceRequest().add(request);
        return requests;
    }

    PmcYardiCredential getYardiCredential() {
        return VistaDeployment.getPmcYardiCredential();
    }

}
