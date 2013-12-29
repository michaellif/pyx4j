/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.unit.occupancy;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;

public interface AptUnitOccupancySegment extends IEntity {

    @I18n(context = "AptUnitOccupancySegment Status")
    @XmlType(name = "AptUnitOccupancySegmentStatus")
    public enum Status {

        occupied,

        available,

        /**
         * a transitional state for imported units, i.e. it's most probably lease, but awaiting final approval to become actually leased
         */
        migrated,

        /**
         * unit has lease that starts in the future and is not available for another lease
         * (from the point of view of availability report it's leased, not in "net exposure")
         */
        reserved,

        /**
         * Unit is vacant (i.e. no one lives there), but is not ready for marketing, needs some action in order to become available.
         * or example: unit is leased right now, and a person gave a notice, it becomes vacant in the future (i.e. not yet scoped).
         */
        pending,

        offMarket,

        /**
         * A special case of {@link #offMarket}. This must have a 'finite' {@link AptUnitOccupancySegment#dateFrom()} and
         * {@link AptUnitOccupancySegment#dateTo()}.
         */
        renovation;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    /**
     * Based on MITS schemas.
     */
    @I18n(context = "OffMarket Type")
    @XmlType(name = "OffMarketType")
    public enum OffMarketType {

        down,

        model,

        employee,

        construction,

        office,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    AptUnit unit();

    @OrderColumn
    @NotNull
    IPrimitive<LogicalDate> dateFrom();

    /**
     * What if there is no limit to this date, what should it be then?
     */
    IPrimitive<LogicalDate> dateTo();

    @NotNull
    IPrimitive<Status> status();

    IPrimitive<OffMarketType> offMarket();

    Lease lease();

    /**
     * Would be good to have an example of a description for occupancy in Java Doc
     * 
     * @deprecated not clear what is this field is supposed to represent
     */
    @Deprecated
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();
}
