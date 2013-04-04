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
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.generator.util.RandomUtil;

public class ProductCatalogGenerator {

    private static final I18n i18n = I18n.get(ProductCatalogGenerator.class);

    private static final String ConcessionId = "ConcessionId";

    private static final String ConcessionTypeId = "Concession.Type.Id";

    private static final String IncludedUtilitiesId = "IncludedUtilitiesId";

    private static final String ExcludedUtilitiesId = "ExcludedUtilitiesId";

    public ProductCatalogGenerator(long seed) {
        if (seed != 0) {
            DataGenerator.setRandomSeed(seed);
        }
    }

    public void generateProductCatalog(ProductCatalog catalog) {

        DataGenerator.cleanRandomDuplicates(ConcessionId);
        DataGenerator.cleanRandomDuplicates(ConcessionTypeId);
        DataGenerator.cleanRandomDuplicates(IncludedUtilitiesId);
        DataGenerator.cleanRandomDuplicates(ExcludedUtilitiesId);

        catalog.services().addAll(createServices(catalog));
        catalog.features().addAll(createFeatures(catalog));
        catalog.concessions().addAll(createConcessions(catalog));

        buildEligibilityMatrix(catalog);
    }

    public List<Service> createServices(ProductCatalog catalog) {
        List<Service> items = new ArrayList<Service>(ARCode.Type.services().size());
        for (ARCode.Type type : ARCode.Type.services()) {
            items.add(createService(catalog, type));
        }
        return items;
    }

