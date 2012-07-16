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
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.BuildingsGenerator;
import com.propertvista.generator.Dashboards;
import com.propertvista.generator.MediaGenerator;
import com.propertvista.generator.PreloadData;
import com.propertvista.generator.ProductCatalogGenerator;
import com.propertvista.generator.gdo.ProductItemTypesGDO;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.admin.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.media.Media;
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
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.vendor.Vendor;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.portal.server.preloader.util.BaseVistaDevDataPreloader;
import com.propertyvista.server.common.reference.PublicDataUpdater;
import com.propertyvista.server.common.reference.geo.GeoLocator.Mode;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;
import com.propertyvista.server.domain.FileBlob;
import com.propertyvista.server.domain.FileImageThumbnailBlob;
import com.propertyvista.server.jobs.TaskRunner;

public class BuildingPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(BuildingPreloader.class);

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Complex.class, Building.class, AptUnit.class, AptUnitItem.class, Floorplan.class, Vendor.class, Elevator.class, Boiler.class,
                    Roof.class, Parking.class, ParkingSpot.class, LockerArea.class, Locker.class, Media.class, FileImageThumbnailBlob.class, FileBlob.class,
                    Feature.class, PropertyManager.class, ProductCatalog.class);
        } else {
            return "This is production";
        }
    }

    private String generate() {
        BuildingsGenerator generator = new BuildingsGenerator(config().buildingsGenerationSeed);

        ProductItemTypesGDO productItemTypes = new ProductItemTypesGDO();
        {
            EntityQueryCriteria<ServiceItemType> criteria = EntityQueryCriteria.create(ServiceItemType.class);
            productItemTypes.serviceItemTypes.addAll(Persistence.service().query(criteria));
        }
        {
            EntityQueryCriteria<FeatureItemType> criteria = EntityQueryCriteria.create(FeatureItemType.class);
            productItemTypes.featureItemTypes.addAll(Persistence.service().query(criteria));
        }

        ProductCatalogGenerator productCatalogGenerator = new ProductCatalogGenerator(productItemTypes);

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

// TODO : let's leave dashboard empty - in runtime the first Building dashboard will be used by default!
//        for (Complex complex : complexes) {
//            complex.dashboard().set(DataGenerator.random(availableDashboards.buildingDashboards));
//        }

        Persistence.service().persist(complexes);
        List<Complex> complexesWithBuildings = new Vector<Complex>();

        // create some management companies:
        List<PropertyManager> managements = new Vector<PropertyManager>();
        for (String mngName : PreloadData.MANAGEMENT_COMPANY) {
            managements.add(generator.createPropertyManager(mngName));
        }
        Persistence.service().persist(managements);

        MerchantAccount merchantAccount = createMerchantAccount();

        // create some portfolios:
        List<Portfolio> portfolios = new Vector<Portfolio>();
        for (String pname : new String[] { "GTA", "East region", "West region" }) {
            Portfolio p = EntityFactory.create(Portfolio.class);
            p.name().setValue(pname);
            portfolios.add(p);
        }
        Persistence.service().persist(portfolios);

        int unitCount = 0;
        List<Building> buildings = generator.createBuildings(config().numResidentialBuildings);

        SharedGeoLocator.setMode(Mode.updateCache);
        int noGeoCount = 0;

        for (Building building : buildings) {
            if (building.info().address().location().isNull()) {
                if (!SharedGeoLocator.populateGeo(building.info().address())) {
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
            productCatalogGenerator.createProductCatalog(building.productCatalog());

            BuildingMerchantAccount bma = building.merchantAccounts().$();
            bma.merchantAccount().set(merchantAccount);
            building.merchantAccounts().add(bma);

            //Media
            if (this.getParameter(VistaDataPreloaderParameter.attachMedia) != Boolean.FALSE) {
                MediaGenerator.generatedBuildingMedia(building);
            }

            building.includedUtilities().add(RandomUtil.randomRetrieveNamed(Utility.class, 3));
            if (DataGenerator.randomBoolean()) {
                building.includedUtilities().add(RandomUtil.randomRetrieveNamed(Utility.class, 3));
            }
            building.externalUtilities().add(RandomUtil.randomRetrieveNamed(Utility.class, 3));

// TODO : let's leave dashboard empty - in runtime the first Building dashboard will be used by default!
//            building.dashboard().set(DataGenerator.random(availableDashboards.buildingDashboards));

            // Elevators
            List<Elevator> elevators = generator.createElevators(building, config().numElevators);
            for (Elevator elevator : elevators) {
                Persistence.service().persist(elevator.warranty().contract().contractor());
                Persistence.service().persist(elevator.maintenance().contract().contractor());
            }

            // Boilers
            List<Boiler> boilers = generator.createBoilers(building, config().numBoilers);
            for (Boiler boiler : boilers) {
                Persistence.service().persist(boiler.warranty().contract().contractor());
                Persistence.service().persist(boiler.maintenance().contract().contractor());
            }

            // Roofs
            List<Roof> roofs = generator.createRoofs(building, config().numRoofs);
            for (Roof roof : roofs) {
                Persistence.service().persist(roof.warranty().contract().contractor());
                Persistence.service().persist(roof.maintenance().contract().contractor());
            }

            // Parking:
            List<Parking> parkings = generator.createParkings(building, config().numParkings);
            for (Parking parking : parkings) {
                generator.createParkingSpots(parking, config().numParkingSpots);
            }

            // Lockers:
            List<LockerArea> lockerAreas = generator.createLockerAreas(building, config().numLockerAreas);
            for (LockerArea item : lockerAreas) {
                generator.createLockers(item, config().numLockers);
            }

            // Amenities:
            generator.createBuildingAmenities(building, 1 + RandomUtil.randomInt(5));

            // Floorplans:
            List<Floorplan> floorplans = generator.createFloorplans(building, config().numFloorplans);
            for (Floorplan floorplan : floorplans) {

                if (this.getParameter(VistaDataPreloaderParameter.attachMedia) != Boolean.FALSE) {
                    MediaGenerator.attachGeneratedFloorplanMedia(floorplan);
                }

            }

            // Units:
            List<AptUnit> units = generator.createUnits(building, floorplans, config().numFloors, config().numUnitsPerFloor);
            unitCount += units.size();
            for (AptUnit unitData : units) {
                productCatalogGenerator.createAptUnitServices(building.productCatalog(), unitData);
            }

            // fill Service Catalog with building elements:

            EntityQueryCriteria<Parking> buildingParkingsCriteria = EntityQueryCriteria.create(Parking.class);
            buildingParkingsCriteria.add(PropertyCriterion.eq(buildingParkingsCriteria.proto().building(), building));
            List<Parking> buildingParkings = Persistence.service().query(buildingParkingsCriteria);

            EntityQueryCriteria<LockerArea> buildingLockerCriteria = EntityQueryCriteria.create(LockerArea.class);
            buildingLockerCriteria.add(PropertyCriterion.eq(buildingLockerCriteria.proto().building(), building));
            List<LockerArea> buildingockers = Persistence.service().query(buildingLockerCriteria);

            EntityQueryCriteria<Roof> buildingRoofsCriteria = EntityQueryCriteria.create(Roof.class);
            buildingRoofsCriteria.add(PropertyCriterion.eq(buildingRoofsCriteria.proto().building(), building));
            List<Roof> buildingRoofs = Persistence.service().query(buildingRoofsCriteria);

// VISTA-1622 - CRM:Product Dictionary:Service item Types - delete not supported
//            for (Service service : building.productCatalog().services()) {
//                switch (service.version().type().getValue()) {
//                case garage:
//                    for (ProductItem item : service.version().items()) {
//                        item.element().set(RandomUtil.random(buildingParkings));
//                    }
//                    break;
//                case storage:
//                    for (ProductItem item : service.version().items()) {
//                        item.element().set(RandomUtil.random(buildingockers));
//                    }
//                    break;
//                case roof:
//                    for (ProductItem item : service.version().items()) {
//                        item.element().set(RandomUtil.random(buildingRoofs));
//                    }
//                    break;
//                }
//            }

            for (Feature feature : building.productCatalog().features()) {
                switch (feature.version().type().getValue()) {
                case parking:
                    for (ProductItem item : feature.version().items()) {
                        item.element().set(RandomUtil.random(buildingParkings));
                    }
                    break;
                case locker:
                    for (ProductItem item : feature.version().items()) {
                        item.element().set(RandomUtil.random(buildingockers));
                    }
                    break;
                }
            }

            Persistence.service().persist(building);
            // Save Versioned Items, 
            // Preload data in a past all for product catalog assignments in LaseSimulator
            Persistence.service().setTransactionSystemTime(DateUtils.detectDateformat("2008-01-01"));
            try {
                for (Concession concession : building.productCatalog().concessions()) {
                    concession.saveAction().setValue(SaveAction.saveAsFinal);
                    Persistence.service().persist(concession);
                }
                for (Feature feature : building.productCatalog().features()) {
                    feature.saveAction().setValue(SaveAction.saveAsFinal);
                    Persistence.service().persist(feature);
                }
                for (Service service : building.productCatalog().services()) {
                    service.saveAction().setValue(SaveAction.saveAsFinal);
                    Persistence.service().persist(service);
                }
            } finally {
                Persistence.service().setTransactionSystemTime(null);
            }

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
        sb.append("Created ").append(buildings.size()).append(" buildings\n");
        sb.append("Created ").append(unitCount).append(" units");
        return sb.toString();
    }

    private MerchantAccount createMerchantAccount() {
        final EntityQueryCriteria<OnboardingMerchantAccount> criteria = EntityQueryCriteria.create(OnboardingMerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().pmc(), VistaDeployment.getCurrentPmc()));
        final List<OnboardingMerchantAccount> accs = TaskRunner.runInAdminNamespace(new Callable<List<OnboardingMerchantAccount>>() {
            @Override
            public List<OnboardingMerchantAccount> call() {
                return Persistence.service().query(criteria);
            }
        });

        MerchantAccount singleAccount = null;

        for (OnboardingMerchantAccount acc : accs) {
            MerchantAccount merchantAccount = EntityFactory.create(MerchantAccount.class);
            merchantAccount.bankId().setValue(acc.bankId().getValue());
            merchantAccount.branchTransitNumber().setValue(acc.branchTransitNumber().getValue());
            merchantAccount.accountNumber().setValue(acc.accountNumber().getValue());
            merchantAccount.chargeDescription().setValue(acc.chargeDescription().getValue());
            merchantAccount.merchantTerminalId().setValue(acc.merchantTerminalId().getValue());
            merchantAccount.invalid().setValue(Boolean.FALSE);
            Persistence.service().persist(merchantAccount);
            if (singleAccount == null) {
                singleAccount = merchantAccount;
            }
            // join accounts
            acc.merchantAccountKey().setValue(merchantAccount.getPrimaryKey());
        }

        TaskRunner.runInAdminNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                Persistence.service().persist(accs);
                return null;
            }
        });

        return singleAccount;

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
