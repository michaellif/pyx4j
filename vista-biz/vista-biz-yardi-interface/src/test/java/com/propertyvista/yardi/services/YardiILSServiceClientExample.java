/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 26, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicReference;

import com.yardi.entity.guestcard40.MarketingSources;
import com.yardi.entity.ils.PhysicalProperty;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.biz.system.yardi.YardiConfigurationFacade;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.server.config.DevYardiCredentials;
import com.propertyvista.server.config.DevYardiCredentials.YardiCredentialId;
import com.propertyvista.test.mock.security.PasswordEncryptorFacadeMock;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
import com.propertyvista.yardi.stubs.YardiStubFactory;

public class YardiILSServiceClientExample {

    public static void main(String[] args) throws YardiServiceException, RemoteException {
        ServerSideFactory.register(PasswordEncryptorFacade.class, PasswordEncryptorFacadeMock.class);
        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(DatabaseType.HSQLDB));

        //SystemConfig.instance().setProxy(SystemConfig.getDevProxy());
        PmcYardiCredential yc = DevYardiCredentials.getTestPmcYardiCredential(YardiCredentialId.localNew);

        String propertyId = yc.propertyListCodes().getValue();

        // propertyId = ".vsl";

        ServerSideFactory.create(YardiConfigurationFacade.class).startYardiTimer();

        if (true) {
            MarketingSources properties = YardiStubFactory.create(YardiILSGuestCardStub.class).getYardiMarketingSources(yc, propertyId);
            if (properties.getProperty() != null) {
                System.out.println("Got " + properties.getProperty().size() + " properties");
            }
        }

        if (false) {
            PhysicalProperty properties = YardiStubFactory.create(YardiILSGuestCardStub.class).getPropertyMarketingInfo(yc, propertyId);

            if (properties.getProperty() != null) {
                System.out.println("Got " + properties.getProperty().size() + " properties");
            }
        }
        AtomicReference<Long> yardiTime = new AtomicReference<>();
        AtomicReference<Long> maxRequestTime = new AtomicReference<>();
        ServerSideFactory.create(YardiConfigurationFacade.class).stopYardiTimer(yardiTime, maxRequestTime);
        System.out.println("yardiTime = " + yardiTime + " (" + TimeUtils.durationFormatSeconds((int) (yardiTime.get() / Consts.SEC2MSEC)) + ")");
    }
}
