/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.xls;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.essentials.server.csv.ImportColumn;

public interface UnitModel extends IEntity {

    @ImportColumn(names = { "Property", "Property Code" })
    IPrimitive<String> property();

    IPrimitive<String> unit();

    IPrimitive<String> unitType();

    IPrimitive<String> unitSqFt();

    // Rent Role Update

    IPrimitive<String> marketRent();

    IPrimitive<String> newMarketRent();

    IPrimitive<String> status();

    IPrimitive<String> date();

}
