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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.IgnoreSessionToken;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;
import com.propertyvista.portal.server.portal.PropertyFinder;
import com.propertyvista.portal.server.ptapp.util.Converter;

@IgnoreSessionToken
public class PortalSiteServicesImpl implements PortalSiteServices {

    @Override
    public void retrievePropertyList(AsyncCallback<PropertyListDTO> callback) {
        //TODO move this all to special table for starte retrival
        retrievePropertyList(callback, null);
    }

    // get entire property list and set Selected flag for the properties that satisfy given search criteria
    public void retrievePropertyList(AsyncCallback<PropertyListDTO> callback, PropertySearchCriteria search) {
        // get entire list
        List<Building> buildings = PropertyFinder.getPropertyList(null);
        // search by criteria
        List<Long> selectedIds = new ArrayList<Long>();
        if (search != null) {
            for (Building selected : PropertyFinder.getPropertyList(search)) {
                selectedIds.add(selected.getPrimaryKey().asLong());
            }
        }
        PropertyListDTO ret = EntityFactory.create(PropertyListDTO.class);
        for (Building building : buildings) {
            Map<Floorplan, List<AptUnit>> fpMap = PropertyFinder.getBuildingFloorplans(building);
            PropertyDTO prop = Converter.convert(building, new ArrayList<Floorplan>(fpMap.keySet()));
            prop.selected().setValue(selectedIds.contains(building.getPrimaryKey().asLong()));
            ret.properties().add(prop);
        }
        callback.onSuccess(ret);
    }

}
