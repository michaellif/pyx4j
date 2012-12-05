/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-03
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.biz.preloader;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public interface GenericProductCatalogFacade {

    /**
     * Creates new empty catalog for the specified building.
     * 
     * @param building
     */
    void createFor(Building building);

    /**
     * Updates catalog for the specified building with units/parkings/lockers found.
     * 
     * @param building
     */
    void updateFor(Building building);

    /**
     * Persist catalog for the specified building.
     * 
     * @param building
     */
    void persistFor(Building building);

    /**
     * Adds new unit as ProductItem to the catalog unit-related services.
     * 
     * @param building
     * @param unit
     */
    void addUnit(Building building, AptUnit unit);
}
