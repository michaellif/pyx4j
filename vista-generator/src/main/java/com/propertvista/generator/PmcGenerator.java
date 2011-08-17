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
import java.util.List;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.DepositType;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceCatalog;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class PmcGenerator {

    List<ServiceItemType> serviceItemTypes = new ArrayList<ServiceItemType>();

    List<ServiceItemType> featureItemTypes = new ArrayList<ServiceItemType>();

    public PmcGenerator() {

        // preload types:
        serviceItemTypes.add(createChargeItemType("Regular Residential Unit", Service.Type.residentialUnit));
        serviceItemTypes.add(createChargeItemType("Regular Commercial Unit", Service.Type.commercialUnit));
        serviceItemTypes.add(createChargeItemType("Regular Short Term Residential Unit", Service.Type.residentialShortTermUnit));
        serviceItemTypes.add(createChargeItemType("Roof Spot", Service.Type.roof));
        serviceItemTypes.add(createChargeItemType("Billboard", Service.Type.sundry));

        featureItemTypes.add(createChargeItemType("Regular Parking", Feature.Type.parking));
        featureItemTypes.add(createChargeItemType("Wide Parking", Feature.Type.parking));
        featureItemTypes.add(createChargeItemType("Narrow Parking", Feature.Type.parking));
        featureItemTypes.add(createChargeItemType("Disabled Parking", Feature.Type.parking));
        featureItemTypes.add(createChargeItemType("Cat", Feature.Type.pet));
        featureItemTypes.add(createChargeItemType("Dog", Feature.Type.pet));
        featureItemTypes.add(createChargeItemType("Small Locker", Feature.Type.locker));
        featureItemTypes.add(createChargeItemType("Medium Locker", Feature.Type.locker));
        featureItemTypes.add(createChargeItemType("Large Locker", Feature.Type.locker));
        featureItemTypes.add(createChargeItemType("Fitness", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Pool", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Furnished", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Key", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Access Card", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Cable", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Water", Feature.Type.utility));
        featureItemTypes.add(createChargeItemType("Gas", Feature.Type.utility));
        featureItemTypes.add(createChargeItemType("Hydro", Feature.Type.utility));

    }

    public List<ServiceItemType> getServiceItemTypes() {
        return serviceItemTypes;
    }

    public List<ServiceItemType> getFeatureItemTypes() {
        return featureItemTypes;
    }

    public ServiceCatalog createServiceCatalog() {
        ServiceCatalog catalog = EntityFactory.create(ServiceCatalog.class);
        catalog.name().setValue(RandomUtil.randomLetters(4));
        return catalog;
    }

    public List<Service> createServices(ServiceCatalog catalog) {
        List<Service> items = new ArrayList<Service>(4);
        for (ServiceItemType item : serviceItemTypes) {
            items.add(createService(catalog, item.serviceType().getValue()));
        }
        return items;
    }

    public List<Feature> createFeatures(ServiceCatalog catalog) {
        List<Feature> items = new ArrayList<Feature>(4);
        for (ServiceItemType item : featureItemTypes) {
            items.add(createFeature(catalog, item.featureType().getValue()));
        }
        return items;
    }

    public List<Concession> createConcessions(ServiceCatalog catalog) {
        List<Concession> items = new ArrayList<Concession>(4);
        for (int i = 0; i < 4; ++i) {
            items.add(createConcession(catalog));
        }
        return items;
    }

// internals:    
    private Service createService(ServiceCatalog catalog, Service.Type type) {
        Service item = EntityFactory.create(Service.class);
        item.catalog().set(catalog);

        item.type().setValue(type);
        item.name().setValue(RandomUtil.randomLetters(6));
        item.description().setValue(RandomUtil.randomLetters(25).toLowerCase());

        item.depositType().setValue(RandomUtil.randomEnum(DepositType.class));

        item.items().add(createServiceItem(type));

        return item;
    }

    private ServiceItem createServiceItem(Service.Type type) {
        ServiceItem item = EntityFactory.create(ServiceItem.class);

        item.type().set(EntityFactory.create(ServiceItemType.class));
        List<ServiceItemType> allowedItemTypes = new ArrayList<ServiceItemType>();
        for (ServiceItemType itemType : serviceItemTypes) {
            if (type.equals(itemType.serviceType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }
        ServiceItemType selectedItem = RandomUtil.random(allowedItemTypes);
        item.type().name().setValue(selectedItem.getStringView());
        item.type().serviceType().setValue(selectedItem.serviceType().getValue());

        item.price().setValue(500d + RandomUtil.randomInt(500));
        item.description().setValue(type.toString() + " description here...");

        return item;
    }

    private Feature createFeature(ServiceCatalog catalog, Feature.Type type) {
        Feature item = EntityFactory.create(Feature.class);
        item.catalog().set(catalog);

        item.type().setValue(type);
        item.name().setValue(RandomUtil.randomLetters(6));
        item.description().setValue(RandomUtil.randomLetters(25).toLowerCase());

        item.priceType().setValue(RandomUtil.randomEnum(Feature.PriceType.class));
        item.depositType().setValue(RandomUtil.randomEnum(DepositType.class));

        item.isRecurring().setValue(RandomUtil.randomBoolean());
        item.isMandatory().setValue(RandomUtil.randomBoolean());

        for (int i = 0; i < 3; ++i) {
            item.items().add(createFeatureItem(type));
        }

        return item;
    }

    private ServiceItem createFeatureItem(Feature.Type type) {
        ServiceItem item = EntityFactory.create(ServiceItem.class);

        item.type().set(EntityFactory.create(ServiceItemType.class));

        List<ServiceItemType> allowedItemTypes = new ArrayList<ServiceItemType>();
        for (ServiceItemType itemType : featureItemTypes) {
            if (type.equals(itemType.featureType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }
        ServiceItemType selectedItem = RandomUtil.random(allowedItemTypes);
        item.type().name().setValue(selectedItem.getStringView());
        item.type().featureType().setValue(selectedItem.featureType().getValue());

        item.price().setValue(100d + RandomUtil.randomInt(100));
        item.description().setValue(type.toString() + " description here...");

        return item;
    }

    private Concession createConcession(ServiceCatalog catalog) {
        Concession concession = EntityFactory.create(Concession.class);
        concession.catalog().set(catalog);

        concession.type().setValue(RandomUtil.random(Concession.Type.values()));

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
            concession.approvedBy().setValue("Geoge W. Bush Jr.");
        }

        concession.effectiveDate().setValue(DataGenerator.randomDate(2));
        concession.expirationDate().setValue(DataGenerator.randomDate(4));

        concession.description().setValue(RandomUtil.randomLetters(25).toLowerCase());

        return concession;
    }

    private ServiceItemType createChargeItemType(String name, Service.Type serviceType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.serviceType().setValue(serviceType);
        return type;
    }

    private ServiceItemType createChargeItemType(String name, Feature.Type featureType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.featureType().setValue(featureType);
        return type;
    }

}
