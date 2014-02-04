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

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;

import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.note.HasNotesAndAttachments;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.shared.adapters.FloorplanCountersUpdateAdapter;

@DiscriminatorValue("AptUnit")
public interface AptUnit extends BuildingElement, PolicyNode, HasNotesAndAttachments {

    @Override
    @Owner
    @MemberColumn(notNull = true)
    @NotNull
    @ReadOnly
    @Detached
    @Caption(name = "Building")
    @XmlTransient
    @JoinColumn
    @Indexed(group = { "b,10", "BuildingUnitNumber,10" })
    Building building();

    @Detached
    @NotNull
    @MemberColumn(modificationAdapters = { FloorplanCountersUpdateAdapter.class })
    @Indexed(group = { "b,11", "f" })
    Floorplan floorplan();

    @ToString
    @EmbeddedEntity
    @Caption(name = "Information")
    AptUnitInfo info();

    @EmbeddedEntity
    AptUnitFinancial financial();

    @Timestamp
    IPrimitive<Date> updated();

    // ----------------------------------------------------
    // parent <-> child relationship:
    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<AptUnitItem> details();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<AptUnitOccupancySegment> unitOccupancySegments();

    // ----------------------------------------------------
    // internals:
    /**
     * Denormalized field used for search, managed by @see AptUnitOccupancyManagerImpl
     * Used in old Starlinght portal only
     */
    @Indexed
    @Deprecated
    @Caption(name = "Availability")
    IPrimitive<LogicalDate> _availableForRent();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = Lease.class)
    ISet<Lease> leases();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = ProductItem.class, mappedBy = ProductItem.BuildingElementColumnId.class)
    ISet<ProductItem> productItems();
}
