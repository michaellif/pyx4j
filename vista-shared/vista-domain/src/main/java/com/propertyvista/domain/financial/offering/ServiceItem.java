/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 22, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.BuildingElement;

public interface ServiceItem extends IEntity {

// TODO Vlads
//    @Owner
//    @Detached
//    @ReadOnly
//    ServiceOrFeature serviceOrFeature();

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "itemType")
    ServiceItemType type();

    IPrimitive<Double> price();

    IPrimitive<String> description();

    @Detached
    BuildingElement element();
}
