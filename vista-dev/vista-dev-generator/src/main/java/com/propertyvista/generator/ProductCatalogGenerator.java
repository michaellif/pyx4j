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
package com.propertyvista.generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.Service.ServiceType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.generator.gdo.ProductItemTypesGDO;
import com.propertyvista.generator.util.RandomUtil;

public class ProductCatalogGenerator {

    private static final I18n i18n = I18n.get(ProductCatalogGenerator.class);

    private final ProductItemTypesGDO serviceItemTypes;

    public ProductCatalogGenerator(ProductItemTypesGDO serviceItemTypes) {
        this.serviceItemTypes = serviceItemTypes;
    }

    private List<ServiceItemType> getServiceItemTypes() {
        return this.serviceItemTypes.serviceItemTypes;
    }

    private List<FeatureItemType> getFeatureItemTypes() {
        return this.serviceItemTypes.featureItemTypes;
    }

    //TODO rename create to something
    public void createProductCatalog(ProductCatalog catalog) {
        DataGenerator.setRandomSeed(RandomUtil.randomInt(1024));

        catalog.services().addAll(createServices(catalog));
        catalog.features().addAll(createFeatures(catalog));
        catalog.concessions().addAll(createConcessions(catalog));

        buildEligibilityMatrix(catalog);
    }

    public List<Service> createServices(ProductCatalog catalog) {
        List<Service> items = new ArrayList<Service>(Service.ServiceType.values().length);
        for (Service.ServiceType type : EnumSet.allOf(Service.ServiceType.class)) {
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
            if (srv.version().serviceType().getValue().equals(Service.ServiceType.residentialUnit)
                    || srv.version().serviceType().getValue().equals(Service.ServiceType.commercialUnit)) {

                int count = catalog.features().size();
                for (int i = 0; i < count; ++i) {
                    srv.version().features().add(catalog.features().get(i));
                }
            }

            int count = Math.min(2, catalog.concessions().size());
            for (int i = 0; i < count; ++i) {
                srv.version().concessions().add(RandomUtil.random(catalog.concessions(), "concessions", count));
            }
        }
    }

// internals:
    private Service createService(ProductCatalog catalog, Service.ServiceType type) {
        Service item = EntityFactory.create(Service.class);
        item.catalog().set(catalog);

        item.version().serviceType().setValue(type);
        item.version().name().setValue(RandomUtil.randomLetters(6));
        item.version().description().setValue("Service description");
        item.version().visibility().setValue(PublicVisibilityType.global);

        item.version().items().addAll(createServiceItems(type));
        return item;
    }

