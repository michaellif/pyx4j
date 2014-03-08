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
import java.util.Map;

import com.yardi.entity.guestcard40.RentableItemType;
import com.yardi.entity.guestcard40.RentableItems;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.Type;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductDeposit.ValueType;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class YardiProductCatalogProcessor {

    public void processCatalog(Building building, RentableItems rentableItems, Key yardiInterfaceId) {
        Persistence.ensureRetrieve(building.productCatalog().services(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().features(), AttachLevel.Attached);

        updateServices(building.productCatalog());
        updateFeatures(building.productCatalog(), rentableItems);

        updateEligibilityMatrixes(building.productCatalog());
    }

    public void updateUnits(Building building, Map<String, BigDecimal> depositInfo) {
        assert (!building.productCatalog().services().isValueDetached());

        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.in(criteria.proto().building(), building);

        List<AptUnit> units = Persistence.service().query(criteria);

        ARCode arCode = getServiceArCode();
        for (Service service : building.productCatalog().services()) {
            if (!service.defaultCatalogItem().isBooleanTrue() && service.code().equals(arCode)) {
                updateUnitItems(service, units, depositInfo);
            }
        }
    }

    public void persistCatalog(Building building) {
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
    private void updateServices(ProductCatalog catalog) {
        deleteServices(catalog);
        catalog.services().clear();
        catalog.services().add(ensureService(catalog));
    }

    private void deleteServices(ProductCatalog catalog) {
        for (Service service : catalog.services()) {
            if (!service.defaultCatalogItem().isBooleanTrue() && service.expiredFrom().isNull()) {
                service.expiredFrom().setValue(SystemDateManager.getLogicalDate());
                Persistence.service().merge(service);
            }
        }
    }

    private Service ensureService(ProductCatalog catalog) {
        ARCode arCode = getServiceArCode();

        assert (arCode != null);

        EntityQueryCriteria<Service> criteria = EntityQueryCriteria.create(Service.class);
        criteria.eq(criteria.proto().catalog(), catalog);
        criteria.eq(criteria.proto().defaultCatalogItem(), false);
        criteria.eq(criteria.proto().code(), arCode);

        Service service = Persistence.service().retrieve(criteria);
        if (service == null) {
            service = EntityFactory.create(Service.class);

            service.defaultCatalogItem().setValue(false);
            service.catalog().set(catalog);
            service.code().set(arCode);
            service.version().name().setValue(arCode.name().getValue());
            service.version().availableOnline().setValue(true);

            ServerSideFactory.create(DefaultProductCatalogFacade.class).fillDefaultDeposits(service);
        }

        service.expiredFrom().setValue(null);

        return service;
    }

    private ARCode getServiceArCode() {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().type(), ARCode.Type.Residential);
        criteria.isNotNull(criteria.proto().yardiChargeCodes());

        return Persistence.service().retrieve(criteria);
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
                feature.expiredFrom().setValue(SystemDateManager.getLogicalDate());
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

            ServerSideFactory.create(DefaultProductCatalogFacade.class).fillDefaultDeposits(feature);
        } else {
            if (isFeatureChanged(feature, typeData)) {
                feature = Persistence.service().retrieve(Feature.class, feature.getPrimaryKey().asDraftKey());
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
                Persistence.ensureRetrieve(service.version().features(), AttachLevel.Attached);
                Persistence.ensureRetrieve(service.version().concessions(), AttachLevel.Attached);

                service.version().features().clear();
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

    private void updateUnitItems(Service service, List<AptUnit> units, Map<String, BigDecimal> depositInfo) {
        Persistence.ensureRetrieve(service.version().items(), AttachLevel.Attached);
        // disable deposit till further processing:
        service.version().depositLMR().enabled().setValue(!units.isEmpty());
        // set Yardi deposit default value/type:
        service.version().depositLMR().valueType().setValue(ValueType.Monetary);
        service.version().depositLMR().value().setValue(BigDecimal.ZERO);

        List<ProductItem> serviceItems = new ArrayList<ProductItem>(service.version().items());
        Collections.sort(serviceItems, new ProductItemByElementComparator());

        for (AptUnit unit : units) {
            ProductItem item = EntityFactory.create(ProductItem.class);
            item.element().set(unit);

            int found = Collections.binarySearch(serviceItems, item, new ProductItemByElementComparator());
            if (found >= 0) {
                item = serviceItems.get(found);
                item.price().setValue(unit.financial()._marketRent().getValue());
            } else {
                item.name().setValue(service.code().name().getStringView());
                item.price().setValue(unit.financial()._marketRent().getValue());
                service.version().items().add(item);
            }

            // update deposit:
            BigDecimal depositValue = depositInfo.get(unit.info().number().getValue());
            if (depositValue != null) {
                item.depositLMR().setValue(depositValue);
                // enable service deposit:
                service.version().depositLMR().enabled().setValue(true);
            } else {
                item.depositLMR().setValue(BigDecimal.ZERO);
            }
        }
    }
}
