/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.unit;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.shared.adapters.FloorplanCountersUpdateAdapter;

@DiscriminatorValue("Unit_BuildingElement")
public interface AptUnit extends BuildingElement, PolicyNode {

    @Detached
    @NotNull
    @JoinColumn
    @MemberColumn(modificationAdapters = { FloorplanCountersUpdateAdapter.class })
    @Indexed(group = { "b,11", "f" })
    Floorplan floorplan();

    @ToString
    @EmbeddedEntity
    @Caption(name = "Information")
    AptUnitInfo info();

    @EmbeddedEntity
    AptUnitFinancial financial();

    //WAS @EmbeddedEntity
    // VladS: This is not used, remains just in case. @Detached and  @Owned added to change the way we retrieve it,
    // By removing it improved portal performance by 15% in profiler. This should not affect the real life application.
    // Check perfomace metrics before enabling it again!
    @Owned
    @Detached
    @Deprecated
    Marketing marketing();

    @Timestamp
    IPrimitive<Date> updated();

    // ----------------------------------------------------
    // parent <-> child relationship:
    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<AptUnitItem> details();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<AptUnitOccupancySegment> _AptUnitOccupancySegment();

    // ----------------------------------------------------
    // internals:
    /**
     * Denormalized field used for search, managed by @see AptUnitOccupancyManagerImpl
     */
    @Indexed
    @Format("MM/dd/yyyy")
    @Caption(name = "Availability")
    IPrimitive<LogicalDate> _availableForRent();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = Lease.class)
    ISet<Lease> _Leases();
}
