/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager;

import java.math.BigDecimal;

public interface YardiBuildingManager extends YardiMockManager {

    public static final String DEFAULT_PROPERTY_CODE = "prop123";

    public static final String DEFAULT_ADDR_STREET = "123 Main St";

    public static final String DEFAULT_ADDR_CITY = "Toronto";

    public static final String DEFAULT_ADDR_PROV = "Ontario";

    public static final String DEFAULT_ADDR_COUNTRY = "Canada";

    public static final String DEFAULT_ADDR_POSTCODE = "A1B 2C3";

    public static final String DEFAULT_UNIT_NO = "1";

    public static final String DEFAULT_UNIT_RENT = "990.00";

    public static final String DEFAULT_FP_NAME = "2bdrm";

    public static final int DEFAULT_FP_BEDS = 2;

    public static final int DEFAULT_FP_BATHS = 1;

    public interface BuildingBuilder {
        BuildingBuilder setAddress(String address);

        BuildingBuilder addFloorplan(String id, int beds, int baths);

        BuildingBuilder addUnit(String id, String fpId, BigDecimal unitRent);
    }

    BuildingBuilder addDefaultBuilding();

    BuildingBuilder addBuilding(String propertyId);
}
