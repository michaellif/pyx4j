/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 10, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils.gottarent.setup;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.ImportColumn;

@Transient
public interface UnitAvailData extends IEntity {
    @ImportColumn(names = { "property_code" })
    IPrimitive<String> propertyCode();

    @ImportColumn(names = { "unit_number" })
    IPrimitive<String> unitNo();

    @ImportColumn(names = { "availability_date" })
    IPrimitive<String> available();

    @ImportColumn(names = { "market_rent" })
    IPrimitive<String> marketRent();
}
