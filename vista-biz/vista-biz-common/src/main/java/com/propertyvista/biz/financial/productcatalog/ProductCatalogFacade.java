/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.productcatalog;

import java.math.BigDecimal;
import java.util.List;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public interface ProductCatalogFacade {

    // build:

    void add(ProductCatalog catalog, Service service);

    void add(ProductCatalog catalog, Feature feature);

    void add(ProductCatalog catalog, Concession concession);

    void del(ProductCatalog catalog, Service service);

    void del(ProductCatalog catalog, Feature feature);

    void del(ProductCatalog catalog, Concession concession);

    // persist:

    void persist(ProductCatalog catalog, boolean finalize);

    void persist(Service service, boolean finalize);

    void persist(Feature feature, boolean finalize);

    void persist(Concession concession, boolean finalize);

    // access:

    List<Service> getServicesFor(ProductCatalog catalog, AptUnit unit, ARCode.Type type);

    // eligibility:
    List<Feature> getFeaturesFor(ProductCatalog catalog, Service service);

    List<Concession> getConcessionsFor(ProductCatalog catalog, Service service);

    // product items:
    List<ProductItem> getItemsFor(ProductCatalog catalog, Service service, BuildingElement element);

    List<ProductItem> getItemsFor(ProductCatalog catalog, Feature feature, BuildingElement element);

    // utils:

    void updateUnitMarketPrice(Building building);

    void updateUnitMarketPrice(Service service);

    BigDecimal calculateItemPrice(ProductItem productItem);
}
