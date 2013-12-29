/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;

import com.propertyvista.domain.property.asset.building.Building;

@Table(prefix = "billing")
@DiscriminatorValue("BuildingArrearsSnapshot")
public interface BuildingArrearsSnapshot extends ArrearsSnapshot<BuildingAgingBuckets> {

    @Owner
    @MemberColumn(notNull = true)
    @Detached
    @NotNull
    @Indexed
    @JoinColumn
    Building building();
}
