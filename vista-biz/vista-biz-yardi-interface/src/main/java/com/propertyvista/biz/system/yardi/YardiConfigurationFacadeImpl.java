/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.system.yardi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.yardi.entity.guestcard40.PropertyMarketingSources;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.stubs.YardiGuestManagementStub;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

public class YardiConfigurationFacadeImpl implements YardiConfigurationFacade {

    @Override
    public void initYardiCredentialCache() {
        YardiCredentials.init();
    }

    @Override
    public void clearYardiCredentialCache() {
        YardiCredentials.clear();
    }

    @Override
    public PmcYardiCredential getYardiCredential(Building building) {
        return YardiCredentials.get(building);
    }

    @Override
    public List<PmcYardiCredential> getYardiCredentials() {
        return YardiCredentials.getAll();
    }

    @Override
    public void startYardiTimer() {
        YardiExecutionTimer.start();
    }

    @Override
    public void incrementYardiTimer(long interval) {
        YardiExecutionTimer.add(interval);
    }

    @Override
    public long stopYardiTimer() {
        return YardiExecutionTimer.stop();
    }

    @Override
    public long stopYardiTimer(AtomicReference<Long> maxTimeResult) {
        return YardiExecutionTimer.stop(maxTimeResult);
    }

    @Override
    public void yardiRequestCompleted(long interval) {
        YardiExecutionTimer.requestCompleted(interval);
    }

    @Override
    public List<String> retrievePropertyCodes(PmcYardiCredential yc, ExecutionMonitor executionMonitor) throws YardiServiceException {
        // create master-list of all configured properties (this assumes that ILS is the master interface for property configurations)
        List<String> masterPropertyList = new ArrayList<>();

        YardiGuestManagementStub ilsStub = ServerSideFactory.create(YardiGuestManagementStub.class);
        for (com.propertyvista.yardi.beans.Property property : ilsStub.getPropertyConfigurations(yc).getProperties()) {
            masterPropertyList.add(property.getCode());
        }

        List<String> propertyCodes = new ArrayList<>();
        if (yc.propertyListCodes().isNull()) {
            propertyCodes.addAll(masterPropertyList);
        } else {
            // validate and  convert property list codes into a list of property codes
            for (String propertyListCode : yc.propertyListCodes().getValue().trim().split("\\s*,\\s*")) {
                for (PropertyMarketingSources sources : ilsStub.getYardiMarketingSources(yc, propertyListCode).getProperty()) {
                    String propertyCode = sources.getPropertyCode();
                    if (!masterPropertyList.contains(propertyCode)) {
                        executionMonitor.addErredEvent("YardiConfig", "Property code not found in the properties configured for ILS: " + propertyCode);
                    }
                    propertyCodes.add(propertyCode);
                }
            }
        }

        // B&P sanity check - ensure selected properties available in B&P PropertyConfigurations
        YardiResidentTransactionsStub bpStub = ServerSideFactory.create(YardiResidentTransactionsStub.class);
        try {
            List<String> bpPropertyList = new ArrayList<>();
            for (com.propertyvista.yardi.beans.Property property : bpStub.getPropertyConfigurations(yc).getProperties()) {
                bpPropertyList.add(property.getCode());
            }

            for (Iterator<String> it = propertyCodes.iterator(); it.hasNext();) {
                String propertyCode = it.next();
                if (!bpPropertyList.contains(propertyCode)) {
                    executionMonitor.addErredEvent("YardiConfig", "Property code configured for ILS not found in the B&P property list: " + propertyCode);
                    it.remove();
                }
            }
        } catch (RemoteException e) {
            throw new YardiServiceException(e);
        }

        return propertyCodes;
    }
}
