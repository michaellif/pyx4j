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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.shared.config.VistaFeatures;

public class ProductCatalogFacadeImpl implements ProductCatalogFacade {

    private final static Logger log = LoggerFactory.getLogger(ProductCatalogFacadeImpl.class);

    @Override
    public void updateUnitMarketPrice(Building building) {
        if (VistaFeatures.instance().productCatalog()) {
            UnitMarketPriceCalculator.updateUnitMarketPrice(building);
        } else {
            log.info("productCatalog feature disabled");
        }
    }

    @Override
    public void updateUnitMarketPrice(Service service) {
        if (VistaFeatures.instance().productCatalog()) {
            UnitMarketPriceCalculator.updateUnitMarketPrice(service);
        } else {
            log.info("productCatalog feature disabled");
        }
    }
}
