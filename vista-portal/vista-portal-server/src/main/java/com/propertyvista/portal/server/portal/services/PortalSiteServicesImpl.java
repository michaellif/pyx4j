/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.GeoCriteria;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.common.domain.ref.City;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.portal.domain.dto.FloorplanDetailsDTO;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;
import com.propertyvista.portal.server.ptapp.util.Converter;

public class PortalSiteServicesImpl implements PortalSiteServices {

    @Override
    public void retrieveCityList(AsyncCallback<Vector<City>> callback) {
        EntityQueryCriteria<City> criteria = EntityQueryCriteria.create(City.class);
        //TODO add criteria to see buildings available
        callback.onSuccess(EntityServicesImpl.secureQuery(criteria));
    }

    @Override
    public void retrievePropertyList(AsyncCallback<Vector<PropertyDTO>> callback, City city) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        List<Building> buildings = PersistenceServicesFactory.getPersistenceService().query(criteria);

        Vector<PropertyDTO> properties = new Vector<PropertyDTO>();
        for (Building b : buildings) {
            properties.add(Converter.convert(b));
        }
        callback.onSuccess(properties);
    }

    @Override
    public void retrievePropertyList(AsyncCallback<Vector<PropertyDTO>> callback, GeoCriteria geoCriteria) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retrievePropertyDetails(AsyncCallback<PropertyDetailsDTO> callback, long propertyId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retrieveFloorplanDetails(AsyncCallback<FloorplanDetailsDTO> callback, long floorplanId) {
        // TODO Auto-generated method stub

    }

}
