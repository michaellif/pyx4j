/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2012-12-03
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.biz.preloader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class GenericProductCatalogFacadeImpl implements GenericProductCatalogFacade {

    @Override
    public void createFor(Building building) {

        building.productCatalog().services().clear();
        building.productCatalog().services().addAll(createDefaultServices(building.productCatalog()));

        building.productCatalog().features().clear();
        building.productCatalog().features().addAll(createDefaultFeatures(building.productCatalog()));

        building.productCatalog().concessions().clear();
        building.productCatalog().concessions().addAll(createDefaultConcessions(building.productCatalog()));

        updateEligibilityMatrixes(building.productCatalog());
    }

    @Override
    public void updateFor(Building building) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addUnit(Building building, AptUnit unit) {
        for (Service service : building.productCatalog().services()) {
            switch (service.serviceType().getValue()) {
            case commercialUnit:
            case residentialUnit:
                service.version().items().add(createUnitItem(unit, service));
                break;
            default:
                break;
            }
        }
    }

    // internals:

    private List<Service> createDefaultServices(ProductCatalog catalog) {
        EntityQueryCriteria<ServiceItemType> criteria = EntityQueryCriteria.create(ServiceItemType.class);
        List<ServiceItemType> itemTypes = Persistence.service().query(criteria);

        // create services of unique types:
        List<Service> items = new ArrayList<Service>();
        List<Service.ServiceType> types = new ArrayList<Service.ServiceType>();
        for (ServiceItemType itemType : itemTypes) {
            if (!types.contains(itemType.serviceType().getValue())) {
                types.add(itemType.serviceType().getValue());
                items.add(createService(catalog, itemType));
            }
        }

        return items;
    }

    private Service createService(ProductCatalog catalog, ServiceItemType type) {
        Service item = EntityFactory.create(Service.class);
        item.catalog().set(catalog);

        item.serviceType().setValue(type.serviceType().getValue());
        item.version().name().setValue(type.name().getValue());

        return item;
    }

    // ----------------------------------------------------------------------------------

    private List<Feature> createDefaultFeatures(ProductCatalog catalog) {
        EntityQueryCriteria<FeatureItemType> criteria = EntityQueryCriteria.create(FeatureItemType.class);
        List<FeatureItemType> itemTypes = Persistence.service().query(criteria);

        // accumulate unique type:
        List<Feature.Type> types = new ArrayList<Feature.Type>();
        for (FeatureItemType itemType : itemTypes) {
            if (!types.contains(itemType.featureType().getValue())) {
                types.add(itemType.featureType().getValue());
            }
        }

        // create corresponding features:
        List<Feature> items = new ArrayList<Feature>(types.size());
        for (Feature.Type type : types) {
            items.add(createFeature(catalog, type, itemTypes));
        }

        return items;
    }

    private Feature createFeature(ProductCatalog catalog, Feature.Type type, List<FeatureItemType> itemTypes) {
        Feature item = EntityFactory.create(Feature.class);
        item.catalog().set(catalog);

        item.featureType().setValue(type);
        item.version().name().setValue(type.toString());

        item.version().recurring().setValue(true);
        item.version().mandatory().setValue(false);

        for (FeatureItemType itemType : itemTypes) {
            if (type.equals(itemType.featureType().getValue())) {
                item.version().items().add(createFeatureItem(itemType));
            }
        }

        return item;
    }

    private ProductItem createFeatureItem(FeatureItemType itemType) {
        ProductItem item = EntityFactory.create(ProductItem.class);

        item.type().set(itemType);
        item.price().setValue(BigDecimal.ZERO);

        return item;
    }

    // ----------------------------------------------------------------------------------

    private Collection<? extends Concession> createDefaultConcessions(ProductCatalog catalog) {
        // TODO Auto-generated method stub
        return new ArrayList<Concession>(0);
    }

    // ----------------------------------------------------------------------------------

    private void updateEligibilityMatrixes(ProductCatalog catalog) {
        for (Service service : catalog.services()) {
            service.version().features().addAll(catalog.features());
            service.version().concessions().addAll(catalog.concessions());
        }
    }

    private ProductItem createUnitItem(AptUnit unit, Service service) {
        ProductItem item = EntityFactory.create(ProductItem.class);

        EntityQueryCriteria<ServiceItemType> criteria = EntityQueryCriteria.create(ServiceItemType.class);
        criteria.eq(criteria.proto().serviceType(), service.serviceType());

        item.type().set(Persistence.service().retrieve(criteria));
        item.price().setValue(BigDecimal.ZERO);
        item.element().set(unit);

        return item;
    }
}
