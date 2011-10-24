/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.converter.BuildingConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanConverter;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.server.common.reference.PublicDataUpdater;

public class BuildingUpdater {

    private final static Logger log = LoggerFactory.getLogger(BuildingUpdater.class);

    public ImportCounters updateUnitAvailability(BuildingIO buildingIO, String imagesBaseFolder) {
        ImportCounters counters = new ImportCounters();
        Building building;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            List<Building> buildings = Persistence.service().query(criteria);
            if (buildings.size() == 0) {
                throw new UserRuntimeException("Building '" + buildingIO.propertyCode().getValue() + "' not found");
            } else if (buildings.size() > 1) {
                throw new UserRuntimeException("More then one building '" + buildingIO.propertyCode().getValue() + "' found");
            } else {
                building = buildings.get(0);
            }
        }

        for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
            Floorplan floorplan;
            {
                EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
                criteria.add(PropertyCriterion.eq(criteria.proto().name(), floorplanIO.name().getValue()));
                List<Floorplan> floorplans = Persistence.service().query(criteria);
                if (floorplans.size() == 0) {
                    throw new UserRuntimeException("Floorplan '" + floorplanIO.name().getValue() + "' in  building '" + buildingIO.propertyCode().getValue()
                            + "' not found");
                } else if (floorplans.size() > 1) {
                    throw new UserRuntimeException("More then one Floorplan '" + floorplanIO.name().getValue() + "' in  building '"
                            + buildingIO.propertyCode().getValue() + "' found");
                } else {
                    floorplan = floorplans.get(0);
                }
            }
            counters.floorplans += 1;

