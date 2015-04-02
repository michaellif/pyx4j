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
 */
package com.propertyvista.biz.system.yardi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.yardi.entity.guestcard40.PropertyMarketingSources;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.beans.Property;
import com.propertyvista.yardi.mappers.BuildingsMapper;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;
import com.propertyvista.yardi.stubs.YardiStubFactory;

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
    public Collection<PmcYardiCredential> getYardiCredentials() {
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
    public void stopYardiTimer(AtomicReference<Long> yardiTimeTotal, AtomicReference<Long> maxTimeResult) {
        YardiExecutionTimer.stop(yardiTimeTotal, maxTimeResult);
    }

    @Override
    public void yardiRequestCompleted(long interval) {
        YardiExecutionTimer.requestCompleted(interval);
    }

    @Override
    public List<String> retrievePropertyCodes(PmcYardiCredential yc, ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException {
        List<String> propertyCodes = new ArrayList<>();

        if (yc.propertyListCodes().isNull()) {
            // create master-list of all configured properties (this assumes that ILS is the master interface for property configurations)
            for (Property property : YardiStubFactory.create(YardiILSGuestCardStub.class).getPropertyConfigurations(yc).getProperties()) {
                propertyCodes.add(BuildingsMapper.getPropertyCode(property.getCode()));
            }
        } else {
            // validate and convert property list codes into a list of property codes
            for (String propertyListCode : yc.propertyListCodes().getValue().trim().split("\\s*,\\s*")) {
                List<PropertyMarketingSources> sourceList = null;
                try {
                    sourceList = YardiStubFactory.create(YardiILSGuestCardStub.class).getYardiMarketingSources(yc, propertyListCode).getProperty();
                    for (PropertyMarketingSources sources : sourceList) {
                        propertyCodes.add(BuildingsMapper.getPropertyCode(sources.getPropertyCode()));
                    }
                } catch (Throwable t) {
                    String error = "Error processing '" + propertyListCode + "': " + t.getMessage();
                    // report configuration issues
                    executionMonitor.addErredEvent("YardiConfig", error);
                    // expected checked exceptions: YardiServiceException, RemoteException; see if we need to interfere...
                    if (t instanceof YardiServiceException) {
                        // Two possible cases are known:
                        // 1. YardiPropertyNoAccessException - non-existent propertyListCode
                        // 2. YardiInterfaceNotConfiguredException - ILS/Guestcard not configured for a property in the list
                        // We send config notification to Notification.YardiSynchronisation users
                        ServerSideFactory.create(NotificationFacade.class).yardiConfigurationError(error);
                        // Now see if we should stop or continue...
                        if (t instanceof YardiPropertyNoAccessException) {
                            // Possible cases here (we continue anyway):
                            // - propertyListCode is invalid - don't care
                            // - building/list has been moved - the building/list will be suspended after we continue
                            continue;
                        }
                        // Can't continue on YardiInterfaceNotConfiguredException because the entire list will be suspended
                    }
                    // Everything else, including RemoteException, must be handled by the callers.
                    throw t;
                }
            }
        }

        if (propertyCodes.size() > 0) {
            // B&P sanity check - ensure selected properties available in B&P PropertyConfigurations
            List<String> bpPropertyList = new ArrayList<>();
            Properties propList = YardiStubFactory.create(YardiResidentTransactionsStub.class).getPropertyConfigurations(yc);
            if (propList != null) {
                for (Property property : propList.getProperties()) {
                    bpPropertyList.add(BuildingsMapper.getPropertyCode(property.getCode()));
                }
            }

            List<String> bpMissingList = new ArrayList<>();
            for (String propertyCode : propertyCodes) {
                if (!bpPropertyList.contains(propertyCode)) {
                    bpMissingList.add(propertyCode);
                }
            }

            if (bpMissingList.size() > 0) {
                bpPropertyList.removeAll(bpMissingList);
                StringBuilder error = new StringBuilder("Properties not configured for B&P interface:");
                for (String code : bpMissingList) {
                    error.append(" ").append(code);
                }
                executionMonitor.addErredEvent("YardiConfig", error.toString());
                ServerSideFactory.create(NotificationFacade.class).yardiConfigurationError(error.toString());
            }
        }

        return propertyCodes;
    }
}
