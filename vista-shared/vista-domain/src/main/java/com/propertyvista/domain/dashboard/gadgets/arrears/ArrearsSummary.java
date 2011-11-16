/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-11-16
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.arrears;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.building.Building;

public interface ArrearsSummary extends Arrears {
    @Detached
    @ReadOnly
    @Owner
    Building belongsTo();

    IPrimitive<String> propertyCode();

    IPrimitive<LogicalDate> statusTimestamp();
}
