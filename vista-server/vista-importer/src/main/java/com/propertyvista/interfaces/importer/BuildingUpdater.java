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

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.converter.BuildingConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanConverter;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.server.common.reference.PublicDataUpdater;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;

public class BuildingUpdater extends ImportPersister {

    private final static Logger log = LoggerFactory.getLogger(BuildingUpdater.class);

    private static final I18n i18n = I18n.get(BuildingUpdater.class);

    //@SuppressWarnings("deprecation")
    public ImportCounters updateUnitAvailability(BuildingIO buildingIO, MediaConfig mediaConfig) {
        ImportCounters counters = new ImportCounters();
        Building building;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            if (buildingIO.propertyCode().isNull()) {
                criteria.add(PropertyCriterion.eq(criteria.proto().externalId(), buildingIO.externalId().getValue()));
            } else {
                criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            }
            List<Building> buildings = Persistence.service().query(criteria);
            if (buildings.size() == 0) {
                throw new UserRuntimeException("Building '" + buildingIO.propertyCode().getValue() + "' with externalId '" + buildingIO.externalId().getValue()
                        + "' not found");
            } else if (buildings.size() > 1) {
                throw new UserRuntimeException("More then one building '" + buildingIO.propertyCode().getValue() + "' with externalId '"
                        + buildingIO.externalId().getValue() + "' found");
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
                if (!unit._availableForRent().equals(aptUnitIO.availableForRent())) {
                    unit._availableForRent().setValue(aptUnitIO.availableForRent().getValue());
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

    public ImportCounters updateData(BuildingIO buildingIO, MediaConfig mediaConfig) {
        if (buildingIO.propertyCode().isNull() && buildingIO.externalId().isNull()) {
            throw new UserRuntimeException("both propertyCode and externalId are empty");
        }
        ImportCounters counters = new ImportCounters();
        boolean buildingIsNew = false;
        Building building;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            if (buildingIO.propertyCode().isNull()) {
                criteria.add(PropertyCriterion.eq(criteria.proto().externalId(), buildingIO.externalId().getValue()));
            } else {
                criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            }

            List<Building> buildings = Persistence.service().query(criteria);
            if (buildings.size() == 0) {
                throw new UserRuntimeException("Building '" + buildingIO.propertyCode().getValue() + "' with externalId '" + buildingIO.externalId().getValue()
                        + "' not found");

//                buildingIsNew = true;
//                building = createBuilding(buildingIO, mediaConfig);
//                counters.buildings += 1;
//                log.debug("created building {}", buildingIO.propertyCode().getValue());
            } else if (buildings.size() > 1) {
                throw new UserRuntimeException("More then one building '" + buildingIO.propertyCode().getValue() + "' found");
            } else {
                building = buildings.get(0);
                if (building.marketing().adBlurbs().getMeta().isDetached()) {
                    Persistence.service().retrieve(building.marketing().adBlurbs());
                }
                if (building.contacts().propertyContacts().getMeta().isDetached()) {
                    Persistence.service().retrieve(building.contacts().propertyContacts());
                }

                boolean buildingUpdated = new BuildingConverter().updateBO(buildingIO, building);
                if (building.info().location().isNull()) {
                    buildingUpdated |= SharedGeoLocator.populateGeo(building);
                }
                if (buildingUpdated) {
                    Persistence.service().persist(building);
                    PublicDataUpdater.updateIndexData(building);
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
                floorplan = createFloorplan(floorplanIO, building, mediaConfig);
            } else {
                floorplanUpdated = new FloorplanConverter().updateBO(floorplanIO, floorplan);
            }

            if (floorplanUpdated) {
                Persistence.service().persist(floorplan);
                counters.floorplans += 1;
                if (floorplanIsNew) {
                    log.debug("created floorplan {} {}", buildingIO.propertyCode().getValue(), floorplanIO.name().getValue());
                } else {
                    log.debug("updated floorplan {} {}", buildingIO.propertyCode().getValue(), floorplanIO.name().getValue());
                }
            } else {
                log.debug("unchanged floorplan {} {}", buildingIO.propertyCode().getValue(), floorplanIO.name().getValue());
            }

            for (AptUnitIO aptUnitIO : floorplanIO.units()) {
                AptUnit unit = null;
                if (!floorplanIsNew) {
                    unit = requestUnit(aptUnitIO, building, floorplan);
                }
                boolean unitIsNew = false;
                boolean unitUpdated = false;
                if (unit == null) {
                    unit = new AptUnitConverter().createBO(aptUnitIO);
                    unit.building().set(building);
                    unit.floorplan().set(floorplan);
                    unitUpdated = true;
                    unitIsNew = true;
                } else {
                    unitUpdated = new AptUnitConverter().updateBO(aptUnitIO, unit);
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
                } else {
                    log.debug("unchanged AptUnit {} {}", buildingIO.propertyCode().getValue() + " " + floorplanIO.name().getValue(), aptUnitIO.number()
                            .getValue());
                }

            }
        }

        for (AptUnitIO aptUnitIO : buildingIO.units()) {
            AptUnit unit = requestUnit(aptUnitIO, building, null);
            boolean unitUpdated = false;
            unitUpdated = new AptUnitConverter().updateBO(aptUnitIO, unit);

            // Temporary Hack for null values
            if (aptUnitIO.containsMemberValue(aptUnitIO.availableForRent().getFieldName()) && aptUnitIO.availableForRent().isNull()) {
                if (!unit._availableForRent().isNull()) {
                    unit._availableForRent().setValue(null);
                    unitUpdated = true;
                }
            }

            if (unitUpdated) {
                Persistence.service().merge(unit);
                counters.units += 1;
                log.debug("updated AptUnit {} {}", buildingIO.propertyCode().getValue(), aptUnitIO.number().getValue());
            } else {
                log.debug("unchanged AptUnit {} {}", buildingIO.propertyCode().getValue(), aptUnitIO.number().getValue());
            }

        }

        return counters;
    }

    public AptUnit requestUnit(AptUnitIO aptUnitIO, Building building, Floorplan floorplan) {
        AptUnit unit = null;
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
        criteria.add(PropertyCriterion.eq(criteria.proto().info().number(), aptUnitIO.number().getValue()));
        if (floorplan != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
        }
        List<AptUnit> units = Persistence.service().query(criteria);

        if (units.size() == 0) {
            criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
            aptUnitIO.number().setValue(AptUnitConverter.trimUnitNumber(aptUnitIO.number().getValue()));
            criteria.add(PropertyCriterion.eq(criteria.proto().info().number(), aptUnitIO.number().getValue()));
            if (floorplan != null) {
                criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
            }
            units = Persistence.service().query(criteria);
        }

        if (units.size() == 1) {
            unit = units.get(0);
        } else if (units.size() == 0) {
            throw new UserRuntimeException(i18n.tr("AptUnit ''{0}'' in building ''{1}'' not found", aptUnitIO.number().getValue(), buildingDebugId(building)));
        } else if (units.size() > 1) {
            throw new UserRuntimeException(i18n.tr("More then one AptUnit ''{0}'' in building ''{1}''", aptUnitIO.number().getValue(),
                    buildingDebugId(building)));
        }

        return unit;
    }

    public String buildingDebugId(Building building) {
        if (!building.propertyCode().isNull()) {
            return building.propertyCode().getValue();
        } else if (!building.externalId().isNull()) {
            return building.externalId().getValue();
        } else {
            return building.getPrimaryKey().toString();
        }
    }

}
