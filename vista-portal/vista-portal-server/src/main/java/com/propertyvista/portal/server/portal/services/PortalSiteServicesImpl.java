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
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.IgnoreSessionToken;

import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.FloorplanDetailsDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;
import com.propertyvista.portal.server.ptapp.util.Converter;

@IgnoreSessionToken
public class PortalSiteServicesImpl implements PortalSiteServices {

    @Override
    public void retrieveCityList(AsyncCallback<Vector<City>> callback) {
        EntityQueryCriteria<City> criteria = EntityQueryCriteria.create(City.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().hasProperties(), Boolean.TRUE));
        callback.onSuccess(EntityServicesImpl.secureQuery(criteria));
    }

    @Override
    public void retrievePropertyList(AsyncCallback<PropertyListDTO> callback) {
        //TODO move this all to special table for starte retrival

        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);

        List<Building> buildings = Persistence.service().query(dbCriteria);

        PropertyListDTO ret = EntityFactory.create(PropertyListDTO.class);
        for (Building building : buildings) {

            if (building.info().address().location().isNull() || building.info().address().location().getValue().getLat() == 0) {
                continue;
            }

            //In memory filters
            EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
            floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
            List<Floorplan> floorplans = Persistence.service().query(floorplanCriteria);

            ret.properties().add(Converter.convert(building, floorplans));
        }
        callback.onSuccess(ret);
    }

    @Override
    public void retrievePropertyDetails(AsyncCallback<PropertyDetailsDTO> callback, Key propertyId) {
        Building building = Persistence.service().retrieve(Building.class, propertyId);
        if (building == null) {
            callback.onSuccess(null);
            return;
        }
        PropertyDetailsDTO dto = EntityFactory.create(PropertyDetailsDTO.class);

        // find floor plans
        EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
        List<Floorplan> floorplans = Persistence.service().query(floorplanCriteria);

        dto.set(Converter.convert(building, floorplans));

        for (Floorplan floorplan : floorplans) {
            dto.floorplans().add(Converter.convert(floorplan));
        }

        if (!building.media().isEmpty()) {
            Persistence.service().retrieve(building.media());
            for (Media m : building.media()) {
                dto.media().add(Converter.convert(m));
            }
        }

        callback.onSuccess(dto);
    }

    @Override
    public void retrieveFloorplanDetails(AsyncCallback<FloorplanDetailsDTO> callback, Key floorplanId) {
        Floorplan floorplan = Persistence.service().retrieve(Floorplan.class, floorplanId);
        if (floorplan == null) {
            callback.onSuccess(null);
            return;
        }

        FloorplanDetailsDTO dto = EntityFactory.create(FloorplanDetailsDTO.class);
        dto.set(Converter.convert(floorplan));

        if (!floorplan.media().isEmpty()) {
            Persistence.service().retrieve(floorplan.media());
            for (Media m : floorplan.media()) {
                dto.media().add(Converter.convert(m));
            }
        }

        // List of building amenities 
        EntityQueryCriteria<BuildingAmenity> amenitysCriteria = EntityQueryCriteria.create(BuildingAmenity.class);
        amenitysCriteria.add(PropertyCriterion.eq(amenitysCriteria.proto().belongsTo(), floorplan.building()));
        for (BuildingAmenity amenity : Persistence.service().query(amenitysCriteria)) {
            AmenityDTO amntDTO = EntityFactory.create(AmenityDTO.class);
            amntDTO.name().setValue(amenity.getStringView());
            dto.buildingAmenities().add(amntDTO);
        }

        //TODO add Details
        callback.onSuccess(dto);

    }

    @Override
    public void retrieveStaticContent(AsyncCallback<PageContent> callback, Key pageContentId) {
//        EntityQueryCriteria<PageContent> criteria = EntityQueryCriteria.create(PageContent.class);
//        criteria.add(PropertyCriterion.eq(criteria.proto().path(), path));
//        PageContent c = Persistence.service().retrieve(criteria);
        callback.onSuccess(null);
    }

}
