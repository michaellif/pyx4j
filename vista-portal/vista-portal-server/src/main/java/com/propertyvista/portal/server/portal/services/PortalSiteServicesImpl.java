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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.GeoCriteria;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.common.domain.ref.City;
import com.propertyvista.domain.Medium;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.portal.domain.dto.FloorplanDetailsDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;
import com.propertyvista.portal.server.ptapp.util.Converter;

public class PortalSiteServicesImpl implements PortalSiteServices {

    @Override
    public void retrieveCityList(AsyncCallback<Vector<City>> callback) {
        EntityQueryCriteria<City> criteria = EntityQueryCriteria.create(City.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().hasProperties(), Boolean.TRUE));
        callback.onSuccess(EntityServicesImpl.secureQuery(criteria));
    }

    @Override
    public void retrievePropertyListByCity(AsyncCallback<PropertyListDTO> callback, City city) {
        PropertySearchCriteria criteria = EntityFactory.create(PropertySearchCriteria.class);
        criteria.city().set(city);
        retrievePropertyList(callback, criteria);
    }

    @Override
    public void retrievePropertyList(AsyncCallback<PropertyListDTO> callback, PropertySearchCriteria criteria) {
        //TODO move this all to special table for starte retrival

        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);
        if ((criteria.city().name().isNull())) {
            dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().info().address().city(), criteria.city().name().getValue()));
        }
        List<Building> buildings = PersistenceServicesFactory.getPersistenceService().query(dbCriteria);

        PropertyListDTO ret = EntityFactory.create(PropertyListDTO.class);
        for (Building building : buildings) {

            //In memory filters
            EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
            floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
            List<Floorplan> floorplans = PersistenceServicesFactory.getPersistenceService().query(floorplanCriteria);

            if (!criteria.numOfBeds().isNull() || !criteria.numOfBath().isNull() || !criteria.price().isNull()) {
                boolean match = true;
                for (Floorplan fp : floorplans) {
                    if (matchCriteria(fp, criteria)) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    continue;
                }
            }

            ret.properties().add(Converter.convert(building, floorplans));
        }
        callback.onSuccess(ret);
    }

    private boolean matchCriteria(Floorplan floorplan, PropertySearchCriteria criteria) {
        if (!criteria.numOfBeds().isNull()) {
            switch (criteria.numOfBeds().getValue()) {
            case all:
                break;
            case oneBedroom:
                return floorplan.bedrooms().getValue() == 1;
            case twoBedroom:
                return floorplan.bedrooms().getValue() == 2;
            case threeBedroom:
                return floorplan.bedrooms().getValue() == 3;
            case fourBedroom:
                return floorplan.bedrooms().getValue() == 4;
            case fiveBedroomAndMore:
                return floorplan.bedrooms().getValue() >= 5;
            }
        }
        if (!criteria.numOfBath().isNull()) {
            switch (criteria.numOfBath().getValue()) {
            case all:
                break;
            case oneBath:
                return floorplan.bathrooms().getValue() == 1;
            case twoBath:
                return floorplan.bathrooms().getValue() == 2;
            case threeBathAndMore:
                return floorplan.bathrooms().getValue() >= 3;
            }
        }
        if (!criteria.price().isNull()) {

        }
        return false;
    }

    @Override
    public void retrievePropertyListByGeo(AsyncCallback<PropertyListDTO> callback, GeoCriteria geoCriteria) {
        // TODO Auto-generated method stub
        retrievePropertyListByCity(callback, (City) null);
    }

    @Override
    public void retrievePropertyDetails(AsyncCallback<PropertyDetailsDTO> callback, Key propertyId) {
        Building building = PersistenceServicesFactory.getPersistenceService().retrieve(Building.class, propertyId);
        PropertyDetailsDTO dto = EntityFactory.create(PropertyDetailsDTO.class);

        // find floor plans
        EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
        List<Floorplan> floorplans = PersistenceServicesFactory.getPersistenceService().query(floorplanCriteria);

        dto.set(Converter.convert(building, floorplans));

        for (Floorplan floorplan : floorplans) {
            dto.floorplans().add(Converter.convert(floorplan));
        }
        //TODO calculate from available units
        dto.price().from().setValue(805d);
        dto.price().to().setValue(1600d);

        if (!building.media().isEmpty()) {
            PersistenceServicesFactory.getPersistenceService().retrieve(building.media());
            for (Medium m : building.media()) {
                dto.media().add(Converter.convert(m));
            }
        }

        callback.onSuccess(dto);
    }

    @Override
    public void retrieveFloorplanDetails(AsyncCallback<FloorplanDetailsDTO> callback, Key floorplanId) {
        Floorplan floorplan = PersistenceServicesFactory.getPersistenceService().retrieve(Floorplan.class, floorplanId);
        FloorplanDetailsDTO dto = EntityFactory.create(FloorplanDetailsDTO.class);
        dto.set(Converter.convert(floorplan));
        //TODO add Details
        callback.onSuccess(dto);

    }

}
