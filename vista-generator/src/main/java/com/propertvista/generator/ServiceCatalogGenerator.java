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

import com.propertvista.generator.gdo.ServiceItemTypes;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.DepositType;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceCatalog;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;

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

        catalog.services().addAll(createServices(catalog));
        catalog.features().addAll(createFeatures(catalog));
        catalog.concessions().addAll(createConcessions(catalog));
        catalog.includedUtilities().addAll(createIncludedUtilities());

        buildEligibilityMatrix(catalog);
    }

    private void buildEligibilityMatrix(ServiceCatalog catalog) {
        for (Service srv : catalog.services()) {
            for (int i = 0; i < 2; ++i) {
                ServiceFeature srvFeature = EntityFactory.create(ServiceFeature.class);
                srvFeature.feature().set(RandomUtil.random(catalog.features()));
                srv.features().add(srvFeature);
            }

            for (int i = 0; i < 2; ++i) {
                ServiceConcession srvConcession = EntityFactory.create(ServiceConcession.class);
                srvConcession.concession().set(RandomUtil.random(catalog.concessions()));
                srv.concessions().add(srvConcession);
            }
        }
    }

    public List<Service> createServices(ServiceCatalog catalog) {
        List<Service> items = new ArrayList<Service>();
        for (ServiceItemType type : getServiceItemTypes()) {
            items.add(createService(catalog, type.serviceType().getValue()));
        }
        return items;
    }

    public List<Feature> createFeatures(ServiceCatalog catalog) {
        List<Feature> items = new ArrayList<Feature>(3);
        for (int i = 0; i < 3; ++i) {
            items.add(createFeature(catalog, RandomUtil.random(getFeatureItemTypes()).featureType().getValue()));
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

// internals:    
    private Service createService(ServiceCatalog catalog, Service.Type type) {
        Service item = EntityFactory.create(Service.class);
        item.catalog().set(catalog);

        item.type().setValue(type);
        item.name().setValue(RandomUtil.randomLetters(6));
        item.description().setValue("Service description here...");

        item.depositType().setValue(RandomUtil.randomEnum(DepositType.class));

        item.items().add(createServiceItem(type));

        return item;
    }

    private ServiceItem createServiceItem(Service.Type type) {
        ServiceItem item = EntityFactory.create(ServiceItem.class);

        List<ServiceItemType> allowedItemTypes = new ArrayList<ServiceItemType>();
        for (ServiceItemType itemType : getServiceItemTypes()) {
            if (type.equals(itemType.serviceType().getValue())) {
                allowedItemTypes.add(itemType);
            }
        }
        ServiceItemType selectedItem = RandomUtil.random(allowedItemTypes);

        item.type().set(selectedItem);
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

        List<ServiceItem> items = new ArrayList<ServiceItem>(4);
        for (int i = 0; i < 4; ++i) {
            ServiceItem item = EntityFactory.create(ServiceItem.class);

            item.type().set(RandomUtil.random(allowedItemTypes));
            item.type().name().setValue(item.type().getStringView());
            item.type().featureType().setValue(item.type().featureType().getValue());

            item.price().setValue(100d + RandomUtil.randomInt(100));
            item.description().setValue(type.toString() + " description here...");

            items.add(item);
        }

        return items;
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

        concession.description().setValue("Concession description here...");

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
        int maxItems = DataGenerator.randomInt(allowedItemTypes.size());
        for (int i = 0; i < maxItems; ++i) {
            items.add(DataGenerator.random(allowedItemTypes));
        }

        return items;
    }

}
