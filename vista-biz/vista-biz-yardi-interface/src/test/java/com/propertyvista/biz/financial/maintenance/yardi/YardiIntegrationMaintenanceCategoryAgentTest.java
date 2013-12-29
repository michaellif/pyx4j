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

import java.rmi.RemoteException;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.settings.PmcYardiCredential.Platform;
import com.propertyvista.yardi.services.YardiMaintenanceRequestsService;

public class YardiIntegrationMaintenanceCategoryAgentTest {

    /**
     * @param args
     * @throws YardiServiceException
     */
    public static void main(String[] args) throws YardiServiceException, RemoteException {

        PmcYardiCredential yc = EntityFactory.create(PmcYardiCredential.class);
        yc.username().setValue("propertyvista-srws");
        yc.password().number().setValue("55548");
        yc.serverName().setValue("aspdb04");
        yc.database().setValue("afqoml_live");
        yc.platform().setValue(Platform.SQL);
        yc.maintenanceRequestsServiceURL().setValue("https://www.iyardiasp.com/8223thirddev/webservices/itfservicerequests.asmx");

        YardiMaintenanceRequestsService.getInstance().loadMaintenanceRequestMeta(yc);
    }

}
