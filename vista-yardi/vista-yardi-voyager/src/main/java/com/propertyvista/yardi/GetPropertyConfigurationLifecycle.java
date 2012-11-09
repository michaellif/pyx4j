/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 26, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.rmi.RemoteException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.axis2.AxisFault;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.mapper.GetPropertyConfigurationsMapper;
import com.propertyvista.yardi.merger.BuildingsMerger;

public class GetPropertyConfigurationLifecycle {

    public List<Building> download(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException, JAXBException {
        Properties properties = YardiTransactions.getPropertyConfigurations(c, yp);
        GetPropertyConfigurationsMapper mapper = new GetPropertyConfigurationsMapper();
        mapper.map(properties);
        List<Building> buildings = mapper.getBuildings();
        return buildings;
    }

    public List<Building> load() {
        List<Building> buildings = Persistence.service().query(new EntityQueryCriteria<Building>(Building.class));
        return buildings;
    }

    public List<Building> merge(YardiClient c, YardiParameters yp, boolean persist) throws AxisFault, RemoteException, JAXBException {
        List<Building> imported = download(c, yp);
        List<Building> existing = load();
        List<Building> merged = new BuildingsMerger().merge(imported, existing);
        if (persist) {
            for (Building building : merged) {
                Persistence.service().persist(building.info().address());
                Persistence.service().persist(building.info());
                Persistence.service().persist(building.marketing());
                Persistence.service().persist(building);
            }
        }
        return merged;
    }
}