    private List<ProductItem> createServiceItems(Service.ServiceType type) {

        List<ServiceItemType> allowedItemTypes = new ArrayList<ServiceItemType>();
        for (ServiceItemType itemType : getServiceItemTypes()) {
            if (type.equals(itemType.serviceType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }

        int count = 0;
        switch (type) {
        case residentialUnit:
// VISTA-1622 - CRM:Product Dictionary:Service item Types - delete not supported
//        case residentialShortTermUnit:
        case commercialUnit:
            return new ArrayList<ProductItem>();
// VISTA-1622 - CRM:Product Dictionary:Service item Types - delete not supported
//        case roof:
//        case garage:
//        case storage:
//        case sundry:
//            count = 1;
//            break;
        }

        List<ProductItem> items = new ArrayList<ProductItem>(count);
        if (!allowedItemTypes.isEmpty()) {
            for (int i = 0; i < count; ++i) {
                ProductItem item = EntityFactory.create(ProductItem.class);
                ServiceItemType selectedItem = RandomUtil.random(allowedItemTypes);

                item.type().set(selectedItem);
                item.type().name().setValue(selectedItem.getStringView());
                item.type().<ServiceItemType> cast().serviceType().setValue(selectedItem.serviceType().getValue());

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

        item.version().featureType().setValue(type);
        item.version().name().setValue(RandomUtil.randomLetters(6));
        item.version().description().setValue("Feature description");
        item.version().visibility().setValue(PublicVisibilityType.global);

        item.version().recurring().setValue(RandomUtil.randomBoolean() && !Feature.Type.nonReccuring().contains(type));
        item.version().mandatory().setValue(RandomUtil.randomBoolean() && !Feature.Type.nonMandatory().contains(type));

        item.version().items().addAll(createFeatureItems(type));
        if (item.version().mandatory().isBooleanTrue()) {
            // if feature is mandatory - the default item should be set!
            ProductItem fi = RandomUtil.random(item.version().items());
            fi.isDefault().setValue(Boolean.TRUE);
        }
        return item;
    }

    private List<ProductItem> createFeatureItems(Feature.Type type) {
        List<FeatureItemType> allowedItemTypes = new ArrayList<FeatureItemType>();
        for (FeatureItemType itemType : getFeatureItemTypes()) {
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
        Concession item = EntityFactory.create(Concession.class);
        item.catalog().set(catalog);

        item.version().fromDate().setValue(DateUtils.detectDateformat("2012-01-01"));
        item.version().type().setValue(RandomUtil.random(Concession.Type.values(), "Concession.Type", Concession.Type.values().length));

        if (item.version().type().getValue() == Concession.Type.percentageOff) {
            item.version().value().setValue(new BigDecimal(RandomUtil.randomDouble(1.0)));
            item.version().description()
                    .setValue(i18n.tr("Special Promotion Applies, {0}% Off The Value Of The Service", item.version().value().getValue().floatValue() * 100));
        } else if (item.version().type().getValue() == Concession.Type.monetaryOff) {
            item.version().value().setValue(new BigDecimal(50d + RandomUtil.randomDouble(50)));
            item.version().description()
                    .setValue(i18n.tr("Special Promotion Applies, ${0} Off The Value Of The Service", item.version().value().getValue().floatValue()));
        } else if (item.version().type().getValue() == Concession.Type.promotionalItem) {
            item.version().value().setValue(new BigDecimal(100d + RandomUtil.randomDouble(100)));
            item.version().description()
                    .setValue(i18n.tr("Special Promotion Applies, ${0} In Promotional Items Or Services", item.version().value().getValue().floatValue()));
        } else if (item.version().type().getValue() == Concession.Type.free) {
            item.version().value().setValue(new BigDecimal(200d + RandomUtil.randomDouble(100)));
            item.version().description().setValue(i18n.tr("Special Promotion Applies, Everything Completely Free"));
        }

        item.version().term().setValue(RandomUtil.random(Concession.Term.values()));
        item.version().condition().setValue(RandomUtil.random(Concession.Condition.values()));
        item.version().mixable().setValue(RandomUtil.randomBoolean());

        item.version().effectiveDate().setValue(DataGenerator.randomDate(2));
        item.version().expirationDate().setValue(DataGenerator.randomDate(4));
        return item;
    }

    public List<FeatureItemType> createIncludedUtilities() {
        List<FeatureItemType> allowedItemTypes = new ArrayList<FeatureItemType>();
        for (FeatureItemType itemType : getFeatureItemTypes()) {
            if (Feature.Type.utility.equals(itemType.featureType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }

        List<FeatureItemType> items = new ArrayList<FeatureItemType>();
        if (!allowedItemTypes.isEmpty()) {
            int maxItems = Math.min(DataGenerator.randomInt(allowedItemTypes.size()) + 1, allowedItemTypes.size());
            for (int i = 0; i < maxItems; ++i) {
                items.add(RandomUtil.random(allowedItemTypes, "IncludedUtilities", maxItems));
            }
        }

        return items;
    }

    public List<FeatureItemType> createExcludedUtilities(List<FeatureItemType> includedOnes) {
        List<FeatureItemType> allowedItemTypes = new ArrayList<FeatureItemType>();
        for (FeatureItemType itemType : getFeatureItemTypes()) {
            if (Feature.Type.utility.equals(itemType.featureType().getValue()) && !includedOnes.contains(itemType)) {
                allowedItemTypes.add(itemType);
            }
        }

        List<FeatureItemType> items = new ArrayList<FeatureItemType>();
        if (!allowedItemTypes.isEmpty()) {
            int maxItems = Math.min(DataGenerator.randomInt(allowedItemTypes.size()) + 1, allowedItemTypes.size());
            for (int i = 0; i < maxItems; ++i) {
                items.add(RandomUtil.random(allowedItemTypes, "ExcludedUtilities", maxItems));
            }
        }

        return items;
    }

    private Service getService(ProductCatalog catalog, Service.ServiceType type) {
        for (Service service : catalog.services()) {
            if (service.version().serviceType().getValue().equals(type)) {
                return service;
            }
        }
        throw new Error("Service of type " + type + " not found");
    }

    private List<ServiceItemType> getServiceItemTypes(ProductCatalog catalog, Service.ServiceType type) {
        List<ServiceItemType> allowedItemTypes = new ArrayList<ServiceItemType>();
        for (ServiceItemType itemType : getServiceItemTypes()) {
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
        List<ProductItem> serviceItems = new ArrayList<ProductItem>();

        BigDecimal price = createUnitMarketRent(unit);
//      Service.Type type = RandomUtil.random(EnumSet.of(Type.residentialUnit, Type.residentialShortTermUnit, Type.commercialUnit));

        serviceItems.add(createBuildingElementServices(catalog, unit, ServiceType.residentialUnit, price));
        serviceItems.add(createBuildingElementServices(catalog, unit, ServiceType.commercialUnit, price));

        return serviceItems;
    }

    public ProductItem createBuildingElementServices(ProductCatalog catalog, BuildingElement buildingElement, Service.ServiceType type, BigDecimal price) {
        Service service = getService(catalog, type);
        List<ServiceItemType> allowedItemTypes = getServiceItemTypes(catalog, type);

        ProductItem item = EntityFactory.create(ProductItem.class);
        ServiceItemType selectedItem = RandomUtil.random(allowedItemTypes);

        item.type().set(selectedItem);
        item.type().name().setValue(selectedItem.getStringView());
        item.type().<ServiceItemType> cast().serviceType().setValue(selectedItem.serviceType().getValue());

        // This value may not be used in all cases and overridden later in generator
        item.price().setValue(price);
        item.description().setValue(type.toString() + " description");
        item.element().set(buildingElement);

        service.version().items().add(item);

        return item;
    }
}
