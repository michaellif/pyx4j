/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 24, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertvista.generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.propertvista.generator.gdo.ProductItemTypesGDO;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.DepositType;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.Service.Type;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class ProductCatalogGenerator {

    private static final I18n i18n = I18n.get(ProductCatalogGenerator.class);

    private final ProductItemTypesGDO serviceItemTypes;

    public ProductCatalogGenerator(ProductItemTypesGDO serviceItemTypes) {
        this.serviceItemTypes = serviceItemTypes;
    }

    private List<ProductItemType> getServiceItemTypes() {
        return this.serviceItemTypes.serviceItemTypes;
    }

    private List<ProductItemType> getFeatureItemTypes() {
        return this.serviceItemTypes.featureItemTypes;
    }

    //TODO rename create to something
    public void createProductCatalog(ProductCatalog catalog) {
        DataGenerator.setRandomSeed(RandomUtil.randomInt(1024));

        catalog.services().addAll(createServices(catalog));
        catalog.features().addAll(createFeatures(catalog));
        catalog.concessions().addAll(createConcessions(catalog));
        catalog.includedUtilities().addAll(createIncludedUtilities());
        catalog.externalUtilities().addAll(createExcludedUtilities(catalog.includedUtilities()));

        buildEligibilityMatrix(catalog);
    }

    public List<Service> createServices(ProductCatalog catalog) {
        List<Service> items = new ArrayList<Service>(Service.Type.values().length);
        for (Service.Type type : EnumSet.allOf(Service.Type.class)) {
            items.add(createService(catalog, type));
        }
        return items;
    }

    public List<Feature> createFeatures(ProductCatalog catalog) {
        List<Feature> items = new ArrayList<Feature>(Feature.Type.values().length);
        for (Feature.Type type : EnumSet.allOf(Feature.Type.class)) {
            items.add(createFeature(catalog, type));
        }
        return items;
    }

    public List<Concession> createConcessions(ProductCatalog catalog) {
        List<Concession> items = new ArrayList<Concession>(3);
        for (int i = 0; i < 3; ++i) {
            items.add(createConcession(catalog));
        }
        return items;
    }

    public void buildEligibilityMatrix(ProductCatalog catalog) {
        for (Service srv : catalog.services()) {
            if (srv.type().getValue().equals(Service.Type.residentialUnit) || srv.type().getValue().equals(Service.Type.residentialShortTermUnit)
                    || srv.type().getValue().equals(Service.Type.commercialUnit)) {

                int count = catalog.features().size();
                for (int i = 0; i < count; ++i) {
                    srv.features().add(catalog.features().get(i));
                }
            }

            int count = Math.min(2, catalog.concessions().size());
            for (int i = 0; i < count; ++i) {
                srv.concessions().add(RandomUtil.random(catalog.concessions(), "concessions", count));
            }
        }
    }

// internals:
    private Service createService(ProductCatalog catalog, Service.Type type) {
        Service item = EntityFactory.create(Service.class);
        item.catalog().set(catalog);

        item.type().setValue(type);
        item.name().setValue(RandomUtil.randomLetters(6));
        item.description().setValue("Service description");

        item.depositType().setValue(RandomUtil.randomEnum(DepositType.class));

        item.items().addAll(createServiceItems(type));

        return item;
    }

    private List<ProductItem> createServiceItems(Service.Type type) {

        List<ProductItemType> allowedItemTypes = new ArrayList<ProductItemType>();
        for (ProductItemType itemType : getServiceItemTypes()) {
            if (type.equals(itemType.serviceType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }

        int count = 0;
        switch (type) {
        case residentialUnit:
        case residentialShortTermUnit:
        case commercialUnit:
            return new ArrayList<ProductItem>();
        case roof:
        case garage:
        case storage:
        case sundry:
            count = 1;
            break;
        }

        List<ProductItem> items = new ArrayList<ProductItem>(count);
        if (!allowedItemTypes.isEmpty()) {
            for (int i = 0; i < count; ++i) {
                ProductItem item = EntityFactory.create(ProductItem.class);
                ProductItemType selectedItem = RandomUtil.random(allowedItemTypes);

                item.type().set(selectedItem);
                item.type().name().setValue(selectedItem.getStringView());
                item.type().serviceType().setValue(selectedItem.serviceType().getValue());

                item.price().setValue(new BigDecimal(500 + RandomUtil.randomInt(500)));
                item.description().setValue(type.toString() + " description");

                items.add(item);
            }
        }

        return items;
    }

    private Feature createFeature(ProductCatalog catalog, Feature.Type type) {
        Feature item = EntityFactory.create(Feature.class);
        item.catalog().set(catalog);

        item.type().setValue(type);
        item.name().setValue(RandomUtil.randomLetters(6));
        item.description().setValue("Feature description");

        item.depositType().setValue(RandomUtil.randomEnum(DepositType.class));

        item.recurring().setValue(RandomUtil.randomBoolean());
        item.mandatory().setValue(RandomUtil.randomBoolean());

        item.items().addAll(createFeatureItems(type));

        return item;
    }

    private List<ProductItem> createFeatureItems(Feature.Type type) {
        List<ProductItemType> allowedItemTypes = new ArrayList<ProductItemType>();
        for (ProductItemType itemType : getFeatureItemTypes()) {
            if (type.equals(itemType.featureType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }

        int count = Math.min(3, allowedItemTypes.size());
        List<ProductItem> items = new ArrayList<ProductItem>(count);
        if (!allowedItemTypes.isEmpty()) {
            for (int i = 0; i < count; ++i) {
                ProductItem item = EntityFactory.create(ProductItem.class);

                item.type().set(RandomUtil.random(allowedItemTypes));
                item.type().name().setValue(item.type().getStringView());
                item.description().setValue(type.toString() + " description");

                switch (type) {
                case parking:
                    item.price().setValue(new BigDecimal(5 + RandomUtil.randomInt(50)));
                    break;
                case locker:
                    item.price().setValue(new BigDecimal(5 + RandomUtil.randomInt(10)));
                    break;
                case pet:
                    item.price().setValue(new BigDecimal(20 + RandomUtil.randomInt(20)));
                    break;
                case booking:
                    item.price().setValue(new BigDecimal(5 + RandomUtil.randomInt(5)));
                    break;
                case addOn:
                    item.price().setValue(new BigDecimal(30 + RandomUtil.randomInt(50)));
                    break;
                case utility:
                    item.price().setValue(new BigDecimal(80 + RandomUtil.randomInt(50)));
                    break;
                }

                items.add(item);
            }
        }

        return items;
    }

    private Concession createConcession(ProductCatalog catalog) {
        Concession concession = EntityFactory.create(Concession.class);
        concession.catalog().set(catalog);

        concession.version().fromDate().setValue(DateUtils.detectDateformat("2012-01-01"));
        concession.version().type().setValue(RandomUtil.random(Concession.Type.values(), "Concession.Type", Concession.Type.values().length));

        if (concession.version().type().getValue() == Concession.Type.percentageOff) {
            concession.version().value().setValue(10d + RandomUtil.randomInt(11));
            concession.version().description()
                    .setValue(i18n.tr("Special Promotion Applies, {0}% Off The Value Of The Service", concession.version().value().getValue()));
        } else if (concession.version().type().getValue() == Concession.Type.monetaryOff) {
            concession.version().value().setValue(50d + RandomUtil.randomInt(50));
            concession.version().description()
                    .setValue(i18n.tr("Special Promotion Applies, ${0} Off The Value Of The Service", concession.version().value().getValue()));
        } else if (concession.version().type().getValue() == Concession.Type.promotionalItem) {
            concession.version().value().setValue(100d + RandomUtil.randomInt(100));
            concession.version().description()
                    .setValue(i18n.tr("Special Promotion Applies, ${0} In Promotional Items Or Services", concession.version().value().getValue()));
        } else if (concession.version().type().getValue() == Concession.Type.free) {
            concession.version().value().setValue(200d + RandomUtil.randomInt(100));
            concession.version().description().setValue(i18n.tr("Special Promotion Applies, Everything Completely Free"));
        }

        concession.version().term().setValue(RandomUtil.random(Concession.Term.values()));
        concession.version().condition().setValue(RandomUtil.random(Concession.Condition.values()));
        concession.version().status().setValue(RandomUtil.random(Concession.Status.values()));
        if (concession.version().status().getValue() == Concession.Status.approved) {
            concession.version().approvedBy().setValue("George W. Bush Jr.");
        }

        concession.version().effectiveDate().setValue(DataGenerator.randomDate(2));
        concession.version().expirationDate().setValue(DataGenerator.randomDate(4));

        return concession;
    }

    public List<ProductItemType> createIncludedUtilities() {
        List<ProductItemType> allowedItemTypes = new ArrayList<ProductItemType>();
        for (ProductItemType itemType : getFeatureItemTypes()) {
            if (Feature.Type.utility.equals(itemType.featureType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }

        List<ProductItemType> items = new ArrayList<ProductItemType>();
        if (!allowedItemTypes.isEmpty()) {
            int maxItems = Math.min(DataGenerator.randomInt(allowedItemTypes.size()) + 1, allowedItemTypes.size());
            for (int i = 0; i < maxItems; ++i) {
                items.add(RandomUtil.random(allowedItemTypes, "IncludedUtilities", maxItems));
            }
        }

        return items;
    }

    public List<ProductItemType> createExcludedUtilities(List<ProductItemType> includedOnes) {
        List<ProductItemType> allowedItemTypes = new ArrayList<ProductItemType>();
        for (ProductItemType itemType : getFeatureItemTypes()) {
            if (Feature.Type.utility.equals(itemType.featureType().getValue()) && !includedOnes.contains(itemType)) {
                allowedItemTypes.add(itemType);
            }
        }

        List<ProductItemType> items = new ArrayList<ProductItemType>();
        if (!allowedItemTypes.isEmpty()) {
            int maxItems = Math.min(DataGenerator.randomInt(allowedItemTypes.size()) + 1, allowedItemTypes.size());
            for (int i = 0; i < maxItems; ++i) {
                items.add(RandomUtil.random(allowedItemTypes, "ExcludedUtilities", maxItems));
            }
        }

        return items;
    }

    private Service getService(ProductCatalog catalog, Service.Type type) {
        for (Service service : catalog.services()) {
            if (service.type().getValue().equals(type)) {
                return service;
            }
        }
        throw new Error("Service of type " + type + " not found");
    }

    private List<ProductItemType> getServiceItemTypes(ProductCatalog catalog, Service.Type type) {
        List<ProductItemType> allowedItemTypes = new ArrayList<ProductItemType>();
        for (ProductItemType itemType : getServiceItemTypes()) {
            if (type.equals(itemType.serviceType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }
        return allowedItemTypes;
    }

    private static BigDecimal createUnitMarketRent(AptUnit unit) {
        BigDecimal base = new BigDecimal(900);
        base = base.add(new BigDecimal(unit.info()._bedrooms().getValue() * 150));
        base = base.add(new BigDecimal(unit.info()._bathrooms().getValue() * 50));
        return base.add(new BigDecimal(RandomUtil.randomInt(200)));
    }

    public List<ProductItem> createAptUnitServices(ProductCatalog catalog, AptUnit unit) {
        Service.Type type = RandomUtil.random(EnumSet.of(Type.residentialUnit, Type.residentialShortTermUnit, Type.commercialUnit));
        List<ProductItem> serviceItems = createBuildingElementServices(catalog, unit, type);
        serviceItems.get(0).price().setValue(createUnitMarketRent(unit));
        unit.financial()._marketRent().set(serviceItems.get(0).price());
        return serviceItems;

    }

    public List<ProductItem> createBuildingElementServices(ProductCatalog catalog, BuildingElement buildingElement, Service.Type type) {
        Service service = getService(catalog, type);
        List<ProductItemType> allowedItemTypes = getServiceItemTypes(catalog, type);

        List<ProductItem> items = new ArrayList<ProductItem>();

        ProductItem item = EntityFactory.create(ProductItem.class);
        ProductItemType selectedItem = RandomUtil.random(allowedItemTypes);

        item.type().set(selectedItem);
        item.type().name().setValue(selectedItem.getStringView());
        item.type().serviceType().setValue(selectedItem.serviceType().getValue());

        // This value may not be used in all cases and overridden later in generator
        item.price().setValue(new BigDecimal(500 + RandomUtil.randomInt(500)));
        item.description().setValue(type.toString() + " description");
        item.element().set(buildingElement);

        service.items().add(item);
        items.add(item);

        return items;
    }
}
