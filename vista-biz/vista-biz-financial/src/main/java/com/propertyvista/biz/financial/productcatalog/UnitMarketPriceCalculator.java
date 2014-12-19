/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 13, 2012
 * @author michaellif
 */
package com.propertyvista.biz.financial.productcatalog;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class UnitMarketPriceCalculator {

    static void updateUnitMarketPrice(Building building) {

        // grab product items from this building catalog
        List<ProductItem> productItems;
        {
            EntityQueryCriteria<ProductItem> criteria = EntityQueryCriteria.create(ProductItem.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().product().holder().catalog().building(), building));
            criteria.add(PropertyCriterion.in(criteria.proto().product().holder().code().type(), ARCode.Type.unitRelatedServicesResidential()));
            criteria.add(PropertyCriterion.eq(criteria.proto().product().holder().defaultCatalogItem(), false));
            criteria.isCurrent(criteria.proto().product());

            productItems = Persistence.service().query(criteria);
        }

        // and units from this building
        List<AptUnit> units;
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));

            units = Persistence.service().query(criteria);
        }

        // calculate:
        updateUnitMarketPrice(units, productItems);
    }

    /**
     * All Service should be taken into account, Units may be moved from one service to another
     */
    static void updateUnitMarketPrice(Service service) {
        Persistence.ensureRetrieve(service.catalog().building(), AttachLevel.IdOnly);
        updateUnitMarketPrice(service.catalog().building());
    }

    private static void updateUnitMarketPrice(List<AptUnit> units, List<ProductItem> productItems) {
        Map<BuildingElement, List<ProductItem>> itemsInCatalog = new HashMap<>();

        // Find only all Service/ ProductItems that apply to units:
        for (ProductItem item : productItems) {
            if (item.element().isInstanceOf(AptUnit.class)) {
                List<ProductItem> items = itemsInCatalog.get(item.element());
                if (items != null) {
                    items.add(item);
                } else {
                    itemsInCatalog.put(item.element(), new LinkedList<>(Arrays.asList(item)));
                }
            }
        }

        for (AptUnit unit : units) {
            updateUnitMarketPrice(unit, itemsInCatalog.get(unit));
        }
    }

    private static void updateUnitMarketPrice(AptUnit unit, List<ProductItem> items) {
        unit.financial()._marketRent().setValue(null);

        if (items != null) {
            for (ProductItem item : items) {
                BigDecimal itemPrice = ServerSideFactory.create(ProductCatalogFacade.class).calculateItemPrice(item);

                // select item with highest price:
                if (unit.financial()._marketRent().isNull() || unit.financial()._marketRent().getValue().compareTo(itemPrice) < 0) {
                    unit.financial()._marketRent().setValue(itemPrice);
                }
            }
        }

        Persistence.service().merge(unit);
    }
}
