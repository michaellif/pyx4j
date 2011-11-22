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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.propertvista.generator.gdo.ServiceItemTypes;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.DepositType;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.Service.Type;
import com.propertyvista.domain.financial.offering.ServiceCatalog;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class ServiceCatalogGenerator {

    private final ServiceItemTypes serviceItemTypes;

    public ServiceCatalogGenerator(ServiceItemTypes serviceItemTypes) {
        this.serviceItemTypes = serviceItemTypes;
    }

    private List<ServiceItemType> getServiceItemTypes() {
        return this.serviceItemTypes.serviceItemTypes;
    }

    private List<ServiceItemType> getFeatureItemTypes() {
        return this.serviceItemTypes.featureItemTypes;
    }

    public void createServiceCatalog(ServiceCatalog catalog) {
        DataGenerator.setRandomSeed(RandomUtil.randomInt(1024));

        catalog.services().addAll(createServices(catalog));
        catalog.features().addAll(createFeatures(catalog));
        catalog.concessions().addAll(createConcessions(catalog));
        catalog.includedUtilities().addAll(createIncludedUtilities());
        catalog.externalUtilities().addAll(createExcludedUtilities(catalog.includedUtilities()));

        buildEligibilityMatrix(catalog);
    }

    public List<Service> createServices(ServiceCatalog catalog) {
        List<Service> items = new ArrayList<Service>(Service.Type.values().length);
        for (Service.Type type : EnumSet.allOf(Service.Type.class)) {
            items.add(createService(catalog, type));
        }
        return items;
    }

    public List<Feature> createFeatures(ServiceCatalog catalog) {
        List<Feature> items = new ArrayList<Feature>(Feature.Type.values().length);
        for (Feature.Type type : EnumSet.allOf(Feature.Type.class)) {
            items.add(createFeature(catalog, type));
        }
        return items;
    }

    public List<Concession> createConcessions(ServiceCatalog catalog) {
        List<Concession> items = new ArrayList<Concession>(3);
        for (int i = 0; i < 3; ++i) {
            items.add(createConcession(catalog));
        }
        return items;
    }

    public void buildEligibilityMatrix(ServiceCatalog catalog) {
        for (Service srv : catalog.services()) {
            if (srv.type().getValue().equals(Service.Type.residentialUnit) || srv.type().getValue().equals(Service.Type.residentialShortTermUnit)
                    || srv.type().getValue().equals(Service.Type.commercialUnit)) {

                for (int i = 0; i < catalog.features().size(); ++i) {
                    ServiceFeature srvFeature = EntityFactory.create(ServiceFeature.class);
                    srvFeature.feature().set(catalog.features().get(i));
                    srv.features().add(srvFeature);
                }
            }

            int count = Math.min(2, catalog.concessions().size());
            for (int i = 0; i < count; ++i) {
                ServiceConcession srvConcession = EntityFactory.create(ServiceConcession.class);
                srvConcession.concession().set(RandomUtil.random(catalog.concessions(), "concessions", count));
                srv.concessions().add(srvConcession);
            }
        }
    }

// internals:    
    private Service createService(ServiceCatalog catalog, Service.Type type) {
        Service item = EntityFactory.create(Service.class);
        item.catalog().set(catalog);

        item.type().setValue(type);
        item.name().setValue(RandomUtil.randomLetters(6));
        item.description().setValue("Service description here...");

        item.depositType().setValue(RandomUtil.randomEnum(DepositType.class));

        item.items().addAll(createServiceItems(type));

        return item;
    }

    private List<ServiceItem> createServiceItems(Service.Type type) {

        List<ServiceItemType> allowedItemTypes = new ArrayList<ServiceItemType>();
        for (ServiceItemType itemType : getServiceItemTypes()) {
            if (type.equals(itemType.serviceType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }

        int count = 0;
        switch (type) {
        case residentialUnit:
        case residentialShortTermUnit:
        case commercialUnit:
            return new ArrayList<ServiceItem>();
        case roof:
        case garage:
        case storage:
        case sundry:
            count = 1;
            break;
        }

        List<ServiceItem> items = new ArrayList<ServiceItem>(count);
        if (!allowedItemTypes.isEmpty()) {
            for (int i = 0; i < count; ++i) {
                ServiceItem item = EntityFactory.create(ServiceItem.class);
                ServiceItemType selectedItem = RandomUtil.random(allowedItemTypes);

                item.type().set(selectedItem);
                item.type().name().setValue(selectedItem.getStringView());
                item.type().serviceType().setValue(selectedItem.serviceType().getValue());

                item.price().setValue(500d + RandomUtil.randomInt(500));
                item.description().setValue(type.toString() + " description here...");

                items.add(item);
            }
        }

        return items;
    }

    private Feature createFeature(ServiceCatalog catalog, Feature.Type type) {
        Feature item = EntityFactory.create(Feature.class);
        item.catalog().set(catalog);

        item.type().setValue(type);
        item.name().setValue(RandomUtil.randomLetters(6));
        item.description().setValue("Feature description here...");

        item.priceType().setValue(RandomUtil.randomEnum(Feature.PriceType.class));
        item.depositType().setValue(RandomUtil.randomEnum(DepositType.class));

        item.isRecurring().setValue(RandomUtil.randomBoolean());
        item.isMandatory().setValue(RandomUtil.randomBoolean());

        item.items().addAll(createFeatureItems(type));

        return item;
    }

    private List<ServiceItem> createFeatureItems(Feature.Type type) {
        List<ServiceItemType> allowedItemTypes = new ArrayList<ServiceItemType>();
        for (ServiceItemType itemType : getFeatureItemTypes()) {
            if (type.equals(itemType.featureType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }

        int count = Math.min(3, allowedItemTypes.size());
        List<ServiceItem> items = new ArrayList<ServiceItem>(count);
        if (!allowedItemTypes.isEmpty()) {
            for (int i = 0; i < count; ++i) {
                ServiceItem item = EntityFactory.create(ServiceItem.class);

                item.type().set(RandomUtil.random(allowedItemTypes));
                item.type().name().setValue(item.type().getStringView());
                item.type().featureType().setValue(item.type().featureType().getValue());

                item.price().setValue(100d + RandomUtil.randomInt(100));
                item.description().setValue(type.toString() + " description here...");

                items.add(item);
            }
        }

        return items;
    }

    private Concession createConcession(ServiceCatalog catalog) {
        Concession concession = EntityFactory.create(Concession.class);
        concession.catalog().set(catalog);

        concession.type().setValue(RandomUtil.random(Concession.Type.values(), "Concession.Type", Concession.Type.values().length));

        if (concession.type().getValue() == Concession.Type.percentageOff) {
            concession.value().setValue(10d + RandomUtil.randomInt(90));
        } else if (concession.type().getValue() == Concession.Type.monetaryOff) {
            concession.value().setValue(50d + RandomUtil.randomInt(50));
        } else if (concession.type().getValue() == Concession.Type.promotionalItem) {
            concession.value().setValue(100d + RandomUtil.randomInt(100));
        } else if (concession.type().getValue() == Concession.Type.free) {
            concession.value().setValue(200d + RandomUtil.randomInt(100));
        }

        concession.term().setValue(RandomUtil.random(Concession.Term.values()));
        concession.condition().setValue(RandomUtil.random(Concession.Condition.values()));
        concession.status().setValue(RandomUtil.random(Concession.Status.values()));
        if (concession.status().getValue() == Concession.Status.approved) {
            concession.approvedBy().setValue("Gorge W. Bush Jr.");
        }

        concession.effectiveDate().setValue(DataGenerator.randomDate(2));
        concession.expirationDate().setValue(DataGenerator.randomDate(4));

        concession.description().setValue(SimpleMessageFormat.format("Concession description here..."));

        return concession;
    }

    public List<ServiceItemType> createIncludedUtilities() {
        List<ServiceItemType> allowedItemTypes = new ArrayList<ServiceItemType>();
        for (ServiceItemType itemType : getFeatureItemTypes()) {
            if (Feature.Type.utility.equals(itemType.featureType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }

        List<ServiceItemType> items = new ArrayList<ServiceItemType>();
        if (!allowedItemTypes.isEmpty()) {
            int maxItems = Math.min(DataGenerator.randomInt(allowedItemTypes.size()) + 1, allowedItemTypes.size());
            for (int i = 0; i < maxItems; ++i) {
                items.add(RandomUtil.random(allowedItemTypes, "IncludedUtilities", maxItems));
            }
        }

        return items;
    }

    public List<ServiceItemType> createExcludedUtilities(List<ServiceItemType> includedOnes) {
        List<ServiceItemType> allowedItemTypes = new ArrayList<ServiceItemType>();
        for (ServiceItemType itemType : getFeatureItemTypes()) {
            if (Feature.Type.utility.equals(itemType.featureType().getValue()) && !includedOnes.contains(itemType)) {
                allowedItemTypes.add(itemType);
            }
        }

        List<ServiceItemType> items = new ArrayList<ServiceItemType>();
        if (!allowedItemTypes.isEmpty()) {
            int maxItems = Math.min(DataGenerator.randomInt(allowedItemTypes.size()) + 1, allowedItemTypes.size());
            for (int i = 0; i < maxItems; ++i) {
                items.add(RandomUtil.random(allowedItemTypes, "ExcludedUtilities", maxItems));
            }
        }

        return items;
    }

    private Service getService(ServiceCatalog catalog, Service.Type type) {
        for (Service service : catalog.services()) {
            if (service.type().getValue().equals(type)) {
                return service;
            }
        }
        throw new Error("Service of type " + type + " not found");
    }

    private List<ServiceItemType> getServiceItemTypes(ServiceCatalog catalog, Service.Type type) {
        List<ServiceItemType> allowedItemTypes = new ArrayList<ServiceItemType>();
        for (ServiceItemType itemType : getServiceItemTypes()) {
            if (type.equals(itemType.serviceType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }
        return allowedItemTypes;
    }

    public static double createUnitMarketRent(AptUnit unit) {
        double base = 900.;
        base += unit.info()._bedrooms().getValue() * 150.0;
        base += unit.info()._bathrooms().getValue() * 50.0;
        return base + RandomUtil.randomInt(200);
    }

    public List<ServiceItem> createAptUnitServices(ServiceCatalog catalog, AptUnit unit) {
        Service.Type type = RandomUtil.random(EnumSet.of(Type.residentialUnit, Type.residentialShortTermUnit, Type.commercialUnit));
        List<ServiceItem> serviceItems = createBuildingElementServices(catalog, unit, type);
        serviceItems.get(0).price().setValue(createUnitMarketRent(unit));
        unit.financial()._marketRent().set(serviceItems.get(0).price());
        return serviceItems;

    }

    public List<ServiceItem> createBuildingElementServices(ServiceCatalog catalog, BuildingElement buildingElement, Service.Type type) {
        Service service = getService(catalog, type);
        List<ServiceItemType> allowedItemTypes = getServiceItemTypes(catalog, type);

        List<ServiceItem> items = new ArrayList<ServiceItem>();

        ServiceItem item = EntityFactory.create(ServiceItem.class);
        ServiceItemType selectedItem = RandomUtil.random(allowedItemTypes);

        item.type().set(selectedItem);
        item.type().name().setValue(selectedItem.getStringView());
        item.type().serviceType().setValue(selectedItem.serviceType().getValue());

        // This value may not be used in all cases nad overriden later in generator
        item.price().setValue(500d + RandomUtil.randomInt(500));
        item.description().setValue(type.toString() + " description here...");
        item.element().set(buildingElement);

        service.items().add(item);
        items.add(item);

        return items;
    }
}
