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
 * @version $Id$
 */
package com.propertyvista.biz.financial.productcatalog;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class UnitMarketPriceCalculator {

    static void updateUnitMarketPrice(Building building) {
        ProductCatalog productCatalog;
        List<AptUnit> units;

        // Retrieve productCatalog with Services that we need e.g. residentialUnit
        {
            EntityQueryCriteria<ProductCatalog> criteria = EntityQueryCriteria.create(ProductCatalog.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
            productCatalog = Persistence.service().retrieve(criteria);
            Persistence.service().retrieve(productCatalog.services());
            for (Service service : productCatalog.services()) {
                if (service.serviceType().getValue() == Service.ServiceType.residentialUnit) {
                    Persistence.service().retrieve(service.version().items());
                }
            }
        }

        // All units from this building
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
            units = Persistence.service().query(criteria);
        }

        updateUnitMarketPrice(units, productCatalog);
    }

    /**
     * All Service should be taken into account, Units may be moved from one service to another
     */
    static void updateUnitMarketPrice(Service service) {
        if (service.catalog().building().getAttachLevel() == AttachLevel.Detached) {
            Persistence.service().retrieve(service.catalog());
        }
        updateUnitMarketPrice(service.catalog().building());
    }

    private static void updateUnitMarketPrice(List<AptUnit> units, ProductCatalog productCatalog) {
        Map<BuildingElement, ProductItem> firstItemsInCatalog = new HashMap<BuildingElement, ProductItem>();

        //Find only first Service/ ProductItem that apply to units
        for (Service service : productCatalog.services()) {
            if (service.serviceType().getValue() == Service.ServiceType.residentialUnit) {
                for (ProductItem item : service.version().items()) {
                    if (item.element().isInstanceOf(AptUnit.class)) {
                        if (!firstItemsInCatalog.containsKey(item.element())) {
                            firstItemsInCatalog.put(item.element(), item);
                        }
                    }
                }
            }
        }

        for (AptUnit unit : units) {
            updateUnitMarketPrice(unit, firstItemsInCatalog.get(unit));
        }
    }

    private static void updateUnitMarketPrice(AptUnit unit, ProductItem productItem) {
        BigDecimal origPrice = unit.financial()._marketRent().getValue();
        BigDecimal currentPrice = calculateUnitMarketPrice(productItem);
        if (!EqualsHelper.equals(origPrice, currentPrice)) {
            unit.financial()._marketRent().setValue(currentPrice);
            Persistence.service().merge(unit);
        }

    }

    private static BigDecimal calculateUnitMarketPrice(ProductItem productItem) {
        if (productItem == null) {
            // There are no service for this unit
            return null;
        }
        //TODO add concessions and adjustments
        return productItem.price().getValue();
    }
}
