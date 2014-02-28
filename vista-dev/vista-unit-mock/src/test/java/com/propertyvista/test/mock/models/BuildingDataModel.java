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
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductDeposit;
import com.propertyvista.domain.financial.offering.ProductDeposit.ValueType;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.pmc.IntegrationSystem;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
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

    public Building addBuilding(String provinceCode) {
        Building building = EntityFactory.create(Building.class);
        building.propertyCode().setValue(String.valueOf(System.currentTimeMillis()).substring(5));
        building.integrationSystemId().setValue(IntegrationSystem.internal);

        building.info().address().province().set(getDataModel(LocationsDataModel.class).getProvinceByCode(provinceCode));

        generateParking(building);
        generateLockerArea(building);

        Persistence.service().persist(building);

        Service standardResidentialService = generateCatalog(building);

        standardResidentialServices.put(building, standardResidentialService);

        Persistence.service().persist(building);
        addItem(building);

        return building;
    }

    /** Creates Building in Ontario */
    public Building addBuilding() {
        return addBuilding("ON");
    }

    public ProductItem addResidentialUnitServiceItem(Building building, BigDecimal price) {
        Service standardResidentialService = standardResidentialServices.get(building);

        standardResidentialService = Persistence.retrieveDraftForEdit(Service.class, standardResidentialService.getPrimaryKey());

        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.name().setValue(arCodes.get(ARCode.Type.Residential).get(0).name().getValue());
        productItem.element().set(generateResidentialUnit(building));
        productItem.price().setValue(price);
        productItem.description().setValue(productItem.name().getValue());

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
        standardResidentialService.code().set(arCodes.get(ARCode.Type.Residential).get(0));
        standardResidentialService.version().name().setValue("Standard Residential Unit");
        standardResidentialService.version().description().setValue("Standard Residential Unit Lease for 1 year term");

        // add deposits
        standardResidentialService
                .version()
                .depositLMR()
                .set(createDeposit(DepositType.LastMonthDeposit, ValueType.Percentage, new BigDecimal("1.0"), standardResidentialService.version().name()
                        .getValue()));

        Persistence.service().persist(standardResidentialService);
        return standardResidentialService;
    }

    private void generateFeatures(Building building, Service standardResidentialService) {
        for (ARCode.Type type : ARCode.Type.features()) {
            List<ARCode> codes = arCodes.get(type);
            for (ARCode arCode : codes) {
                generateFeature(building, standardResidentialService, arCode);
            }
        }
    }

    private void generateFeature(Building building, Service standardResidentialService, ARCode arCode) {
        Feature feature = EntityFactory.create(Feature.class);

        feature.catalog().set(building.productCatalog());

        feature.code().set(arCode);

        feature.version().name().setValue("Regular " + arCode.name().getValue());
        feature.version().description().setValue("Feature - " + arCode.name().getValue());

        switch (arCode.type().getValue()) {
        case Parking:
            feature.version().recurring().setValue(true);
            feature.version().depositSecurity()
                    .set(createDeposit(DepositType.SecurityDeposit, ValueType.Percentage, new BigDecimal("1.00"), feature.version().name().getValue()));
            for (Parking parking : building.parkings()) {
                generateFeatureItem(feature, parking, arCode, "80.00");
            }
            break;
        case Locker:
            feature.version().recurring().setValue(true);
            feature.version().depositSecurity()
                    .set(createDeposit(DepositType.SecurityDeposit, ValueType.Percentage, new BigDecimal("1.00"), feature.version().name().getValue()));
            for (LockerArea lockerArea : building.lockerAreas()) {
                generateFeatureItem(feature, lockerArea, arCode, "60.00");
            }
            break;
        case Pet:
            feature.version().recurring().setValue(true);
            feature.version().depositSecurity()
                    .set(createDeposit(DepositType.SecurityDeposit, ValueType.Monetary, new BigDecimal("200.00"), feature.version().name().getValue()));
            generateFeatureItem(feature, arCode, "20");
            break;
        case AddOn:
            feature.version().recurring().setValue(true);
            generateFeatureItem(feature, arCode, "40");
            break;
        case OneTime:
            feature.version().recurring().setValue(false);
            generateFeatureItem(feature, arCode, "30");
            break;

        default:
            break;
        }

        Persistence.service().persist(feature);

        standardResidentialService = Persistence.retrieveDraftForEdit(Service.class, standardResidentialService.getPrimaryKey());

        standardResidentialService.version().features().add(feature);
        Persistence.service().persist(standardResidentialService);

    }

    private ProductDeposit createDeposit(DepositType depositType, ValueType valueType, BigDecimal value, String desc) {
        ProductDeposit deposit = EntityFactory.create(ProductDeposit.class);
        deposit.enabled().setValue(true);
        deposit.depositType().setValue(depositType);
        deposit.valueType().setValue(valueType);
        deposit.value().setValue(value);
        String description = deposit.depositType().getStringView() + ", " + desc;
        deposit.description().setValue(description.length() > 40 ? description.substring(0, 40) : description);
        return deposit;
    }

    private ProductItem generateFeatureItem(Feature feature, BuildingElement element, ARCode code, String price) {
        ProductItem productItem = EntityFactory.create(ProductItem.class);
        productItem.name().setValue(code.name().getValue());
        productItem.price().setValue(new BigDecimal(price));
        productItem.description().setValue(code.name().getStringView() + " Description");
        if (element != null) {
            productItem.element().set(element);
        }
        feature.version().items().add(productItem);
        return productItem;
    }

    private ProductItem generateFeatureItem(Feature feature, ARCode code, String price) {
        return generateFeatureItem(feature, null, code, price);
    }

    private void generateConcessions(Building building) {
        // TODO Auto-generated method stub

    }

}
