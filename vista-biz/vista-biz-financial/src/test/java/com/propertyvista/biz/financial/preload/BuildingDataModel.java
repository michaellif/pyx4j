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
package com.propertyvista.biz.financial.preload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;

import com.propertyvista.biz.occupancy.OccupancyFacade;
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
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;

public class BuildingDataModel {

    enum Usage {
        available, used
    }

    private Map<Service.ServiceType, List<ServiceItemType>> serviceMeta;

    private Map<Feature.Type, List<FeatureItemType>> featureMeta;

    private final ProductItemTypesDataModel productItemTypesDataModel;

    private Building building;

    private Service standardResidentialService;

    public BuildingDataModel(PreloadConfig config, ProductItemTypesDataModel productItemTypesDataModel) {
        this.productItemTypesDataModel = productItemTypesDataModel;
        createServiceMeta();
    }

    public IEntity getBuilding() {
        return building;
    }

    public AptUnit generateResidentialUnit() {
        AptUnit unit = EntityFactory.create(AptUnit.class);
        unit.building().set(building);
        AptUnitOccupancySegment segment = EntityFactory.create(AptUnitOccupancySegment.class);
        segment.dateFrom().setValue(OccupancyFacade.MIN_DATE);
        segment.dateTo().setValue(OccupancyFacade.MAX_DATE);
        segment.status().setValue(AptUnitOccupancySegment.Status.pending);
        unit.unitOccupancySegments().add(segment);
        Persistence.service().persist(unit);
        return unit;
    }

    public void generate() {

        building = EntityFactory.create(Building.class);

        generateParking();
        generateLockerArea();

        Persistence.service().persist(building);

        generateCatalog();

        Persistence.service().persist(building);
    }

    private void generateParking() {
        Parking parking = EntityFactory.create(Parking.class);
        building._Parkings().setAttachLevel(AttachLevel.Attached);
        building._Parkings().add(parking);
    }

    private void generateLockerArea() {
        LockerArea lockerArea = EntityFactory.create(LockerArea.class);
        building._LockerAreas().setAttachLevel(AttachLevel.Attached);
        building._LockerAreas().add(lockerArea);
    }

    private void createServiceMeta() {
        serviceMeta = new HashMap<Service.ServiceType, List<ServiceItemType>>();
        featureMeta = new HashMap<Feature.Type, List<FeatureItemType>>();

        for (ServiceItemType serviceItemType : productItemTypesDataModel.getServiceItemTypes()) {
            if (!serviceMeta.containsKey(serviceItemType.serviceType().getValue())) {
                serviceMeta.put(serviceItemType.serviceType().getValue(), new ArrayList<ServiceItemType>());
            }
            serviceMeta.get(serviceItemType.serviceType().getValue()).add(serviceItemType);
        }

        for (FeatureItemType featureItemType : productItemTypesDataModel.getFeatureItemTypes()) {
            if (!featureMeta.containsKey(featureItemType.featureType().getValue())) {
                featureMeta.put(featureItemType.featureType().getValue(), new ArrayList<FeatureItemType>());
            }
            featureMeta.get(featureItemType.featureType().getValue()).add(featureItemType);
        }
    }

    private void generateCatalog() {

        generateResidentialUnitService();

        generateFeatures();

        generateConcessions();

    }

    private void generateResidentialUnitService() {
        standardResidentialService = EntityFactory.create(Service.class);
        standardResidentialService.catalog().set(building.productCatalog());
        standardResidentialService.version().type().setValue(Service.ServiceType.residentialUnit);
        standardResidentialService.version().name().setValue("Standard Residential Unit");
        standardResidentialService.version().description().setValue("Standard Residential Unit Lease for 1 year term");

        standardResidentialService.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(standardResidentialService);

    }

    public ProductItem generateResidentialUnitServiceItem() {
        standardResidentialService = Persistence.retrieveDraftForEdit(Service.class, standardResidentialService.getPrimaryKey());

        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(serviceMeta.get(Service.ServiceType.residentialUnit).get(0));
        productItem.element().set(generateResidentialUnit());
        productItem.price().setValue(new BigDecimal("930.30"));
        productItem.description().setValue(productItem.type().name().getValue());

        standardResidentialService.version().items().add(productItem);
        standardResidentialService.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(standardResidentialService);

        return productItem;
    }

    private void generateFeatures() {
        for (Feature.Type serviceType : featureMeta.keySet()) {
            generateFeature(serviceType);
        }
    }

    private void generateFeature(Feature.Type type) {
        Feature feature = EntityFactory.create(Feature.class);

        feature.catalog().set(building.productCatalog());

        feature.version().type().setValue(type);
        feature.version().name().setValue("Regular " + type.name());
        feature.version().description().setValue("Feature - " + type.name());

        switch (feature.version().type().getValue()) {
        case parking:
            feature.version().recurring().setValue(true);
            for (Parking parking : building._Parkings()) {
                for (ProductItemType productItemType : featureMeta.get(Feature.Type.parking)) {
                    generateParkingFeatureItem(feature, parking, productItemType);
                }
            }
            break;
        case locker:
            feature.version().recurring().setValue(true);
            for (LockerArea lockerArea : building._LockerAreas()) {
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
        case booking:
            feature.version().recurring().setValue(false);
            for (ProductItemType productItemType : featureMeta.get(Feature.Type.booking)) {
                generateBookingFeatureItem(feature, productItemType);
            }
            break;

        default:
            break;
        }

        feature.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(feature);

        standardResidentialService = Persistence.retrieveDraftForEdit(Service.class, standardResidentialService.getPrimaryKey());

        standardResidentialService.version().features().add(feature);
        standardResidentialService.saveAction().setValue(SaveAction.saveAsFinal);
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

    private void generateConcessions() {
        // TODO Auto-generated method stub

    }

}