            for (AptUnitIO aptUnitIO : floorplanIO.units()) {
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                criteria.add(PropertyCriterion.eq(criteria.proto().info().number(), aptUnitIO.number().getValue()));
                List<AptUnit> units = Persistence.service().query(criteria);

                AptUnit unit;
                if (units.size() == 0) {
                    throw new UserRuntimeException("AptUnit '" + aptUnitIO.number().getValue() + "' in '" + floorplanIO.name().getValue() + "' in '"
                            + buildingIO.propertyCode().getValue() + "' not found");
                } else if (units.size() > 1) {
                    throw new UserRuntimeException("More then one AptUnit '" + aptUnitIO.number().getValue() + "' in '" + floorplanIO.name().getValue()
                            + "' in '" + buildingIO.propertyCode().getValue() + "' found");
                } else {
                    unit = units.get(0);
                }

                // Update
                if (!unit.availableForRent().equals(aptUnitIO.availableForRent())) {
                    unit.availableForRent().setValue(aptUnitIO.availableForRent().getValue());
                    Persistence.service().persist(unit);
                    counters.units += 1;
                    counters.buildings = 1;
                    log.debug("updated AptUnit {} {}", buildingIO.propertyCode().getValue() + " " + floorplanIO.name().getValue(), aptUnitIO.number()
                            .getValue());
                }

            }
        }
        return counters;
    }

    public ImportCounters updateData(BuildingIO buildingIO, String imagesBaseFolder, boolean ignoreMissingMedia) {
        if (buildingIO.propertyCode().isNull()) {
            throw new UserRuntimeException("propertyCode can't be empty");
        }
        ImportCounters counters = new ImportCounters();
        boolean buildingIsNew = false;
        Building building;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            List<Building> buildings = Persistence.service().query(criteria);
            if (buildings.size() == 0) {
                buildingIsNew = true;
                building = new BuildingConverter().createDBO(buildingIO);
                Persistence.service().persist(building);
                PublicDataUpdater.updateIndexData(building);
                counters.buildings += 1;
                log.debug("created building {}", buildingIO.propertyCode().getValue());
            } else if (buildings.size() > 1) {
                throw new UserRuntimeException("More then one building '" + buildingIO.propertyCode().getValue() + "' found");
            } else {
                building = buildings.get(0);
                if (building.marketing().adBlurbs().getMeta().isDetached()) {
                    Persistence.service().retrieve(building.marketing().adBlurbs());
                }
                if (building.contacts().phones().getMeta().isDetached()) {
                    Persistence.service().retrieve(building.contacts().phones());
                }

                boolean buildingUpdated = new BuildingConverter().updateDBO(buildingIO, building);

                if (buildingUpdated) {
                    Persistence.service().persist(building);
                    counters.buildings += 1;
                    log.debug("updated building {}", buildingIO.propertyCode().getValue());
                }
            }
        }

        for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
            if (floorplanIO.name().isNull()) {
                throw new UserRuntimeException("Floorplan name in  building '" + buildingIO.propertyCode().getValue() + "' can't be empty");
            }
            boolean floorplanIsNew = false;
            Floorplan floorplan = null;
            if (!buildingIsNew) {
                EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
                criteria.add(PropertyCriterion.eq(criteria.proto().name(), floorplanIO.name().getValue()));
                List<Floorplan> floorplans = Persistence.service().query(criteria);
                if (floorplans.size() == 1) {
                    floorplan = floorplans.get(0);
                } else if (floorplans.size() > 1) {
                    throw new UserRuntimeException("More then one Floorplan '" + floorplanIO.name().getValue() + "' in  building '"
                            + buildingIO.propertyCode().getValue() + "' found");
                }
            }
            boolean floorplanUpdated = false;
            if (floorplan == null) {
                floorplanIsNew = true;
                floorplanUpdated = true;
                floorplan = new FloorplanConverter().createDBO(floorplanIO);
                floorplan.building().set(building);
            } else {
                floorplanUpdated = new FloorplanConverter().updateDBO(floorplanIO, floorplan);
            }

            if (floorplanUpdated) {
                Persistence.service().persist(floorplan);
                counters.floorplans += 1;
                if (floorplanIsNew) {
                    log.debug("created floorplan {} {}", buildingIO.propertyCode().getValue(), floorplanIO.name().getValue());
                } else {
                    log.debug("updated floorplan {} {}", buildingIO.propertyCode().getValue(), floorplanIO.name().getValue());
                }
            }

            for (AptUnitIO aptUnitIO : floorplanIO.units()) {
                AptUnit unit = null;
                if (!floorplanIsNew) {
                    EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                    criteria.add(PropertyCriterion.eq(criteria.proto().info().number(), aptUnitIO.number().getValue()));
                    List<AptUnit> units = Persistence.service().query(criteria);
                    if (units.size() == 1) {
                        unit = units.get(0);
                    } else if (units.size() > 1) {
                        throw new UserRuntimeException("More then one AptUnit '" + aptUnitIO.number().getValue() + "' in '" + floorplanIO.name().getValue()
                                + "' in '" + buildingIO.propertyCode().getValue() + "' found");
                    }
                }
                boolean unitIsNew = false;
                boolean unitUpdated = false;
                if (unit == null) {
                    unit = new AptUnitConverter().createDBO(aptUnitIO);
                    unit.belongsTo().set(building);
                    unit.floorplan().set(floorplan);
                    unitUpdated = true;
                    unitIsNew = true;
                } else {
                    unitUpdated = new AptUnitConverter().updateDBO(aptUnitIO, unit);
                }

                if (unitUpdated) {
                    Persistence.service().merge(unit);
                    counters.units += 1;
                    if (unitIsNew) {
                        log.debug("created AptUnit {} {}", buildingIO.propertyCode().getValue() + " " + floorplanIO.name().getValue(), aptUnitIO.number()
                                .getValue());
                    } else {
                        log.debug("updated AptUnit {} {}", buildingIO.propertyCode().getValue() + " " + floorplanIO.name().getValue(), aptUnitIO.number()
                                .getValue());
                    }
                }

            }
        }
        return counters;
    }
}
