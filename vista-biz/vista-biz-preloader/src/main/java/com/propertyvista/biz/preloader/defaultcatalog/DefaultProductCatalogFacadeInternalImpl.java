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
package com.propertyvista.biz.preloader.defaultcatalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class DefaultProductCatalogFacadeInternalImpl implements DefaultProductCatalogFacade {

    @Override
    public void createFor(Building building) {
        Persistence.ensureRetrieve(building, AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().services(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().features(), AttachLevel.Attached);

        // create default catalog items:
        building.productCatalog().services().addAll(createDefaultServices(building.productCatalog()));
        building.productCatalog().features().addAll(createDefaultFeatures(building.productCatalog()));

        updateEligibilityMatrixes(building.productCatalog());
    }

    @Override
    public void updateFor(Building buildingId) {
        if (true)
            return; // TODO not implemented currently!..

        Building building = Persistence.service().retrieve(Building.class, buildingId.getPrimaryKey());
        if (building == null) {
            throw new IllegalArgumentException("Building " + buildingId.getPrimaryKey() + " was not found!");
        }

        Persistence.ensureRetrieve(building.productCatalog(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().services(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().features(), AttachLevel.Attached);

        // TODO: review this!

        // remove old default catalog items:
        deleteDefaultServices(building.productCatalog());
        deleteDefaultFeatures(building.productCatalog());

        // create new ones:
        createFor(building);
    }

    @Override
    public void persistFor(Building building) {
        assert (!building.productCatalog().isValueDetached());
        assert (!building.productCatalog().services().isValueDetached());
        assert (!building.productCatalog().features().isValueDetached());

        // Save services and features:
        for (Feature feature : building.productCatalog().features()) {
            if (feature.defaultCatalogItem().isBooleanTrue()) {
                Persistence.service().persist(feature);
            }
        }

        for (Service service : building.productCatalog().services()) {
            if (service.defaultCatalogItem().isBooleanTrue()) {
                Persistence.service().persist(service);
            }
        }
    }

    @Override
    public void addUnit(Building building, AptUnit unit) {
        Persistence.ensureRetrieve(building, AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().services(), AttachLevel.Attached);

        for (Service service : building.productCatalog().services()) {
            if (service.defaultCatalogItem().isBooleanTrue()) {
                if (ARCode.Type.unitRelatedServices().contains(service.code().type().getValue())) {
                    ProductItem item = createUnitItem(unit, service);
                    Persistence.service().persist(item);
                }
            }
        }
    }

    @Override
    public void updateUnit(Building buildingId, AptUnit unit) {
        Building building = Persistence.service().retrieve(Building.class, buildingId.getPrimaryKey());
        if (building == null) {
            throw new IllegalArgumentException("Building " + buildingId.getPrimaryKey() + " was not found!");
        }

        Persistence.ensureRetrieve(building.productCatalog(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().services(), AttachLevel.Attached);

        for (Service service : building.productCatalog().services()) {
            if (service.defaultCatalogItem().isBooleanTrue()) {
                if (ARCode.Type.unitRelatedServices().contains(service.code().type().getValue())) {
                    updateUnitItem(unit, service);
                }
            }
        }
    }

    // internals:

    @Override
    public void fillDefaultDeposits(Product<?> entity) {
        DefaultDepositManager.fillDefaultDeposits(entity);
    }

    private void deleteDefaultServices(ProductCatalog catalog) {
        Iterator<Service> serviceIterator = catalog.services().iterator();
        while (serviceIterator.hasNext()) {
            Service service = serviceIterator.next();
            if (service.defaultCatalogItem().isBooleanTrue()) {
                serviceIterator.remove();
            }
        }
    }

    private List<Service> createDefaultServices(ProductCatalog catalog) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.in(criteria.proto().type(), ARCode.Type.services());

        // create services:
        List<Service> services = new ArrayList<Service>();
        for (ARCode code : Persistence.service().query(criteria)) {
            services.add(createService(catalog, code));
        }

        return services;
    }

    private Service createService(ProductCatalog catalog, ARCode code) {
        Service service = EntityFactory.create(Service.class);
        service.defaultCatalogItem().setValue(true);

        service.catalog().set(catalog);
        service.code().set(code);
        service.version().name().setValue(code.name().getValue());
        service.version().availableOnline().setValue(false);

        fillDefaultDeposits(service);

        return service;
    }

    // ----------------------------------------------------------------------------------

    private void deleteDefaultFeatures(ProductCatalog catalog) {
        Iterator<Feature> featureIterator = catalog.features().iterator();
        while (featureIterator.hasNext()) {
            Feature feature = featureIterator.next();
            if (feature.defaultCatalogItem().isBooleanTrue()) {
                featureIterator.remove();
            }
        }
    }

    private List<Feature> createDefaultFeatures(ProductCatalog catalog) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.in(criteria.proto().type(), ARCode.Type.features());
        List<ARCode> codes = Persistence.service().query(criteria);
        List<Feature> items = new ArrayList<Feature>();
        for (ARCode code : codes) {
            items.add(createFeature(catalog, code));
        }
        return items;
    }

    private Feature createFeature(ProductCatalog catalog, ARCode code) {
        Feature feature = EntityFactory.create(Feature.class);
        feature.defaultCatalogItem().setValue(true);

        feature.catalog().set(catalog);
        feature.code().set(code);
        feature.version().name().setValue(code.name().getValue());
        feature.version().recurring().setValue(!ARCode.Type.nonReccuringFeatures().contains(code));
        feature.version().mandatory().setValue(false);
        feature.version().availableOnline().setValue(false);

        fillDefaultDeposits(feature);

        feature.version().items().add(createFeatureItem(code));

        return feature;
    }

    private ProductItem createFeatureItem(ARCode code) {
        ProductItem item = EntityFactory.create(ProductItem.class);

        item.name().setValue(code.name().getValue());
        item.price().setValue(BigDecimal.ZERO);

        return item;
    }

    // ----------------------------------------------------------------------------------

    private void updateEligibilityMatrixes(ProductCatalog catalog) {
        for (Service service : catalog.services()) {
            if (service.defaultCatalogItem().isBooleanTrue()) {
                for (Feature feature : catalog.features()) {
                    Persistence.ensureRetrieve(feature, AttachLevel.Attached);
                    if (feature.defaultCatalogItem().isBooleanTrue()) {
                        service.version().features().add(feature);
                    }
                }
            }
        }
    }

    private ProductItem createUnitItem(AptUnit unit, Service service) {
        assert (ARCode.Type.unitRelatedServices().contains(service.code().type().getValue()));

        ProductItem item = EntityFactory.create(ProductItem.class);
        item.product().set(service.version());

        item.name().setValue(service.code().name().getValue());
        item.price().setValue(unit.financial()._marketRent().isNull() ? BigDecimal.ZERO : unit.financial()._marketRent().getValue());
        item.element().set(unit);

        return item;
    }

    private void updateUnitItem(AptUnit unit, Service service) {
        assert (ARCode.Type.unitRelatedServices().contains(service.code().type().getValue()));

        EntityQueryCriteria<ProductItem> criteria = EntityQueryCriteria.create(ProductItem.class);
        criteria.eq(criteria.proto().product(), service.version());
        criteria.eq(criteria.proto().element(), unit);

        for (ProductItem item : Persistence.service().query(criteria)) {
            item.price().setValue(unit.financial()._marketRent().isNull() ? BigDecimal.ZERO : unit.financial()._marketRent().getValue());
            Persistence.service().merge(item);
        }
    }
}
