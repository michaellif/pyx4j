/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt.services;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.ApptUnit;
import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Floorplan;
import com.propertyvista.portal.domain.Picture;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.domain.util.PrintUtil;
import com.propertyvista.portal.rpc.pt.services.ApartmentServices;
import com.propertyvista.portal.server.pt.PtUserDataAccess;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class ApartmentServicesImpl extends ApplicationEntityServicesImpl implements ApartmentServices {
    private final static Logger log = LoggerFactory.getLogger(ApartmentServicesImpl.class);

    @Override
    public void retrieveUnitSelection(AsyncCallback<UnitSelection> callback, UnitSelectionCriteria selectionCriteria) {

        log.info("Asking to retrieve unit selection");

        EntityQueryCriteria<UnitSelection> criteria = EntityQueryCriteria.create(UnitSelection.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        UnitSelection unitSelection = secureRetrieve(criteria);
        unitSelection.selectionCriteria().set(selectionCriteria);

        loadAvailableUnits(unitSelection);

        callback.onSuccess(unitSelection);
    }

    @Override
    public void retrieve(AsyncCallback<UnitSelection> callback, Long tenantId) {
        log.info("Retrieving unit selection for tenant {}", tenantId);
        EntityQueryCriteria<UnitSelection> criteria = EntityQueryCriteria.create(UnitSelection.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        UnitSelection unitSelection = secureRetrieve(criteria);
        if (unitSelection == null) {
            log.info("Creating new unit selection");
            unitSelection = EntityFactory.create(UnitSelection.class);
        }

//        log.info("Found unit selection\n{}", PrintUtil.print(unitSelection));

        callback.onSuccess(unitSelection);
    }

    @Override
    public void save(AsyncCallback<UnitSelection> callback, UnitSelection unitSelection) {
        log.info("Saving unit selection\n{}", PrintUtil.print(unitSelection));

        applyApplication(unitSelection);
        secureSave(unitSelection);

        callback.onSuccess(unitSelection);
    }

    public List<ApptUnit> loadAvailableUnits(UnitSelectionCriteria selectionCriteria) {
        log.info("Looking for units {}", selectionCriteria);

        // find building first, don't use building from unit selection
        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().propertyCode(), selectionCriteria.propertyCode().getValue()));
        Building building = PersistenceServicesFactory.getPersistenceService().retrieve(buildingCriteria);
        if (building == null) {
            log.info("Could not find building for propertyCode {}", selectionCriteria.propertyCode().getStringView());
            return null;
        }

        // find floor plan
        EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().name(), selectionCriteria.floorplanName().getValue()));
        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
        Floorplan floorplan = PersistenceServicesFactory.getPersistenceService().retrieve(floorplanCriteria);

        if (floorplan == null) {
            log.info("Could not find floorplan {}", selectionCriteria.floorplanName());
            return null;
        }

        // find units
        log.info("Found floorplan {}, now will look for building {}", floorplan, building);
        EntityQueryCriteria<ApptUnit> criteria = EntityQueryCriteria.create(ApptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
        criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));

        if (!selectionCriteria.availableFrom().isNull()) {
            criteria.add(new PropertyCriterion(criteria.proto().avalableForRent(), PropertyCriterion.Restriction.GREATER_THAN_OR_EQUAL, selectionCriteria
                    .availableFrom().getValue()));
        }
        if (!selectionCriteria.availableTo().isNull()) {
            criteria.add(new PropertyCriterion(criteria.proto().avalableForRent(), PropertyCriterion.Restriction.LESS_THAN_OR_EQUAL, selectionCriteria
                    .availableTo().getValue()));
        }

        List<ApptUnit> units = PersistenceServicesFactory.getPersistenceService().query(criteria);
        log.info("Found {} units", units.size());
        return units;
    }

    public void loadAvailableUnits(UnitSelection unitSelection) {

        List<ApptUnit> units = loadAvailableUnits(unitSelection.selectionCriteria());
        if (units == null || units.isEmpty()) {
            log.info("Did not find any available units");
        }

        ApptUnit firstUnit = units.get(0);
        Floorplan floorplan = firstUnit.floorplan();

        unitSelection.building().set(firstUnit.building());
        unitSelection.availableUnits().floorplan().set(floorplan);
        for (Picture picture : floorplan.pictures()) {
            prepareImage(picture);
        }

        unitSelection.availableUnits().units().addAll(units);
    }

    //TODO If IE6 ?
    private static void prepareImage(Picture picture) {
        if (!picture.content().isNull()) {
            picture.contentBase64().setValue(new Base64().encodeToString(picture.content().getValue()));
        }
    }
}
