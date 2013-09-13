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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.pmc.IntegrationSystem;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.test.mock.MockDataModel;

public class BuildingDataModel extends MockDataModel<Building> {

    private static Map<ARCode.Type, List<ARCode>> arCodes;

    private final Map<Building, Service> standardResidentialServices;

    public BuildingDataModel() {
        standardResidentialServices = new HashMap<Building, Service>();
    }

    @Override
    protected void generate() {
        arCodes = new HashMap<ARCode.Type, List<ARCode>>();

        for (ARCode arCode : getDataModel(ARCodeDataModel.class).getAllItems()) {
            if (!arCodes.containsKey(arCode.type().getValue())) {
                arCodes.put(arCode.type().getValue(), new ArrayList<ARCode>());
            }
            arCodes.get(arCode.type().getValue()).add(arCode);
        }
    }

    public Building addBuilding() {

        Building building = EntityFactory.create(Building.class);
        building.propertyCode().setValue(String.valueOf(System.currentTimeMillis()).substring(5));
        building.integrationSystemId().setValue(IntegrationSystem.internal);

        building.info().address().province().set(getDataModel(LocationsDataModel.class).getProvinceByCode("ON"));

        generateParking(building);
        generateLockerArea(building);

        Persistence.service().persist(building);

        Service standardResidentialService = generateCatalog(building);

        standardResidentialServices.put(building, standardResidentialService);

        Persistence.service().persist(building);
        addItem(building);

        return building;
    }

    public ProductItem addResidentialUnitServiceItem(Building building, BigDecimal price) {
        Service standardResidentialService = standardResidentialServices.get(building);

        standardResidentialService = Persistence.retrieveDraftForEdit(Service.class, standardResidentialService.getPrimaryKey());

        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.code().set(arCodes.get(ARCode.Type.Residential).get(0));
        productItem.element().set(generateResidentialUnit(building));
        productItem.price().setValue(price);
        productItem.description().setValue(productItem.code().name().getValue());

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
        standardResidentialService.type().setValue(ARCode.Type.Residential);
        standardResidentialService.version().name().setValue("Standard Residential Unit");
        standardResidentialService.version().description().setValue("Standard Residential Unit Lease for 1 year term");

        Persistence.service().persist(standardResidentialService);
        return standardResidentialService;
    }

    private void generateFeatures(Building building, Service standardResidentialService) {
        for (ARCode.Type type : ARCode.Type.features()) {
            generateFeature(building, standardResidentialService, type);
        }
    }

    private void generateFeature(Building building, Service standardResidentialService, ARCode.Type type) {
        Feature feature = EntityFactory.create(Feature.class);

        feature.catalog().set(building.productCatalog());

        feature.type().setValue(type);

        feature.version().name().setValue("Regular " + type.name());
        feature.version().description().setValue("Feature - " + type.name());

        switch (type) {
        case Parking:
            feature.version().recurring().setValue(true);
            for (Parking parking : building.parkings()) {
                for (ARCode arCode : arCodes.get(type)) {
                    generateFeatureItem(feature, parking, arCode, "80.00");
                }
            }
            break;
        case Locker:
            feature.version().recurring().setValue(true);
            for (LockerArea lockerArea : building.lockerAreas()) {
                for (ARCode arCode : arCodes.get(type)) {
                    generateFeatureItem(feature, lockerArea, arCode, "60.00");
                }
            }
            break;
        case Pet:
            feature.version().recurring().setValue(true);
            for (ARCode arCode : arCodes.get(type)) {
                generateFeatureItem(feature, arCode, "20");
            }
            break;
        case AddOn:
            feature.version().recurring().setValue(true);
            for (ARCode arCode : arCodes.get(type)) {
                generateFeatureItem(feature, arCode, "40");
            }
            break;
        case OneTime:
            feature.version().recurring().setValue(false);
            for (ARCode arCode : arCodes.get(type)) {
                generateFeatureItem(feature, arCode, "30");
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

    private void generateFeatureItem(Feature feature, BuildingElement element, ARCode code, String price) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.code().set(code);
        productItem.price().setValue(new BigDecimal(price));
        productItem.description().setValue(code.name().getValue());
        if (element != null) {
            productItem.element().set(element);
        }
        feature.version().items().add(productItem);
    }

    private void generateFeatureItem(Feature feature, ARCode code, String price) {
        generateFeatureItem(feature, null, code, price);
    }

    private void generateConcessions(Building building) {
        // TODO Auto-generated method stub

    }

}
