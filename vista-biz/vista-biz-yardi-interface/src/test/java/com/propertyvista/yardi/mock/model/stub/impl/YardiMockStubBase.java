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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.domain.YardiBuilding;

public class YardiMockStubBase {
    private final Set<String> noAccess = new HashSet<>();

    public void enablePropertyAccess(String propertyCode, boolean enable) {
        if (enable) {
            noAccess.remove(propertyCode);
        } else {
            noAccess.add(propertyCode);
        }
    }

    public boolean hasAccess(String propertyCode) {
        return !noAccess.contains(propertyCode);
    }

    public String ping(PmcYardiCredential yc) {
        return "OK";
    }

    public String getPluginVersion(PmcYardiCredential yc) {
        return null;
    }

    public void validate(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
    }

    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        return getProperties();
    }

    List<YardiBuilding> getYardiBuildings() {
        return YardiMock.server().getModel().getBuildings();
    }

    YardiBuilding getYardiBuilding(String propertyCode) {
        for (YardiBuilding building : getYardiBuildings()) {
            if (propertyCode.equals(building.buildingId().getValue())) {
                return building;
            }
        }
        return null;
    }

    Properties getProperties() {
        Properties properties = new Properties();
        for (YardiBuilding building : getYardiBuildings()) {
            com.propertyvista.yardi.beans.Property property = new com.propertyvista.yardi.beans.Property();
            property.setCode(building.buildingId().getValue());
            properties.getProperties().add(property);
        }
        return properties;
    }
}
