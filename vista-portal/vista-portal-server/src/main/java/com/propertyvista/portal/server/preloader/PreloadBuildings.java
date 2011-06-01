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

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.domain.Email;
import com.propertyvista.domain.Phone;
import com.propertyvista.domain.marketing.yield.AddOn;
import com.propertyvista.domain.marketing.yield.Amenity;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.portal.domain.ptapp.LeaseTerms;
import com.propertyvista.portal.server.generator.BuildingsGenerator;
import com.propertyvista.portal.server.importer.Importer;

public class PreloadBuildings extends BaseVistaDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PreloadBuildings.class);

    // private static String resourceFileName(String fileName) {
    // return PreloadBuildings.class.getPackage().getName().replace('.', '/') +
    // "/" + fileName;
    // }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Building.class, AptUnit.class, Floorplan.class, Email.class, Phone.class, Complex.class, Utility.class, AptUnitItem.class,
                    Amenity.class, Concession.class, AddOn.class, LeaseTerms.class, Parking.class, Locker.class);
        } else {
            return "This is production";
        }
    }

    private String generate() {
        BuildingsGenerator generator = new BuildingsGenerator(DemoData.BUILDINGS_GENERATION_SEED);

        LeaseTerms leaseTerms = generator.createLeaseTerms();
        PersistenceServicesFactory.getPersistenceService().persist(leaseTerms);

        List<Building> buildings = generator.createBuildings(DemoData.NUM_RESIDENTIAL_BUILDINGS);
        int unitCount = 0;
        for (Building building : buildings) {
            // TODO Need to be saving PropertyProfile, PetCharge
            persist(building);

            // parkings
            List<Parking> parkings = generator.createParkings(building, DemoData.NUM_PARKINGS);
            for (Parking parking : parkings) {
                persist(parking);
            }

            // lockers
            List<Locker> lockers = generator.createLockers(building, DemoData.NUM_LOCKERS);
            for (Locker locker : lockers) {
                persist(locker);
            }

            List<Floorplan> floorplans = generator.createFloorplans(building, DemoData.NUM_FLOORPLANS);
            for (Floorplan floorplan : floorplans) {
                persist(floorplan);
            }

            List<AptUnitDTO> units = generator.createUnits(building, floorplans, DemoData.NUM_FLOORS, DemoData.NUM_UNITS_PER_FLOOR);
            unitCount += units.size();
            for (AptUnitDTO unitDTO : units) {
                AptUnit unit = down(unitDTO, AptUnit.class);

                for (Utility utility : unitDTO.info().utilities()) {
                    persist(utility);
                }
                for (AptUnitAmenity amenity : unitDTO.amenities()) {
                    persist(amenity);
                }
                for (AddOn addOn : unitDTO.addOns()) {
                    persist(addOn);
                }
                for (Concession concession : unitDTO.financial().concessions()) {
                    persist(concession);
                }

                persist(unit); // persist real unit here, not DTO!..

                // persist internal lists and set correct belongness: 
                for (AptUnitOccupancy occupancy : unitDTO.occupancies()) {
                    occupancy.unit().set(unit);
                    persist(occupancy);
                }
                for (AptUnitItem detail : unitDTO.details()) {
                    detail.belongsTo().set(unit);
                    persist(detail);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(buildings.size()).append(" buildings, ").append(unitCount).append(" units");
        return sb.toString();
    }

    public String importData() {
        try {
            Importer importer = new Importer();
            importer.start();

            StringBuilder sb = new StringBuilder();

            sb.append("Imported ").append(importer.getMapper().getBuildings().size()).append(" buildings, ");
            sb.append(importer.getMapper().getFloorplans().size()).append(" floorplans");
            sb.append(importer.getMapper().getUnits().size()).append(" units");

            return sb.toString();
        } catch (Exception e) {
            log.error("Failed to import XML data", e);
            return "Failed to import XML data";
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
            EntityQueryCriteria<AptUnitDTO> criteria = new EntityQueryCriteria<AptUnitDTO>(AptUnitDTO.class);
            criteria.add(new PropertyCriterion(criteria.proto().belongsTo(), Restriction.EQUAL, building.getPrimaryKey()));
            List<AptUnitDTO> units = PersistenceServicesFactory.getPersistenceService().query(criteria);
            sb.append("\tBuilding has ").append(units.size()).append(" units\n");

            for (AptUnitDTO unit : units) {
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
                sb.append("\t\t").append(unit.financial().concessions()).append("\n");
                sb.append("\n");
                sb.append("\t\t").append(unit.info().utilities()).append("\n");
                sb.append("\t\t").append(unit.amenities()).append("\n");
                sb.append("\t\t").append(unit.details()).append("\n");
                sb.append("\t\t").append(unit.addOns()).append("\n");
                sb.append("\t\t").append(unit.financial().concessions()).append("\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    private static void persist(IEntity entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }

    // Genric DTO -> O convertion:
    public static <S extends IEntity, D extends S> S down(D src, Class<S> dstClass) {
        S dst = EntityFactory.create(dstClass);
        dst.set(src);
        return dst;
    }
}