    public List<Feature> createFeatures(ProductCatalog catalog) {
        List<Feature> items = new ArrayList<Feature>(ARCode.Type.features().size());
        for (ARCode.Type type : ARCode.Type.features()) {
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
        for (Service service : catalog.services()) {
            if (ARCode.Type.services().contains(service.type().getValue()) && !service.isDefaultCatalogItem().isBooleanTrue()) {
                for (Feature feature : catalog.features()) {
                    if (!feature.isDefaultCatalogItem().isBooleanTrue()) {
                        service.version().features().add(feature);
                    }
                }
            }

            int count = Math.min(2, catalog.concessions().size());
            for (int i = 0; i < count; ++i) {
                service.version().concessions().add(RandomUtil.random(catalog.concessions(), ConcessionId, count));
            }
        }
    }

// internals:
    private List<ARCode> getARCodes(ARCode.Type type) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().type(), type);
        return Persistence.service().query(criteria);
    }

    private Service createService(ProductCatalog catalog, ARCode.Type type) {
        Service item = EntityFactory.create(Service.class);
        item.catalog().set(catalog);

        item.type().setValue(type);
        item.version().name().setValue(RandomUtil.randomLetters(6));
        item.version().description().setValue("Service description");

        item.version().items().addAll(createServiceItems(type));
        return item;
    }

    private List<ProductItem> createServiceItems(ARCode.Type type) {
        if (ARCode.Type.unitRelatedServices().contains(type)) {
            return new ArrayList<ProductItem>(); // no items for unit services - will be added by createBuildingElementServices latter!
        }

        // create some default item(s): 
        List<ARCode> allowedItemTypes = new ArrayList<ARCode>();
        for (ARCode item : getARCodes(type)) {
            if (type.equals(item.type().getValue())) {
                allowedItemTypes.add(item);
            }
        }

        int count = 1;
        List<ProductItem> items = new ArrayList<ProductItem>(count);
        if (!allowedItemTypes.isEmpty()) {
            for (int i = 0; i < count; ++i) {
                ProductItem item = EntityFactory.create(ProductItem.class);
                ARCode selectedItem = RandomUtil.random(allowedItemTypes);

                item.code().set(selectedItem);
                item.price().setValue(new BigDecimal(500 + RandomUtil.randomInt(500)));
                item.description().setValue(item.code().getStringView() + " description");

                items.add(item);
            }
        }

        return items;
    }

    private Feature createFeature(ProductCatalog catalog, ARCode.Type type) {
        Feature item = EntityFactory.create(Feature.class);
        item.catalog().set(catalog);

        item.type().setValue(type);
        item.version().name().setValue(RandomUtil.randomLetters(6));
        item.version().description().setValue("Feature description");

        item.version().recurring().setValue(RandomUtil.randomBoolean() && !ARCode.Type.nonReccuringFeatures().contains(type));
        item.version().mandatory().setValue(RandomUtil.randomBoolean() && !ARCode.Type.nonMandatoryFeatures().contains(type));

        item.version().items().addAll(createFeatureItems(type));
        if (item.version().mandatory().isBooleanTrue()) {
            // if feature is mandatory - the default item should be set!
            ProductItem fi = RandomUtil.random(item.version().items());
            fi.isDefault().setValue(Boolean.TRUE);
        }
        return item;
    }

    private List<ProductItem> createFeatureItems(ARCode.Type type) {
        List<ARCode> allowedItemTypes = new ArrayList<ARCode>();
        for (ARCode item : getARCodes(type)) {
            if (type.equals(item.type().getValue())) {
                allowedItemTypes.add(item);
            }
        }

        int count = Math.min(3, allowedItemTypes.size());
        List<ProductItem> items = new ArrayList<ProductItem>(count);
        if (!allowedItemTypes.isEmpty()) {
            for (int i = 0; i < count; ++i) {
                ProductItem item = EntityFactory.create(ProductItem.class);

                item.code().set(RandomUtil.random(allowedItemTypes));
                item.description().setValue(item.code().getStringView() + " description");

                switch (type) {
                case Parking:
                    item.price().setValue(new BigDecimal(5 + RandomUtil.randomInt(50)));
                    break;
                case Locker:
                    item.price().setValue(new BigDecimal(5 + RandomUtil.randomInt(10)));
                    break;
                case Pet:
                    item.price().setValue(new BigDecimal(20 + RandomUtil.randomInt(20)));
                    break;
                case AddOn:
                    item.price().setValue(new BigDecimal(30 + RandomUtil.randomInt(50)));
                    break;
                case OneTime:
                    item.price().setValue(new BigDecimal(20 + RandomUtil.randomInt(20)));
                    break;
                case Utility:
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
        item.version().type().setValue(RandomUtil.random(Concession.Type.values(), ConcessionTypeId, Concession.Type.values().length));

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

        item.version().effectiveDate().setValue(DataGenerator.randomDateInLastYearMonthShifted(2));
        item.version().expirationDate().setValue(DataGenerator.randomDateInLastYearMonthShifted(4));
        return item;
    }

    public List<ARCode> createIncludedUtilities() {
        List<ARCode> allowedItemTypes = new ArrayList<ARCode>();
        allowedItemTypes.addAll(getARCodes(ARCode.Type.Utility));

        List<ARCode> items = new ArrayList<ARCode>();
        if (!allowedItemTypes.isEmpty()) {
            int maxItems = Math.min(DataGenerator.randomInt(allowedItemTypes.size()) + 1, allowedItemTypes.size());
            for (int i = 0; i < maxItems; ++i) {
                items.add(RandomUtil.random(allowedItemTypes, IncludedUtilitiesId, maxItems));
            }
        }

        return items;
    }

    public List<ARCode> createExcludedUtilities(List<ARCode> includedOnes) {
        List<ARCode> allowedItemTypes = new ArrayList<ARCode>();
        for (ARCode itemType : getARCodes(ARCode.Type.Utility)) {
            if (!includedOnes.contains(itemType)) {
                allowedItemTypes.add(itemType);
            }
        }

        List<ARCode> items = new ArrayList<ARCode>();
        if (!allowedItemTypes.isEmpty()) {
            int maxItems = Math.min(DataGenerator.randomInt(allowedItemTypes.size()) + 1, allowedItemTypes.size());
            for (int i = 0; i < maxItems; ++i) {
                items.add(RandomUtil.random(allowedItemTypes, ExcludedUtilitiesId, maxItems));
            }
        }

        return items;
    }

    private Service getService(ProductCatalog catalog, ARCode.Type type) {
        for (Service service : catalog.services()) {
            if (service.type().getValue().equals(type) && !service.isDefaultCatalogItem().isBooleanTrue()) {
                return service;
            }
        }
        throw new Error("Service of type " + type + " not found");
    }

    private static BigDecimal createUnitMarketRent(AptUnit unit) {
        BigDecimal base = new BigDecimal(900);
        base = base.add(new BigDecimal(unit.info()._bedrooms().getValue() * 150));
        base = base.add(new BigDecimal(unit.info()._bathrooms().getValue() * 50));
        return base.add(new BigDecimal(RandomUtil.randomInt(200)));
    }

    public List<ProductItem> createAptUnitServices(ProductCatalog catalog, AptUnit unit) {
        List<ProductItem> serviceItems = new ArrayList<ProductItem>();

        for (ARCode.Type type : ARCode.Type.unitRelatedServices()) {
            serviceItems.add(createBuildingElementServices(catalog, unit, type, createUnitMarketRent(unit)));
        }

        return serviceItems;
    }

    public ProductItem createBuildingElementServices(ProductCatalog catalog, BuildingElement buildingElement, ARCode.Type type, BigDecimal price) {
        Service service = getService(catalog, type);

        ProductItem item = EntityFactory.create(ProductItem.class);
        ARCode selectedItem = RandomUtil.random(getARCodes(type));

        item.code().set(selectedItem);
        // This value may not be used in all cases and overridden later in generator
        item.price().setValue(price);
        item.description().setValue(item.code().getStringView() + " description");
        item.element().set(buildingElement);

        service.version().items().add(item);

        return item;
    }
}
