/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.test.mock.MockDataModel;

public class BuildingDataModel extends MockDataModel<Building> {

    enum Usage {
        available, used
    }

    private static Map<Service.ServiceType, List<ServiceItemType>> serviceMeta;

    private Map<Feature.Type, List<FeatureItemType>> featureMeta;

    private final Map<Building, Service> standardResidentialServices;

    public BuildingDataModel() {
        standardResidentialServices = new HashMap<Building, Service>();
    }

    @Override
    protected void generate() {
        createServiceMeta();

        Building building = EntityFactory.create(Building.class);
        building.propertyCode().setValue(String.valueOf(System.currentTimeMillis()).substring(5));

        building.info().address().province().set(getDataModel(LocationsDataModel.class).getProvinceByCode("ON"));

        generateParking(building);
        generateLockerArea(building);

        Persistence.service().persist(building);

        Service standardResidentialService = generateCatalog(building);

        standardResidentialServices.put(building, standardResidentialService);

        Persistence.service().persist(building);
        addItem(building);
        super.setCurrentItem(building);
    }

    @Override
    public void setCurrentItem(Building item) {
        throw new NotImplementedException();
    }

    public ProductItem addResidentialUnitServiceItem(BigDecimal price) {
        Service standardResidentialService = standardResidentialServices.get(getCurrentItem());

        standardResidentialService = Persistence.retrieveDraftForEdit(Service.class, standardResidentialService.getPrimaryKey());

        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(serviceMeta.get(Service.ServiceType.residentialUnit).get(0));
        productItem.element().set(generateResidentialUnit(getCurrentItem()));
        productItem.price().setValue(price);
        productItem.description().setValue(productItem.type().name().getValue());

        standardResidentialService.version().items().add(productItem);
        Persistence.service().persist(standardResidentialService);

        return productItem;
    }

    private AptUnit generateResidentialUnit(Building building) {
        AptUnit unit = EntityFactory.create(AptUnit.class);
        unit.building().set(building);

        ServerSideFactory.create(BuildingFacade.class).persist(unit);
        return unit;
    }

    private void generateParking(Building building) {
        Parking parking = EntityFactory.create(Parking.class);
        building.parkings().setAttachLevel(AttachLevel.Attached);
        building.parkings().add(parking);
    }

    private void generateLockerArea(Building building) {
        LockerArea lockerArea = EntityFactory.create(LockerArea.class);
        building.lockerAreas().setAttachLevel(AttachLevel.Attached);
        building.lockerAreas().add(lockerArea);
    }

    private void createServiceMeta() {
        serviceMeta = new HashMap<Service.ServiceType, List<ServiceItemType>>();
        featureMeta = new HashMap<Feature.Type, List<FeatureItemType>>();

        for (ServiceItemType serviceItemType : getDataModel(ProductItemTypesDataModel.class).getServiceItemTypes()) {
            if (!serviceMeta.containsKey(serviceItemType.serviceType().getValue())) {
                serviceMeta.put(serviceItemType.serviceType().getValue(), new ArrayList<ServiceItemType>());
            }
            serviceMeta.get(serviceItemType.serviceType().getValue()).add(serviceItemType);
        }

        for (FeatureItemType featureItemType : getDataModel(ProductItemTypesDataModel.class).getFeatureItemTypes()) {
            if (!featureMeta.containsKey(featureItemType.featureType().getValue())) {
                featureMeta.put(featureItemType.featureType().getValue(), new ArrayList<FeatureItemType>());
            }
            featureMeta.get(featureItemType.featureType().getValue()).add(featureItemType);
        }
    }

    private Service generateCatalog(Building building) {

        ServerSideFactory.create(DefaultProductCatalogFacade.class).createFor(building);
        ServerSideFactory.create(DefaultProductCatalogFacade.class).persistFor(building);

        Service standardResidentialService = generateResidentialUnitService(building);

        generateFeatures(building, standardResidentialService);

        generateConcessions(building);

        return standardResidentialService;
    }

    private Service generateResidentialUnitService(Building building) {
        Service standardResidentialService = EntityFactory.create(Service.class);
        standardResidentialService.catalog().set(building.productCatalog());
        standardResidentialService.serviceType().setValue(Service.ServiceType.residentialUnit);
        standardResidentialService.version().name().setValue("Standard Residential Unit");
        standardResidentialService.version().description().setValue("Standard Residential Unit Lease for 1 year term");

        Persistence.service().persist(standardResidentialService);
        return standardResidentialService;
    }

