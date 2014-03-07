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
package com.propertyvista.biz.financial.productcatalog;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class ProductCatalogFacadeImpl implements ProductCatalogFacade {

    private final static Logger log = LoggerFactory.getLogger(ProductCatalogFacadeImpl.class);

    @Override
    public void updateUnitMarketPrice(Building building) {
        UnitMarketPriceCalculator.updateUnitMarketPrice(building);
    }

    @Override
    public void updateUnitMarketPrice(Service service) {
        UnitMarketPriceCalculator.updateUnitMarketPrice(service);
    }

    @Override
    public void add(ProductCatalog catalog, Service service) {
        // TODO Auto-generated method stub

    }

    @Override
    public void add(ProductCatalog catalog, Feature feature) {
        // TODO Auto-generated method stub

    }

    @Override
    public void add(ProductCatalog catalog, Concession concession) {
        // TODO Auto-generated method stub

    }

    @Override
    public void del(ProductCatalog catalog, Service service) {
        // TODO Auto-generated method stub

    }

    @Override
    public void del(ProductCatalog catalog, Feature feature) {
        // TODO Auto-generated method stub

    }

    @Override
    public void del(ProductCatalog catalog, Concession concession) {
        // TODO Auto-generated method stub

    }

    @Override
    public void persist(ProductCatalog catalog, boolean finalize) {
        Persistence.service().persist(catalog.concessions());
        Persistence.service().persist(catalog.features());
        Persistence.service().persist(catalog.services());
    }

    @Override
    public void persist(Service service, boolean finalize) {
        // TODO Auto-generated method stub

    }

    @Override
    public void persist(Feature feature, boolean finalize) {
        // TODO Auto-generated method stub

    }

    @Override
    public void persist(Concession concession, boolean finalize) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Service> getServicesFor(ProductCatalog catalog, AptUnit unit, ARCode.Type type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Feature> getFeaturesFor(ProductCatalog catalog, Service service) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Concession> getConcessionsFor(ProductCatalog catalog, Service service) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ProductItem> getItemsFor(ProductCatalog catalog, Service service, BuildingElement element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ProductItem> getItemsFor(ProductCatalog catalog, Feature feature, BuildingElement element) {
        // TODO Auto-generated method stub
        return null;
    }
}
