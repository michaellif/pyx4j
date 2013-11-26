/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 3, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.ils.PhysicalProperty;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;

public class YardiILSGuestCardService extends YardiAbstractService {
    private static final Logger log = LoggerFactory.getLogger(YardiILSGuestCardService.class);

    private static class SingletonHolder {
        public static final YardiILSGuestCardService INSTANCE = new YardiILSGuestCardService();
    }

    public static YardiILSGuestCardService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void getUnitAvailability(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        if (VistaDeployment.getPmcYardiBuildings(yc).size() == 0) {
            return;
        }

        YardiILSGuestCardStub stub = ServerSideFactory.create(YardiILSGuestCardStub.class);

        log.info("Getting marketing info for property {}", propertyId);
        PhysicalProperty marketingInfo = stub.getPropertyMarketingInfo(yc, propertyId);
    }
}
