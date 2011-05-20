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
package com.propertyvista.portal.server.ptapp.services;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.Picture;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.domain.ptapp.AvailableUnitsByFloorplan;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.domain.ptapp.UnitSelectionCriteria;
import com.propertyvista.portal.domain.util.VistaDataPrinter;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.util.Converter;

public class ApartmentServiceImpl extends ApplicationEntityServiceImpl implements ApartmentService {

    private final static Logger log = LoggerFactory.getLogger(ApartmentServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<UnitSelection> callback, Long tenantId) {
        EntityQueryCriteria<UnitSelection> criteria = EntityQueryCriteria.create(UnitSelection.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
        UnitSelection unitSelection = secureRetrieve(criteria);
        if (unitSelection == null) {
            log.debug("Creating new unit selection");
            unitSelection = EntityFactory.create(UnitSelection.class);
        } else {
            //            log.info("Loaded existing unit selection {}", unitSelection);
        }

        //        log.info("Found unit selection\n{}", PrintUtil.print(unitSelection));
        log.debug("Loading unit selection with criteria {}", unitSelection.selectionCriteria());

        loadTransientData(unitSelection);

        callback.onSuccess(unitSelection);
    }

    @Override
    public void save(AsyncCallback<UnitSelection> callback, UnitSelection unitSelection) {
        log.debug("Saving unit selection\n{}", VistaDataPrinter.print(unitSelection));
        log.debug("Saving unit selection with criteria {}", unitSelection.selectionCriteria());

        saveApplicationEntity(unitSelection);

        loadTransientData(unitSelection);

        callback.onSuccess(unitSelection);
    }

    @Override
    public void retrieveUnitSelection(AsyncCallback<AvailableUnitsByFloorplan> callback, UnitSelectionCriteria selectionCriteria) {
        callback.onSuccess(loadAvailableUnits(selectionCriteria));
    }

    public void loadTransientData(UnitSelection unitSelection) {
        unitSelection.availableUnits().set(loadAvailableUnits(unitSelection.selectionCriteria()));
    }

    public EntityQueryCriteria<AptUnit> createAptUnitCriteria(UnitSelectionCriteria selectionCriteria) {
        log.info("Looking for units from {} to {}", selectionCriteria.availableFrom().getStringView(), selectionCriteria.availableTo().getStringView());

        // find building first, don't use building from unit selection
        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().info().propertyCode(), selectionCriteria.propertyCode().getValue()));
        Building building = PersistenceServicesFactory.getPersistenceService().retrieve(buildingCriteria);
        if (building == null) {
            log.debug("Could not find building for propertyCode {}", selectionCriteria.propertyCode().getStringView());
            return null;
        }

        // find floor plan
        EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().name(), selectionCriteria.floorplanName().getValue()));
        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
        Floorplan floorplan = PersistenceServicesFactory.getPersistenceService().retrieve(floorplanCriteria);

        if (floorplan == null) {
            log.debug("Could not find floorplan {}", selectionCriteria.floorplanName());
            return null;
        }

        // find units
        log.debug("Found floorplan {}, now can look for Units in building {}", floorplan, building);
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().info().building(), building));
        criteria.add(PropertyCriterion.eq(criteria.proto().marketing().floorplan(), floorplan));

        if (!selectionCriteria.availableFrom().isNull()) {
            criteria.add(new PropertyCriterion(criteria.proto().avalableForRent(), PropertyCriterion.Restriction.GREATER_THAN_OR_EQUAL, selectionCriteria
                    .availableFrom().getValue()));
        }
        if (!selectionCriteria.availableTo().isNull()) {
            criteria.add(new PropertyCriterion(criteria.proto().avalableForRent(), PropertyCriterion.Restriction.LESS_THAN_OR_EQUAL, selectionCriteria
                    .availableTo().getValue()));
        }
        return criteria;
    }

    public boolean areUnitsAvailable(UnitSelectionCriteria selectionCriteria) {
        EntityQueryCriteria<AptUnit> criteria = createAptUnitCriteria(selectionCriteria);
        if (criteria == null) {
            log.info("Could not construct a valid criteria based on selection criteria");
            return false;
        }
        int count = PersistenceServicesFactory.getPersistenceService().count(criteria);
        log.debug("Found {} units", count);
        return (count > 0);
    }

    public AvailableUnitsByFloorplan loadAvailableUnits(UnitSelectionCriteria selectionCriteria) {
        log.debug("Loading available units {}", selectionCriteria);
        AvailableUnitsByFloorplan availableUnits = EntityFactory.create(AvailableUnitsByFloorplan.class);
        EntityQueryCriteria<AptUnit> criteria = createAptUnitCriteria(selectionCriteria);
        if (criteria == null) {
            return availableUnits;
        }
        List<AptUnit> units = PersistenceServicesFactory.getPersistenceService().query(criteria);
        log.debug("Found {} units", units.size());
        if (!units.isEmpty()) {
            AptUnit firstUnit = units.get(0);
            Floorplan floorplan = firstUnit.marketing().floorplan();

            for (Picture picture : floorplan.pictures()) {
                prepareImage(picture);
            }
            availableUnits.floorplan().set(Converter.convert(floorplan));

            for (AptUnit unit : units) {
                availableUnits.units().add(Converter.convert(unit));
            }
        }
        return availableUnits;
    }

    //TODO If IE6 ?
    private static void prepareImage(Picture picture) {
        if (!picture.content().isNull()) {
            picture.contentBase64().setValue(new Base64().encodeToString(picture.content().getValue()));
        }
    }
}
