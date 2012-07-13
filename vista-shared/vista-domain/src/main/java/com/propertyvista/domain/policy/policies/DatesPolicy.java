/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2012
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("DatesPolicy")
@LowestApplicableNode(value = Building.class)
public interface DatesPolicy extends Policy, TenantsAccessiblePolicy {

    @Format("yyyy")
    @Editor(type = EditorType.yearpicker)
    @Caption(description = "Building history start year")
    IPrimitive<LogicalDate> yearRangeStart();

    @Caption(description = "Building history future span")
    IPrimitive<Integer> yearRangeFutureSpan();
}
