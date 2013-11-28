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
import java.util.Iterator;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class DefaultProductCatalogFacadeImpl implements DefaultProductCatalogFacade {

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
        assert (building.productCatalog().services().getAttachLevel() != AttachLevel.Attached);
        assert (building.productCatalog().features().getAttachLevel() != AttachLevel.Attached);

        // Save services and features:
        for (Feature feature : building.productCatalog().features()) {
            if (feature.isDefaultCatalogItem().isBooleanTrue()) {
                Persistence.service().persist(feature);
            }
        }

        for (Service service : building.productCatalog().services()) {
            if (service.isDefaultCatalogItem().isBooleanTrue()) {
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
            if (service.isDefaultCatalogItem().isBooleanTrue()) {
                if (ARCode.Type.unitRelatedServices().contains(service.type().getValue())) {
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
            if (service.isDefaultCatalogItem().isBooleanTrue()) {
                if (ARCode.Type.unitRelatedServices().contains(service.type().getValue())) {
                    updateUnitItem(unit, service);
                }
            }
        }
    }

    // internals:

    private void deleteDefaultServices(ProductCatalog catalog) {
        Iterator<Service> serviceIterator = catalog.services().iterator();
        while (serviceIterator.hasNext()) {
            Service service = serviceIterator.next();
            if (service.isDefaultCatalogItem().isBooleanTrue()) {
                serviceIterator.remove();
            }
        }
    }

    private List<Service> createDefaultServices(ProductCatalog catalog) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.in(criteria.proto().type(), ARCode.Type.services());

        // create services:
        List<Service> items = new ArrayList<Service>();
        for (ARCode code : Persistence.service().query(criteria)) {
            items.add(createService(catalog, code));
        }

        return items;
    }

    private Service createService(ProductCatalog catalog, ARCode code) {
        Service item = EntityFactory.create(Service.class);
        item.isDefaultCatalogItem().setValue(true);

        item.catalog().set(catalog);
        item.type().setValue(code.type().getValue());
        item.version().name().setValue(code.name().getValue());

        return item;
    }

    // ----------------------------------------------------------------------------------

    private void deleteDefaultFeatures(ProductCatalog catalog) {
        Iterator<Feature> featureIterator = catalog.features().iterator();
        while (featureIterator.hasNext()) {
            Feature feature = featureIterator.next();
            if (feature.isDefaultCatalogItem().isBooleanTrue()) {
                featureIterator.remove();
            }
        }
    }

    private List<Feature> createDefaultFeatures(ProductCatalog catalog) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.in(criteria.proto().type(), ARCode.Type.features());
        List<ARCode> codes = Persistence.service().query(criteria);

        // accumulate unique type:
        List<ARCode.Type> types = new ArrayList<ARCode.Type>();
        for (ARCode code : codes) {
            if (!types.contains(code.type().getValue())) {
                types.add(code.type().getValue());
            }
        }

        // create corresponding features:
        List<Feature> items = new ArrayList<Feature>(types.size());
        for (ARCode.Type type : types) {
            items.add(createFeature(catalog, type, codes));
        }

        return items;
    }

    private Feature createFeature(ProductCatalog catalog, ARCode.Type type, List<ARCode> codes) {
        Feature item = EntityFactory.create(Feature.class);
        item.isDefaultCatalogItem().setValue(true);

        item.catalog().set(catalog);
        item.type().setValue(type);
        item.version().name().setValue(type.toString());
        item.version().recurring().setValue(!ARCode.Type.nonReccuringFeatures().contains(type));
        item.version().mandatory().setValue(false);

        for (ARCode code : codes) {
            if (type.equals(code.type().getValue())) {
                item.version().items().add(createFeatureItem(code));
            }
        }

        return item;
    }

    private ProductItem createFeatureItem(ARCode code) {
        ProductItem item = EntityFactory.create(ProductItem.class);

        item.code().set(code);
        item.price().setValue(BigDecimal.ZERO);

        return item;
    }

    // ----------------------------------------------------------------------------------

    private void updateEligibilityMatrixes(ProductCatalog catalog) {
        for (Service service : catalog.services()) {
            if (service.isDefaultCatalogItem().isBooleanTrue()) {
                for (Feature feature : catalog.features()) {
                    Persistence.ensureRetrieve(feature, AttachLevel.Attached);
                    if (feature.isDefaultCatalogItem().isBooleanTrue()) {
                        service.version().features().add(feature);
                    }

                    service.version().concessions().clear();
                }
            }
        }
    }

    private ProductItem createUnitItem(AptUnit unit, Service service) {
        ProductItem item = EntityFactory.create(ProductItem.class);
        item.product().set(service.version());

        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().type(), service.type());

        assert (ARCode.Type.unitRelatedServices().contains(service.type().getValue()));

        item.code().set(Persistence.service().retrieve(criteria));
        item.price().setValue(unit.financial()._marketRent().isNull() ? BigDecimal.ZERO : unit.financial()._marketRent().getValue());
        item.element().set(unit);

        return item;
    }

    private void updateUnitItem(AptUnit unit, Service service) {
        EntityQueryCriteria<ProductItem> criteria = EntityQueryCriteria.create(ProductItem.class);
        criteria.eq(criteria.proto().product(), service.version());
        criteria.eq(criteria.proto().element(), unit);

        for (ProductItem item : Persistence.service().query(criteria)) {
            item.price().setValue(unit.financial()._marketRent().isNull() ? BigDecimal.ZERO : unit.financial()._marketRent().getValue());
            Persistence.service().merge(item);
        }
    }
}
