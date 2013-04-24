/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 12, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.biz.financial.maintenance.yardi;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.settings.PmcYardiCredential.Platform;
import com.propertyvista.domain.tenant.lease.Tenant;

public class YardiIntegrationMaintenanceAgentTest {

    /**
     * @param args
     * @throws YardiServiceException
     */
    public static void main(String[] args) throws YardiServiceException {
        PmcYardiCredential yc = EntityFactory.create(PmcYardiCredential.class);
        yc.username().setValue("propertyvista-srws");
        yc.credential().setValue("55548");
        yc.serverName().setValue("aspdb04");
        yc.database().setValue("afqoml_live");
        yc.platform().setValue(Platform.SQL);
        yc.maintenanceRequestsServiceURL().setValue("https://www.iyardiasp.com/8223thirddev/webservices/itfservicerequests.asmx");

        //maintenanceAgent.getOpenMaintenanceRequests(createTenant());
        //maintenanceAgent.getClosedMaintenanceRequests(createTenant());
        //maintenanceAgent.postMaintenanceRequest(createMaintenanceRequest(createTenant()));
        //maintenanceAgent.cancelMaintenanceRequest(createMaintenanceRequest(createTenant()));
    }

    private static Tenant createTenant() {
        Tenant tenant = EntityFactory.create(Tenant.class);
        tenant.participantId().setValue("t0005339");
        tenant.lease().unit().building().propertyCode().setValue("prvista1");
        tenant.lease().unit().info().number().setValue("145");
        tenant.customer().person().name().firstName().setValue("Bob Wild");
        tenant.customer().person().homePhone().setValue("22233344");
        tenant.customer().person().email().setValue("bob@i.ua");
        return tenant;
    }

    private static MaintenanceRequest createMaintenanceRequest(Tenant tenant) {
        MaintenanceRequest req = EntityFactory.create(MaintenanceRequest.class);
        req.id().setValue(new Key(10241));
        req.leaseParticipant().set(tenant);
        req.description().setValue("test description #2");
        req.category().name().setValue("HVAC");

        MaintenanceRequestCategory subCategory = EntityFactory.create(MaintenanceRequestCategory.class);
        subCategory.name().setValue("Vents");
        req.category().subCategories().add(subCategory);
        req.permissionToEnter().setValue(true);

        //req.submitted().setValue(new LogicalDate());
        req.updated().setValue(new LogicalDate());
        // TODO req.status().setValue(MaintenanceRequestStatus.Submitted);
        return req;
    }
}
