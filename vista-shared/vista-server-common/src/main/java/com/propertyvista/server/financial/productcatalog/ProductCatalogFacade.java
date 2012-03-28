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
package com.propertyvista.server.financial.productcatalog;

import java.math.BigDecimal;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;

public interface ProductCatalogFacade {

    void updateUnitMarketPrice(Building building);

    BigDecimal getUnitMarketPrice(AptUnit unit);

    void updateUnitRentPrice(Lease lease);

    BigDecimal getUnitRentPrice(AptUnit unit);

}
