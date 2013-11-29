/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.yardi.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import com.yardi.entity.guestcard40.RentableItemType;
import com.yardi.entity.guestcard40.RentableItems;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.Type;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class YardiProductCatalogProcessor {

    public void processCatalog(Building building, RentableItems rentableItems, Key yardiInterfaceId) {
        Persistence.ensureRetrieve(building, AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().services(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().features(), AttachLevel.Attached);

        building.productCatalog().services().clear();
        building.productCatalog().services().addAll(createServices(building.productCatalog(), rentableItems));

        building.productCatalog().features().clear();
        building.productCatalog().features().addAll(createFeatures(building.productCatalog(), rentableItems));

        updateEligibilityMatrixes(building.productCatalog());
    }

    public void updateUnits(Building building) {
        assert (!building.productCatalog().isValueDetached());
        assert (building.productCatalog().services().getAttachLevel() != AttachLevel.Attached);

        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.in(criteria.proto().building(), building);

        List<AptUnit> units = Persistence.service().query(criteria);

        for (Service service : building.productCatalog().services()) {
            if (!service.isDefaultCatalogItem().isBooleanTrue()) {
                if (ARCode.Type.unitRelatedServices().contains(service.code().type().getValue())) {
                    updateUnitItems(service, units);
                }
            }
        }
    }

    private void updateUnitItems(Service service, List<AptUnit> units) {
        Persistence.ensureRetrieve(service.version().items(), AttachLevel.Attached);

        List<ProductItem> serviceItems = service.version().items();
        Collections.sort(serviceItems, new Comparator<ProductItem>() {
            @Override
            public int compare(ProductItem o1, ProductItem o2) {
                return o1.element().getPrimaryKey().compareTo(o2.element().getPrimaryKey());
            }
        });

        for (AptUnit unit : units) {
            ProductItem item = EntityFactory.create(ProductItem.class);
            item.element().set(unit);

            if (Collections.binarySearch(serviceItems, item, new Comparator<ProductItem>() {
                @Override
                public int compare(ProductItem o1, ProductItem o2) {
                    return o1.element().getPrimaryKey().compareTo(o2.element().getPrimaryKey());
                }
            }) < 0) {
                item.name().setValue(service.code().name().getStringView());
                service.version().items().add(item);
            }
        }

        // update items price:
        for (ProductItem item : service.version().items()) {
            item.price().setValue(service.version().price().getValue());
        }
    }

    public void persistCatalog(Building building) {
        assert (!building.productCatalog().isValueDetached());
        assert (building.productCatalog().services().getAttachLevel() != AttachLevel.Attached);
        assert (building.productCatalog().features().getAttachLevel() != AttachLevel.Attached);

        // Save services and features:
        for (Feature feature : building.productCatalog().features()) {
            if (!feature.isDefaultCatalogItem().isBooleanTrue()) {
                Persistence.service().merge(feature);
            }
        }

        for (Service service : building.productCatalog().services()) {
            if (!service.isDefaultCatalogItem().isBooleanTrue()) {
                Persistence.service().merge(service);
            }
        }
    }

    // ----------------------------------------------------------------------------------

    // internals:

    private class ProductTypeData {

        private final RentableItemType yariItemType;

        private final ARCode arCode;

        private ProductTypeData(RentableItemType yariItemType, ARCode arCode) {
            this.yariItemType = yariItemType;
            this.arCode = arCode;
        }

        public RentableItemType getYariItemType() {
            return yariItemType;
        }

        public ARCode getArCode() {
            return arCode;
        }
    }

    private Collection<ProductTypeData> retrieveProductTypeData(RentableItems rentableItems, EnumSet<Type> forProductTypes) {
        Collection<ProductTypeData> productTypeData = new ArrayList<ProductTypeData>();

        for (RentableItemType itemType : rentableItems.getItemType()) {
            EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
            criteria.in(criteria.proto().type(), forProductTypes);
            criteria.eq(criteria.proto().yardiChargeCodes().$().yardiChargeCode(), itemType.getChargeCode());
            ARCode arCode = Persistence.service().retrieve(criteria);
            if (arCode != null) {
                productTypeData.add(new ProductTypeData(itemType, arCode));
            }
        }

        return productTypeData;
    }

    private List<Service> createServices(ProductCatalog catalog, RentableItems rentableItems) {
        List<Service> items = new ArrayList<Service>();

        for (ProductTypeData typeData : retrieveProductTypeData(rentableItems, ARCode.Type.services())) {
            items.add(ensureService(catalog, typeData));
        }

        return items;
    }

    private Service ensureService(ProductCatalog catalog, ProductTypeData typeData) {
        EntityQueryCriteria<Service> criteria = EntityQueryCriteria.create(Service.class);
        criteria.eq(criteria.proto().catalog(), catalog);
        criteria.eq(criteria.proto().isDefaultCatalogItem(), false);
        criteria.eq(criteria.proto().code().type(), typeData.getArCode().type().getValue());
        criteria.eq(criteria.proto().version().name(), typeData.getYariItemType().getCode());

        Service service = Persistence.service().retrieve(criteria);
        if (service == null) {
            service = EntityFactory.create(Service.class);
            service.isDefaultCatalogItem().setValue(false);
            service.catalog().set(catalog);
            service.code().set(typeData.getArCode());
            service.version().name().setValue(typeData.getYariItemType().getCode());
        } else {
            service = Persistence.secureRetrieveDraft(Service.class, service.getPrimaryKey());
        }

        service.version().description().setValue(typeData.getYariItemType().getDescription());
        service.version().price().setValue(new BigDecimal(typeData.getYariItemType().getRent()));

        return service;
    }

    private List<Feature> createFeatures(ProductCatalog catalog, RentableItems rentableItems) {
        List<Feature> items = new ArrayList<Feature>();

        for (ProductTypeData typeData : retrieveProductTypeData(rentableItems, ARCode.Type.features())) {
            items.add(ensureFeature(catalog, typeData));
        }

        return items;
    }

    private Feature ensureFeature(ProductCatalog catalog, ProductTypeData typeData) {
        EntityQueryCriteria<Feature> criteria = EntityQueryCriteria.create(Feature.class);
        criteria.eq(criteria.proto().catalog(), catalog);
        criteria.eq(criteria.proto().isDefaultCatalogItem(), false);
        criteria.eq(criteria.proto().code(), typeData.getArCode());
        criteria.eq(criteria.proto().version().name(), typeData.getYariItemType().getCode());

        Feature feature = Persistence.service().retrieve(criteria);
        if (feature == null) {
            feature = EntityFactory.create(Feature.class);
            feature.isDefaultCatalogItem().setValue(false);
            feature.catalog().set(catalog);
            feature.code().set(typeData.getArCode());
            feature.version().name().setValue(typeData.getYariItemType().getCode());
            feature.version().recurring().setValue(!ARCode.Type.nonReccuringFeatures().contains(typeData.getArCode().type().getValue()));
            feature.version().mandatory().setValue(false);
            feature.version().items().add(createFeatureItem(typeData.getArCode()));
        } else {
            feature = Persistence.secureRetrieveDraft(Feature.class, feature.getPrimaryKey());
        }

        feature.version().description().setValue(typeData.getYariItemType().getDescription());
        feature.version().price().setValue(new BigDecimal(typeData.getYariItemType().getRent()));

        // update items price:
        for (ProductItem item : feature.version().items()) {
            item.price().setValue(feature.version().price().getValue());
        }

        return feature;
    }

    private ProductItem createFeatureItem(ARCode code) {
        ProductItem item = EntityFactory.create(ProductItem.class);

        item.name().setValue(code.name().getStringView());
        item.price().setValue(BigDecimal.ZERO);

        return item;
    }

    private void updateEligibilityMatrixes(ProductCatalog catalog) {
        for (Service service : catalog.services()) {
            if (!service.isDefaultCatalogItem().isBooleanTrue()) {
                for (Feature feature : catalog.features()) {
                    Persistence.ensureRetrieve(feature, AttachLevel.Attached);
                    if (!feature.isDefaultCatalogItem().isBooleanTrue()) {
                        service.version().features().add(feature);
                    }

                    service.version().concessions().clear();
                }
            }
        }
    }
}
