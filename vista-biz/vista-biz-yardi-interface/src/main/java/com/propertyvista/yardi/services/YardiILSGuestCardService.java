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

import com.yardi.entity.ils.Availability;
import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.ils.PhysicalProperty;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.processors.YardiILSMarketingProcessor;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;

public class YardiILSGuestCardService extends YardiAbstractService {

    private static final Logger log = LoggerFactory.getLogger(YardiILSGuestCardService.class);

    private static class SingletonHolder {
        public static final YardiILSGuestCardService INSTANCE = new YardiILSGuestCardService();
    }

    private YardiILSGuestCardService() {
    }

    public static YardiILSGuestCardService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /** Update availability for specific unit */
    // TODO - move to ILS/GuestCard v4 - UnitAvailability_LoginByUnit
    public void updateUnitAvailability(PmcYardiCredential yc, final AptUnit aptUnit) throws YardiServiceException, RemoteException {
        YardiILSGuestCardStub stub = ServerSideFactory.create(YardiILSGuestCardStub.class);
        String propertyId = aptUnit.building().propertyCode().getValue();
        PhysicalProperty marketingInfo = stub.getPropertyMarketingInfo(yc, propertyId);

        // process new availability data
        for (ILSUnit ilsUnit : marketingInfo.getProperty().get(0).getILSUnit()) {
            if (aptUnit.info().number().getValue("").equals(ilsUnit.getUnit().getInformation().get(0).getUnitID())) {
                final Availability avail = ilsUnit.getAvailability();
                log.info("New Unit Availability: {}: {}", aptUnit.getStringView(), (avail == null || avail.getVacateDate() == null ? "Not " : "") + "Available");

                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
                    @Override
                    public Void execute() throws YardiServiceException {
                        new YardiILSMarketingProcessor().updateAvailability(aptUnit, avail);
                        return null;
                    }
                });
                break;
            }
        }
    }
}
