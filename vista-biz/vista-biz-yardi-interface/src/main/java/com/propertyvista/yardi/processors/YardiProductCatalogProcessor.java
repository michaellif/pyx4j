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
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.Pair;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IVersionedEntity.SaveAction;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityGraph;

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

        updateServices(building.productCatalog(), rentableItems);
        updateFeatures(building.productCatalog(), rentableItems);

        updateEligibilityMatrixes(building.productCatalog());
    }

    public void updateUnits(Building building, Map<String, BigDecimal> depositInfo) {
        assert (!building.productCatalog().services().isValueDetached());

        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.in(criteria.proto().building(), building);

        List<AptUnit> units = Persistence.service().query(criteria);

        ARCode arCode = getServiceArCode();
        List<Pair<Service, Service>> updatedServices = new ArrayList<>();
        for (Service service : building.productCatalog().services()) {
            if (!service.defaultCatalogItem().getValue(false) && service.code().equals(arCode)) {
                updatedServices.add(updateUnitItems(service, units, depositInfo));
            }
        }

        replaceOriginalDraftServices(building.productCatalog(), updatedServices);
    }

    public void persistCatalog(Building building) {
        assert (!building.productCatalog().services().isValueDetached());
        assert (!building.productCatalog().features().isValueDetached());

        // Save services and features:
        for (Feature feature : building.productCatalog().features()) {
            if (!feature.defaultCatalogItem().getValue(false)) {
                Persistence.service().merge(feature);
            }
        }

        for (Service service : building.productCatalog().services()) {
            if (!service.defaultCatalogItem().getValue(false)) {
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
        for (YardiRentableItemTypeData typeData : retrieveYardiRentableItemTypeData(rentableItems, ARCode.Type.services())) {
            ensureService(catalog, typeData);
        }

        // create default one if no set in Yardi: 
        if (!isAnyServicePresent(catalog)) {
            ARCode arCode = getServiceArCode();
            assert (arCode != null);

            RentableItemType itemType = new RentableItemType();
            itemType.setRent("0.00");
            itemType.setCode(arCode.type().getValue().name());
            itemType.setDescription(arCode.name().getStringView());

            ensureService(catalog, new YardiRentableItemTypeData(itemType, arCode));
        }
    }

    private void deleteServices(ProductCatalog catalog) {
        for (Service service : catalog.services()) {
            if (!service.defaultCatalogItem().getValue(false) && service.expiredFrom().isNull()) {
                service.expiredFrom().setValue(SystemDateManager.getLogicalDate());
            }
        }
    }

    private boolean isAnyServicePresent(ProductCatalog catalog) {
        boolean found = false;

        for (Service service : catalog.services()) {
            if (!service.defaultCatalogItem().getValue(false) && service.expiredFrom().isNull()) {
                found = true;
                break;
            }
        }

        return found;
    }

    private Service ensureService(ProductCatalog catalog, YardiRentableItemTypeData typeData) {
        Service service = findService(catalog, typeData);
        if (service == null) {
            service = EntityFactory.create(Service.class);
            catalog.services().add(service);

            service.defaultCatalogItem().setValue(false);
            service.catalog().set(catalog);
            service.code().set(typeData.getArCode());
            service.yardiCode().setValue(typeData.getItemType().getCode());

            service.version().name().setValue(typeData.getItemType().getCode());
            service.version().availableOnline().setValue(true);

            ServerSideFactory.create(DefaultProductCatalogFacade.class).fillDefaultDeposits(service);
        } else {
            if (isServiceChanged(service, typeData)) {
                catalog.services().remove(service);
                service = Persistence.retrieveDraftForEdit(Service.class, service.getPrimaryKey().asDraftKey());
                service.saveAction().setValue(SaveAction.saveAsFinal);
                catalog.services().add(service);
            }
        }

        service.version().description().setValue(typeData.getItemType().getDescription());
        service.version().price().setValue(new BigDecimal(typeData.getItemType().getRent()));

        service.expiredFrom().setValue(null);

        return service;
    }

    private ARCode getServiceArCode() {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().type(), ARCode.Type.Residential);
        criteria.isNotNull(criteria.proto().yardiChargeCodes());

        return Persistence.service().retrieve(criteria);
    }

    private Service findService(ProductCatalog catalog, YardiRentableItemTypeData typeData) {
        Service result = null;

        for (Service item : catalog.services()) {
            //@formatter:off
            if (!item.defaultCatalogItem().getValue(false) 
              && item.code().equals(typeData.getArCode())
              && CommonsStringUtils.equals(item.yardiCode().getValue(), typeData.getItemType().getCode())) {
            //@formatter:on
                result = item;
                break;
            }
        }

        return result;
    }

    private boolean isServiceChanged(Service service, YardiRentableItemTypeData itemTypeData) {
        boolean isChanged = false;

        if (!isChanged) {
            isChanged = (!service.version().price().isNull() && service.version().price().getValue()
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
        for (YardiRentableItemTypeData typeData : retrieveYardiRentableItemTypeData(rentableItems, ARCode.Type.features())) {
            ensureFeature(catalog, typeData);
        }
    }

    private void deleteFeatures(ProductCatalog catalog) {
        for (Feature feature : catalog.features()) {
            if (!feature.defaultCatalogItem().getValue(false) && feature.expiredFrom().isNull()) {
                feature.expiredFrom().setValue(SystemDateManager.getLogicalDate());
            }
        }
    }

    private Feature ensureFeature(ProductCatalog catalog, YardiRentableItemTypeData typeData) {
        Feature feature = findFeature(catalog, typeData);
        if (feature == null) {
            feature = EntityFactory.create(Feature.class);
            catalog.features().add(feature);

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
                catalog.features().remove(feature);
                feature = Persistence.retrieveDraftForEdit(Feature.class, feature.getPrimaryKey().asDraftKey());
                feature.saveAction().setValue(SaveAction.saveAsFinal);
                catalog.features().add(feature);
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

    private Feature findFeature(ProductCatalog catalog, YardiRentableItemTypeData typeData) {
        Feature result = null;

        for (Feature item : catalog.features()) {
            //@formatter:off
            if (!item.defaultCatalogItem().getValue(false) 
              && item.code().equals(typeData.getArCode())
              && CommonsStringUtils.equals(item.yardiCode().getValue(), typeData.getItemType().getCode())) {
            //@formatter:on
                result = item;
                break;
            }
        }

        return result;
    }

    private boolean isFeatureChanged(Feature feature, YardiRentableItemTypeData itemTypeData) {
        boolean isChanged = false;

        if (!isChanged) {
            isChanged = (!feature.version().price().isNull() && feature.version().price().getValue()
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
        List<Feature> newFeatures = new ArrayList<>();
        for (Feature feature : catalog.features()) {
            if (!feature.defaultCatalogItem().getValue(false)) {
                newFeatures.add(feature);
            }
        }

        List<Pair<Service, Service>> updatedServices = new ArrayList<>();

        for (Service service : catalog.services()) {
            if (!service.defaultCatalogItem().getValue(false)) {
                if (service.id().isNull()) {
                    service.version().features().addAll(newFeatures);
                } else {
                    if (!compareEligibilityMatrixData(service.version().features(), newFeatures)) {
                        Pair<Service, Service> updated = retrieveOriginalDraftServices(service);

                        updated.getB().version().features().clear();
                        updated.getB().version().features().addAll(newFeatures);
                        updated.getB().saveAction().setValue(SaveAction.saveAsFinal);

                        updatedServices.add(updated);
                    }
                }
            }
        }

        replaceOriginalDraftServices(catalog, updatedServices);
    }

    private boolean compareEligibilityMatrixData(List<Feature> current, List<Feature> newOnes) {
        Collection<Key> currentKeys = new ArrayList<>(current.size());
        for (Feature item : current) {
            currentKeys.add(item.getPrimaryKey());
        }

        Collection<Key> newKeys = new ArrayList<>(newOnes.size());
        for (Feature item : newOnes) {
            if (item.getPrimaryKey() != null) {
                newKeys.add(item.getPrimaryKey().asCurrentKey());
            }
        }

        return EqualsHelper.equals(currentKeys, newKeys);
    }

    private Pair<Service, Service> retrieveOriginalDraftServices(Service original) {
        Pair<Service, Service> result = new Pair<>(original, original);

        if (original.getPrimaryKey() != null && !original.getPrimaryKey().isDraft()) {
            result.setB(Persistence.retrieveDraftForEdit(Service.class, original.getPrimaryKey().asDraftKey()));
        }

        return result;
    }

    private void replaceOriginalDraftServices(ProductCatalog catalog, List<Pair<Service, Service>> updatedServices) {
        for (Pair<Service, Service> updated : updatedServices) {
            if (!updated.getA().equals(updated.getB())) {
                catalog.services().remove(updated.getA());
                catalog.services().add(updated.getB());
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

    private Pair<Service, Service> updateUnitItems(Service service, List<AptUnit> units, Map<String, BigDecimal> depositInfo) {
        Pair<Service, Service> updated = retrieveOriginalDraftServices(service);
        Persistence.ensureRetrieve(updated.getA().version().items(), AttachLevel.Attached);
        Persistence.ensureRetrieve(updated.getB().version().items(), AttachLevel.Attached);

        Service originalService = updated.getB().duplicate();

        // disable deposit till further processing:
        updated.getB().version().depositLMR().enabled().setValue(!units.isEmpty());
        // set Yardi deposit default value/type:
        updated.getB().version().depositLMR().valueType().setValue(ValueType.Monetary);
        updated.getB().version().depositLMR().value().setValue(new BigDecimal("0.00"));

        List<ProductItem> serviceItems = new ArrayList<ProductItem>(updated.getB().version().items());
        Collections.sort(serviceItems, new ProductItemByElementComparator());

        for (AptUnit unit : units) {
            ProductItem item = EntityFactory.create(ProductItem.class);
            item.element().set(unit);

            int found = Collections.binarySearch(serviceItems, item, new ProductItemByElementComparator());
            if (found >= 0) {
                item = serviceItems.get(found);
                item.price().setValue(unit.financial()._marketRent().getValue());
            } else {
                item.name().setValue(updated.getB().code().name().getStringView());
                item.price().setValue(unit.financial()._marketRent().getValue());
                updated.getB().version().items().add(item);
            }

            // update deposit:
            BigDecimal depositValue = depositInfo.get(unit.info().number().getValue());
            if (depositValue != null) {
                item.depositLMR().setValue(depositValue);
                // enable service deposit:
                updated.getB().version().depositLMR().enabled().setValue(true);
            } else {
                item.depositLMR().setValue(new BigDecimal("0.00"));
            }
        }

        if (EntityGraph.fullyEqualValues(originalService, updated.getB())) {
            updated.setB(updated.getA()); // ignore update...
        } else {
            updated.getB().saveAction().setValue(SaveAction.saveAsFinal);
        }

        return updated;
    }
}
