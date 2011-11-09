/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author dmitry
 */
package com.propertyvista.portal.server.preloader;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.BuildingsGenerator;
import com.propertvista.generator.Dashboards;
import com.propertvista.generator.MediaGenerator;
import com.propertvista.generator.ServiceCatalogGenerator;
import com.propertvista.generator.gdo.ServiceItemTypes;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceCatalog;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.marketing.yield.Amenity;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.PropertyManager;
import com.propertyvista.domain.property.PropertyPhone;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.domain.property.vendor.Vendor;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.portal.domain.ptapp.LeaseTerms;
import com.propertyvista.portal.server.importer.Importer;
import com.propertyvista.server.common.generator.UnitRelatedData;
import com.propertyvista.server.common.reference.PublicDataUpdater;
import com.propertyvista.server.common.reference.geo.GeoLocator.Mode;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;
import com.propertyvista.server.domain.FileBlob;
import com.propertyvista.server.domain.ThumbnailBlob;

public class BuildingPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(BuildingPreloader.class);

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Complex.class, Building.class, AptUnit.class, AptUnitItem.class, Floorplan.class, Email.class, Phone.class, Amenity.class,
                    LeaseTerms.class, Vendor.class, Elevator.class, Boiler.class, Roof.class, Parking.class, ParkingSpot.class, LockerArea.class, Locker.class,
                    Media.class, ThumbnailBlob.class, FileBlob.class, Feature.class, PropertyManager.class, ServiceCatalog.class);
        } else {
            return "This is production";
        }
    }

    private String generate() {
        BuildingsGenerator generator = new BuildingsGenerator(DemoData.BUILDINGS_GENERATION_SEED);

        ServiceItemTypes serviceItemTypes = new ServiceItemTypes();
        {
            EntityQueryCriteria<ServiceItemType> criteria = EntityQueryCriteria.create(ServiceItemType.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), ServiceItemType.Type.service));
            serviceItemTypes.serviceItemTypes.addAll(Persistence.service().query(criteria));
        }
        {
            EntityQueryCriteria<ServiceItemType> criteria = EntityQueryCriteria.create(ServiceItemType.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), ServiceItemType.Type.feature));
            serviceItemTypes.featureItemTypes.addAll(Persistence.service().query(criteria));
        }

        ServiceCatalogGenerator pmcGenerator = new ServiceCatalogGenerator(serviceItemTypes);

        LeaseTerms leaseTerms = generator.createLeaseTerms();
        Persistence.service().persist(leaseTerms);

        Dashboards availableDashboards = new Dashboards();
        {
            EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), DashboardMetadata.DashboardType.building));
            availableDashboards.buildingDashboards.addAll(Persistence.service().query(criteria));
        }

        // create some complexes:
        List<Complex> complexes = new Vector<Complex>();
        complexes.add(generator.createComplex("Complex #1"));
        complexes.add(generator.createComplex("Complex #2"));
        complexes.add(generator.createComplex("Complex #3"));

        for (Complex complex : complexes) {
            complex.dashboard().set(DataGenerator.random(availableDashboards.buildingDashboards));
        }

        Persistence.service().persist(complexes);

        // create some StarlightPmc:
        List<PropertyManager> pmcs = new Vector<PropertyManager>();
        pmcs.add(generator.createPmc("PMC #1"));
        pmcs.add(generator.createPmc("PMC #2"));
        pmcs.add(generator.createPmc("PMC #3"));
        pmcs.add(generator.createPmc("Blue Ridge Realty Inc."));
        Persistence.service().persist(pmcs);

        int unitCount = 0;
        List<Building> buildings = generator.createBuildings(config().getNumResidentialBuildings());

        List<Complex> complexesWithBuildins = new Vector<Complex>();

        SharedGeoLocator.setMode(Mode.useCacheOnly);

        for (Building building : buildings) {
            if (building.info().address().location().isNull()) {
                SharedGeoLocator.populateGeo(building.info().address());
            }
            Persistence.service().persist(building);

            if (DataGenerator.randomBoolean()) {
                building.complex().set(DataGenerator.random(complexes));
                if (!complexesWithBuildins.contains(building.complex())) {
                    complexesWithBuildins.add(building.complex());
                    building.complexPrimary().setValue(Boolean.TRUE);
                } else {
                    building.complexPrimary().setValue(Boolean.FALSE);
                }
            }

            // TODO Need to be saving PropertyProfile, PetCharge
            building.propertyManager().set(DataGenerator.random(pmcs)); // temporary for Starlight!..

            // Service Catalog:
            ServiceCatalog catalog = EntityFactory.create(ServiceCatalog.class);
            catalog.belongsTo().set(building);
            Persistence.service().persist(catalog);

            pmcGenerator.createServiceCatalog(catalog);

            Persistence.service().persist(catalog.features());
            Persistence.service().persist(catalog.concessions());
            Persistence.service().persist(catalog.services());

            Persistence.service().merge(catalog);
            building.serviceCatalog().set(catalog);

            //Media
            if (this.getParameter(VistaDataPreloaderParameter.attachMedia) != Boolean.FALSE) {
                MediaGenerator.generatedBuildingMedia(building);
                Persistence.service().persist(building.media());
            }

            building.dashboard().set(DataGenerator.random(availableDashboards.buildingDashboards));

            Persistence.service().merge(building);

            // Elevators
            List<Elevator> elevators = generator.createElevators(building, config().getNumElevators());
            for (Elevator elevator : elevators) {
                CmpanyVendorPersistHelper.persistWarranty(elevator.warranty());
                CmpanyVendorPersistHelper.persistMaintenance(elevator.maintenance());
                Persistence.service().persist(elevator);
            }

            // Boilers
            List<Boiler> boilers = generator.createBoilers(building, config().getNumBoilers());
            for (Boiler boiler : boilers) {
                CmpanyVendorPersistHelper.persistWarranty(boiler.warranty());
                CmpanyVendorPersistHelper.persistMaintenance(boiler.maintenance());
                Persistence.service().persist(boiler);
            }

            // Roofs
            List<Roof> roofs = generator.createRoofs(building, config().getNumRoofs());
            for (Roof roof : roofs) {
                CmpanyVendorPersistHelper.persistWarranty(roof.warranty());
                CmpanyVendorPersistHelper.persistMaintenance(roof.maintenance());
                Persistence.service().persist(roof);
            }

            // Parking:
            List<Parking> parkings = generator.createParkings(building, config().getNumParkings());
            for (Parking parking : parkings) {
                Persistence.service().persist(parking);

                List<ParkingSpot> spots = generator.createParkingSpots(parking, config().getNumParkingSpots());
                for (ParkingSpot spot : spots) {
                    Persistence.service().persist(spot);
                }
            }

            // Lockers:
            List<LockerArea> lockerAreas = generator.createLockerAreas(building, config().getNumLockerAreas());
            for (LockerArea item : lockerAreas) {
                Persistence.service().persist(item);

                List<Locker> lockers = generator.createLockers(item, config().getNumLockers());
                for (Locker locker : lockers) {
                    Persistence.service().persist(locker);
                }
            }

            // Amenities:
            List<BuildingAmenity> amenities = generator.createBuildingAmenities(building, 1 + RandomUtil.randomInt(5));
            for (BuildingAmenity item : amenities) {
                Persistence.service().persist(item);
            }

            // Floorplans:
            List<FloorplanDTO> floorplans = generator.createFloorplans(building, config().getNumFloorplans());
            for (FloorplanDTO floorplanDTO : floorplans) {

                if (this.getParameter(VistaDataPreloaderParameter.attachMedia) != Boolean.FALSE) {
                    MediaGenerator.attachGeneratedFloorplanMedia(floorplanDTO);
                }

                Floorplan floorplan = floorplanDTO.clone(Floorplan.class);
                Persistence.service().persist(floorplan); // persist real unit here, not DTO!..
                floorplanDTO.setPrimaryKey(floorplan.getPrimaryKey());

                for (FloorplanAmenity amenity : floorplanDTO.amenities()) {
                    amenity.belongsTo().set(floorplan);
                    Persistence.service().persist(amenity);
                }
            }

            // Units:
            List<UnitRelatedData> units = generator.createUnits(building, floorplans, config().getNumFloors(), config().getNumUnitsPerFloor());
            unitCount += units.size();
            for (UnitRelatedData unitData : units) {
                // persist plain internal lists:

                AptUnit unit = unitData.clone(AptUnit.class);
                Persistence.service().persist(unit); // persist real unit here, not DTO!..

                // persist internal lists and with belongness:
                for (AptUnitOccupancy occupancy : unitData.occupancies()) {
                    occupancy.unit().set(unit);
                    Persistence.service().persist(occupancy);
                }
                for (AptUnitItem detail : unitData.details()) {
                    detail.belongsTo().set(unit);
                    Persistence.service().persist(detail);
                }
            }

            // fill Service Catalog with building elements:

            EntityQueryCriteria<AptUnit> buildingUnitsCriteria = EntityQueryCriteria.create(AptUnit.class);
            buildingUnitsCriteria.add(PropertyCriterion.eq(buildingUnitsCriteria.proto().belongsTo(), building));
            List<AptUnit> buildingUnits = Persistence.service().query(buildingUnitsCriteria);

            EntityQueryCriteria<Parking> buildingParkingsCriteria = EntityQueryCriteria.create(Parking.class);
            buildingParkingsCriteria.add(PropertyCriterion.eq(buildingParkingsCriteria.proto().belongsTo(), building));
            List<Parking> buildingParkings = Persistence.service().query(buildingParkingsCriteria);

            EntityQueryCriteria<LockerArea> buildingLockerCriteria = EntityQueryCriteria.create(LockerArea.class);
            buildingLockerCriteria.add(PropertyCriterion.eq(buildingLockerCriteria.proto().belongsTo(), building));
            List<LockerArea> buildingockers = Persistence.service().query(buildingLockerCriteria);

            EntityQueryCriteria<Roof> buildingRoofsCriteria = EntityQueryCriteria.create(Roof.class);
            buildingRoofsCriteria.add(PropertyCriterion.eq(buildingRoofsCriteria.proto().belongsTo(), building));
            List<Roof> buildingRoofs = Persistence.service().query(buildingRoofsCriteria);

            for (Service service : catalog.services()) {
                switch (service.type().getValue()) {
                case residentialUnit:
                case residentialShortTermUnit:
                case commercialUnit:
                    for (ServiceItem item : service.items()) {
                        item.element().set(RandomUtil.random(buildingUnits));
                        Persistence.service().persist(item);
                    }
                    break;
                case garage:
                    for (ServiceItem item : service.items()) {
                        item.element().set(RandomUtil.random(buildingParkings));
                        Persistence.service().persist(item);
                    }
                    break;
                case storage:
                    for (ServiceItem item : service.items()) {
                        item.element().set(RandomUtil.random(buildingockers));
                        Persistence.service().persist(item);
                    }
                    break;
                case roof:
                    for (ServiceItem item : service.items()) {
                        item.element().set(RandomUtil.random(buildingRoofs));
                        Persistence.service().persist(item);
                    }
                    break;
                }
            }

            Persistence.service().merge(catalog);

            //Do not publish until data is clean-up
            if (true) {
                PublicDataUpdater.updateIndexData(building);
            }
        }
        SharedGeoLocator.save();

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(buildings.size()).append(" buildings\n");
        sb.append("Created ").append(unitCount).append(" units");
        return sb.toString();
    }

    public String importData() {
        try {
            Importer importer = new Importer();
            importer.setAttachMedia(this.getParameter(VistaDataPreloaderParameter.attachMedia) != Boolean.FALSE);

            importer.start(config().getNumResidentialBuildings());

            StringBuilder sb = new StringBuilder();

            sb.append("Imported ").append(importer.getModel().getBuildings().size()).append(" buildings\n");
            sb.append("Imported ").append(importer.getModel().getFloorplans().size()).append(" floorplans\n");
            sb.append("Imported ").append(importer.getModel().getUnits().size()).append(" units\n");

            return sb.toString();
        } catch (Exception e) {
            log.error("Failed to import XML data", e);
            throw new Error("Failed to import XML data");
        }
    }

    @Override
    public String create() {
        StringBuilder sb = new StringBuilder();
        sb.append(generate());
        if (!config().minimizePreloadTime) {
            //sb.append("\n");
            //sb.append(importData());
        }
        return sb.toString();
    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");

        List<Parking> parkings = Persistence.service().query(new EntityQueryCriteria<Parking>(Parking.class));
        sb.append(parkings.size()).append(" parkings\n");
        for (Parking parking : parkings) {
            sb.append("\t");
            sb.append(parking);
            sb.append("\n");
        }

        List<Locker> lockers = Persistence.service().query(new EntityQueryCriteria<Locker>(Locker.class));
        sb.append(lockers.size()).append(" lockers\n");
        for (Locker locker : lockers) {
            sb.append("\t");
            sb.append(locker);
            sb.append("\n");
        }

        List<Floorplan> floorplans = Persistence.service().query(new EntityQueryCriteria<Floorplan>(Floorplan.class));
        sb.append(floorplans.size()).append(" floorplans\n");
        for (Floorplan floorplan : floorplans) {
            sb.append("\t");
            sb.append(floorplan);
            sb.append("\n");
        }

        // EntityQueryCriteria<Floorplan> floorplanCriteria =
        // EntityQueryCriteria.create(Floorplan.class);
        // floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().name(),
        // DemoData.REGISTRATION_DEFAULT_FLOORPLAN));
        // floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().propertyCode(),
        // DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE));
        // Floorplan floorplan =
        // Persistence.service().retrieve(floorplanCriteria);
        // sb.append("Floorplan: ").append(floorplan);

        List<Building> buildings = Persistence.service().query(new EntityQueryCriteria<Building>(Building.class));
        sb.append("\n\nLoaded ").append(buildings.size()).append(" buildings\n\n");
        for (Building building : buildings) {
            // b.append(building.getStringView());
            sb.append(building.info().type().getStringView());
            sb.append("\t");
            sb.append(building.info().address().streetNumber().getStringView()).append(", ");
            sb.append(building.info().address().streetName().getStringView()).append(", ");
            sb.append(building.info().address().streetType().getStringView()).append(", ");
            sb.append(building.info().address().city().getStringView()).append(" ").append(building.info().address().province().getStringView()).append(", ");
            sb.append(building.info().address().postalCode().getStringView()).append(", ").append(building.info().address().country().getStringView());

            // phones
            sb.append("\t");

            for (PropertyPhone phone : building.contacts().phones()) {
                sb.append(phone.number().getStringView());
                sb.append("/").append(phone.type().getStringView());
            }

            // // email
            // b.append("\t");
            // b.append(building.email().getStringView());

            sb.append("\n");

            // get the units
            EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
            criteria.add(new PropertyCriterion(criteria.proto().belongsTo(), Restriction.EQUAL, building.getPrimaryKey()));
            List<AptUnit> units = Persistence.service().query(criteria);
            sb.append("\tBuilding has ").append(units.size()).append(" units\n");

            for (AptUnit unit : units) {
                sb.append("\t");
                sb.append(unit.info().floor().getStringView()).append(" floor");
                sb.append(" ");
                sb.append(unit.info().area().getStringView()).append(" sq. ft.");
                sb.append(" ");
                sb.append(unit.belongsTo().propertyCode().getStringView());
                sb.append(" ");
                sb.append(unit.floorplan());
                sb.append(" | ");
                sb.append(unit.floorplan().name().getStringView()); // .append(" ").append(unit.floorplan().pictures());
                sb.append("\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }
}
