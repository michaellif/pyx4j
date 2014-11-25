/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.stub.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.beans.Messages;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.manager.YardiConfigurationManager;
import com.propertyvista.yardi.services.YardiHandledErrorMessages;

public class YardiMockStubBase {
    private Class<? extends YardiInterface> myInterface;

    @SuppressWarnings("unchecked")
    YardiMockStubBase() {
        for (Class<?> ifc : getClass().getInterfaces()) {
            if (YardiInterface.class.isAssignableFrom(ifc)) {
                myInterface = (Class<? extends YardiInterface>) ifc;
            }
        }
    }

    public String ping(PmcYardiCredential yc) {
        return "OK";
    }

    public String getPluginVersion(PmcYardiCredential yc) {
        return null;
    }

    public void validate(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        YardiMock.server().validate();
    }

    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        YardiMock.server().validate();
        return getProperties();
    }

    // ----------- implementation -----------

    Collection<String> getPropertyCodes() {
        return YardiMock.server().getManager(YardiConfigurationManager.class).getProperties(myInterface);
    }

    // Cycle through yardi buildings to return the subset per configured property codes.
    // If any of property codes is not found, report 'No Access' error.
    List<YardiBuilding> getYardiBuildings() throws YardiServiceException {
        List<YardiBuilding> buildings = new ArrayList<>();
        List<String> propertyCodes = new ArrayList<>(getPropertyCodes());
        for (YardiBuilding building : YardiMock.server().getModel().getBuildings()) {
            int idx = propertyCodes.indexOf(building.buildingId().getValue());
            if (idx >= 0) {
                buildings.add(building);
                propertyCodes.remove(idx);
            }
        }
        if (!propertyCodes.isEmpty()) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess + ":" + propertyCodes.get(0));
        }
        return buildings;
    }

    YardiBuilding getYardiBuilding(String propertyCode) throws YardiServiceException {
        for (YardiBuilding building : getYardiBuildings()) {
            if (propertyCode.equals(building.buildingId().getValue())) {
                return building;
            }
        }
        Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess + ":" + propertyCode);
        return null;
    }

    Properties getProperties() throws YardiServiceException {
        Properties properties = new Properties();
        for (YardiBuilding building : getYardiBuildings()) {
            com.propertyvista.yardi.beans.Property property = new com.propertyvista.yardi.beans.Property();
            property.setCode(building.buildingId().getValue());
            properties.getProperties().add(property);
        }
        return properties;
    }
}
