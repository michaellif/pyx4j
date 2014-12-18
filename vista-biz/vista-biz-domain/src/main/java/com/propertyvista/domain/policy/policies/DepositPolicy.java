/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 24, 2012
 * @author igor
 */
package com.propertyvista.domain.policy.policies;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("DepositPolicy")
@LowestApplicableNode(value = Building.class)
public interface DepositPolicy extends Policy {

    @NotNull
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> annualInterestRate();

    @NotNull
    IPrimitive<Integer> securityDepositRefundWindow();
}
