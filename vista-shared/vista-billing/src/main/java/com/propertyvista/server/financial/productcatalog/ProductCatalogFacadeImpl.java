/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.financial.productcatalog;

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
import com.propertyvista.domain.tenant.lease.Lease;

public class ProductCatalogFacadeImpl implements ProductCatalogFacade {

    @Override
    public void updateUnitMarketPrice(Building building) {
        ProductCatalog productCatalog;
        List<AptUnit> units;

        // Retrieve productCatalog with Services that we need e.g. residentialUnit
        {
            EntityQueryCriteria<ProductCatalog> criteria = EntityQueryCriteria.create(ProductCatalog.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
            productCatalog = Persistence.service().retrieve(criteria);
            Persistence.service().retrieve(productCatalog.services());
            for (Service service : productCatalog.services()) {
                if (service.version().type().getValue() == Service.Type.residentialUnit) {
                    Persistence.service().retrieve(service.version().items());
                }
            }
        }

        // All units from this building
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), building));
            units = Persistence.service().query(criteria);
        }

        updateUnitMarketPrice(units, productCatalog);
    }

    /**
     * All Service should be taken into account, Units may be moved from one service to another
     */
    @Override
    public void updateUnitMarketPrice(Service service) {
        if (service.catalog().building().getAttachLevel() == AttachLevel.Detached) {
            Persistence.service().retrieve(service.catalog());
        }
        updateUnitMarketPrice(service.catalog().building());
    }

    private void updateUnitMarketPrice(List<AptUnit> units, ProductCatalog productCatalog) {
        Map<BuildingElement, ProductItem> firstItemsInCatalog = new HashMap<BuildingElement, ProductItem>();

        //Find only first Service/ ProductItem that apply to units
        for (Service service : productCatalog.services()) {
            if (service.version().type().getValue() == Service.Type.residentialUnit) {
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

    private void updateUnitMarketPrice(AptUnit unit, ProductItem productItem) {
        BigDecimal origPrice = unit.financial()._marketRent().getValue();
        BigDecimal currentPrice = calculateUnitMarketPrice(productItem);
        if (!EqualsHelper.equals(origPrice, currentPrice)) {
            unit.financial()._marketRent().setValue(currentPrice);
            Persistence.service().merge(unit);
        }

    }

    public BigDecimal calculateUnitMarketPrice(ProductItem productItem) {
        if (productItem == null) {
            // There are no service for this unit
            return null;
        }
        //TODO add concessions and adjustments
        return productItem.price().getValue();
    }

    /*
     * TODO Move to LeaseFacade
     */
    @Deprecated
    @Override
    public void updateUnitRentPrice(Lease lease) {
        Persistence.service().retrieve(lease.unit());

        BigDecimal origPrice = lease.unit().financial()._unitRent().getValue();
        BigDecimal currentPrice = getUnitRentPrice(lease.unit());

        if ((origPrice != null && !origPrice.equals(currentPrice)) || (origPrice == null && currentPrice != null)) {
            lease.unit().financial()._unitRent().setValue(currentPrice);
            Persistence.service().merge(lease.unit());
            Persistence.service().commit();
        }
    }

    /*
     * Move to LeaseFacade
     */
    @Deprecated
    public BigDecimal getUnitRentPrice(AptUnit unit) {
        EntityQueryCriteria<Lease> leaseCriteria = new EntityQueryCriteria<Lease>(Lease.class);
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().unit(), unit));
        Lease lease = Persistence.service().retrieve(leaseCriteria);
        if (lease != null && !lease.version().leaseProducts().isNull() && !lease.version().leaseProducts().serviceItem().isNull()) {
            //TODO add concessions and adjustments
            return lease.version().leaseProducts().serviceItem().item().price().getValue();
        }
        return null;
    }
}
