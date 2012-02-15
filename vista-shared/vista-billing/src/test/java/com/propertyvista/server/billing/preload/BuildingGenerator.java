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

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.DepositType;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class BuildingGenerator {

    private static final int UNITS_PER_TYPE = 2;

    private Map<Service.Type, List<ProductItemType>> serviceMeta;

    private Map<Feature.Type, List<ProductItemType>> featureMeta;

    private List<AptUnit> residentialUnits;;

    private final DataModel dataModel;

    private final Building building;

    public BuildingGenerator(DataModel dataModel) {
        this.dataModel = dataModel;
        building = EntityFactory.create(Building.class);

        createServiceMeta();

    }

    private void createServiceMeta() {
        serviceMeta = new HashMap<Service.Type, List<ProductItemType>>();
        featureMeta = new HashMap<Feature.Type, List<ProductItemType>>();

        List<ProductItemType> productItemTypes = dataModel.query(ProductItemType.class);
        for (ProductItemType productItemType : productItemTypes) {
            if (ProductItemType.Type.service.equals(productItemType.type().getValue())) {
                if (!serviceMeta.containsKey(productItemType.serviceType().getValue())) {
                    serviceMeta.put(productItemType.serviceType().getValue(), new ArrayList<ProductItemType>());
                }
                serviceMeta.get(productItemType.serviceType().getValue()).add(productItemType);
            } else if (ProductItemType.Type.feature.equals(productItemType.type().getValue())) {
                if (!featureMeta.containsKey(productItemType.featureType().getValue())) {
                    featureMeta.put(productItemType.featureType().getValue(), new ArrayList<ProductItemType>());
                }
                featureMeta.get(productItemType.featureType().getValue()).add(productItemType);
            }
        }
    }

    private void generate() {
        generateAptUnits();
        generateCatalog();

    }

    private void generateAptUnits() {
        residentialUnits = new ArrayList<AptUnit>();
        for (ProductItemType type : serviceMeta.get(Service.Type.residentialUnit)) {
            for (int i = 0; i < UNITS_PER_TYPE; i++) {
                generateResidentialUnit();
            }
        }
    }

    private void generateResidentialUnit() {
        AptUnit unit = EntityFactory.create(AptUnit.class);
        building._Units().add(unit);
        residentialUnits.add(unit);
    }

    private void generateCatalog() {
        building.productCatalog();
        generateResidentialUnitService();
        generateFeatures();
        generateConcessions();
    }

    private void generateResidentialUnitService() {
        Service service = EntityFactory.create(Service.class);
        building.productCatalog().services().add(service);

        service.type().setValue(Service.Type.residentialUnit);
        service.name().setValue("Standard Residential Unit");
        service.description().setValue("Standard Residential Unit Lease for 1 year term");

        service.depositType().setValue(RandomUtil.randomEnum(DepositType.class));

        int index = 0;
        for (ProductItemType type : serviceMeta.get(Service.Type.residentialUnit)) {
            for (int i = 0; i < UNITS_PER_TYPE; i++) {
                generateResidentialUnitServiceItem(service, type, residentialUnits.get(index++));
            }
        }
    }

    private void generateResidentialUnitServiceItem(Service service, ProductItemType type, AptUnit unit) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.type().set(type);
        productItem.element().set(unit);
        productItem.price().setValue(new BigDecimal(500 + RandomUtil.randomInt(500)));

        service.items().add(productItem);
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

        generateFeatureItems(type);
        building.productCatalog().features().add(feature);
    }

    private void generateFeatureItems(Feature.Type type) {
        for (Parking parking : building._Parkings()) {
            generateParkingFeatureItem(parking);
        }
    }

    private void generateParkingFeatureItem(Parking parking) {
        // TODO Auto-generated method stub

    }

    private void generateConcessions() {
        // TODO Auto-generated method stub

    }

    public static Building generate(DataModel dataModel) {
        BuildingGenerator generator = new BuildingGenerator(dataModel);
        generator.generate();
        return generator.building;
    }

}
