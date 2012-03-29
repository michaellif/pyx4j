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
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;

public class ProductCatalogFacadeImpl implements ProductCatalogFacade {

    @Override
    public void updateUnitMarketPrice(Building building) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), building));
        updateUnitMarketPrice(Persistence.service().query(criteria));
    }

    @Override
    public void updateUnitMarketPrice(Service service) {
        List<AptUnit> units = new Vector<AptUnit>();
        for (ProductItem item : service.version().items()) {
            if (item.element() instanceof AptUnit) {
                units.add((AptUnit) item.element());
            }
        }

        if (!units.isEmpty()) {
            Persistence.service().retrieve(units);
            updateUnitMarketPrice(units);
        }
    }

    private void updateUnitMarketPrice(List<AptUnit> units) {
        for (AptUnit unit : units) {
            BigDecimal origPrice = unit.financial()._marketRent().getValue();
            BigDecimal currentPrice = getUnitMarketPrice(unit);
            if ((origPrice != null && !origPrice.equals(currentPrice)) || (origPrice == null && currentPrice != null)) {
                unit.financial()._marketRent().setValue(currentPrice);
                Persistence.service().persist(unit.financial());
                Persistence.service().commit();
            }
        }
    }

    /*
     * Move to LeaseFacade
     */
    @Deprecated
    @Override
    public void updateUnitRentPrice(Lease lease) {
        Persistence.service().retrieve(lease.unit());

        BigDecimal origPrice = lease.unit().financial()._unitRent().getValue();
        BigDecimal currentPrice = getUnitRentPrice(lease.unit());

        if ((origPrice != null && !origPrice.equals(currentPrice)) || (origPrice == null && currentPrice != null)) {
            lease.unit().financial()._unitRent().setValue(currentPrice);
            Persistence.service().persist(lease.unit().financial());
            Persistence.service().commit();
        }
    }

    @Override
    public BigDecimal getUnitMarketPrice(AptUnit unit) {
        EntityQueryCriteria<ProductItem> serviceItemCriteria = new EntityQueryCriteria<ProductItem>(ProductItem.class);
        serviceItemCriteria.add(PropertyCriterion.eq(serviceItemCriteria.proto().element(), unit));
        ProductItem item = Persistence.service().retrieve(serviceItemCriteria);
        if (item != null) {
            //TODO add concessions and adjustments
            return item.price().getValue();
        }
        return null;
    }

    /*
     * Move to LeaseFacade
     */
    @Deprecated
    @Override
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
