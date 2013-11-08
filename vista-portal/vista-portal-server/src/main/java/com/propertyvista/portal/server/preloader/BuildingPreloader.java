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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.financial.productcatalog.ProductCatalogFacade;
import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.PropertyManager;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.vendor.Vendor;
import com.propertyvista.generator.BuildingsGenerator;
import com.propertyvista.generator.BuildingsGenerator.BuildingsGeneratorConfig;
import com.propertyvista.generator.MediaGenerator;
import com.propertyvista.generator.PreloadData;
import com.propertyvista.generator.ProductCatalogGenerator;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.server.common.reference.PublicDataUpdater;
import com.propertyvista.server.common.reference.geo.GeoLocator.Mode;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;
import com.propertyvista.server.domain.FileBlob;
import com.propertyvista.server.domain.FileImageThumbnailBlob;

public class BuildingPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(BuildingPreloader.class);

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Complex.class, Building.class, AptUnit.class, AptUnitItem.class, Floorplan.class, Vendor.class, Elevator.class, Boiler.class,
                    Roof.class, Parking.class, ParkingSpot.class, LockerArea.class, Locker.class, MediaFile.class, FileImageThumbnailBlob.class,
                    FileBlob.class, Feature.class, PropertyManager.class, ProductCatalog.class);
        } else {
            return "This is production";
        }
    }

    private String generate() {
        BuildingsGenerator buildingGenerator = new BuildingsGenerator(config().buildingsGenerationSeed);
        ProductCatalogGenerator productCatalogGenerator = new ProductCatalogGenerator(0);

        // create some complexes:
        List<Complex> complexes = new Vector<Complex>();
        for (int i = 0; i < config().numComplexes; ++i) {
            complexes.add(buildingGenerator.createComplex("Complex #" + i));
        }

        Persistence.service().persist(complexes);
        List<Complex> complexesWithBuildings = new Vector<Complex>();

        // create some management companies:
        List<PropertyManager> managements = new Vector<PropertyManager>();
        for (String mngName : PreloadData.MANAGEMENT_COMPANY) {
            managements.add(buildingGenerator.createPropertyManager(mngName));
        }
        Persistence.service().persist(managements);

        MerchantAccount merchantAccount = getMerchantAccount();

        // create some portfolios:
        List<Portfolio> portfolios = new Vector<Portfolio>();
        for (String pname : new String[] { "GTA", "East region", "West region" }) {
            Portfolio p = EntityFactory.create(Portfolio.class);
            p.name().setValue(pname);
            portfolios.add(p);
        }
        Persistence.service().persist(portfolios);

        BuildingsGeneratorConfig config = new BuildingsGeneratorConfig();
        config.provinceCode = config().province;

        int unitCount = 0;
        List<Building> buildings = buildingGenerator.createBuildings(config().numResidentialBuildings, config);

        SharedGeoLocator.setMode(Mode.updateCache);
        int noGeoCount = 0;

        for (Building building : buildings) {
            if (building.info().location().isNull()) {
                if (!SharedGeoLocator.populateGeo(building)) {
                    noGeoCount++;
                    log.warn("Unable find location for {}", building.info().address().getStringView());
                }
            }

            if (DataGenerator.randomBoolean()) {
                Complex complex = DataGenerator.random(complexes);
                building.complex().set(complex);
                building.complexPrimary().setValue(!complexesWithBuildings.contains(complex));
                complexesWithBuildings.add(complex);
            }

            // TODO Need to be saving PropertyProfile, PetCharge
            building.propertyManager().set(DataGenerator.random(managements)); // temporary for Starlight!..

            // Service Catalog:

            ServerSideFactory.create(DefaultProductCatalogFacade.class).createFor(building);
            productCatalogGenerator.generateProductCatalog(building.productCatalog());
            building.useDefaultProductCatalog().setValue(true);

            if (merchantAccount != null) {
                BuildingMerchantAccount bma = building.merchantAccounts().$();
                bma.merchantAccount().set(merchantAccount);
                building.merchantAccounts().add(bma);
            }

            //Media
            if (this.getParameter(VistaDataPreloaderParameter.attachMedia) != Boolean.FALSE) {
                MediaGenerator.generatedBuildingMedia(building);
            }

// TODO : let's leave dashboard empty - in runtime the first Building dashboard will be used by default!
//            building.dashboard().set(DataGenerator.random(availableDashboards.buildingDashboards));

            // Elevators
            List<Elevator> elevators = buildingGenerator.createElevators(building, config().numElevators);
            for (Elevator elevator : elevators) {
                Persistence.service().persist(elevator.warranty().contract().contractor());
                Persistence.service().persist(elevator.maintenance().contract().contractor());
            }

            // Boilers
            List<Boiler> boilers = buildingGenerator.createBoilers(building, config().numBoilers);
            for (Boiler boiler : boilers) {
                Persistence.service().persist(boiler.warranty().contract().contractor());
                Persistence.service().persist(boiler.maintenance().contract().contractor());
            }

            // Roofs
            List<Roof> roofs = buildingGenerator.createRoofs(building, config().numRoofs);
            for (Roof roof : roofs) {
                Persistence.service().persist(roof.warranty().contract().contractor());
                Persistence.service().persist(roof.maintenance().contract().contractor());
            }

            // Parking:
            List<Parking> parkings = buildingGenerator.createParkings(building, config().numParkings);
            for (Parking parking : parkings) {
                buildingGenerator.createParkingSpots(parking, config().numParkingSpots);
            }

            // Lockers:
            List<LockerArea> lockerAreas = buildingGenerator.createLockerAreas(building, config().numLockerAreas);
            for (LockerArea item : lockerAreas) {
                buildingGenerator.createLockers(item, config().numLockers);
            }

            // Amenities:
            buildingGenerator.createBuildingAmenities(building, 1 + RandomUtil.randomInt(5));

            // Floorplans:
            List<Floorplan> floorplans = buildingGenerator.createFloorplans(building, config().numFloorplans);
            for (Floorplan floorplan : floorplans) {
                if (this.getParameter(VistaDataPreloaderParameter.attachMedia) != Boolean.FALSE) {
                    MediaGenerator.attachGeneratedFloorplanMedia(floorplan);
                }
            }

            Persistence.service().persist(building);

            // fill Service Catalog with building elements:
// VISTA-1622 - CRM:Product Dictionary:Service item Types - delete not supported
//            for (Service service : building.productCatalog().services()) {
//                switch (service.version().type().getValue()) {
//                case garage:
//                    for (ProductItem item : service.version().items()) {
//                        item.element().set(RandomUtil.random(parkings));
//                    }
//                    break;
//                case storage:
//                    for (ProductItem item : service.version().items()) {
//                        item.element().set(RandomUtil.random(lockerAreas));
//                    }
//                    break;
//                case roof:
//                    for (ProductItem item : service.version().items()) {
//                        item.element().set(RandomUtil.random(roofs));
//                    }
//                    break;
//                }
//            }

            for (Feature feature : building.productCatalog().features()) {
                switch (feature.type().getValue()) {
                case Parking:
                    for (ProductItem item : feature.version().items()) {
                        item.element().set(RandomUtil.random(parkings));
                    }
                    break;
                case Locker:
                    for (ProductItem item : feature.version().items()) {
                        item.element().set(RandomUtil.random(lockerAreas));
                    }
                    break;
                }
            }

            // Save Versioned Items,
            // Preload data in a past all for product catalog assignments in LaseSimulator
            SystemDateManager.setDate(DateUtils.detectDateformat("2008-01-01"));
            try {
                ServerSideFactory.create(ProductCatalogFacade.class).persist(building.productCatalog(), true);
            } finally {
                SystemDateManager.resetDate();
            }

            // Units:
            List<AptUnit> units = buildingGenerator.createUnits(building, floorplans, config().numFloors, config().numUnitsPerFloor);
            try {
                unitCount += units.size();
                for (AptUnit unit : units) {
                    SystemDateManager.setDate(RandomUtil.randomLogicalDate(2010, 2012));
                    ServerSideFactory.create(BuildingFacade.class).persist(unit);
                    productCatalogGenerator.createAptUnitServices(building.productCatalog(), unit);
                }
            } finally {
                SystemDateManager.resetDate();
            }
            ServerSideFactory.create(ProductCatalogFacade.class).persist(building.productCatalog(), true);

            ServerSideFactory.create(ProductCatalogFacade.class).updateUnitMarketPrice(building);

            //Do not publish until data is clean-up
            if (true) {
                PublicDataUpdater.updateIndexData(building);
            }
        }
        SharedGeoLocator.save();
        if (noGeoCount > 0) {
            noGeoCount++;
            log.warn("GeoLocation not found for {} buildings", noGeoCount);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(complexes.size()).append(" complexes\n");
        sb.append("Created ").append(buildings.size()).append(" buildings\n");
        sb.append("Created ").append(unitCount).append(" units");
        return sb.toString();
    }

    private MerchantAccount getMerchantAccount() {
        return Persistence.service().retrieve(EntityQueryCriteria.create(MerchantAccount.class));
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

}
