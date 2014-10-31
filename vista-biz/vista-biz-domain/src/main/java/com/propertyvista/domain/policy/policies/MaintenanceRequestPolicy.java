/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.TimeWindow;
import com.propertyvista.domain.maintenance.MaintenanceRequestWindow;
import com.propertyvista.domain.maintenance.PermissionToEnterNote;
import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("MaintenanceRequestPolicy")
@LowestApplicableNode(value = Building.class)
public interface MaintenanceRequestPolicy extends Policy {

    /** This text is used next to the permission check box */
    @Owned
    IList<PermissionToEnterNote> permissionToEnterNote();

    //------- Tenant Preferences ---------

    @Owned
    IList<MaintenanceRequestWindow> tenantPreferredWindows();

    //------- Scheduling ------------

    /** If true, allowed time is ignored */
    @Caption(name = "Allow to schedule any time")
    IPrimitive<Boolean> schedulingAllowedAnyTime();

    @EmbeddedEntity
    TimeWindow schedulingAllowedTime();

    @NotNull
    @Caption(name = "Max allowed hour window")
    IPrimitive<Integer> schedulingMaxAllowedWindow();
}