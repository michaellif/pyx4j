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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.financial.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ParkingRent;
import com.propertyvista.domain.financial.offering.PetCharge;
import com.propertyvista.domain.financial.offering.ResidentialRent;
import com.propertyvista.domain.financial.offering.StorageRent;
import com.propertyvista.domain.marketing.yield.Amenity;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.portal.domain.ptapp.LeaseTerms;
import com.propertyvista.portal.server.generator.BuildingsGenerator;
import com.propertyvista.portal.server.generator.MediaGenerator;
import com.propertyvista.portal.server.importer.Importer;
import com.propertyvista.portal.server.portal.PublicDataUpdater;
import com.propertyvista.server.common.generator.UnitRelatedData;
import com.propertyvista.server.domain.FileBlob;
import com.propertyvista.server.domain.ThumbnailBlob;

public class BuildingPreloader extends BaseVistaDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(BuildingPreloader.class);

    public BuildingPreloader(PreloadConfig config) {
        super(config);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Building.class, AptUnit.class, AptUnitItem.class, Floorplan.class, Email.class, Phone.class, Complex.class, Utility.class,
                    Amenity.class, Concession.class, LeaseTerms.class, Parking.class, ParkingSpot.class, LockerArea.class, Locker.class, Media.class,
                    ThumbnailBlob.class, FileBlob.class, Feature.class, ResidentialRent.class, ParkingRent.class, StorageRent.class, PetCharge.class);
        } else {
            return "This is production";
        }
    }

    private String generate() {
        BuildingsGenerator generator = new BuildingsGenerator(DemoData.BUILDINGS_GENERATION_SEED);

        LeaseTerms leaseTerms = generator.createLeaseTerms();
        PersistenceServicesFactory.getPersistenceService().persist(leaseTerms);

        List<Building> buildings = generator.createBuildings(config.getNumResidentialBuildings());
        int unitCount = 0;
        for (Building building : buildings) {
            // TODO Need to be saving PropertyProfile, PetCharge
            persist(building);

            // Parking:
            List<Parking> parkings = generator.createParkings(building, config.getNumParkings());
            for (Parking parking : parkings) {
                persist(parking);

                List<ParkingSpot> spots = generator.createParkingSpots(parking, config.getNumParkingSpots());
                for (ParkingSpot spot : spots) {
                    persist(spot);
                }
            }

            // Lockers:
            List<LockerArea> lockerAreas = generator.createLockerAreas(building, config.getNumLockerAreas());
            for (LockerArea lockerArea : lockerAreas) {
                persist(lockerArea);

                List<Locker> lockers = generator.createLockers(lockerArea, config.getNumLockers());
                for (Locker locker : lockers) {
                    persist(locker);
                }
            }

            // Floorplans:
            List<FloorplanDTO> floorplans = generator.createFloorplans(building, config.getNumFloorplans());
            for (FloorplanDTO floorplanDTO : floorplans) {

                if (this.getParameter(MediaGenerator.ATTACH_MEDIA_PARAMETER) != Boolean.FALSE) {
                    MediaGenerator.attachGeneratedFloorplanMedia(floorplanDTO);
                }

                // persist plain internal lists:
                for (Feature feature : floorplanDTO.features()) {
                    for (Concession concession : feature.concessions()) {
                        persist(concession);
                    }
                    persist(feature);
                }

                Floorplan floorplan = down(floorplanDTO, Floorplan.class);
                persist(floorplan); // persist real unit here, not DTO!..

                for (FloorplanAmenity amenity : floorplanDTO.amenities()) {
                    amenity.belongsTo().set(floorplan);
                    persist(amenity);
                }
            }

            // Units:
            List<UnitRelatedData> units = generator.createUnits(building, floorplans, config.getNumFloors(), config.getNumUnitsPerFloor());
            unitCount += units.size();
            for (UnitRelatedData unitData : units) {
                // persist plain internal lists:
                for (Utility utility : unitData.info().utilities()) {
                    persist(utility);
                }

                AptUnit unit = down(unitData, AptUnit.class);
                persist(unit); // persist real unit here, not DTO!..

                // persist internal lists and with belongness: 
                for (AptUnitOccupancy occupancy : unitData.occupancies()) {
                    occupancy.unit().set(unit);
                    persist(occupancy);
                }
                for (AptUnitItem detail : unitData.details()) {
                    detail.belongsTo().set(unit);
                    persist(detail);
                }
            }

            //Do not publish until data is clean-up
            if (true) {
                PublicDataUpdater.updateIndexData(building);
            }

        }

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(buildings.size()).append(" buildings, ").append(unitCount).append(" units");
        return sb.toString();
    }

    public String importData() {
        try {
            Importer importer = new Importer();
            importer.setAttachMedia(this.getParameter(MediaGenerator.ATTACH_MEDIA_PARAMETER) != Boolean.FALSE);

            importer.start();

            StringBuilder sb = new StringBuilder();

            sb.append("Imported ").append(importer.getModel().getBuildings().size()).append(" buildings, ");
            sb.append(importer.getModel().getFloorplans().size()).append(" floorplans");
            sb.append(importer.getModel().getUnits().size()).append(" units");

            return sb.toString();
        } catch (Exception e) {
            log.error("Failed to import XML data", e);
            throw new Error("Failed to import XML data");
        }
    }

    @Override
    public String create() {
        String generated = generate();
        String imported = importData();

        StringBuilder sb = new StringBuilder();

        sb.append("--------- GENERATED ----------");
        sb.append(generated);
        sb.append("--------- IMPORTED -----------");
        sb.append(imported);

        return sb.toString();
    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");

        List<Parking> parkings = PersistenceServicesFactory.getPersistenceService().query(new EntityQueryCriteria<Parking>(Parking.class));
        sb.append(parkings.size()).append(" parkings\n");
        for (Parking parking : parkings) {
            sb.append("\t");
            sb.append(parking);
            sb.append("\n");
        }

        List<Locker> lockers = PersistenceServicesFactory.getPersistenceService().query(new EntityQueryCriteria<Locker>(Locker.class));
        sb.append(lockers.size()).append(" lockers\n");
        for (Locker locker : lockers) {
            sb.append("\t");
            sb.append(locker);
            sb.append("\n");
        }

        List<Floorplan> floorplans = PersistenceServicesFactory.getPersistenceService().query(new EntityQueryCriteria<Floorplan>(Floorplan.class));
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
        // PersistenceServicesFactory.getPersistenceService().retrieve(floorplanCriteria);
        // sb.append("Floorplan: ").append(floorplan);

        List<Building> buildings = PersistenceServicesFactory.getPersistenceService().query(new EntityQueryCriteria<Building>(Building.class));
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

            for (Phone phone : building.contacts().phones()) {
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
            List<AptUnit> units = PersistenceServicesFactory.getPersistenceService().query(criteria);
            sb.append("\tBuilding has ").append(units.size()).append(" units\n");

            for (AptUnit unit : units) {
                sb.append("\t");
                sb.append(unit.info().floor().getStringView()).append(" floor");
                sb.append(" ");
                sb.append(unit.info().area().getStringView()).append(" sq. ft.");
                sb.append(" ");
                sb.append(unit.belongsTo().info().propertyCode().getStringView());
                sb.append(" ");
                sb.append(unit.marketing().floorplan());
                sb.append(" | ");
                sb.append(unit.marketing().floorplan().name().getStringView()); // .append(" ").append(unit.floorplan().pictures());
                sb.append("\n");
                sb.append("\t\t").append(unit.info().utilities()).append("\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    // Genric DTO -> O convertion:
    public static <S extends IEntity, D extends S> S down(D src, Class<S> dstClass) {
        S dst = EntityFactory.create(dstClass);
        dst.set(src);
        return dst;
    }
}
