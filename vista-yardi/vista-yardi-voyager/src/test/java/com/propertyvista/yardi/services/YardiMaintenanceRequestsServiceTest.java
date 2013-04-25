/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.yardi.entity.maintenance.ServiceRequests;
import com.yardi.ws.operations.GetServiceRequest_Search;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.settings.PmcYardiCredential.Platform;

@Ignore
public class YardiMaintenanceRequestsServiceTest {

    @Before
    public void init() throws Exception {
        VistaTestDBSetup.init();

        Persistence.service().startBackgroundProcessTransaction();

        Lifecycle.startElevatedUserContext();

        NamespaceManager.setNamespace("t" + System.currentTimeMillis());

        TestLifecycle.testSession(null, VistaBasicBehavior.CRM);
        TestLifecycle.testNamespace(NamespaceManager.getNamespace());
        TestLifecycle.beginRequest();
    }

    @After
    public void end() {
        Persistence.service().commit();
    }

    @Test
    public void testPostRequests() throws YardiServiceException, IOException, JAXBException {
        PmcYardiCredential yc = EntityFactory.create(PmcYardiCredential.class);
        yc.username().setValue("propertyvista-srws");
        yc.credential().setValue("55548");
        yc.serverName().setValue("aspdb04");
        yc.database().setValue("afqoml_live");
        yc.platform().setValue(Platform.SQL);
        yc.maintenanceRequestsServiceURL().setValue("https://www.iyardiasp.com/8223thirddev/webservices/itfservicerequests.asmx");

        //YardiMaintenanceRequestsService.getInstance().getOpenMaintenanceRequests(yc, "prvista1", "t0005339");
        //YardiMaintenanceRequestsService.getInstance().getClosedMaintenanceRequests(yc, "prvista1", "t0005339");
        YardiMaintenanceRequestsService.getInstance().loadMeta(yc);
        GetServiceRequest_Search params = new GetServiceRequest_Search();
        params.setYardiPropertyId("prvista1");
        YardiMaintenanceRequestsService.getInstance().getRequestsByParameters(yc, params);

        //Mandatory predefined info which should exist in Yardi:
        //1. Property should exist
        //2. Unit should exist
        //3. Tenant should exist
        //4. Vendor should exist
        //YardiMaintenanceRequestsService.getInstance().postMaintenanceRequest(createServiceRequest());
//        YardiMaintenanceRequestsService.getInstance().postMaintenanceRequest(createShortServiceRequest());
    }

    private ServiceRequests createServiceRequest() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("ServiceRequest.xml", getClass()));
        ServiceRequests requests = MarshallUtil.unmarshal(ServiceRequests.class, xml);
        return requests;
    }

    private ServiceRequests createShortServiceRequest() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("ShortServiceRequest.xml", getClass()));
        ServiceRequests requests = MarshallUtil.unmarshal(ServiceRequests.class, xml);
        return requests;
    }
}
