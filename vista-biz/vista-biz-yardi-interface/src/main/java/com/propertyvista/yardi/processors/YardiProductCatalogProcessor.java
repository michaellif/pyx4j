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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

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

        updateServices(building.productCatalog(), rentableItems);
        updateFeatures(building.productCatalog(), rentableItems);

        updateEligibilityMatrixes(building.productCatalog());
    }

    public void updateUnits(Building building) {
        assert (!building.productCatalog().isValueDetached());
        assert (!building.productCatalog().services().isValueDetached());

        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.in(criteria.proto().building(), building);

        List<AptUnit> units = Persistence.service().query(criteria);

        for (Service service : building.productCatalog().services()) {
            if (!service.defaultCatalogItem().isBooleanTrue()) {
                if (ARCode.Type.unitRelatedServices().contains(service.code().type().getValue())) {
                    updateUnitItems(service, units);
                }
            }
        }
    }

    public void persistCatalog(Building building) {
        assert (!building.productCatalog().isValueDetached());
        assert (!building.productCatalog().services().isValueDetached());
        assert (!building.productCatalog().features().isValueDetached());

        // Save services and features:
        for (Feature feature : building.productCatalog().features()) {
            if (!feature.defaultCatalogItem().isBooleanTrue()) {
                Persistence.service().merge(feature);
            }
        }

        for (Service service : building.productCatalog().services()) {
            if (!service.defaultCatalogItem().isBooleanTrue()) {
                Persistence.service().merge(service);
            }
        }
    }

    // ----------------------------------------------------------------------------------
    // internals:

    public static class YardiRentableItemTypeData {

        private final RentableItemType itemType;

        private final ARCode arCode;

        public YardiRentableItemTypeData(RentableItemType itemType, ARCode arCode) {
            this.itemType = itemType;
            this.arCode = arCode;
        }

        public RentableItemType getItemType() {
            return itemType;
        }

        public ARCode getArCode() {
            return arCode;
        }
    }

    private Collection<YardiRentableItemTypeData> retrieveYardiRentableItemTypeData(RentableItems rentableItems, EnumSet<Type> forProductTypes) {
        Collection<YardiRentableItemTypeData> productTypeData = new ArrayList<YardiRentableItemTypeData>();

        for (RentableItemType itemType : rentableItems.getItemType()) {
            EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
            criteria.in(criteria.proto().type(), forProductTypes);
            criteria.eq(criteria.proto().yardiChargeCodes().$().yardiChargeCode(), itemType.getChargeCode());
            ARCode arCode = Persistence.service().retrieve(criteria);
            if (arCode != null) {
                productTypeData.add(new YardiRentableItemTypeData(itemType, arCode));
            }
        }

        return productTypeData;
    }

    // ----------------------------------------------------------------------------------

    private void updateServices(ProductCatalog catalog, RentableItems rentableItems) {
        deleteServices(catalog);
        catalog.services().clear();
        for (YardiRentableItemTypeData typeData : retrieveYardiRentableItemTypeData(rentableItems, ARCode.Type.services())) {
            catalog.services().add(ensureService(catalog, typeData));
        }
    }

    private void deleteServices(ProductCatalog catalog) {
        for (Service service : catalog.services()) {
            if (!service.defaultCatalogItem().isBooleanTrue() && service.expiredFrom().isNull()) {
                service.expiredFrom().setValue(new LogicalDate(SystemDateManager.getDate()));
                Persistence.service().merge(service);
            }
        }
    }

    private Service ensureService(ProductCatalog catalog, YardiRentableItemTypeData typeData) {
        EntityQueryCriteria<Service> criteria = EntityQueryCriteria.create(Service.class);
        criteria.eq(criteria.proto().catalog(), catalog);
        criteria.eq(criteria.proto().defaultCatalogItem(), false);
        criteria.eq(criteria.proto().code(), typeData.getArCode());
        criteria.eq(criteria.proto().yardiCode(), typeData.getItemType().getCode());

        Service service = Persistence.service().retrieve(criteria);
        if (service == null) {
            service = EntityFactory.create(Service.class);
            service.defaultCatalogItem().setValue(false);
            service.catalog().set(catalog);
            service.code().set(typeData.getArCode());
            service.yardiCode().setValue(typeData.getItemType().getCode());
            service.version().name().setValue(typeData.getItemType().getCode());
            service.version().availableOnline().setValue(true);
        } else {
            if (isServiceChanged(service, typeData)) {
                service = Persistence.secureRetrieveDraft(Service.class, service.getPrimaryKey());
            }
        }

        service.version().description().setValue(typeData.getItemType().getDescription());
        service.version().price().setValue(new BigDecimal(typeData.getItemType().getRent()));

        service.expiredFrom().setValue(null);

        return service;
    }

    private boolean isServiceChanged(Service service, YardiRentableItemTypeData itemTypeData) {

        boolean isChanged = false;

        if (!isChanged) {
            isChanged = (service.version().price().isNull() || service.version().price().getValue()
                    .compareTo(new BigDecimal(itemTypeData.getItemType().getRent())) != 0);
        }
        if (!isChanged) {
            isChanged = !CommonsStringUtils.equals(service.version().description().getValue(), itemTypeData.getItemType().getDescription());
        }

        return isChanged;
    }

    // ----------------------------------------------------------------------------------

    private void updateFeatures(ProductCatalog catalog, RentableItems rentableItems) {
        deleteFeatures(catalog);
        catalog.features().clear();
        for (YardiRentableItemTypeData typeData : retrieveYardiRentableItemTypeData(rentableItems, ARCode.Type.features())) {
            catalog.features().add(ensureFeature(catalog, typeData));
        }
    }

    private void deleteFeatures(ProductCatalog catalog) {
        for (Feature feature : catalog.features()) {
            if (!feature.defaultCatalogItem().isBooleanTrue() && feature.expiredFrom().isNull()) {
                feature.expiredFrom().setValue(new LogicalDate(SystemDateManager.getDate()));
                Persistence.service().merge(feature);
            }
        }
    }

    private Feature ensureFeature(ProductCatalog catalog, YardiRentableItemTypeData typeData) {
        EntityQueryCriteria<Feature> criteria = EntityQueryCriteria.create(Feature.class);
        criteria.eq(criteria.proto().catalog(), catalog);
        criteria.eq(criteria.proto().defaultCatalogItem(), false);
        criteria.eq(criteria.proto().code(), typeData.getArCode());
        criteria.eq(criteria.proto().yardiCode(), typeData.getItemType().getCode());

        Feature feature = Persistence.service().retrieve(criteria);
        if (feature == null) {
            feature = EntityFactory.create(Feature.class);
            feature.defaultCatalogItem().setValue(false);
            feature.catalog().set(catalog);
            feature.code().set(typeData.getArCode());
            feature.yardiCode().setValue(typeData.getItemType().getCode());
            feature.version().name().setValue(typeData.getItemType().getCode());
            feature.version().recurring().setValue(!ARCode.Type.nonReccuringFeatures().contains(typeData.getArCode().type().getValue()));
            feature.version().mandatory().setValue(false);
            feature.version().availableOnline().setValue(true);
            feature.version().items().add(createFeatureItem(typeData.getArCode()));
        } else {
            if (isFeatureChanged(feature, typeData)) {
                feature = Persistence.secureRetrieveDraft(Feature.class, feature.getPrimaryKey());
            }
        }

        feature.version().description().setValue(typeData.getItemType().getDescription());
        feature.version().price().setValue(new BigDecimal(typeData.getItemType().getRent()));

        // update items price:
        Persistence.ensureRetrieve(feature.version().items(), AttachLevel.Attached);
        for (ProductItem item : feature.version().items()) {
            item.price().setValue(feature.version().price().getValue());
        }

        feature.expiredFrom().setValue(null);

        return feature;
    }

    private boolean isFeatureChanged(Feature feature, YardiRentableItemTypeData itemTypeData) {

        boolean isChanged = false;

        if (!isChanged) {
            isChanged = (feature.version().price().isNull() || feature.version().price().getValue()
                    .compareTo(new BigDecimal(itemTypeData.getItemType().getRent())) != 0);
        }
        if (!isChanged) {
            isChanged = !CommonsStringUtils.equals(feature.version().description().getValue(), itemTypeData.getItemType().getDescription());
        }

        return isChanged;
    }

    private ProductItem createFeatureItem(ARCode code) {
        ProductItem item = EntityFactory.create(ProductItem.class);

        item.name().setValue(code.name().getStringView());
        item.price().setValue(BigDecimal.ZERO);

        return item;
    }

    // ----------------------------------------------------------------------------------

    private void updateEligibilityMatrixes(ProductCatalog catalog) {
        for (Service service : catalog.services()) {
            if (!service.defaultCatalogItem().isBooleanTrue()) {
                for (Feature feature : catalog.features()) {
                    Persistence.ensureRetrieve(feature, AttachLevel.Attached);
                    if (!feature.defaultCatalogItem().isBooleanTrue()) {
                        service.version().features().add(feature);
                    }

                    service.version().concessions().clear();
                }
            }
        }
    }

    // ----------------------------------------------------------------------------------

    private class ProductItemByElementComparator implements Comparator<ProductItem> {
        @Override
        public int compare(ProductItem o1, ProductItem o2) {
            return o1.element().getPrimaryKey().compareTo(o2.element().getPrimaryKey());
        }
    };

    private void updateUnitItems(Service service, List<AptUnit> units) {
        Persistence.ensureRetrieve(service.version().items(), AttachLevel.Attached);

        List<ProductItem> serviceItems = new ArrayList<ProductItem>(service.version().items());
        Collections.sort(serviceItems, new ProductItemByElementComparator());

        for (AptUnit unit : units) {
            ProductItem item = EntityFactory.create(ProductItem.class);
            item.element().set(unit);

            if (Collections.binarySearch(serviceItems, item, new ProductItemByElementComparator()) < 0) {
                item.name().setValue(service.code().name().getStringView());
                service.version().items().add(item);
            }
        }

        // update items price:
        for (ProductItem item : service.version().items()) {
            item.price().setValue(service.version().price().getValue());
        }
    }
}