    private void generateFeatures(Building building, Service standardResidentialService) {
        for (Feature.Type serviceType : featureMeta.keySet()) {
            generateFeature(building, standardResidentialService, serviceType);
        }
    }

    private void generateFeature(Building building, Service standardResidentialService, Feature.Type type) {
        Feature feature = EntityFactory.create(Feature.class);

        feature.catalog().set(building.productCatalog());

        feature.featureType().setValue(type);
        feature.version().name().setValue("Regular " + type.name());
        feature.version().description().setValue("Feature - " + type.name());

        switch (feature.featureType().getValue()) {
        case parking:
            feature.version().recurring().setValue(true);
            for (Parking parking : building.parkings()) {
                for (ProductItemType productItemType : featureMeta.get(Feature.Type.parking)) {
                    generateParkingFeatureItem(feature, parking, productItemType);
                }
            }
            break;
        case locker:
            feature.version().recurring().setValue(true);
            for (LockerArea lockerArea : building.lockerAreas()) {
                for (ProductItemType productItemType : featureMeta.get(Feature.Type.locker)) {
                    generateLockerAreaFeatureItem(feature, lockerArea, productItemType);
                }
            }
            break;
        case pet:
            feature.version().recurring().setValue(true);
            for (ProductItemType productItemType : featureMeta.get(Feature.Type.pet)) {
                generatePetFeatureItem(feature, productItemType);
            }
            break;
        case addOn:
            feature.version().recurring().setValue(true);
            for (ProductItemType productItemType : featureMeta.get(Feature.Type.addOn)) {
                generateAddOnFeatureItem(feature, productItemType);
            }
            break;
        case oneTimeCharge:
            feature.version().recurring().setValue(true);
            for (ProductItemType productItemType : featureMeta.get(Feature.Type.oneTimeCharge)) {
                generateOneTimeChargeFeatureItem(feature, productItemType);
            }
            break;
        case booking:
            feature.version().recurring().setValue(false);
            for (ProductItemType productItemType : featureMeta.get(Feature.Type.booking)) {
                generateBookingFeatureItem(feature, productItemType);
            }
            break;

        default:
            break;
        }

        Persistence.service().persist(feature);

        standardResidentialService = Persistence.retrieveDraftForEdit(Service.class, standardResidentialService.getPrimaryKey());

        standardResidentialService.version().features().add(feature);
        Persistence.service().persist(standardResidentialService);

    }

    private void generateParkingFeatureItem(Feature feature, Parking parking, ProductItemType type) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(type);
        productItem.element().set(parking);
        productItem.price().setValue(new BigDecimal("80.00"));
        productItem.description().setValue(type.name().getValue());
        feature.version().items().add(productItem);
    }

    private void generateLockerAreaFeatureItem(Feature feature, LockerArea lockerArea, ProductItemType type) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(type);
        productItem.element().set(lockerArea);
        productItem.price().setValue(new BigDecimal("60.00"));
        productItem.description().setValue(type.name().getValue());
        feature.version().items().add(productItem);
    }

    private void generateAddOnFeatureItem(Feature feature, ProductItemType type) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(type);
        productItem.price().setValue(new BigDecimal("40.00"));
        productItem.description().setValue(type.name().getValue());
        feature.version().items().add(productItem);
    }

    private void generateOneTimeChargeFeatureItem(Feature feature, ProductItemType type) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(type);
        productItem.price().setValue(new BigDecimal("30.00"));
        productItem.description().setValue(type.name().getValue());
        feature.version().items().add(productItem);
    }

    private void generatePetFeatureItem(Feature feature, ProductItemType type) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(type);
        productItem.price().setValue(new BigDecimal("20.00"));
        productItem.description().setValue(type.name().getValue());
        feature.version().items().add(productItem);
    }

    private void generateBookingFeatureItem(Feature feature, ProductItemType type) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(type);
        productItem.price().setValue(new BigDecimal("100.00"));
        productItem.description().setValue(type.name().getValue());
        feature.version().items().add(productItem);
    }

    private void generateConcessions(Building building) {
        // TODO Auto-generated method stub

    }

}
