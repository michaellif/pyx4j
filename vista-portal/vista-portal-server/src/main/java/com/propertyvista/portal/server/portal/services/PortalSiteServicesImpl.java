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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.IgnoreSessionToken;

import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;
import com.propertyvista.portal.server.ptapp.util.Converter;

@IgnoreSessionToken
public class PortalSiteServicesImpl implements PortalSiteServices {

    @Override
    public void retrievePropertyList(AsyncCallback<PropertyListDTO> callback) {
        //TODO move this all to special table for starte retrival

        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);

        List<Building> buildings = Persistence.service().query(dbCriteria);

        PropertyListDTO ret = EntityFactory.create(PropertyListDTO.class);
        for (Building building : buildings) {
            if (!PublicVisibilityType.global.equals(building.marketing().visibility().getValue())) {
                continue;
            }

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

}
