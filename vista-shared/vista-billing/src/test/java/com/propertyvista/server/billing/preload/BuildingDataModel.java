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
package com.propertyvista.server.billing.preload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.financial.offering.DepositType;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class BuildingDataModel {

    enum Usage {
        available, used
    }

    private Map<Service.Type, List<ProductItemType>> serviceMeta;

    private Map<Feature.Type, List<ProductItemType>> featureMeta;

    private final ProductItemTypesDataModel productItemTypesDataModel;

    private Building building;

    private Service standardResidentialService;

    private boolean persist;

    public BuildingDataModel(ProductItemTypesDataModel productItemTypesDataModel) {
        this.productItemTypesDataModel = productItemTypesDataModel;
        createServiceMeta();
    }

    public IEntity getBuilding() {
        return building;
    }

    public AptUnit generateResidentialUnit() {
        AptUnit unit = EntityFactory.create(AptUnit.class);
        building._Units().add(unit);
        if (persist) {
            Persistence.service().persist(building);
        }
        return unit;
    }

    public ProductItem generateResidentialUnitServiceItem() {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(serviceMeta.get(Service.Type.residentialUnit).get(0));
        productItem.element().set(generateResidentialUnit());
        productItem.price().setValue(new BigDecimal("930.30"));

        standardResidentialService.items().add(productItem);

        if (persist) {
            Persistence.service().persist(standardResidentialService);
        }

        return productItem;
    }

    public void generate(boolean persist) {
        this.persist = persist;

        building = EntityFactory.create(Building.class);

        generateParking();
        generateLockerArea();
        generateCatalog();

        if (persist) {
            Persistence.service().persist(building);
        }
    }

    private void generateParking() {
        Parking parking = EntityFactory.create(Parking.class);
        building._Parkings().add(parking);
    }

    private void generateLockerArea() {
        LockerArea lockerArea = EntityFactory.create(LockerArea.class);
        building._LockerAreas().add(lockerArea);
    }

    private void createServiceMeta() {
        serviceMeta = new HashMap<Service.Type, List<ProductItemType>>();
        featureMeta = new HashMap<Feature.Type, List<ProductItemType>>();

        for (ProductItemType serviceItemType : productItemTypesDataModel.getServiceItemTypes()) {
            if (!serviceMeta.containsKey(serviceItemType.serviceType().getValue())) {
                serviceMeta.put(serviceItemType.serviceType().getValue(), new ArrayList<ProductItemType>());
            }
            serviceMeta.get(serviceItemType.serviceType().getValue()).add(serviceItemType);
        }

        for (ProductItemType featureItemType : productItemTypesDataModel.getFeatureItemTypes()) {
            if (!featureMeta.containsKey(featureItemType.featureType().getValue())) {
                featureMeta.put(featureItemType.featureType().getValue(), new ArrayList<ProductItemType>());
            }
            featureMeta.get(featureItemType.featureType().getValue()).add(featureItemType);
        }
    }

    private void generateCatalog() {
        building.productCatalog();
        generateResidentialUnitService();
        generateFeatures();
        generateConcessions();
    }

    private void generateResidentialUnitService() {
        standardResidentialService = EntityFactory.create(Service.class);
        building.productCatalog().services().add(standardResidentialService);

        standardResidentialService.type().setValue(Service.Type.residentialUnit);
        standardResidentialService.name().setValue("Standard Residential Unit");
        standardResidentialService.description().setValue("Standard Residential Unit Lease for 1 year term");

        standardResidentialService.depositType().setValue(RandomUtil.randomEnum(DepositType.class));

    }

    private void generateFeatures() {
        for (Feature.Type serviceType : featureMeta.keySet()) {
            generateFeature(serviceType);
        }
    }

    private void generateFeature(Feature.Type type) {
        Feature feature = EntityFactory.create(Feature.class);
        feature.catalog().set(building.productCatalog());

        feature.type().setValue(type);
        feature.name().setValue("Regular " + type.name());
        feature.description().setValue("Feature - " + type.name());

        feature.depositType().setValue(RandomUtil.randomEnum(DepositType.class));

        switch (feature.type().getValue()) {
        case parking:
            feature.isRecurring().setValue(true);
            for (Parking parking : building._Parkings()) {
                for (ProductItemType productItemType : featureMeta.get(Feature.Type.parking)) {
                    generateParkingFeatureItem(feature, parking, productItemType);
                }
            }
            break;
        case locker:
            feature.isRecurring().setValue(true);
            for (LockerArea lockerArea : building._LockerAreas()) {
                for (ProductItemType productItemType : featureMeta.get(Feature.Type.locker)) {
                    generateLockerAreaFeatureItem(feature, lockerArea, productItemType);
                }
            }
            break;
        case pet:
            feature.isRecurring().setValue(true);
            for (ProductItemType productItemType : featureMeta.get(Feature.Type.pet)) {
                generatePetFeatureItem(feature, productItemType);
            }
            break;
        case addOn:
            feature.isRecurring().setValue(false);
            for (ProductItemType productItemType : featureMeta.get(Feature.Type.addOn)) {
                generateAddOnFeatureItem(feature, productItemType);
            }
            break;

        default:
            break;
        }

        building.productCatalog().features().add(feature);

        standardResidentialService.features().add(feature);

    }

    private void generateParkingFeatureItem(Feature feature, Parking parking, ProductItemType type) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(type);
        productItem.element().set(parking);
        productItem.price().setValue(new BigDecimal("44.50"));
        feature.items().add(productItem);
    }

    private void generateLockerAreaFeatureItem(Feature feature, LockerArea lockerArea, ProductItemType type) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(type);
        productItem.element().set(lockerArea);
        productItem.price().setValue(new BigDecimal("23.88"));
        feature.items().add(productItem);
    }

    private void generatePetFeatureItem(Feature feature, ProductItemType type) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(type);
        productItem.price().setValue(new BigDecimal("10.00"));
        feature.items().add(productItem);
    }

    private void generateAddOnFeatureItem(Feature feature, ProductItemType type) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(type);
        productItem.price().setValue(new BigDecimal("40.00"));
        feature.items().add(productItem);
    }

    private void generateConcessions() {
        // TODO Auto-generated method stub

    }

}
